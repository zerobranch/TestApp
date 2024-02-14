package com.github.zerobranch.beebox.domain.repository

import com.github.zerobranch.beebox.domain.models.WordCategory
import kotlinx.coroutines.flow.Flow

interface WordCategoryRepository {
    fun create(name: String, description: String): Flow<Unit>
    fun getAll(): Flow<List<WordCategory>>
    fun getAllCrossRefs(): Flow<Map<String, Long>>
    fun delete(wordCategory: WordCategory): Flow<Unit>
    fun change(id: Long, name: String, description: String): Flow<Unit>
    fun containsWordsIsCategory(categoryId: Long): Flow<Boolean>
    fun getAllWithRelations(): Flow<Map<Long, Int>>
}