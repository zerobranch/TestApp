package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.domain.models.DBBackupInfo
import com.github.zerobranch.beebox.domain.repository.BackupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BackupUseCase @Inject constructor(
    private val repository: BackupRepository
) {
    fun clearCache(): Flow<Unit> = repository.clearCache()

    fun onDBFileUriReceived(uri: String): Flow<DBBackupInfo> = repository.createTempFile(uri)

    fun importDB(backupInfo: DBBackupInfo): Flow<Unit> = repository.importDB(backupInfo)

    fun getDatabaseFile(): Flow<String> = repository.getDatabaseFile()
}