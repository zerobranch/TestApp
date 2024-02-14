package com.github.zerobranch.beebox.domain.models.words

import java.io.Serializable

abstract class SimpleWord : Serializable {
    abstract val word: String
    abstract val translates: String
}