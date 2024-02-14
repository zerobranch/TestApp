package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.domain.models.TrainingType
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.domain.models.training.ProgressTrainingWord
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrainingUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    fun getProgressTrainings(): Flow<List<ProgressTraining>> =
        trainingRepository.getProgressTrainings()

    fun createAndGetTraining(
        mainTrainingType: TrainingType,
        words: List<Word>
    ): Flow<ProgressTraining> {
        val trainingTypes = List(words.size) { index ->
            if (mainTrainingType == TrainingType.IN_ENGLISH_AND_RUSSIAN) {
                TrainingType.IN_ENGLISH.takeIf { index % 2 == 0 } ?: TrainingType.IN_RUSSIAN
            } else {
                mainTrainingType
            }
        }.shuffled()

        return trainingRepository.createAndGetTraining(
            mainTrainingType = mainTrainingType,
            trainingWords = words.shuffled().mapIndexed { index, word ->
                ProgressTrainingWord(
                    trainingType = trainingTypes[index],
                    word = word,
                    order = index
                )
            }
        )
    }

    fun updateTraining(progressTraining: ProgressTraining): Flow<Unit> =
        trainingRepository.updateProgressTraining(progressTraining)

    fun deleteTraining(id: Long): Flow<Unit> = trainingRepository.deleteTraining(id)
}