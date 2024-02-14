package com.github.zerobranch.beebox.settings

import com.github.zerobranch.beebox.commons_app.base.NavCommand

interface SettingsNavProvider {
    val toBackup: NavCommand
}