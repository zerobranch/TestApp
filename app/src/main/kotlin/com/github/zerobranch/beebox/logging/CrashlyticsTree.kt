package com.github.zerobranch.beebox.logging

import android.util.Log
import com.github.zerobranch.beebox.App
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.logging.crash.CrashEventLogger
import com.github.zerobranch.beebox.logging.crash.CrashLogger
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ASSERT && tag != App.APP_CRASH_TAG) {
            if (t != null) {
//                Firebase.crashlytics.recordException(Throwable("[$tag]", t))
            } else {
//                Firebase.crashlytics.recordException(Throwable("[$tag] $message"))
            }
        }

        if (tag == CrashLogger.TAG) {
//            Firebase.crashlytics.log(message)
        } else if (tag.isNotNull() && tag.contains(CrashEventLogger.TAG)) {
            val key = tag.replace("${CrashEventLogger.TAG}:", "")
//            Firebase.crashlytics.setCustomKey(key, message)
        }
    }
}