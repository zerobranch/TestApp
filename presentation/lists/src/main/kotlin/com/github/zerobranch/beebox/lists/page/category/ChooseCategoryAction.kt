package com.github.zerobranch.beebox.lists.page.category

import com.github.zerobranch.beebox.domain.models.WordCategory

sealed interface CategoriesPageAction {
    data class ToCategoryWordList(val wordCategory: WordCategory) : CategoriesPageAction
    data class OpenEditCategoryDialog(val wordCategory: WordCategory) : CategoriesPageAction
    data class OpenDeleteDialog(val wordCategory: WordCategory) : CategoriesPageAction
    data class Reload(val position: Int) : CategoriesPageAction
}