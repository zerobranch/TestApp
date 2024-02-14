package com.github.zerobranch.beebox.word_groups_dialog

import com.github.zerobranch.beebox.domain.models.words.DraftWord

sealed interface WordGroupAction {
    data class OpenInBrowser(val url: String) : WordGroupAction
    data class OpenDeleteDialog(val word: DraftWord) : WordGroupAction
    data object OpenExitDialog : WordGroupAction
    data object GoToChooseCategory : WordGroupAction
    data object GoToChooseWordType : WordGroupAction
    data object ValidationError : WordGroupAction
    data object GoToLocalSearch : WordGroupAction
    data object BaseError : WordGroupAction
    data object Exit : WordGroupAction
}