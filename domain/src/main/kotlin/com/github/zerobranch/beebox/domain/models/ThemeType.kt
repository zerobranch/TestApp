package com.github.zerobranch.beebox.domain.models

import kotlinx.serialization.SerialName
import java.io.Serializable

enum class ThemeType(val type: String) : Serializable {
    @SerialName("light")
    LIGHT("light"),

    @SerialName("night")
    NIGHT("night"),

    @SerialName("follow_system")
    FOLLOW_SYSTEM("follow_system"),

    @SerialName("auto_battery")
    AUTO_BATTERY("auto_battery");

    fun isLight() = this == LIGHT

    fun isFollowSystem() = this == FOLLOW_SYSTEM

    fun isNotFollowSystem() = this != FOLLOW_SYSTEM

    fun isNight() = this == NIGHT

    fun inverse(): ThemeType = if (this == LIGHT) NIGHT else LIGHT

    companion object {
        fun parse(type: String): ThemeType? = values().firstOrNull { it.type == type }
    }
}