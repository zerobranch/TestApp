package com.github.zerobranch.beebox.local_search

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.usecase.WordUseCase
import com.github.zerobranch.beebox.logging.wtf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class LocalSearchViewModel @Inject constructor(
    private val wordUseCase: WordUseCase
) : BaseMainViewModel() {
    private companion object {
        const val DEBOUNCE_TIME = 300L
    }

    override val screenName: String = "LocalSearch"

    private val _searchState = MutableStateFlow<List<Word>>(emptyList())
    val searchState = _searchState.asStateFlow()

    private val _action = OnceFlow<LocalSearchAction>()
    val action = _action.asSharedFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        setupQueryFlow()
    }

    fun onQueryChanged(query: String) {
        if (query.isBlank()) _searchState.fresh { emptyList() }
        queryFlow.tryEmit(query.trim())
    }

    fun onWordClick(word: Word) {
        _action.tryEmit(LocalSearchAction.ReturnResult(word))
    }

    private fun setupQueryFlow() {
        queryFlow.debounce(DEBOUNCE_TIME)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(emptyList())
                } else {
                    wordUseCase.search(query)
                }
            }
            .onIO()
            .onEach { words -> _searchState.fresh { words } }
            .catch { th -> javaClass.wtf(screenName, th, "setupQueryFlow failed") }
            .inViewModel()
    }
}