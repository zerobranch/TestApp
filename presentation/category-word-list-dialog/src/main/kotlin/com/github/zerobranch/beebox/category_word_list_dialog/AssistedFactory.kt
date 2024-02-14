package com.github.zerobranch.beebox.category_word_list_dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.zerobranch.beebox.domain.models.WordCategory

@dagger.assisted.AssistedFactory
internal interface ViewModelFactory {
    fun create(wordCategory: WordCategory): CategoryWordListViewModel
}

context(Fragment)
@Suppress("UNCHECKED_CAST")
internal inline fun <reified VM : ViewModel> ViewModelFactory.get(
    wordCategory: WordCategory
): VM = viewModels<VM> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = create(wordCategory) as T
    }
}.value