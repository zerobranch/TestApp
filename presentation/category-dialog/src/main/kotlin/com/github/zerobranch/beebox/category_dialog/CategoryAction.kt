package com.github.zerobranch.beebox.category_dialog

sealed interface CategoryAction {
    object Dismiss : CategoryAction
}