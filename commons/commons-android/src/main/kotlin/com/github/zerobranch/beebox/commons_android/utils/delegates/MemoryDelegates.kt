package com.github.zerobranch.beebox.commons_android.utils.delegates

import android.content.Context
import android.webkit.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.clearCache() {
    withContext(Dispatchers.IO) {
        cacheDir.deleteRecursively()
    }
}

fun clearCookies() {
    CookieManager.getInstance().apply {
        removeAllCookies(null)
        flush()
    }
}

fun Context.clearData() {
    runCatching {
        Runtime.getRuntime().exec("pm clear $packageName")
    }
}
