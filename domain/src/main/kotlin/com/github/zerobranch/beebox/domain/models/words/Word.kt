package com.github.zerobranch.beebox.domain.models.words

import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import org.threeten.bp.ZonedDateTime
import java.io.Serializable

data class Word(
    override val word: String,
    override val translates: String,
    val comment: String,
    val rank: Int,
    val parentWord: Word?,
    val transcriptions: List<Transcription>,
    val categories: List<WordCategory>,
    val wordTypes: List<WordType>,
    val hints: List<Hint>,
    val createdDate: ZonedDateTime,
    val typesCount: Int = 0,
    val categoriesCount: Int = 0,
    val isSelected: Boolean = false,
) : Serializable, SimpleWord() {
    val mainUsAudio: String?
        get() = transcriptions
            .map { listOf(it.firstTranscriptionAudio, it.secondTranscriptionAudio) }
            .flatten()
            .firstOrNull { it.contains("us") }

    val mainUkAudio: String?
        get() = transcriptions
            .map { listOf(it.firstTranscriptionAudio, it.secondTranscriptionAudio) }
            .flatten()
            .firstOrNull { it.contains("uk") }
}