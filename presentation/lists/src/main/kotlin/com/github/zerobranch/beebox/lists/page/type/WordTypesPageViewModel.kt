package com.github.zerobranch.beebox.lists.page.type

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.commons_java.ext.indexOfOrNull
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.usecase.WordTypeUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class WordTypesPageViewModel @Inject constructor(
    private val wordTypeUseCase: WordTypeUseCase
) : BaseMainViewModel() {

    override val screenName: String = "WordTypesPage"

    private val _wordTypesState = MutableStateFlow<List<WordType>?>(null)
    val wordTypesState = _wordTypesState.asStateFlow()

    private val _action = OnceFlow<WordTypesPageAction>()
    val action = _action.asSharedFlow()

    init {
        wordTypeUseCase.getAllWithWordsCount()
            .onIO()
            .onEach { _wordTypesState.fresh { it } }
            .catch { th -> javaClass.error(screenName, th, "WordType getAll failed") }
            .inViewModel()
    }

    fun onWordTypeClick(wordType: WordType) {
        _action.tryEmit(WordTypesPageAction.ToTypeWordList(wordType))
    }

    fun onSwipeDelete(wordType: WordType) {
        wordTypeUseCase.containsWordsIsType(wordType.id)
            .onIO()
            .onEach { contains ->
                if (contains) {
                    _action.tryEmit(WordTypesPageAction.OpenDeleteDialog(wordType))
                } else {
                    hardDeleteWordType(wordType)
                }
            }
            .catch { th -> javaClass.error(screenName, th, "containsWordsIsType failed") }
            .inViewModel()
    }

    fun onSwipeEdit(item: WordType) {
        _action.tryEmit(WordTypesPageAction.OpenEditWordTypeDialog(item))
    }

    fun onDialogDeleteClick(wordType: WordType) {
        hardDeleteWordType(wordType)
    }

    fun onDialogCancelClick(wordType: WordType) {
        _action.tryEmit(
            WordTypesPageAction.Reload(
                _wordTypesState.value?.indexOfOrNull(wordType) ?: return
            )
        )
    }

    private fun hardDeleteWordType(wordType: WordType) {
        wordTypeUseCase.delete(wordType)
            .onIO()
            .catch { th -> javaClass.error(screenName, th, "WordType delete failed") }
            .inViewModel()
    }
}