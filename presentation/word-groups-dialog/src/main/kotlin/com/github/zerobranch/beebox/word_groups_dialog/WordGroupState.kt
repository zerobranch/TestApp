package com.github.zerobranch.beebox.word_groups_dialog

import com.github.zerobranch.beebox.domain.models.words.DraftWord

class WordGroupState(
    val draft: DraftWord,
    val controlState: ControlState
)

enum class ControlState {
    START, MIDDLE, END, SINGLE
}