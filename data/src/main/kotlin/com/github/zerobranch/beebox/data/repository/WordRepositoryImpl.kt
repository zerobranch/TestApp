package com.github.zerobranch.beebox.data.repository

import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.data.base.NetworkErrorParser
import com.github.zerobranch.beebox.data.mappers.toDomain
import com.github.zerobranch.beebox.data.mappers.toEntity
import com.github.zerobranch.beebox.data.source.db.AppDatabase
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.exceptions.ExistChildWordException
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.repository.WordRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val moshi: Moshi,
    private val networkErrorParser: NetworkErrorParser
) : WordRepository {

    override fun getWordCount(): Flow<Int> =
        flow { emit(appDatabase.wordDao().getWordCount()) }
            .catch { networkErrorParser.invoke(it) }

    override fun getWordTypesCount(): Flow<Int> =
        flow { emit(appDatabase.wordDao().getWordTypesCount()) }
            .catch { networkErrorParser.invoke(it) }

    override fun getWordCategoriesCount(): Flow<Int> =
        flow { emit(appDatabase.wordDao().getWordCategoriesCount()) }
            .catch { networkErrorParser.invoke(it) }

    override fun search(word: String): Flow<List<Word>> =
        flow { emit(appDatabase.wordDao().search(word)) }
            .map { entities ->
                val wordDao = appDatabase.wordDao()
                entities.map { entity ->
                    val typeCount = wordDao.getWordTypesCount(entity.word.word)
                    val categoriesCount = wordDao.getWordCategoriesCount(entity.word.word)
                    entity.toDomain(moshi, typeCount, categoriesCount)
                }
            }
            .catch { networkErrorParser.invoke(it) }

    override fun getAll(): Flow<List<Word>> =
        appDatabase.wordDao()
            .readAllLife()
            .map { entities ->
                val wordDao = appDatabase.wordDao()
                entities.map { entity ->
                    val typeCount = wordDao.getWordTypesCount(entity.word.word)
                    val categoriesCount = wordDao.getWordCategoriesCount(entity.word.word)
                    entity.toDomain(moshi, typeCount, categoriesCount)
                }
            }
            .catch { networkErrorParser.invoke(it) }

    override fun getAll(
        ignoreWordCategory: WordCategory?,
        ignoreWordType: WordType?
    ): Flow<List<Word>> =
        getAll()
            .map { words ->
                if (ignoreWordCategory.isNotNull()) {
                    words.filterNot { word -> word.categories.any { it.id == ignoreWordCategory.id } }
                } else {
                    words
                }
            }
            .map { words ->
                if (ignoreWordType.isNotNull()) {
                    words.filterNot { word -> word.wordTypes.any { it.id == ignoreWordType.id } }
                } else {
                    words
                }
            }
            .catch { networkErrorParser.invoke(it) }

    override fun getAllByFilter(
        wordCategories: List<WordCategory>,
        wordTypes: List<WordType>
    ): Flow<List<Word>> =
        appDatabase.wordDao()
            .readAllLife()
            .map { fulls ->
                if (wordTypes.isNotEmpty()) {
                    fulls.filter { wordFull ->
                        wordFull.wordTypes.any { type ->
                            wordTypes.any { type.typeId == it.id }
                        }
                    }
                } else {
                    fulls
                }
            }
            .map { fulls ->
                if (wordCategories.isNotEmpty()) {
                    fulls.filter { wordFull ->
                        wordFull.wordCategories.any { category ->
                            wordCategories.any { category.categoryId == it.id }
                        }
                    }
                } else {
                    fulls
                }
            }
            .map { entities ->
                val wordDao = appDatabase.wordDao()
                entities.map { entity ->
                    val typeCount = wordDao.getWordTypesCount(entity.word.word)
                    val categoriesCount = wordDao.getWordCategoriesCount(entity.word.word)
                    entity.toDomain(moshi, typeCount, categoriesCount)
                }
            }
            .catch { networkErrorParser.invoke(it) }

    override fun getAllByType(wordType: WordType): Flow<List<Word>> =
        appDatabase.wordDao()
            .readAllLife()
            .map { fulls ->
                fulls.filter { wordFull ->
                    wordFull.wordTypes.any { it.typeId == wordType.id }
                }
            }
            .map { entities ->
                val wordDao = appDatabase.wordDao()
                entities.map { entity ->
                    val typeCount = wordDao.getWordTypesCount(entity.word.word)
                    val categoriesCount = wordDao.getWordCategoriesCount(entity.word.word)
                    entity.toDomain(moshi, typeCount, categoriesCount)
                }
            }
            .catch { networkErrorParser.invoke(it) }

    override fun getAllByCategory(category: WordCategory): Flow<List<Word>> =
        appDatabase.wordDao()
            .readAllLife()
            .map { fulls ->
                fulls.filter { wordFull ->
                    wordFull.wordCategories.any { it.categoryId == category.id }
                }
            }
            .map { entities ->
                val wordDao = appDatabase.wordDao()
                entities.map { entity ->
                    val typeCount = wordDao.getWordTypesCount(entity.word.word)
                    val categoriesCount = wordDao.getWordCategoriesCount(entity.word.word)
                    entity.toDomain(moshi, typeCount, categoriesCount)
                }
            }
            .catch { networkErrorParser.invoke(it) }

    override fun create(draftWord: DraftWord): Flow<Unit> = flow {
        emit(
            appDatabase.wordDao().insert(
                draftWord.toEntity(),
                wordTypesIds = draftWord.wordTypes.map { it.id },
                wordCategoriesIds = draftWord.categories.map { it.id }
            )
        )
    }.catch { networkErrorParser.invoke(it) }

    override fun createAll(drafts: List<DraftWord>): Flow<Unit> = flow {
        drafts.forEach { draft ->
            appDatabase.wordDao().insert(
                draft.toEntity(),
                wordTypesIds = draft.wordTypes.map { it.id },
                wordCategoriesIds = draft.categories.map { it.id }
            )
        }
        emit(Unit)
    }.catch { networkErrorParser.invoke(it) }

    override fun change(draftWord: DraftWord): Flow<Unit> = flow {
        emit(
            appDatabase.runInTransaction {
                val dao = appDatabase.wordDao()
                val oldWordDate = dao.getById(draftWord.word)?.createdDate

                dao.deleteWord(draftWord.word)
                dao.insert(
                    draftWord.toEntity(oldWordDate),
                    wordTypesIds = draftWord.wordTypes.map { it.id },
                    wordCategoriesIds = draftWord.categories.map { it.id }
                )
            }
        )
    }.catch { networkErrorParser.invoke(it) }

    override fun softDeleteWord(word: Word) = flow {
        val wordChildCount = appDatabase.wordDao().wordChildCount(word.word) ?: 0
        if (wordChildCount > 0) {
            throw ExistChildWordException()
        }

        emit(appDatabase.wordDao().deleteWord(word.word))
    }.catch { networkErrorParser.invoke(it) }

    override fun hardDeleteWordWithChildren(word: Word) = flow {
        appDatabase.runInTransaction {
            appDatabase.wordDao().readAllByParentWord(word.word).forEach { child ->
                appDatabase.wordDao().deleteWord(child.word.word)
            }

            appDatabase.wordDao().deleteWord(word.word)
        }
        emit(Unit)
    }.catch { networkErrorParser.invoke(it) }

    override fun hardDeleteWordWithoutChildren(word: Word) = flow {
        val wordDao = appDatabase.wordDao()
        appDatabase.runInTransaction {
            wordDao.readAllByParentWord(word.word).forEach { child ->
                wordDao.changeWord(child.word.apply { parentWordId = null })
            }

            wordDao.deleteWord(word.word)
        }
        emit(Unit)
    }.catch { networkErrorParser.invoke(it) }

    override fun addCategoryToWord(word: Word, category: WordCategory) = flow {
        emit(appDatabase.wordDao().addCategoryToWord(word.word, category.id))
    }.catch { networkErrorParser.invoke(it) }

    override fun addTypeToWord(word: Word, type: WordType) = flow {
        emit(appDatabase.wordDao().addTypeToWord(word.word, type.id))
    }.catch { networkErrorParser.invoke(it) }

    override fun deleteCategoryFromWord(word: Word, category: WordCategory) = flow {
        emit(appDatabase.wordDao().deleteWordCategoryCrossRef(word.word, category.id))
    }.catch { networkErrorParser.invoke(it) }

    override fun deleteTypeFromWord(word: Word, type: WordType) = flow {
        emit(appDatabase.wordDao().deleteWordTypeCrossRef(word.word, type.id))
    }.catch { networkErrorParser.invoke(it) }
}