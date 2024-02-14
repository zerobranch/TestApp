package com.github.zerobranch.beebox.data.repository

import android.content.Context
import androidx.core.content.FileProvider
import com.github.zerobranch.beebox.commons_java.ext.zip
import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.repository.FileLoggingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.Reader
import javax.inject.Inject

class FileLoggingRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val beeboxConfig: BeeboxConfig
) : FileLoggingRepository {
    companion object {
        private const val LOG_DIRECTORY = "logs"
    }

    private val logFile: File? = getLogFile()

    @Synchronized
    override fun getLogReader(): Reader? = logFile?.reader()?.buffered()

    @Synchronized
    override fun replaceLogFile(logs: String) {
        logFile?.run {
            logFile.delete()
            logFile.createNewFile()
            logFile.writer().buffered().use { it.appendLine(logs) }
        }
    }

    @Synchronized
    override fun addLog(log: String) {
        FileOutputStream(logFile, true).writer().buffered().use { it.appendLine(log) }
    }

    @Synchronized
    override fun getLogFileUri(): String? =
        runCatching {
            val zipPath = logFile?.zip(beeboxConfig.attachArchivePassword) ?: return@runCatching null
            FileProvider.getUriForFile(context, beeboxConfig.applicationId, zipPath)
        }.getOrNull()?.toString()

    private fun getLogFile(): File? = runCatching {
        val directory = File(context.filesDir, LOG_DIRECTORY)
        directory.mkdir()

        val file = File(directory, beeboxConfig.beeboxLogFileName)
        if (!file.exists()) {
            file.createNewFile()
        }

        file
    }.getOrNull()
}