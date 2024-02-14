package com.github.zerobranch.beebox.settings

import androidx.lifecycle.viewModelScope
import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : BaseMainViewModel() {
    override val screenName: String = "Settings"

    val uiState = flow {
        emit(
            listOf(
                UiItems.Appearance,
                UiItems.Backup,
            )
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _actions = OnceFlow<SettingsAction>()
    val actions = _actions.asSharedFlow()

    fun onAppearanceClick() {

    }

    fun onBackupClick() {
        _actions.tryEmit(SettingsAction.GoToBackup)
    }
}