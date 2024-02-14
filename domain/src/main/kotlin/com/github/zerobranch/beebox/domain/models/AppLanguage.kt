package com.github.zerobranch.beebox.domain.models

import java.io.Serializable
import java.util.Locale

enum class AppLanguage(
    val lang: String,
    val locale: Locale
) : Serializable {
    ENGLISH("en", Locale.ENGLISH),
    RUSSIAN("ru", Locale("ru", "RU"));

    companion object {
        fun getByLang(lang: String?): AppLanguage? = values().firstOrNull { it.lang == lang }
    }
}
