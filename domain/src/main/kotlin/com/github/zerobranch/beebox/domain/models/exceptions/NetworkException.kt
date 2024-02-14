package com.github.zerobranch.beebox.domain.models.exceptions

import java.io.IOException

open class NetworkException(
    val originalThrowable: Throwable? = null
) : IOException() {

    override val message: String?
        get() = "NetworkError [${originalThrowable?.message}]"

    override val cause: Throwable?
        get() = originalThrowable?.cause

    override fun setStackTrace(stackTrace: Array<out StackTraceElement>) {
        super.setStackTrace(originalThrowable?.stackTrace ?: stackTrace)
    }
}