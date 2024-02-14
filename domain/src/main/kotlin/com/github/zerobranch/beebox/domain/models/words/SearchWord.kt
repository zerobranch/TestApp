package com.github.zerobranch.beebox.domain.models.words

import java.io.Serializable

data class SearchWord(
    override val word: String,
    override val translates: String
) : Serializable, SimpleWord()