package com.github.zerobranch.beebox.backup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.zerobranch.beebox.domain.models.DBBackupInfo

@dagger.assisted.AssistedFactory
internal interface ViewModelFactory {
    fun create(dbBackupUri: DBBackupInfo?): BackupViewModel
}

context(Fragment)
@Suppress("UNCHECKED_CAST")
internal inline fun <reified VM : ViewModel> ViewModelFactory.get(
    dbBackupUri: DBBackupInfo?
): VM = viewModels<VM> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = create(dbBackupUri) as T
    }
}.value