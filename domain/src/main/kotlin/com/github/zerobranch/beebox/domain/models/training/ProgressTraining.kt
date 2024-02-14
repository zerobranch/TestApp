package com.github.zerobranch.beebox.domain.models.training

import com.github.zerobranch.beebox.domain.models.TrainingType
import org.threeten.bp.ZonedDateTime
import java.io.Serializable

data class ProgressTraining(
    val id: Long,
    val mainTrainingType: TrainingType,
    val trainingWords: List<ProgressTrainingWord>,
    val createdDate: ZonedDateTime,
    val position: Int = 0
) : Serializable