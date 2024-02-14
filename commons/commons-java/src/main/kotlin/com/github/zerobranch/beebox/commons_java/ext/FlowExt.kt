package com.github.zerobranch.beebox.commons_java.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

fun <T> Flow<T>.throttleFirst(duration: Long, timeUnit: TimeUnit): Flow<T> = flow {
    var lastEmissionTime = 0L

    collect { upstream ->
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastEmissionTime > timeUnit.toMillis(duration)) {
            lastEmissionTime = currentTime
            emit(upstream)
        }
    }
}