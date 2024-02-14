package com.github.zerobranch.beebox.commons_java.ext

import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun Number.toPrettyCount(): String {
    val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
    val numValue = this.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        (DecimalFormat("#0.0")
            .format(numValue / 10.0.pow((base * 3).toDouble()))
            .replace(",0", "") + suffix[base])
            .trim()

    } else {
        DecimalFormat("#,##0").format(numValue)
    }
}

fun Number.toPrettyFormat(
    maximumFractionDigits: Int = 2,
    isSignedFormat: Boolean = false
): String {
    val format = NumberFormat.getNumberInstance()
    format.maximumFractionDigits = maximumFractionDigits
    var formatted = format.format(this).replace(',', '.')

    if (isSignedFormat) {
        formatted = (if (this.toFloat() >= 0) "+" else "") + formatted
    }

    return formatted
}