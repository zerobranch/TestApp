package com.github.zerobranch.beebox.logging.crash

import timber.log.Timber

object CrashEventLogger {
    const val TAG = "CrashLoggerEvent"

    private const val WEB_VIEW_VERSION = "web_view_version"
    private const val INSTALLER_PACKAGE_NAME = "installer_package_name"
    private const val SCREEN_READER = "screen_reader"
    private const val FLAVOR = "flavor"
    private const val IS_FIRST_LAUNCH = "is_first_launch"
    private const val REGION = "region"
    private const val IS_AUTHORIZED = "is_authorized"
    private const val DEVICE_ID = "device_id"
    private const val GOOGLE_PLAY_SERVICES = "google_play_services"

    fun setInstallerPackageName(packageName: String) {
        val source = if (packageName == "com.android.vending") "google_play" else packageName
        event(INSTALLER_PACKAGE_NAME, source)
    }

    fun setWebViewVersion(version: String) {
        event(WEB_VIEW_VERSION, version)
    }

    fun setScreenReaderEnabledState(enabled: Boolean) {
        event(SCREEN_READER, enabled)
    }

    fun setFlavor(flavor: String) {
        event(FLAVOR, flavor)
    }

    fun setAuthorize(isAuthorized: Boolean) {
        event(IS_AUTHORIZED, isAuthorized)
    }

    fun setDeviceId(deviceId: String) {
        event(DEVICE_ID, deviceId)
    }

    fun setFirstLaunch(isFirstLaunch: Boolean) {
        event(IS_FIRST_LAUNCH, isFirstLaunch)
    }

    fun setRegion(region: String?, geoId: Long) {
        event(REGION, "${region ?: "unknown"} ($geoId)")
    }

    fun setGoogleServicesAvailabilityStatus(status: String) {
        event(GOOGLE_PLAY_SERVICES, status)
    }

    private fun event(key: String, value: Any) {
        Timber.tag("${TAG}:$key").i(value.toString())
    }
}