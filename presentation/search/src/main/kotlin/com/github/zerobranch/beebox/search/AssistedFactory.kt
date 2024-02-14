package com.github.zerobranch.beebox.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType

@dagger.assisted.AssistedFactory
internal interface ViewModelFactory {
    fun create(
        wordType: WordType?,
        wordCategory: WordCategory?
    ): SearchViewModel
}

context(Fragment)
@Suppress("UNCHECKED_CAST")
internal inline fun <reified VM : ViewModel> ViewModelFactory.get(
    wordType: WordType?,
    wordCategory: WordCategory?
): VM = viewModels<VM> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            create(wordType, wordCategory) as T
    }
}.value