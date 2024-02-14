package com.github.zerobranch.beebox.domain.models.words

import java.io.Serializable

data class Transcription(
    val id: String = "",
    val title: String = "",
    val firstTranscriptionHint: String = "",
    val firstTranscription: String = "",
    val firstTranscriptionAudio: String = "",
    val secondTranscriptionHint: String = "",
    val secondTranscription: String = "",
    val secondTranscriptionAudio: String = "",
) : Serializable