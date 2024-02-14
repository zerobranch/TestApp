package com.github.zerobranch.beebox.auth

import android.net.Uri
import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.domain.models.DBBackupInfo
import com.github.zerobranch.beebox.domain.usecase.BackupUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
@Suppress("OPT_IN_USAGE")
class AuthViewModel @Inject constructor(
    private val backupUseCase: BackupUseCase,
) : BaseMainViewModel() {
    override val screenName: String = "Auth"

    private val _action = OnceFlow<AuthAction>()
    val action = _action.asSharedFlow()

    private var pendingDBBackupInfo: DBBackupInfo? = null

    fun onCreate(uri: Uri) {
        backupUseCase
            .clearCache()
            .flatMapConcat { backupUseCase.onDBFileUriReceived(uri.toString()) }
            .onIO()
            .onEach { backupInfo ->
                pendingDBBackupInfo = backupInfo
                _action.tryEmit(AuthAction.Success)
            }
            .catch { th ->
                _action.tryEmit(AuthAction.Error)
                javaClass.error(screenName, th, "onDBFileUriReceived failed")
            }
            .inViewModel()
    }

    fun onLaunchAppClick() {
        _action.tryEmit(AuthAction.GoToMain())
    }

    fun onFingerprintClick() {
        _action.tryEmit(AuthAction.LaunchBiometricAuth)
    }

    fun onBiometricSuccess() {
        val pendingDBBackupInfo = pendingDBBackupInfo
        if (pendingDBBackupInfo.isNotNull()) {
            _action.tryEmit(AuthAction.GoToMain(pendingDBBackupInfo))
        } else {
            _action.tryEmit(AuthAction.GoToMain())
        }
    }
}