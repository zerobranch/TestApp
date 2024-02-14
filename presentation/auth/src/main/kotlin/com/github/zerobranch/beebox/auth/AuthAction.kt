package com.github.zerobranch.beebox.auth

import com.github.zerobranch.beebox.domain.models.DBBackupInfo

sealed interface AuthAction {
    data object LaunchBiometricAuth : AuthAction
    data class GoToMain(val backupInfo: DBBackupInfo? = null) : AuthAction
    data object Reset : AuthAction
    data object Error : AuthAction
    data object Success : AuthAction
}