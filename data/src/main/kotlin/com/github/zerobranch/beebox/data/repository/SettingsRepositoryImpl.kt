package com.github.zerobranch.beebox.data.repository

import androidx.datastore.core.DataStore
import com.github.zerobranch.beebox.data.source.local.AppPrefsDatastore
import com.github.zerobranch.beebox.domain.models.AppSettings
import com.github.zerobranch.beebox.domain.models.ThemeType
import com.github.zerobranch.beebox.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val datastore: AppPrefsDatastore,
    private val settingsDataStore: DataStore<AppSettings>
) : SettingsRepository {
    private companion object {
        val TEST_VALUE_KEY = AppPrefsDatastore.stringKey("test_value")
    }

    override fun getThemeType(): Flow<ThemeType> = settingsDataStore.data.map { it.themeType }

    override fun saveThemeType(themeType: ThemeType): Flow<Unit> = flow {
        settingsDataStore.updateData { it.copy(themeType = themeType) }
        emit(Unit)
    }

    override fun isFirstLaunch(): Flow<Boolean> = settingsDataStore.data.map { it.isFirstLaunch }

    override fun setNotFirstLaunch(): Flow<Unit> = flow {
        settingsDataStore.updateData { it.copy(isFirstLaunch = false) }
        emit(Unit)
    }

    fun getTestValue(): Flow<String> =
        datastore
            .get(TEST_VALUE_KEY)
            .map { it ?: "something" }

    fun setTestValue(value: String): Flow<Unit> =
        flow { emit(datastore.set(TEST_VALUE_KEY, value)) }
}