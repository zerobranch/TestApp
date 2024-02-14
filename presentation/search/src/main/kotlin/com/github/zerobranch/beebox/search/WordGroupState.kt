package com.github.zerobranch.beebox.search

import com.github.zerobranch.beebox.domain.models.words.SimpleWord

data class WordGroupState(
    val words: List<SimpleWord>,
    val isWordGroupActive: Boolean,
    val isLocalWords: Boolean
)