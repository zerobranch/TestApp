package com.github.zerobranch.beebox.domain.repository

import com.github.zerobranch.beebox.domain.models.ThemeType
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getThemeType(): Flow<ThemeType>
    fun saveThemeType(themeType: ThemeType): Flow<Unit>
    fun isFirstLaunch(): Flow<Boolean>
    fun setNotFirstLaunch(): Flow<Unit>
}