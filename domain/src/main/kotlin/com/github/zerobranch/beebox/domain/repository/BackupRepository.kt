package com.github.zerobranch.beebox.domain.repository

import com.github.zerobranch.beebox.domain.models.DBBackupInfo
import kotlinx.coroutines.flow.Flow

interface BackupRepository {
    fun clearCache(): Flow<Unit>
    fun createTempFile(uriRaw: String): Flow<DBBackupInfo>
    fun importDB(backupInfo: DBBackupInfo): Flow<Unit>
    fun getDatabaseFile(): Flow<String>
}