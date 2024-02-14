package com.github.zerobranch.beebox.data.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.zerobranch.beebox.data.entity.WordCategoryCrossRefEntity
import com.github.zerobranch.beebox.data.entity.WordCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WordCategoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<WordCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entity: WordCategoryEntity)

    @Transaction
    @Query("SELECT * FROM word_categories ORDER BY createdDate DESC")
    abstract fun readAllLife(): Flow<List<WordCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM word_categories ORDER BY createdDate DESC")
    abstract fun readAll(): List<WordCategoryEntity>

    @Query("SELECT * FROM word_categories_cross_ref")
    abstract fun readAllCrossRefsLife(): Flow<List<WordCategoryCrossRefEntity>>

    @Query("SELECT * FROM word_categories_cross_ref WHERE categoryId = :categoryId")
    abstract fun getWordsIdsByCategory(categoryId: Long): List<WordCategoryCrossRefEntity>

    @Query("UPDATE word_categories SET name = :name, description = :description WHERE categoryId = :id")
    abstract fun update(id: Long, name: String, description: String)

    @Query("DELETE FROM word_categories")
    abstract fun deleteAll()

    @Query("DELETE FROM word_categories WHERE categoryId = :id")
    abstract fun delete(id: Long)

    @Query("DELETE FROM word_categories_cross_ref WHERE categoryId = :categoryId")
    abstract fun deleteWordsFromCategory(categoryId: Long)
}