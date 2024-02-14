package com.github.zerobranch.beebox.training_settings_dialog

import androidx.lifecycle.viewModelScope
import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.domain.models.TrainingType
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.TrainingUseCase
import com.github.zerobranch.beebox.domain.usecase.WordUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@Suppress("OPT_IN_USAGE")
class TrainingSettingsViewModel @AssistedInject constructor(
    private val wordUseCase: WordUseCase,
    private val draftWordUseCase: DraftWordUseCase,
    private val trainingUseCase: TrainingUseCase,
    @Assisted private val trainingType: TrainingType
) : BaseMainViewModel() {

    override val screenName: String = "TrainingSettings"

    private val _action = OnceFlow<TrainingSettingsAction>()
    val action = _action.asSharedFlow()

    val uiState = flow {
        emitAll(
            draftWordUseCase.draftWordState.map {
                UiState(
                    wordTypes = it.wordTypes,
                    wordCategories = it.categories
                )
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null).filterNotNull()

    init {
        draftWordUseCase.reset()
    }

    override fun onCleared() {
        super.onCleared()
        draftWordUseCase.reset()
    }

    fun onWordTypeDeleteClick(wordType: WordType) {
        draftWordUseCase.onWordTypeChanged(wordType.copy(isSelected = false))
    }

    fun onCategoryDeleteClick(category: WordCategory) {
        draftWordUseCase.onCategoryChanged(category.copy(isSelected = false))
    }

    fun onAddWordCategoryClick() {
        _action.tryEmit(TrainingSettingsAction.GoToChooseCategory)
    }

    fun onAddWordTypeClick() {
        _action.tryEmit(TrainingSettingsAction.GoToChooseWordType)
    }

    fun onCloseClick() {
        _action.tryEmit(TrainingSettingsAction.Exit)
    }

    fun onLaunchClick() {
        wordUseCase
            .getAllByFilter(
                draftWordUseCase.draftWordState.value.categories,
                draftWordUseCase.draftWordState.value.wordTypes
            )
            .flatMapConcat { words ->
                trainingUseCase.createAndGetTraining(
                    mainTrainingType = trainingType,
                    words = words
                )
            }
            .onIO()
            .onEach { training ->
                _action.tryEmit(TrainingSettingsAction.GoToTraining(training))
            }
            .catch { th -> javaClass.error(screenName, th, "getAllByFilter failed") }
            .inViewModel()
    }
}