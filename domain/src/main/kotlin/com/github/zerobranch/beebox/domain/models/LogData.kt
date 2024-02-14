package com.github.zerobranch.beebox.domain.models

import java.io.Serializable

data class LogData(
    val priority: Int,
    val tag: String?,
    val message: String,
    val th: Throwable?
) : Serializable