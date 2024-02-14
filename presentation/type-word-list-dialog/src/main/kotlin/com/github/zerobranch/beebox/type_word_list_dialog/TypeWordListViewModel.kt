package com.github.zerobranch.beebox.type_word_list_dialog

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.commons_java.ext.indexOfOrNull
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.exceptions.ExistChildWordException
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.WordUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach

class TypeWordListViewModel @AssistedInject constructor(
    private val wordUseCase: WordUseCase,
    private val draftWordUseCase: DraftWordUseCase,
    @Assisted private val wordType: WordType
) : BaseMainViewModel() {

    override val screenName: String = "TypeWordList"

    private val _wordsState = MutableStateFlow<List<Word>?>(null)
    val wordsState = _wordsState.asStateFlow()

    private val _action = OnceFlow<TypeWordListAction>()
    val action = _action.asSharedFlow()

    init {
        wordUseCase.getAllByType(wordType)
            .onIO()
            .onEach { _wordsState.fresh { it } }
            .catch { th -> javaClass.error(screenName, th, "getAllByType failed") }
            .inViewModel()
    }

    fun onWordClick(word: Word) {
        draftWordUseCase.reset().set(word)
        _action.tryEmit(TypeWordListAction.OpenEditWordDialog)
    }

    fun onSwipeDelete(word: Word) {
        _action.tryEmit(TypeWordListAction.OpenDeleteDialog(word))
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
                    _action.tryEmit(TypeWordListAction.OpenConfirmDeleteDialog(word))
                } else {
                    javaClass.error(screenName, th, "softDeleteWord failed") }
            }
            .inViewModel()
    }

    fun onDialogCancelClick(word: Word) {
        _action.tryEmit(
            TypeWordListAction.Reload(
                _wordsState.value?.indexOfOrNull(word) ?: return
            )
        )
    }

    fun onAddClick() {
        _action.tryEmit(TypeWordListAction.ToAddWord(wordType.copy(isSelected = true)))
    }
}