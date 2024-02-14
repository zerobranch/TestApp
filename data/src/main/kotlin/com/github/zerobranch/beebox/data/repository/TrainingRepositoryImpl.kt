package com.github.zerobranch.beebox.data.repository

import com.github.zerobranch.beebox.data.base.NetworkErrorParser
import com.github.zerobranch.beebox.data.entity.training.ProgressTrainingEntity
import com.github.zerobranch.beebox.data.mappers.toDomain
import com.github.zerobranch.beebox.data.mappers.toEntity
import com.github.zerobranch.beebox.data.source.db.AppDatabase
import com.github.zerobranch.beebox.domain.models.TrainingType
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.domain.models.training.ProgressTrainingWord
import com.github.zerobranch.beebox.domain.repository.TrainingRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.ZonedDateTime
import java.io.IOException
import javax.inject.Inject

class TrainingRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val moshi: Moshi,
    private val networkErrorParser: NetworkErrorParser
) : TrainingRepository {

    override fun getProgressTrainings(): Flow<List<ProgressTraining>> {
        val wordDao = appDatabase.wordDao()
        return appDatabase.trainingDao().getProgressTrainings()
            .map { trainingsWithWords ->
                trainingsWithWords.map { trainingWithWords ->
                    trainingWithWords.toDomain { wordId ->
                        val typeCount = wordDao.getWordTypesCount(wordId)
                        val categoriesCount = wordDao.getWordCategoriesCount(wordId)
                        wordDao.readById(wordId)?.toDomain(moshi, typeCount, categoriesCount)
                    }
                }
            }
            .catch { networkErrorParser.invoke(it) }
    }

    override fun deleteTraining(id: Long): Flow<Unit> =
        flow { emit(appDatabase.trainingDao().deleteProgressTraining(id)) }
            .catch { networkErrorParser.invoke(it) }

    override fun updateProgressTraining(progressTraining: ProgressTraining): Flow<Unit> =
        flow { emit(appDatabase.trainingDao().updateProgressTraining(progressTraining.toEntity())) }
            .catch { networkErrorParser.invoke(it) }

    override fun createAndGetTraining(
        mainTrainingType: TrainingType,
        trainingWords: List<ProgressTrainingWord>
    ): Flow<ProgressTraining> {
        val trainingDao = appDatabase.trainingDao()
        val wordDao = appDatabase.wordDao()

        return flow {
            val trainingId = trainingDao.insertProgressTraining(
                ProgressTrainingEntity(
                    mainTrainingType = mainTrainingType,
                    createdDate = ZonedDateTime.now(),
                    position = 0
                )
            )

            trainingDao.insertProgressTrainingWords(
                trainingWords.map { trainingWord ->
                    trainingWord.toEntity(trainingId)
                }
            )
            emit(trainingId)
        }
            .map { trainingId ->
                val trainingWithWords = trainingDao
                    .getProgressTrainingById(trainingId)
                    ?: throw IOException("trainingId not found")

                trainingWithWords.toDomain { wordId ->
                    val typeCount = wordDao.getWordTypesCount(wordId)
                    val categoriesCount = wordDao.getWordCategoriesCount(wordId)
                    wordDao.readById(wordId)?.toDomain(moshi, typeCount, categoriesCount)
                }
            }
            .catch { networkErrorParser.invoke(it) }
    }
}