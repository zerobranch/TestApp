package com.github.zerobranch.beebox.lists

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class ListsViewModel @Inject constructor(
) : BaseMainViewModel() {

    override val screenName: String = "Lists"

    private val _action = OnceFlow<ListsAction>()
    val action = _action.asSharedFlow()

    fun onAddClick(currentItem: Int) {
        when (currentItem) {
            0 -> _action.tryEmit(ListsAction.OpenSearchWord)
            1 -> _action.tryEmit(ListsAction.OpenAddCategory)
            else -> _action.tryEmit(ListsAction.OpenAddWordType)
        }
    }
}
