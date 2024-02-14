package com.github.zerobranch.beebox.commons_java.ext

import java.util.concurrent.TimeUnit

fun millisToSecond(millis: Long) = TimeUnit.MILLISECONDS.toSeconds(millis)

fun millisToMinutes(millis: Long) = TimeUnit.MILLISECONDS.toMinutes(millis)
