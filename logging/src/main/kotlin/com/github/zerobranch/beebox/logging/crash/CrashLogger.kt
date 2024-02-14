package com.github.zerobranch.beebox.logging.crash

import timber.log.Timber

object CrashLogger {
    const val TAG = "CrashLogger"

    fun onAppLaunch() {
        log("launch_app")
    }

    fun onAppCrash() {
        log("crash")
    }

    fun onNetworkStateChanged(networkState: String, transportType: String, isWpnUp: Boolean?) {
        val vpnState = isWpnUp?.run {
            if (isWpnUp) "VPN" else "NOT_VPN"
        } ?: "UNKNOWN"
        log("network: $networkState - $transportType ($vpnState)")
    }

    fun onScreenOpen(screenName: String, rootPage: String) {
        log("open $screenName ($rootPage)")
    }

    fun onDialogClose(screenName: String, rootPage: String) {
        log("close $screenName ($rootPage)")
    }

    fun onForegroundApp() {
        log("move_to_foreground")
    }

    fun onBackgroundApp() {
        log("move_to_background")
    }

    fun click(screenName: String, msg: String) {
        log("[$screenName] click $msg")
    }

    fun event(screenName: String, msg: String) {
        log("[$screenName] event $msg")
    }

    private fun log(msg: String) {
        Timber.tag(TAG).i(msg)
    }
}