package com.github.zerobranch.beebox.choose_type_dialog

import com.github.zerobranch.beebox.domain.models.WordType

sealed interface ChooseTypeAction {
    data object OpenCreateWordTypeDialog : ChooseTypeAction
    data class OpenEditWordTypeDialog(val wordType: WordType) : ChooseTypeAction
    data class OpenDeleteDialog(val wordType: WordType) : ChooseTypeAction
    data class Reload(val position: Int) : ChooseTypeAction
}
