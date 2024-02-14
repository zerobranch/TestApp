package com.github.zerobranch.beebox.word_groups_dialog

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.words.DraftWord

interface WordGroupNavProvider {
    val toChooseCategoryScreen: NavCommand
    val toChooseWordTypeScreen: NavCommand
    val toLocalSearchScreen: NavCommand

    fun toDeleteDialog(
        resultKey: String,
        word: DraftWord,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand

    fun toOpenExitDialog(
        resultKey: String,
        title: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand
}