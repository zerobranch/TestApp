package com.github.zerobranch.beebox.commons_java.ext

import kotlin.math.round

fun Float.roundToTenths() = round(this * 10f) / 10f