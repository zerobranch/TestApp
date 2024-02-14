package com.github.zerobranch.beebox.commons_android.utils.delegates

import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate

object ThemeDelegate {
    const val LIGHT = 0
    const val NIGHT = 1
    const val FOLLOW_SYSTEM = 2
    const val AUTO_BATTERY = 3

    fun isLight(resources: Resources): Boolean = currentTheme(resources) == LIGHT

    fun currentTheme(resources: Resources): Int {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> NIGHT
            Configuration.UI_MODE_NIGHT_NO -> LIGHT
            else -> FOLLOW_SYSTEM
        }
    }

    fun installTheme(themeType: Int) {
        AppCompatDelegate.setDefaultNightMode(
            when (themeType) {
                LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                NIGHT -> AppCompatDelegate.MODE_NIGHT_YES
                FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                AUTO_BATTERY -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}