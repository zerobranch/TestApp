package com.github.zerobranch.beebox.search

import com.github.zerobranch.beebox.domain.models.words.DraftWord

sealed interface SearchAction {
    data object ShowBaseError : SearchAction
    data object ShowNetworkError : SearchAction
    data object GoToAddWord : SearchAction
    data object ToEditWordDialog : SearchAction
    data object ClearQuery : SearchAction
    data class GoToWordGroup(val drafts: List<DraftWord>) : SearchAction
}
