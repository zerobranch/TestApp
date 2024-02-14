package com.github.zerobranch.beebox.choose_category_dialog

import com.github.zerobranch.beebox.domain.models.WordCategory

sealed interface ChooseCategoryAction {
    data object OpenCreateCategoryDialog : ChooseCategoryAction
    data class OpenEditCategoryDialog(val wordCategory: WordCategory) : ChooseCategoryAction
    data class OpenDeleteDialog(val wordCategory: WordCategory) : ChooseCategoryAction
    data class Reload(val position: Int) : ChooseCategoryAction
}
