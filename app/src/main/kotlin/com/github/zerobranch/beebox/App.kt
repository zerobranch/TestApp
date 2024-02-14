package com.github.zerobranch.beebox

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import com.github.zerobranch.beebox.auth.AuthActivity
import com.github.zerobranch.beebox.commons_android.utils.delegates.LanguageDelegate
import com.github.zerobranch.beebox.commons_android.utils.delegates.ThemeDelegate
import com.github.zerobranch.beebox.commons_android.utils.ext.isOtherProcess
import com.github.zerobranch.beebox.commons_android.utils.ext.trustAllCertificateFactory
import com.github.zerobranch.beebox.commons_app.utils.parseThemeType
import com.github.zerobranch.beebox.commons_app.utils.value
import com.github.zerobranch.beebox.domain.models.AppLanguage
import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.usecase.FileLoggingUseCase
import com.github.zerobranch.beebox.domain.usecase.SettingsUseCase
import com.github.zerobranch.beebox.logging.AndroidRemoteDebuggerTree
import com.github.zerobranch.beebox.logging.CrashlyticsTree
import com.github.zerobranch.beebox.logging.FileLoggingTree
import com.github.zerobranch.beebox.logging.crash.CrashEventLogger
import com.github.zerobranch.beebox.logging.crash.CrashLogger
import com.github.zerobranch.beebox.logging.wtf
import com.github.zerobranch.beebox.services.NetworkConnectionServices
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import zerobranch.androidremotedebugger.AndroidRemoteDebugger
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    companion object {
        const val APP_CRASH_TAG = "AppFatalCrash"
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var settingsUseCase: SettingsUseCase

    @Inject
    lateinit var fileLoggingTree: FileLoggingTree

    @Inject
    lateinit var fileLoggingUseCase: FileLoggingUseCase

    @Inject
    lateinit var networkConnectionServices: NetworkConnectionServices

    @Inject
    lateinit var beeBoxConfig: BeeboxConfig

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (applicationContext.isOtherProcess()) return

        AndroidThreeTen.init(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        setupAppTheme()
        initLogging()
        initCrashLogging()
        initLogExceptionHandler()

        // todo убрать
        ThemeDelegate.installTheme(ThemeDelegate.LIGHT)
        LanguageDelegate.installAppLanguage(this, AppLanguage.RUSSIAN.locale)

        trustAllCertificate()
    }

    private fun initLogging() {
        fileLoggingUseCase.prepare()

        if (BuildConfig.DEBUG) {
//            WebView.setWebContentsDebuggingEnabled(true)

            AndroidRemoteDebugger.init(
                AndroidRemoteDebugger.Builder(applicationContext)
                    .disableNotifications()
                    .build()
            )
            Timber.plant(
                Timber.DebugTree(),
                AndroidRemoteDebuggerTree(),
                fileLoggingTree,
                CrashlyticsTree()
            )
        } else {
            Timber.plant(CrashlyticsTree(), fileLoggingTree)
        }
    }

    private fun setupAppTheme() {
        runBlocking {
            val systemTheme = parseThemeType(ThemeDelegate.currentTheme(resources))
            val savedTheme = settingsUseCase.getThemeType().first()
            val theme = systemTheme.takeIf { savedTheme.isFollowSystem() } ?: savedTheme
            ThemeDelegate.installTheme(theme.value)
        }
    }

    private fun initLogExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(LogExceptionHandler())
    }

    private fun initCrashLogging() {
        CrashLogger.onAppLaunch()
        networkConnectionServices.launch()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())

        runBlocking {
            CrashEventLogger.setFirstLaunch(settingsUseCase.isFirstLaunch.first())
            //        CrashEventLogger.setFlavor(beeBoxConfig.flavor)
            CrashEventLogger.setScreenReaderEnabledState(beeBoxConfig.isScreenReaderEnabled)
            CrashEventLogger.setInstallerPackageName(beeBoxConfig.installerPackageName)
            CrashEventLogger.setWebViewVersion(beeBoxConfig.webViewVersion)
        }
    }

    private fun trustAllCertificate() {
        HttpsURLConnection.setDefaultSSLSocketFactory(trustAllCertificateFactory.first)
    }

    private class LogExceptionHandler : Thread.UncaughtExceptionHandler {
        private val originalHandler = Thread.getDefaultUncaughtExceptionHandler()

        override fun uncaughtException(t: Thread, th: Throwable) {
            javaClass.wtf(APP_CRASH_TAG, th)
            CrashLogger.onAppCrash()
            originalHandler?.uncaughtException(t, th)
        }
    }

    class AppLifecycleListener : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            CrashLogger.onForegroundApp()
        }

        override fun onStop(owner: LifecycleOwner) {
            CrashLogger.onBackgroundApp()
        }
    }
}