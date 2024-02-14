package com.github.zerobranch.beebox.data.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "words")
class WordEntity(
    @PrimaryKey
    val word: String,
    val translate: String,
    val comment: String,
    val rank: Int,
    val transcriptions: List<TranscriptionEntity>,
    val createdDate: ZonedDateTime,
    val hints: List<HintEntity>,
    var parentWordId: String? = null,
) {
    @Ignore
    var parentWord: WordFull? = null
}