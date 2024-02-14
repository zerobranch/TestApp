package com.github.zerobranch.beebox.domain.models.words

import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import java.io.Serializable

data class DraftWord(
    val word: String = "",
    val parentWord: Word? = null,
    val transcriptions: List<Transcription> = emptyList(),
    val translates: String = "",
    val comment: String = "",
    val rank: Int = -1,
    val hints: List<Hint> = emptyList(),
    val categories: List<WordCategory> = emptyList(),
    val wordTypes: List<WordType> = emptyList(),
) : Serializable
