package com.github.zerobranch.beebox.commons_java.ext

fun <T : Any> T?.notNull(lazyMessage: String): T {
    if (this == null) {
        throw IllegalStateException(lazyMessage)
    } else {
        return this
    }
}