package com.github.zerobranch.beebox.choose_category_dialog

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.WordCategory

interface ChooseCategoryNavProvider {
    fun toDeleteDialog(
        resultKey: String,
        wordCategory: WordCategory,
        title: String,
        description: String,
        actionBtnText: String,
        cancelBtnText: String,
    ): NavCommand

    val toCreateCategoryDialog: NavCommand
    fun toEditCategoryDialog(wordCategory: WordCategory): NavCommand
}