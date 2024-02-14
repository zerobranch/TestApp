package com.github.zerobranch.beebox.data.repository

import com.github.zerobranch.beebox.data.base.NetworkErrorParser
import com.github.zerobranch.beebox.data.entity.WordCategoryEntity
import com.github.zerobranch.beebox.data.mappers.toDomain
import com.github.zerobranch.beebox.data.source.db.AppDatabase
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.repository.WordCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

class WordCategoryRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val networkErrorParser: NetworkErrorParser
) : WordCategoryRepository {

    override fun create(name: String, description: String): Flow<Unit> =
        flow {
            emit(
                appDatabase.wordCategoriesDao().insert(
                    WordCategoryEntity(
                        categoryId = System.currentTimeMillis(),
                        name = name,
                        description = description,
                        createdDate = ZonedDateTime.now()
                    )
                )
            )
        }.catch { networkErrorParser.invoke(it) }

    override fun getAll(): Flow<List<WordCategory>> =
        appDatabase.wordCategoriesDao().readAllLife()
            .map { entities -> entities.map { it.toDomain() } }
            .catch { networkErrorParser.invoke(it) }

    override fun getAllCrossRefs(): Flow<Map<String, Long>> =
        appDatabase.wordCategoriesDao().readAllCrossRefsLife()
            .map { entities -> entities.associate { it.word to it.categoryId } }
            .catch { networkErrorParser.invoke(it) }

    override fun getAllWithRelations(): Flow<Map<Long, Int>> = flow {
        val allCategories = appDatabase.wordCategoriesDao().readAll()
        val wordDao = appDatabase.wordDao()
        emit(
            allCategories.associate {
                it.categoryId to wordDao.getWordCountByCategory(it.categoryId)
            }
        )
    }.catch { networkErrorParser.invoke(it) }

    override fun containsWordsIsCategory(categoryId: Long): Flow<Boolean> {
        return flow {
            emit(appDatabase.wordCategoriesDao().getWordsIdsByCategory(categoryId).isNotEmpty())
        }.catch { networkErrorParser.invoke(it) }
    }

    override fun delete(wordCategory: WordCategory): Flow<Unit> =
        flow {
            appDatabase.runInTransaction {
                appDatabase.wordCategoriesDao().deleteWordsFromCategory(wordCategory.id)
                appDatabase.wordCategoriesDao().delete(wordCategory.id)
            }
            emit(Unit)
        }.catch { networkErrorParser.invoke(it) }

    override fun change(id: Long, name: String, description: String): Flow<Unit> =
        flow { emit(appDatabase.wordCategoriesDao().update(id, name, description)) }
            .catch { networkErrorParser.invoke(it) }
}