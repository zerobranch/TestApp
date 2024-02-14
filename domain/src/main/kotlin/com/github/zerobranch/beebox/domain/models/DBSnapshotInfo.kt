package com.github.zerobranch.beebox.domain.models

import java.io.Serializable

data class DBSnapshotInfo(
    val wordsCount: Int,
    val wordTypesCount: Int,
    val wordCategoriesCount: Int,
    val dbUri: String
) : Serializable