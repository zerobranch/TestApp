package com.github.zerobranch.beebox.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val isFirstLaunch: Boolean = true,
    val deviceId: String? = null,
    val themeType: ThemeType = ThemeType.FOLLOW_SYSTEM
)