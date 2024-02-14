package com.github.zerobranch.beebox.logging

import android.util.Log
import com.github.zerobranch.beebox.domain.usecase.FileLoggingUseCase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileLoggingTree @Inject constructor(
    private val fileLoggingUseCase: FileLoggingUseCase,
) : Timber.DebugTree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean = (priority >= Log.INFO)

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        fileLoggingUseCase.emitLog(priority, tag, message, t)
    }
}
