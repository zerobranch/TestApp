package com.github.zerobranch.beebox.navigation

import com.github.zerobranch.beebox.R
import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.settings.SettingsNavProvider
import javax.inject.Inject

class SettingsNavProviderImpl @Inject constructor() : SettingsNavProvider {
    override val toBackup: NavCommand =
        NavCommand(R.id.action_settings_to_backup_screen)
}