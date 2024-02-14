package com.github.zerobranch.beebox.data.mappers

import com.github.zerobranch.beebox.data.entity.training.ProgressTrainingEntity
import com.github.zerobranch.beebox.data.entity.training.ProgressTrainingWithWords
import com.github.zerobranch.beebox.data.entity.training.ProgressTrainingWordEntity
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.domain.models.training.ProgressTrainingWord
import com.github.zerobranch.beebox.domain.models.words.Word

fun ProgressTraining.toEntity() = ProgressTrainingEntity(
    trainingId = id,
    mainTrainingType = mainTrainingType,
    createdDate = createdDate,
    position = position,
)

fun ProgressTrainingWord.toEntity(progressTrainingId: Long) = ProgressTrainingWordEntity(
    progressTrainingId = progressTrainingId,
    word = word.word,
    trainingType = trainingType,
    order = order,
)

fun ProgressTrainingWithWords.toDomain(wordGetter: (String) -> Word?) = ProgressTraining(
    id = training.trainingId,
    mainTrainingType = training.mainTrainingType,
    trainingWords = trainingWords.mapNotNull { it.toDomain(wordGetter) },
    createdDate = training.createdDate,
    position = training.position,
)

fun ProgressTrainingWordEntity.toDomain(wordGetter: (String) -> Word?): ProgressTrainingWord? {
    val wordDomain = wordGetter.invoke(word) ?: return null

    return ProgressTrainingWord(
        id = id,
        trainingType = trainingType,
        word = wordDomain,
        order = order,
    )
}
