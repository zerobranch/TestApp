package com.github.zerobranch.beebox.add_word_dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@dagger.assisted.AssistedFactory
internal interface ViewModelFactory {
    fun create(isEditMode: Boolean): AddWordViewModel
}

context(Fragment)
@Suppress("UNCHECKED_CAST")
internal inline fun <reified VM : ViewModel> ViewModelFactory.get(
    isEditMode: Boolean
): VM = viewModels<VM> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = create(isEditMode) as T
    }
}.value