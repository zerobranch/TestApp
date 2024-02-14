package com.github.zerobranch.beebox.backup

import android.net.Uri
import org.threeten.bp.LocalDateTime

sealed interface BackupAction {
    data object OpeningDBError : BackupAction
    data object CopyingDBError : BackupAction
    data object OpenFileChooser : BackupAction
    data object Rebirth : BackupAction
    data class Share(
        val databaseUri: Uri,
        val targetPackage: String,
        val date: LocalDateTime,
        val dbVersion: Int,
        val typesCount: Int,
        val categoriesCount: Int,
        val wordsCount: Int,
        val isTelegramChecked: Boolean,
    ) : BackupAction
}