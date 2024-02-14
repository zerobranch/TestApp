package com.github.zerobranch.beebox.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "word_categories")
class WordCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long,
    val name: String,
    val description: String,
    val createdDate: ZonedDateTime
)
