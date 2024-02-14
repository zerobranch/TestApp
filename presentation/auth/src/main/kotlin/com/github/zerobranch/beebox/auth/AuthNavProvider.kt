package com.github.zerobranch.beebox.auth

import com.github.zerobranch.beebox.commons_app.base.ActivityNavCommand
import com.github.zerobranch.beebox.domain.models.DBBackupInfo

interface AuthNavProvider {
    fun toMain(backupInfo: DBBackupInfo? = null): ActivityNavCommand
}