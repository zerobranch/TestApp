package com.github.zerobranch.beebox.settings

sealed interface UiItems {
    data object Appearance : UiItems
    data object Backup : UiItems
}