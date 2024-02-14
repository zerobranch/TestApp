package com.github.zerobranch.beebox.di

import android.content.Context
import android.os.Build
import com.github.zerobranch.beebox.BuildConfig
import com.github.zerobranch.beebox.R
import com.github.zerobranch.beebox.commons_android.utils.ext.installerPackageName
import com.github.zerobranch.beebox.commons_android.utils.ext.isScreenReaderEnabled
import com.github.zerobranch.beebox.commons_android.utils.ext.webViewVersion
import com.github.zerobranch.beebox.data.source.local.AppConstants
import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.models.DeviceConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.github.zerobranch.beebox.commons_app.R as CommonR

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Singleton
    fun provideDeviceConfig() = DeviceConfig(
        androidVersion = Build.VERSION.RELEASE,
        androidAPILevel = Build.VERSION.SDK_INT,
        displayedVersion = Build.DISPLAY,
        deviceBrand = Build.BRAND,
        deviceModel = Build.MODEL,
        device = Build.DEVICE,
        cPUABI = Build.SUPPORTED_ABIS.joinToString(),
        manufacturer = Build.MANUFACTURER
    )

    @Provides
    @Singleton
    fun provideBeeBoxConfig(
        @ApplicationContext context: Context,
    ) = BeeboxConfig(
        appName = context.getString(CommonR.string.app_name),
        packageName = context.packageName,
        baseUrl = BuildConfig.BASE_URL,
        applicationId = BuildConfig.APPLICATION_ID,
        versionCode = BuildConfig.VERSION_CODE,
        databaseName = BuildConfig.DATA_BASE_NAME,
        versionName = BuildConfig.VERSION_NAME,
        attachArchivePassword = "",
        beeboxLogFileName = BuildConfig.BEEBOX_LOG_FILE_NAME,
        buildType = BuildConfig.BUILD_TYPE,
        isScreenReaderEnabled = context.isScreenReaderEnabled(),
        installerPackageName = context.installerPackageName.toString(),
        webViewVersion = context.webViewVersion,
        telegramPackage = "org.telegram.messenger",
        databaseVersion = AppConstants.DATABASE_VERSION
    )
}