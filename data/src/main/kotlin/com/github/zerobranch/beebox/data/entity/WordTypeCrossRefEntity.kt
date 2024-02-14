package com.github.zerobranch.beebox.data.entity

import androidx.room.Entity

@Entity(
    tableName = "word_types_cross_ref",
    primaryKeys = ["word", "typeId"]
)
data class WordTypeCrossRefEntity(
    val word: String,
    val typeId: Long
)