package com.github.zerobranch.beebox.navigation

import androidx.core.os.bundleOf
import com.github.zerobranch.beebox.auth.AuthNavProvider
import com.github.zerobranch.beebox.commons_app.base.ActivityNavCommand
import com.github.zerobranch.beebox.domain.models.DBBackupInfo
import com.github.zerobranch.beebox.ui.MainActivity
import javax.inject.Inject

class AuthNavProviderImpl @Inject constructor() : AuthNavProvider {
    override fun toMain(backupInfo: DBBackupInfo?): ActivityNavCommand =
        ActivityNavCommand(
            MainActivity::class.java,
            bundleOf(MainActivity.DB_BACKUP_KEY to backupInfo)
        )
}