package com.github.zerobranch.beebox.type_word_list_dialog

import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.Word

sealed interface TypeWordListAction {
    object OpenEditWordDialog : TypeWordListAction
    data class ToAddWord(val wordType: WordType) : TypeWordListAction
    data class OpenDeleteDialog(val word: Word) : TypeWordListAction
    data class OpenConfirmDeleteDialog(val word: Word) : TypeWordListAction
    data class Reload(val position: Int) : TypeWordListAction
}