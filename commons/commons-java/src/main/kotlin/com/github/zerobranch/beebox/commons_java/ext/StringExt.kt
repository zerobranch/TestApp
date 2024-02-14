package com.github.zerobranch.beebox.commons_java.ext

import java.util.Locale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
val String.base64: String
    get() = Base64.encode(toByteArray(Charsets.UTF_8))

@OptIn(ExperimentalEncodingApi::class)
val String.fromBase64: String
    get() = String(
        Base64.decode(toByteArray(Charsets.UTF_8)),
        Charsets.UTF_8
    )

fun String.capitalized(): String {
    return replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

fun String.shortenLong(maxLength: Int): String {
    if (length <= maxLength) return this
    val maxLengthWithDots = maxLength - 3

    return StringBuilder()
        .append(substring(0, maxLengthWithDots / 2))
        .append("...")
        .append(substring(length - maxLengthWithDots / 2, length))
        .toString()
}

inline fun <reified T : Enum<T>> String.getEnum(): T {
    return enumValueOf(this)
}

val String.bytes: Int
    get() = toByteArray(Charsets.UTF_8).size