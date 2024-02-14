package com.github.zerobranch.beebox.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.zerobranch.beebox.BuildConfig
import com.github.zerobranch.beebox.data.repository.BackupRepositoryImpl
import com.github.zerobranch.beebox.data.repository.DeviceInfoRepositoryImpl
import com.github.zerobranch.beebox.data.repository.FileLoggingRepositoryImpl
import com.github.zerobranch.beebox.data.repository.SearchRepositoryImpl
import com.github.zerobranch.beebox.data.repository.SettingsRepositoryImpl
import com.github.zerobranch.beebox.data.repository.TrainingRepositoryImpl
import com.github.zerobranch.beebox.data.repository.WordCategoryRepositoryImpl
import com.github.zerobranch.beebox.data.repository.WordRepositoryImpl
import com.github.zerobranch.beebox.data.repository.WordTypeRepositoryImpl
import com.github.zerobranch.beebox.data.source.db.AppDatabase
import com.github.zerobranch.beebox.data.source.db.MemoryDatabase
import com.github.zerobranch.beebox.data.source.local.AppPrefsDatastore
import com.github.zerobranch.beebox.data.source.local.serializers.AppSettingsSerializer
import com.github.zerobranch.beebox.domain.models.AppSettings
import com.github.zerobranch.beebox.domain.repository.BackupRepository
import com.github.zerobranch.beebox.domain.repository.DeviceInfoRepository
import com.github.zerobranch.beebox.domain.repository.FileLoggingRepository
import com.github.zerobranch.beebox.domain.repository.SearchRepository
import com.github.zerobranch.beebox.domain.repository.SettingsRepository
import com.github.zerobranch.beebox.domain.repository.TrainingRepository
import com.github.zerobranch.beebox.domain.repository.WordCategoryRepository
import com.github.zerobranch.beebox.domain.repository.WordRepository
import com.github.zerobranch.beebox.domain.repository.WordTypeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "prefs_datastore"
    )

    private val Context.settingsDataStore: DataStore<AppSettings> by dataStore(
        fileName = "settings_datastore",
        serializer = AppSettingsSerializer
    )

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<AppSettings> = context.settingsDataStore

    @Provides
    @Singleton
    fun providePrefsDataStore(@ApplicationContext context: Context) =
        AppPrefsDatastore(context.dataStore)

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, BuildConfig.DATA_BASE_NAME)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideMemoryDatabase(@ApplicationContext context: Context): MemoryDatabase =
        Room.inMemoryDatabaseBuilder(context, MemoryDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class BindsModule {
        @Binds
        abstract fun bindSearchRepository(arg: SearchRepositoryImpl): SearchRepository

        @Binds
        abstract fun bindFileLoggingRepository(arg: FileLoggingRepositoryImpl): FileLoggingRepository

        @Binds
        abstract fun bindSettingsRepository(arg: SettingsRepositoryImpl): SettingsRepository

        @Binds
        abstract fun bindDeviceInfoRepository(arg: DeviceInfoRepositoryImpl): DeviceInfoRepository

        @Binds
        abstract fun bindWordRepository(arg: WordRepositoryImpl): WordRepository

        @Binds
        abstract fun bindWordCategoryRepository(arg: WordCategoryRepositoryImpl): WordCategoryRepository

        @Binds
        abstract fun bindWordTypeRepository(arg: WordTypeRepositoryImpl): WordTypeRepository

        @Binds
        abstract fun bindTrainingRepository(arg: TrainingRepositoryImpl): TrainingRepository

        @Binds
        abstract fun bindBackupRepository(arg: BackupRepositoryImpl): BackupRepository
    }
}