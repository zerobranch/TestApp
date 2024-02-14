package com.github.zerobranch.beebox.domain.models

import java.io.File
import java.io.Serializable

data class DBBackupInfo(
    val dbFile: File,
    val dbVersion: Int,
    val wordsCount: Int,
    val wordTypesCount: Int,
    val wordCategoriesCount: Int,
) : Serializable