package com.github.zerobranch.beebox.add_word_dialog

sealed interface AddWordAction {
    data class OpenInBrowser(val url: String) : AddWordAction
    data object GoToChooseCategory : AddWordAction
    data object GoToChooseWordType : AddWordAction
    data object GoToLocalSearch : AddWordAction
    data object ValidationError : AddWordAction
    data object BaseError : AddWordAction
    data object Exit : AddWordAction
}