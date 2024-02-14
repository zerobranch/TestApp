package com.github.zerobranch.beebox.domain.repository

import com.github.zerobranch.beebox.domain.models.TrainingType
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.domain.models.training.ProgressTrainingWord
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    fun deleteTraining(id: Long): Flow<Unit>
    fun updateProgressTraining(progressTraining: ProgressTraining): Flow<Unit>
    fun getProgressTrainings(): Flow<List<ProgressTraining>>
    fun createAndGetTraining(
        mainTrainingType: TrainingType,
        trainingWords: List<ProgressTrainingWord>
    ): Flow<ProgressTraining>
}