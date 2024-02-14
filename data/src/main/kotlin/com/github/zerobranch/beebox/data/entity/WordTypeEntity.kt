package com.github.zerobranch.beebox.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "word_types")
class WordTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val typeId: Long,
    val name: String,
    val description: String,
    val createdDate: ZonedDateTime
)
