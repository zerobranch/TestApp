package com.github.zerobranch.beebox.commons_android.utils.delegates

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import java.util.Locale

object LanguageDelegate {
    fun installAppLanguage(context: Context, locale: Locale) {
        updateResources(context, locale)

        val appContext = context.applicationContext
        if (appContext !== context) {
            updateResources(appContext, locale)
        }
    }

    private fun updateResources(context: Context, locale: Locale) {
        Locale.setDefault(locale)

        val res = context.resources
        val currentLocale = res.configuration.getConfigLocale()

        if (currentLocale == locale) return

        val config = Configuration(res.configuration)

        @Suppress("DEPRECATION")
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> setLocale(config, locale)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> config.setLocale(locale)
            else -> config.locale = locale
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(locale)
        }

        @Suppress("DEPRECATION")
        res.updateConfiguration(config, res.displayMetrics)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLocale(config: Configuration, locale: Locale) {
        val set = linkedSetOf(locale)
        val defaultLocales = LocaleList.getDefault()
        val allLocales = List<Locale>(defaultLocales.size()) { defaultLocales[it] }

        set.addAll(allLocales)

        config.setLocales(LocaleList(*set.toTypedArray()))
    }

    private fun Configuration.getConfigLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) locales.get(0) else locale
    }
}
