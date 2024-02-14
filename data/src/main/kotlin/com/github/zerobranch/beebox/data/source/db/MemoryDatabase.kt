package com.github.zerobranch.beebox.data.source.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.zerobranch.beebox.data.entity.WordCategoryCrossRefEntity
import com.github.zerobranch.beebox.data.entity.WordCategoryEntity
import com.github.zerobranch.beebox.data.entity.WordEntity
import com.github.zerobranch.beebox.data.entity.WordTypeCrossRefEntity
import com.github.zerobranch.beebox.data.entity.WordTypeEntity
import com.github.zerobranch.beebox.data.entity.training.ProgressTrainingEntity
import com.github.zerobranch.beebox.data.entity.training.ProgressTrainingWordEntity

@Database(
    entities = [
        WordCategoryEntity::class,
        WordCategoryCrossRefEntity::class,
        WordTypeCrossRefEntity::class,
        WordEntity::class,
        WordTypeEntity::class,
        ProgressTrainingEntity::class,
        ProgressTrainingWordEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    ZonedDateTimeConverter::class,
    HintConverter::class,
    TranscriptionConverter::class,
    TrainingTypeConverter::class,
)
abstract class MemoryDatabase : RoomDatabase()