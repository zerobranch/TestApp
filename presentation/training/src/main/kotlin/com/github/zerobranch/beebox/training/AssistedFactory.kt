package com.github.zerobranch.beebox.training

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining

@dagger.assisted.AssistedFactory
internal interface ViewModelFactory {
    fun create(progressTraining: ProgressTraining): TrainingViewModel
}

context(Fragment)
@Suppress("UNCHECKED_CAST")
internal inline fun <reified VM : ViewModel> ViewModelFactory.get(
    progressTraining: ProgressTraining
): VM = viewModels<VM> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = create(progressTraining) as T
    }
}.value