package com.github.zerobranch.beebox.domain.repository

import com.github.zerobranch.beebox.domain.models.WordType
import kotlinx.coroutines.flow.Flow

interface WordTypeRepository {
    fun create(name: String, description: String): Flow<Unit>
    fun getAll(): Flow<List<WordType>>
    fun getAllCrossRefs(): Flow<Map<String, Long>>
    fun delete(wordType: WordType): Flow<Unit>
    fun containsWordsIsType(wordTypeId: Long): Flow<Boolean>
    fun change(id: Long, name: String, description: String): Flow<Unit>
    fun getAllWithRelations(): Flow<Map<Long, Int>>
}