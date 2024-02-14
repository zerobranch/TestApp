package com.github.zerobranch.beebox.word_type_dialog

sealed interface WordTypeAction {
    object Dismiss : WordTypeAction
}