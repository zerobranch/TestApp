package com.github.zerobranch.beebox.domain.repository

import java.io.Reader

interface FileLoggingRepository {
    fun getLogReader(): Reader?
    fun replaceLogFile(logs: String)
    fun addLog(log: String)
    fun getLogFileUri(): String?
}