package com.github.zerobranch.beebox.data.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.zerobranch.beebox.data.entity.WordTypeCrossRefEntity
import com.github.zerobranch.beebox.data.entity.WordTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WordTypesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<WordTypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entity: WordTypeEntity)

    @Transaction
    @Query("SELECT * FROM word_types ORDER BY createdDate DESC")
    abstract fun readAllLife(): Flow<List<WordTypeEntity>>

    @Transaction
    @Query("SELECT * FROM word_types ORDER BY createdDate DESC")
    abstract fun readAll(): List<WordTypeEntity>

    @Query("SELECT * FROM word_types_cross_ref")
    abstract fun readAllCrossRefsLife(): Flow<List<WordTypeCrossRefEntity>>

    @Query("SELECT * FROM word_types_cross_ref WHERE typeId = :typeId")
    abstract fun getWordsIdsByType(typeId: Long): List<WordTypeCrossRefEntity>

    @Query("UPDATE word_types SET name = :name, description = :description WHERE typeId = :id")
    abstract fun update(id: Long, name: String, description: String)

    @Query("DELETE FROM word_types")
    abstract fun deleteAll()

    @Query("DELETE FROM word_types WHERE typeId = :id")
    abstract fun delete(id: Long)

    @Query("DELETE FROM word_types_cross_ref WHERE typeId = :typeId")
    abstract fun deleteWordsFromType(typeId: Long)
}