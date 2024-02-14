package com.github.zerobranch.beebox.local_search

import com.github.zerobranch.beebox.domain.models.words.Word

sealed interface LocalSearchAction {
    data class ReturnResult(val word: Word) : LocalSearchAction
}