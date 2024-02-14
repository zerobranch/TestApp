package com.github.zerobranch.beebox.lists.page.type

import com.github.zerobranch.beebox.domain.models.WordType

sealed interface WordTypesPageAction {
    data class ToTypeWordList(val wordType: WordType) : WordTypesPageAction
    data class OpenEditWordTypeDialog(val wordType: WordType) : WordTypesPageAction
    data class OpenDeleteDialog(val wordType: WordType) : WordTypesPageAction
    data class Reload(val position: Int) : WordTypesPageAction
}