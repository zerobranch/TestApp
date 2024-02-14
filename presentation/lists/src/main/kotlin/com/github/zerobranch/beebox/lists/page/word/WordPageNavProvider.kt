package com.github.zerobranch.beebox.lists.page.word

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.words.Word

interface WordPageNavProvider {
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
}