package com.github.zerobranch.beebox.data.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.github.zerobranch.beebox.data.source.db.AppDatabase
import com.github.zerobranch.beebox.domain.models.BeeboxConfig
import com.github.zerobranch.beebox.domain.models.DBBackupInfo
import com.github.zerobranch.beebox.domain.repository.BackupRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val beeboxConfig: BeeboxConfig,
    @ApplicationContext private val context: Context,
) : BackupRepository {
    private companion object {
        const val DIRECTORY = "db_temps"
    }

    private val dbTempDirectory: File
        get() = File(context.cacheDir, DIRECTORY)

    override fun clearCache(): Flow<Unit> =
        flow {
            dbTempDirectory.run {
                deleteRecursively()
                mkdir()
            }
            emit(Unit)
        }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun createTempFile(uriRaw: String): Flow<DBBackupInfo> =
        flow {
            val uri = uriRaw.toUri()
            val file = File.createTempFile(
                beeboxConfig.appName.lowercase(),
                ".tmp",
                dbTempDirectory
            )

            file.outputStream().buffered().use { bos ->
                context.contentResolver?.openInputStream(uri)?.buffered()?.use { bis ->
                    bis.copyTo(bos)
                    bis.close()
                }
            }

            emit(file)
        }
            .map { dbFile ->
                val db = SQLiteDatabase.openDatabase(dbFile.absolutePath, null, 0)
                DBBackupInfo(
                    dbFile = dbFile,
                    dbVersion = db.version,
                    wordsCount = db.getTableCount("words"),
                    wordCategoriesCount = db.getTableCount("word_categories"),
                    wordTypesCount = db.getTableCount("word_types")
                ).also { db.close() }
            }

    override fun importDB(backupInfo: DBBackupInfo): Flow<Unit> =
        flow {
            appDatabase.close()
            val dbFile = context.getDatabasePath(beeboxConfig.databaseName)
            context.deleteDatabase(beeboxConfig.databaseName)
            backupInfo.dbFile.copyTo(dbFile, overwrite = true)
            emit(Unit)
        }

    override fun getDatabaseFile(): Flow<String> =
        flow {
            val dbFile = context.getDatabasePath(beeboxConfig.databaseName)
            val toFile = File(context.cacheDir, dbFile.name)
            dbFile.copyTo(toFile, overwrite = true)
            emit(
                FileProvider.getUriForFile(context, beeboxConfig.applicationId, toFile).toString()
            )
        }

    private fun SQLiteDatabase.getTableCount(table: String): Int {
        val cursor = query(table, null, null, null, null, null, null)
        return cursor.count.also { cursor.close() }
    }
}
