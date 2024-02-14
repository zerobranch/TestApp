package com.github.zerobranch.beebox.choose_type_dialog

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.WordType

interface ChooseTypeNavProvider {
    fun toDeleteDialog(
        resultKey: String,
        wordType: WordType,
        title: String,
        description: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand

    val toCreateWordTypeDialog: NavCommand
    fun toEditWordTypeDialog(wordType: WordType): NavCommand
}