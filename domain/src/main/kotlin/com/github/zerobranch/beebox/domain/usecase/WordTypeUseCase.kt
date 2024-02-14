package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.repository.WordTypeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordTypeUseCase @Inject constructor(
    private val repository: WordTypeRepository
) {
    fun change(id: Long, name: String, description: String): Flow<Unit> =
        repository.change(id, name, description)

    fun create(name: String, description: String): Flow<Unit> = repository.create(name, description)

    fun getAll(): Flow<List<WordType>> = repository.getAll()

    @ExperimentalCoroutinesApi
    fun getAllWithWordsCount(): Flow<List<WordType>> =
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

    fun delete(wordType: WordType): Flow<Unit> = repository.delete(wordType)

    fun containsWordsIsType(wordTypeId: Long): Flow<Boolean> =
        repository.containsWordsIsType(wordTypeId)
}