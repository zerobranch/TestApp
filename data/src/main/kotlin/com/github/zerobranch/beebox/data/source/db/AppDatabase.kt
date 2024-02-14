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
import com.github.zerobranch.beebox.data.source.db.dao.TrainingDao
import com.github.zerobranch.beebox.data.source.db.dao.WordCategoriesDao
import com.github.zerobranch.beebox.data.source.db.dao.WordDao
import com.github.zerobranch.beebox.data.source.db.dao.WordTypesDao
import com.github.zerobranch.beebox.data.source.local.AppConstants

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
    version = AppConstants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    ZonedDateTimeConverter::class,
    HintConverter::class,
    TranscriptionConverter::class,
    TrainingTypeConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun wordTypesDao(): WordTypesDao
    abstract fun wordCategoriesDao(): WordCategoriesDao
    abstract fun trainingDao(): TrainingDao
}