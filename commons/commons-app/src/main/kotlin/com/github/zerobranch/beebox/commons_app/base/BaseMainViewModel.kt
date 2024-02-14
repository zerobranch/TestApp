package com.github.zerobranch.beebox.commons_app.base

import com.github.zerobranch.beebox.commons_android.base.BaseViewModel
import com.github.zerobranch.beebox.logging.crash.CrashLogger

abstract class BaseMainViewModel : BaseViewModel() {
    protected fun logClick(msg: String): Unit = CrashLogger.click(screenName, msg)

    protected fun logEvent(msg: String): Unit = CrashLogger.event(screenName, msg)
}