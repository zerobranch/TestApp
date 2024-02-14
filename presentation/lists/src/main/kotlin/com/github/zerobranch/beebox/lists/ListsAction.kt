package com.github.zerobranch.beebox.lists

sealed interface ListsAction {
    object OpenSearchWord : ListsAction
    object OpenAddCategory : ListsAction
    object OpenAddWordType : ListsAction
}