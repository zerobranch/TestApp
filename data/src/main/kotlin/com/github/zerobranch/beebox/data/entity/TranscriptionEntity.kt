package com.github.zerobranch.beebox.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TranscriptionEntity(
    val id: String = "",
    val title: String = "",
    val firstTranscriptionHint: String = "",
    val firstTranscription: String = "",
    val secondTranscriptionHint: String = "",
    val secondTranscription: String = "",
    val firstTranscriptionAudio: String = "",
    val secondTranscriptionAudio: String = "",
)