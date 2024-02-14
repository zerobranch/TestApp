package com.github.zerobranch.beebox.category_word_list_dialog

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.words.Word

interface CategoryWordListNavProvider {
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
    fun toAddWordScreen(wordCategory: WordCategory): NavCommand
}