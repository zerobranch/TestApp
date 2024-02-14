package com.github.zerobranch.beebox.domain.models.training

import com.github.zerobranch.beebox.domain.models.TrainingType
import com.github.zerobranch.beebox.domain.models.words.Word
import java.io.Serializable

data class ProgressTrainingWord(
    val id: Long = 0,
    val trainingType: TrainingType,
    val word: Word,
    val order: Int
) : Serializable