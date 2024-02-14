package com.github.zerobranch.beebox.word_groups_dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.zerobranch.beebox.domain.models.words.DraftWord

@dagger.assisted.AssistedFactory
internal interface ViewModelFactory {
    fun create(drafts: List<DraftWord>): WordGroupViewModel
}

context(Fragment)
@Suppress("UNCHECKED_CAST")
internal inline fun <reified VM : ViewModel> ViewModelFactory.get(
    drafts: List<DraftWord>
): VM = viewModels<VM> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = create(drafts) as T
    }
}.value
