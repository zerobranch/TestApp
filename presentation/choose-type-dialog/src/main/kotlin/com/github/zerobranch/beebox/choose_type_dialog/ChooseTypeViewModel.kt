package com.github.zerobranch.beebox.choose_type_dialog

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.commons_java.ext.indexOfOrNull
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.WordTypeUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class ChooseTypeViewModel @Inject constructor(
    private val draftWordEvents: DraftWordUseCase,
    private val wordTypeUseCase: WordTypeUseCase
) : BaseMainViewModel() {

    override val screenName: String = "ChooseType"

    private val _wordTypesState = MutableStateFlow<List<WordType>?>(null)
    val wordTypesState = _wordTypesState.asStateFlow()

    private val _action = OnceFlow<ChooseTypeAction>()
    val action = _action.asSharedFlow()

    init {
        wordTypeUseCase.getAllWithWordsCount()
            .onIO()
            .map { wordTypes ->
                val draftWordTypes = draftWordEvents.wordTypes
                wordTypes.map { wordType ->
                    if (draftWordTypes.any { it.id == wordType.id }) {
                        wordType.copy(isSelected = true)
                    } else {
                        wordType
                    }
                }
            }
            .flowOn(Dispatchers.Default)
            .onEach { _wordTypesState.fresh { it } }
            .catch { th -> javaClass.error(screenName, th, "WordType getAll failed") }
            .inViewModel()
    }

    fun onWordTypeClick(wordType: WordType) {
        _wordTypesState.fresh {
            this?.map {
                it.takeIf { it.id != wordType.id } ?: it.copy(isSelected = !it.isSelected)
            }
        }
        draftWordEvents.onWordTypeChanged(wordType.copy(isSelected = !wordType.isSelected))
    }

    fun onAddWordTypeClick() {
        _action.tryEmit(ChooseTypeAction.OpenCreateWordTypeDialog)
    }

    fun onSwipeDelete(wordType: WordType) {
        wordTypeUseCase.containsWordsIsType(wordType.id)
            .onIO()
            .onEach { contains ->
                if (contains) {
                    _action.tryEmit(ChooseTypeAction.OpenDeleteDialog(wordType))
                } else {
                    hardDeleteWordType(wordType)
                }
            }
            .catch { th -> javaClass.error(screenName, th, "containsWordsIsType failed") }
            .inViewModel()
    }

    fun onSwipeEdit(item: WordType) {
        _action.tryEmit(ChooseTypeAction.OpenEditWordTypeDialog(item))
    }

    fun onDialogDeleteClick(wordType: WordType) {
        hardDeleteWordType(wordType)
    }

    fun onDialogCancelClick(wordType: WordType) {
        _action.tryEmit(
            ChooseTypeAction.Reload(
                _wordTypesState.value?.indexOfOrNull(wordType) ?: return
            )
        )
    }

    private fun hardDeleteWordType(wordType: WordType) {
        wordTypeUseCase.delete(wordType)
            .onIO()
            .onEach { draftWordEvents.onWordTypeChanged(wordType.copy(isSelected = false)) }
            .catch { th -> javaClass.error(screenName, th, "WordType delete failed") }
            .inViewModel()
    }
}
