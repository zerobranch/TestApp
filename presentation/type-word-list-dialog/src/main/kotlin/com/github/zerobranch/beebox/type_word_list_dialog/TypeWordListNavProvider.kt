package com.github.zerobranch.beebox.type_word_list_dialog

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.Word

interface TypeWordListNavProvider {
    fun toDeleteDialog(
        resultKey: String,
        word: Word,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand

    fun toConfirmDeleteDialog(
        resultKey: String,
        word: Word,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand

    val toEditWordScreen: NavCommand
    fun toAddWordScreen(wordType: WordType): NavCommand
}