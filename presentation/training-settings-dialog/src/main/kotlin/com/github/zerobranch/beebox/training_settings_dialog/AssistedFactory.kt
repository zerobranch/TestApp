package com.github.zerobranch.beebox.training_settings_dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.zerobranch.beebox.domain.models.TrainingType

@dagger.assisted.AssistedFactory
internal interface ViewModelFactory {
    fun create(trainingType: TrainingType): TrainingSettingsViewModel
}

context(Fragment)
@Suppress("UNCHECKED_CAST")
internal inline fun <reified VM : ViewModel> ViewModelFactory.get(
    trainingType: TrainingType
): VM = viewModels<VM> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = create(trainingType) as T
    }
}.value