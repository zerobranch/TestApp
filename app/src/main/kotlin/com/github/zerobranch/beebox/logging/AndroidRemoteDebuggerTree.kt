package com.github.zerobranch.beebox.logging

import com.github.zerobranch.beebox.App
import timber.log.Timber
import zerobranch.androidremotedebugger.AndroidRemoteDebugger

class AndroidRemoteDebuggerTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean = tag != App.APP_CRASH_TAG

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        AndroidRemoteDebugger.Log.log(priority, tag, message, t)
    }
}