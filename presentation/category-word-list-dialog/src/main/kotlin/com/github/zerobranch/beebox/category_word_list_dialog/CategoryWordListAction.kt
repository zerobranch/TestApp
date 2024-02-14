package com.github.zerobranch.beebox.category_word_list_dialog

import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.words.Word

sealed interface CategoryWordListAction {
    object OpenEditWordDialog : CategoryWordListAction
    data class ToAddWord(val wordCategory: WordCategory) : CategoryWordListAction
    data class OpenDeleteDialog(val word: Word) : CategoryWordListAction
    data class OpenConfirmDeleteDialog(val word: Word) : CategoryWordListAction
    data class Reload(val position: Int) : CategoryWordListAction
}