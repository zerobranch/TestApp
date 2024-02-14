package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.commons_java.ext.bytes
import com.github.zerobranch.beebox.commons_java.ext.isNotNullOrBlank
import com.github.zerobranch.beebox.commons_java.ext.stringWithFormat
import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.models.DeviceConfig
import com.github.zerobranch.beebox.domain.models.LogData
import com.github.zerobranch.beebox.domain.repository.DeviceInfoRepository
import com.github.zerobranch.beebox.domain.repository.FileLoggingRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileLoggingUseCase @Inject constructor(
    private val fileLoggingRepository: FileLoggingRepository,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val beeboxConfig: BeeboxConfig,
    private val deviceConfig: DeviceConfig
) {
    companion object {
        private const val LOG_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss"
        private const val LOG_MAX_FILE_SIZE = 50 * 1024 * 1024 // 50 MB
        private const val META_INFO_PREFIX = "---"
    }

    private val _logFlow = MutableSharedFlow<LogData>(
        replay = 100,
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }
    )

    fun prepare() {
        scope.launch {
            actualizationLogs()

            _logFlow
                .onEach { logData -> log(logData) }
                .catch {}
                .collect()
        }
    }

    fun emitLog(priority: Int, tag: String?, message: String, th: Throwable?) =
        scope.launch {
            _logFlow.emit(LogData(priority, tag, message, th))
        }


    fun getLogFileUri(): Flow<String?> = flow {
        actualizationLogs()
        emit(fileLoggingRepository.getLogFileUri())
    }

    private fun log(logData: LogData) {
        fileLoggingRepository.addLog(
            buildString {
                append("${ZonedDateTime.now().toInstant().epochSecond}")
                append(" ")
                append(LocalDateTime.now().stringWithFormat(LOG_TIME_FORMAT))
                append(" ")
                append(getLogName(logData.priority))
                append(" ")
                append(logData.tag)
                append(" ")
                append(logData.message)

                if (logData.th != null) {
                    append(" ")
                    append(logData.th.stackTraceToString())
                }
            }
        )
    }

    private suspend fun actualizationLogs() {
        fileLoggingRepository.replaceLogFile(
            getActualLogsBySize(getActualLogsByTime())
                .toMutableList()
                .apply { addAll(0, getUserInfo()) }
                .joinToString(separator = "\n")
        )
    }

    private fun getActualLogsBySize(logs: List<String>): List<String> {
        var currentSize = 0
        var startIndex = 0

        for (i in logs.size - 1 downTo 0) {
            val lineSize = logs[i].bytes
            if (currentSize + lineSize >= LOG_MAX_FILE_SIZE) {
                break
            } else {
                currentSize += lineSize
                startIndex = i
            }
        }

        return logs.subList(startIndex, logs.size)
    }

    private fun getActualLogsByTime(): List<String> {
        val logs = ArrayList<String>()
        val startLogLine = ZonedDateTime.now().minusHours(24).toInstant().epochSecond

        fileLoggingRepository.getLogReader()?.use { reader ->
            reader.forEachLine { line ->
                val firstWord = line.split(" ").firstOrNull()
                if (firstWord.isNotNullOrBlank() && firstWord != META_INFO_PREFIX) {
                    val timestamp = firstWord.toLongOrNull()
                    if (timestamp != null) {
                        if (timestamp >= startLogLine) {
                            logs.add(line)
                        }
                    } else {
                        logs.add(line)
                    }
                }
            }
        }

        return logs
    }

    private suspend fun getUserInfo(): List<String> =
        mutableListOf(
            "$META_INFO_PREFIX deviceId: ${deviceInfoRepository.deviceId.first()}",
            "$META_INFO_PREFIX App version: ${beeboxConfig.versionName}",
            "$META_INFO_PREFIX App version code: ${beeboxConfig.versionCode}",
            "$META_INFO_PREFIX Device brand: ${deviceConfig.deviceBrand}",
            "$META_INFO_PREFIX Device model: ${deviceConfig.deviceModel}",
            "$META_INFO_PREFIX OS version: ${deviceConfig.androidVersion}",
            "$META_INFO_PREFIX SDK version: ${deviceConfig.androidAPILevel}",
            "$META_INFO_PREFIX Build displayed version: ${deviceConfig.displayedVersion}",
            "\n"
        )

    private fun getLogName(priority: Int): String =
        when (priority) {
            2 -> "V"
            3 -> "D"
            4 -> "I"
            5 -> "W"
            6 -> "E"
            7 -> "WTF"
            else -> "UnknownLogPriority"
        }
}
