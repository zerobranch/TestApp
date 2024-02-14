package com.github.zerobranch.beebox.lists.page.word

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.commons_java.ext.indexOfOrNull
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.models.exceptions.ExistChildWordException
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.WordUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WordsPageViewModel @Inject constructor(
    private val wordUseCase: WordUseCase,
    private val draftWordUseCase: DraftWordUseCase,
) : BaseMainViewModel() {

    override val screenName: String = "WordsPage"

    private val _wordsState = MutableStateFlow<List<Word>?>(null)
    val wordsState = _wordsState.asStateFlow()

    private val _action = OnceFlow<WordsPageAction>()
    val action = _action.asSharedFlow()

    init {
        wordUseCase.getAll()
            .onIO()
            .onEach { _wordsState.fresh { it } }
            .catch { th -> javaClass.error(screenName, th, "Words getAll failed") }
            .inViewModel()
    }

    fun onWordClick(word: Word) {
        draftWordUseCase.reset().set(word)
        _action.tryEmit(WordsPageAction.OpenEditWordDialog)
    }

    fun onSwipeDelete(word: Word) {
        _action.tryEmit(WordsPageAction.OpenDeleteDialog(word))
    }

    fun onDialogConfirmDeleteClick(word: Word) {
        wordUseCase.hardDeleteWordWithoutChildren(word)
            .onIO()
            .catch { th -> javaClass.error(screenName, th, "softDeleteWord failed") }
            .inViewModel()
    }

    fun onDialogDeleteClick(word: Word) {
        wordUseCase.softDeleteWord(word)
            .onIO()
            .catch { th ->
                if (th is ExistChildWordException) {
                    _action.tryEmit(WordsPageAction.OpenConfirmDeleteDialog(word))
                } else {
                    javaClass.error(screenName, th, "softDeleteWord failed") }
                }
            .inViewModel()
    }

    fun onDialogCancelClick(word: Word) {
        _action.tryEmit(
            WordsPageAction.Reload(
                _wordsState.value?.indexOfOrNull(word) ?: return
            )
        )
    }
}