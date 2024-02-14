package com.github.zerobranch.beebox.commons_java

sealed class LoadableData<out T> {
    object Initial : LoadableData<Nothing>()
    object Loading : LoadableData<Nothing>()
    data class Success<T>(val data: T) : LoadableData<T>()
    data class Failure(val isNoInternet: Boolean = false) : LoadableData<Nothing>()
}

data class ReloadableData<out T>(val data: T?, val status: Status) {
    companion object {
        fun <T> init(): ReloadableData<T> = ReloadableData(null, Status.Idle)
    }

    fun hasData() = data != null

    fun isIdle() = status is Status.Idle

    fun isLoading() = status is Status.Loading

    fun isNotLoading() = status !is Status.Loading

    fun isFailure() = status is Status.Failure
}
sealed class Status {
    object Idle : Status()
    object Loading : Status()
    data class Failure(val isNoInternet: Boolean = false, val repeatAction: (() -> Unit)? = null) : Status()
}