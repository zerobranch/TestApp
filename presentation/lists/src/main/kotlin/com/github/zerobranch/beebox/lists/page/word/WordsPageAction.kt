package com.github.zerobranch.beebox.lists.page.word

import com.github.zerobranch.beebox.domain.models.words.Word

sealed interface WordsPageAction {
    object OpenEditWordDialog : WordsPageAction
    data class OpenDeleteDialog(val word: Word) : WordsPageAction
    data class OpenConfirmDeleteDialog(val word: Word) : WordsPageAction
    data class Reload(val position: Int) : WordsPageAction
}