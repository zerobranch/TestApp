package com.github.zerobranch.beebox.search

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_app.utils.isNoInternet
import com.github.zerobranch.beebox.commons_java.ReloadableData
import com.github.zerobranch.beebox.commons_java.Status
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.SearchWord
import com.github.zerobranch.beebox.domain.models.words.SimpleWord
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.SearchUseCase
import com.github.zerobranch.beebox.domain.usecase.WordUseCase
import com.github.zerobranch.beebox.logging.error
import com.github.zerobranch.beebox.logging.wtf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@Suppress("OPT_IN_USAGE")
class SearchViewModel @AssistedInject constructor(
    private val searchUseCase: SearchUseCase,
    private val draftWordUseCase: DraftWordUseCase,
    private val wordUseCase: WordUseCase,
    @Assisted private val wordType: WordType?,
    @Assisted private val wordCategory: WordCategory?
) : BaseMainViewModel() {
    private companion object {
        const val DEBOUNCE_TIME = 300L
    }

    override val screenName: String = "Add"

    private val _searchState = MutableStateFlow<ReloadableData<List<SimpleWord>>>(ReloadableData.init())
    val searchState = _searchState.asStateFlow()

    private val _wordsGroupState = MutableStateFlow(
        WordGroupState(
            words = emptyList(),
            isWordGroupActive = false,
            isLocalWords = wordType.isNotNull() || wordCategory.isNotNull(),
        )
    )
    val wordsGroupState = _wordsGroupState.asStateFlow()

    private val isWordGroupActive: Boolean
        get() = _wordsGroupState.value.isWordGroupActive

    private val currentGroupWords = mutableListOf<SimpleWord>()
    private val currentWords = mutableListOf<SimpleWord>()

    private val _action = OnceFlow<SearchAction>()
    val action = _action.asSharedFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        setupQueryFlow()
        loadAllWords()
    }

    fun onQueryChanged(query: String) {
        if (query.isBlank()) _searchState.fresh { copy(data = null, status = Status.Idle) }
        queryFlow.tryEmit(query.trim())
    }

    fun onAddWordClick() {
        draftWordUseCase
            .reset()
            .onWordTypeChanged(wordType)
            .onCategoryChanged(wordCategory)

        _action.tryEmit(SearchAction.GoToAddWord)
    }

    fun onSearchWordClick(item: SearchWord) {
        if (isWordGroupActive) {
            if (currentGroupWords.any { it.word == item.word }) return

            currentGroupWords.add(item)
            _wordsGroupState.fresh { copy(words = currentGroupWords.toList()) }
            clearQuery()
        } else {
            searchUseCase
                .getWord(item)
                .onEach { word ->
                    draftWordUseCase.apply {
                        reset()
                        word?.run { set(word) }
                        onWordTypeChanged(wordType)
                        onCategoryChanged(wordCategory)
                    }

                    _action.tryEmit(SearchAction.GoToAddWord)
                }
                .catch { th ->
                    _searchState.fresh { copy(status = Status.Failure()) }
                    javaClass.wtf(screenName, th, "getWord failed")
                }
                .inViewModel()
        }
    }

    fun onWordClick(word: Word) {
        draftWordUseCase
            .reset()
            .set(word)
            .onWordTypeChanged(wordType)
            .onCategoryChanged(wordCategory)

        _action.tryEmit(SearchAction.ToEditWordDialog)
    }

    fun onWordGroupCheckedChange(checked: Boolean) {
        _wordsGroupState.fresh {
            copy(
                words = if (checked) currentGroupWords.toList() else currentWords.toList(),
                isWordGroupActive = checked
            )
        }
    }

    fun onCreateWordGroupClick() {
        draftWordUseCase
            .reset()
            .onWordTypeChanged(wordType)
            .onCategoryChanged(wordCategory)

        searchUseCase.parseDraftWords(currentGroupWords)
            .onIO()
            .onEach { drafts -> _action.tryEmit(SearchAction.GoToWordGroup(drafts)) }
            .catch { th -> javaClass.error(screenName, th, "WordType getAll failed") }
            .inViewModel()
    }

    fun onSwipeDelete(searchWord: SimpleWord) {
        currentGroupWords.remove(searchWord)
        _wordsGroupState.fresh { copy(words = currentGroupWords.toList()) }
    }

    fun onSwitchWord(word: Word) {
        (getSwitchWordFlow(word) ?: return)
            .onIO()
            .catch { th -> javaClass.error(screenName, th, "onSwitchWord failed") }
            .inViewModel()
    }

    fun onSearchEnterClick() {
        val item = _searchState.value.data?.firstOrNull() ?: return
        when (item) {
            is Word -> onWordClick(item)
            is SearchWord -> onSearchWordClick(item)
        }
    }

    fun onClearQueryClick() {
        clearQuery()
    }

    private fun loadAllWords() {
        wordUseCase
            .getAll()
            .map { words ->
                words.map {  word ->
                    word.copy(
                        isSelected = word.wordTypes.any { it.id == wordType?.id } ||
                                word.categories.any { it.id == wordCategory?.id }
                    )
                }
            }
            .onIO()
            .onEach { words ->
                currentWords.clear()
                currentWords.addAll(words)

                if (!isWordGroupActive) {
                    _wordsGroupState.fresh { copy(words = currentWords.toList()) }
                }
            }
            .catch { th -> javaClass.error(screenName, th, "getAll failed") }
            .inViewModel()
    }

    private fun setupQueryFlow() {
        queryFlow.debounce(DEBOUNCE_TIME)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(null)
                } else {
                    searchUseCase
                        .search(query)
                        .onStart { _searchState.fresh { copy(status = Status.Loading) } }
                        .catch { th ->
                            _searchState.fresh { copy(status = Status.Failure()) }
                            javaClass.error(screenName, th, "search failed")

                            if (th.isNoInternet) {
                                _action.tryEmit(SearchAction.ShowNetworkError)
                            } else {
                                _action.tryEmit(SearchAction.ShowBaseError)
                            }
                        }
                }
            }
            .onIO()
            .onEach { words -> _searchState.fresh { copy(data = words, status = Status.Idle) } }
            .catch { th ->
                _searchState.fresh { copy(status = Status.Failure()) }
                javaClass.wtf(screenName, th, "setupQueryFlow failed")
            }
            .inViewModel()
    }

    private fun getSwitchWordFlow(word: Word): Flow<Unit>? {
        var flow: Flow<Unit>? = null
        if (word.isSelected) {
            if (wordCategory.isNotNull()) {
                flow = wordUseCase.deleteCategoryFromWord(word, wordCategory)
            } else if (wordType.isNotNull()) {
                flow = wordUseCase.deleteTypeFromWord(word, wordType)
            }
        } else {
            if (wordCategory.isNotNull()) {
                flow = wordUseCase.addCategoryToWord(word, wordCategory)
            } else if (wordType.isNotNull()) {
                flow = wordUseCase.addTypeToWord(word, wordType)
            }
        }
        return flow
    }

    private fun clearQuery() {
        _action.tryEmit(SearchAction.ClearQuery)
    }
}
