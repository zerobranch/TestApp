package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.repository.WordCategoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordCategoryUseCase @Inject constructor(
    private val repository: WordCategoryRepository
) {
    fun change(id: Long, name: String, description: String): Flow<Unit> =
        repository.change(id, name, description)

    fun create(name: String, description: String): Flow<Unit> = repository.create(name, description)

    fun getAll(): Flow<List<WordCategory>> = repository.getAll()

    @ExperimentalCoroutinesApi
    fun getAllWithWordsCount(): Flow<List<WordCategory>> =
        repository.getAllCrossRefs()
            .flatMapMerge { repository.getAll() }
            .flatMapConcat { wordTypes ->
                repository.getAllWithRelations()
                    .map { wordsCount ->
                        wordTypes.map { wordType ->
                            wordType.copy(wordsCount = wordsCount[wordType.id] ?: 0)
                        }
                    }
            }

    fun delete(wordCategory: WordCategory): Flow<Unit> = repository.delete(wordCategory)

    fun containsWordsIsCategory(categoryId: Long): Flow<Boolean> =
        repository.containsWordsIsCategory(categoryId)
}