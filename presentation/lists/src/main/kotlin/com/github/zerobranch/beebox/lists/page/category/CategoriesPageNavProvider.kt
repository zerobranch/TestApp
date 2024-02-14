package com.github.zerobranch.beebox.lists.page.category

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.WordCategory

interface CategoriesPageNavProvider {
    fun toDeleteDialog(
        resultKey: String,
        wordCategory: WordCategory,
        title: String,
        description: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand

    fun toEditCategoryDialog(wordCategory: WordCategory): NavCommand
    fun toCategoryWordList(wordCategory: WordCategory): NavCommand
}