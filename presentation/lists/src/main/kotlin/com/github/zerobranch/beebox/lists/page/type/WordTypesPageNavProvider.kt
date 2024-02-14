package com.github.zerobranch.beebox.lists.page.type

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.WordType

interface WordTypesPageNavProvider {
    fun toDeleteDialog(
        resultKey: String,
        wordType: WordType,
        title: String,
        description: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand

    fun toEditWordTypeDialog(wordType: WordType): NavCommand
    fun toTypeWordList(wordType: WordType): NavCommand
}