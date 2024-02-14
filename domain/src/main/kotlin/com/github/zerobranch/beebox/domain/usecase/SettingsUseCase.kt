package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.models.DeviceConfig
import com.github.zerobranch.beebox.domain.models.ThemeType
import com.github.zerobranch.beebox.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsUseCase @Inject constructor(
    private val repository: SettingsRepository,
    private val deviceConfig: DeviceConfig,
    private val beeBoxConfig: BeeboxConfig
) {
    val isFirstLaunch: Flow<Boolean> = repository.isFirstLaunch()

    init {
        repository.setNotFirstLaunch()
    }

    fun getThemeType(): Flow<ThemeType> = repository.getThemeType()
    fun saveThemeType(themeType: ThemeType): Flow<Unit> = repository.saveThemeType(themeType)

    fun getDeviceConfig(): DeviceConfig = deviceConfig

    fun getBeeBoxConfig(): BeeboxConfig = beeBoxConfig
}