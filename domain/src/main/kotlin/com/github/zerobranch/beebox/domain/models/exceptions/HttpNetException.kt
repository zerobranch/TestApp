package com.github.zerobranch.beebox.domain.models.exceptions

class HttpNetException(
    val code: Int,
    val statusMessage: String,
    originalThrowable: Throwable? = null
) : NetworkException(originalThrowable) {
    override val message: String?
        get() = "HttpException code: $code, message: $statusMessage [${originalThrowable?.message}]"
}