package com.github.zerobranch.beebox.data.entity

import androidx.room.Entity

@Entity(
    tableName = "word_categories_cross_ref",
    primaryKeys = ["word", "categoryId"]
)
data class WordCategoryCrossRefEntity(
    val word: String,
    val categoryId: Long
)