package com.github.zerobranch.beebox.data.entity.training

import androidx.room.Embedded
import androidx.room.Relation

class ProgressTrainingWithWords(
    @Embedded val training: ProgressTrainingEntity,
    @Relation(
        parentColumn = "trainingId",
        entityColumn = "progressTrainingId"
    )
    val trainingWords: List<ProgressTrainingWordEntity>
)