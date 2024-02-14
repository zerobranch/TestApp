package com.github.zerobranch.beebox.data.entity.training

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.zerobranch.beebox.domain.models.TrainingType
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "progress_training")
class ProgressTrainingEntity(
    @PrimaryKey(autoGenerate = true)
    val trainingId: Long = 0,
    val mainTrainingType: TrainingType,
    val createdDate: ZonedDateTime,
    val position: Int
)