package com.github.zerobranch.beebox.data.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.zerobranch.beebox.data.entity.WordCategoryCrossRefEntity
import com.github.zerobranch.beebox.data.entity.WordEntity
import com.github.zerobranch.beebox.data.entity.WordFull
import com.github.zerobranch.beebox.data.entity.WordTypeCrossRefEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

@Dao
abstract class WordDao {

    @Transaction
    open fun insert(
        word: WordEntity,
        wordCategoriesIds: List<Long>,
        wordTypesIds: List<Long>,
    ) {
        insertWord(word)

        insertWordCategoryCrossRef(
            wordCategoriesIds
                .map { wordCategoryId ->
                    WordCategoryCrossRefEntity(
                        word = word.word,
                        categoryId = wordCategoryId
                    )
                }
        )

        insertWordTypeCrossRef(
            wordTypesIds
                .map { wordTypeId ->
                    WordTypeCrossRefEntity(
                        word = word.word,
                        typeId = wordTypeId
                    )
                }
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWord(entity: WordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWordCategoryCrossRef(entities: List<WordCategoryCrossRefEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWordTypeCrossRef(entities: List<WordTypeCrossRefEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWordCategoryCrossRef(entity: WordCategoryCrossRefEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWordTypeCrossRef(entity: WordTypeCrossRefEntity)

    open fun addCategoryToWord(word: String, categoryId: Long) {
        insertWordCategoryCrossRef(
            WordCategoryCrossRefEntity(
                word = word,
                categoryId = categoryId
            )
        )
    }

    open fun addTypeToWord(word: String, typeId: Long) {
        insertWordTypeCrossRef(
            WordTypeCrossRefEntity(
                word = word,
                typeId = typeId
            )
        )
    }

    @Transaction
    @Query("SELECT * FROM words ORDER BY word ASC")
    abstract fun readAllLifeInternal(): Flow<List<WordFull>>

    @Transaction
    @Query("SELECT * FROM words ORDER BY word ASC")
    abstract fun readAllInternal(): List<WordFull>

    @Transaction
    @Query("SELECT * FROM words WHERE words.word Like :word ORDER BY word ASC")
    abstract fun searchInternal(word: String): List<WordFull>

    @Transaction
    @Query("SELECT * FROM words WHERE word = :word")
    abstract fun readByIdInternal(word: String): WordFull?

    @Transaction
    @Query("SELECT * FROM words WHERE parentWordId = :parentWordId")
    abstract fun readAllByParentWord(parentWordId: String): List<WordFull>

    @Transaction
    @Query("SELECT COUNT(*) FROM words WHERE parentWordId = :parentWordId LIMIT 1")
    abstract fun wordChildCount(parentWordId: String): Int?

    @Query("SELECT COUNT(*) FROM word_types_cross_ref WHERE typeId = :typeId LIMIT 1")
    abstract fun getWordCountByType(typeId: Long): Int

    @Query("SELECT COUNT(*) FROM word_categories_cross_ref WHERE categoryId = :categoryId LIMIT 1")
    abstract fun getWordCountByCategory(categoryId: Long): Int

    @Query("SELECT COUNT(*) FROM word_categories_cross_ref WHERE word = :word LIMIT 1")
    abstract fun getWordCategoriesCount(word: String): Int

    @Query("SELECT COUNT(*) FROM word_types_cross_ref WHERE word = :word LIMIT 1")
    abstract fun getWordTypesCount(word: String): Int

    @Query("SELECT COUNT(*) FROM word_types LIMIT 1")
    abstract fun getWordTypesCount(): Int

    @Query("SELECT COUNT(*) FROM word_categories LIMIT 1")
    abstract fun getWordCategoriesCount(): Int

    @Query("SELECT COUNT(*) FROM words LIMIT 1")
    abstract fun getWordCount(): Int

    @Query("SELECT * FROM words WHERE word = :word")
    abstract fun getById(word: String): WordEntity?

    open fun readAllLife(): Flow<List<WordFull>> =
        readAllLifeInternal()
            .onEach { wordsFull ->
                wordsFull.onEach { wordFull ->
                    wordFull.word.parentWord = wordFull.word.parentWordId?.let { readByIdInternal(it) }
                }
            }

    open fun readById(wordId: String): WordFull? =
        readByIdInternal(wordId)?.apply {
            word.parentWord = word.parentWordId?.let { readByIdInternal(it) }
        }

    open fun readAll(): List<WordFull> =
        readAllInternal()
            .onEach { wordFull ->
                wordFull.word.parentWord = wordFull.word.parentWordId?.let { readByIdInternal(it) }
            }

    open fun search(word: String): List<WordFull> =
        searchInternal("$word%")
            .onEach { wordFull ->
                wordFull.word.parentWord = wordFull.word.parentWordId?.let { readByIdInternal(it) }
            }

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract fun changeWord(word: WordEntity)

    @Query("DELETE FROM words WHERE word = :word")
    abstract fun deleteWordInternal(word: String)

    @Transaction
    open fun deleteWord(word: String) {
        deleteWordInternal(word)
        deleteWordTypesCrossRef(word)
        deleteWordCategoriesCrossRef(word)
    }

    @Query("DELETE FROM word_categories_cross_ref WHERE word = :word")
    abstract fun deleteWordCategoriesCrossRef(word: String)

    @Query("DELETE FROM word_types_cross_ref WHERE word = :word")
    abstract fun deleteWordTypesCrossRef(word: String)

    @Query("DELETE FROM word_categories_cross_ref WHERE word = :word AND categoryId = :categoryId")
    abstract fun deleteWordCategoryCrossRef(word: String, categoryId: Long)

    @Query("DELETE FROM word_types_cross_ref WHERE word = :word AND typeId = :typeId")
    abstract fun deleteWordTypeCrossRef(word: String, typeId: Long)
}