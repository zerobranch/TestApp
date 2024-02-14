package com.github.zerobranch.beebox.domain.models

import org.threeten.bp.ZonedDateTime
import java.io.Serializable

data class WordCategory(
    val id: Long,
    val name: String,
    val description: String,
    val createdDate: ZonedDateTime,
    val isSelected: Boolean = false,
    val wordsCount: Int = 0
) : Serializable