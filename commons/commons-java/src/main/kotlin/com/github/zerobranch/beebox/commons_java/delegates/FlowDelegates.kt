package com.github.zerobranch.beebox.commons_java.delegates

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

@Suppress("FunctionName")
fun <T> SingleSharedFlow(): MutableSharedFlow<T> =
    MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

@Suppress("FunctionName")
fun <T> OnceFlow(): MutableSharedFlow<T> =
    MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

inline fun <T> MutableStateFlow<T>.fresh(function: T.() -> T) {
    val prevValue = value
    val nextValue = function(prevValue)
    tryEmit(nextValue)
}

suspend fun <T> MutableSharedFlow<T>.emitWhenSubscribed(value: T) {
    if (subscriptionCount.value > 0) {
        tryEmit(value)
        return
    }

    subscriptionCount.first { it > 0 } // waiting for subscribers
    tryEmit(value)
}