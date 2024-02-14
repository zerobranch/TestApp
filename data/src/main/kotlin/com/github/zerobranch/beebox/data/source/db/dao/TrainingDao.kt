package com.github.zerobranch.beebox.data.source.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.zerobranch.beebox.data.entity.training.ProgressTrainingEntity
import com.github.zerobranch.beebox.data.entity.training.ProgressTrainingWithWords
import com.github.zerobranch.beebox.data.entity.training.ProgressTrainingWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TrainingDao {
    @Transaction
    @Query("SELECT * FROM progress_training WHERE trainingId = :id")
    abstract fun getProgressTrainingById(id: Long): ProgressTrainingWithWords?

    @Transaction
    @Query("SELECT * FROM progress_training ORDER BY createdDate DESC")
    abstract fun getProgressTrainings(): Flow<List<ProgressTrainingWithWords>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProgressTraining(entity: ProgressTrainingEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateProgressTraining(entity: ProgressTrainingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertProgressTrainingWords(entity: List<ProgressTrainingWordEntity>)

    @Query("DELETE FROM progress_training WHERE trainingId = :id")
    abstract fun deleteProgressTrainingInternal(id: Long)

    @Query("DELETE FROM progress_training_words WHERE progressTrainingId = :id")
    abstract fun deleteProgressTrainingWordsInternal(id: Long)

    fun deleteProgressTraining(trainingId: Long) {
        deleteProgressTrainingWordsInternal(trainingId)
        deleteProgressTrainingInternal(trainingId)
    }
}