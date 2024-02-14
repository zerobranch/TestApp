package com.github.zerobranch.beebox.settings

sealed interface SettingsAction {
    data object GoToBackup : SettingsAction
}