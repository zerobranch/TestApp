package com.github.zerobranch.beebox.data.entity.training

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.zerobranch.beebox.domain.models.TrainingType

@Entity(tableName = "progress_training_words")
class ProgressTrainingWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val progressTrainingId: Long,
    val word: String,
    val trainingType: TrainingType,
    val order: Int
)