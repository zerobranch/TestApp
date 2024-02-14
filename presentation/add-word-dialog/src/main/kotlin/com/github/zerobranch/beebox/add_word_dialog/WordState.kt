package com.github.zerobranch.beebox.add_word_dialog

import com.github.zerobranch.beebox.domain.models.words.DraftWord

data class WordState(
    val draftWord: DraftWord,
    val isEditMode: Boolean,
)