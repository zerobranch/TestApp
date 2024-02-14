package com.github.zerobranch.beebox.recent_trainings_dialog

import androidx.lifecycle.viewModelScope
import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.ext.indexOfOrNull
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.domain.usecase.TrainingUseCase
import com.github.zerobranch.beebox.logging.wtf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecentTrainingsViewModel @Inject constructor(
    private val trainingUseCase: TrainingUseCase
) : BaseMainViewModel() {

    override val screenName: String = "RecentTrainings"

    private val _action = OnceFlow<RecentTrainingsAction>()
    val action = _action.asSharedFlow()

    private val _uiState = flow { emitAll(trainingUseCase.getProgressTrainings()) }
        .onIO()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    val uiState = _uiState.filterNotNull()

    fun onCloseClick() {
        _action.tryEmit(RecentTrainingsAction.Exit)
    }

    fun onRecentTrainingClick(progressTraining: ProgressTraining) {
        _action.tryEmit(RecentTrainingsAction.GoToTraining(progressTraining))
    }

    fun onSwipeDelete(training: ProgressTraining) {
        _action.tryEmit(RecentTrainingsAction.OpenDeleteDialog(training))
    }

    fun onDialogDeleteClick(training: ProgressTraining) {
        trainingUseCase.deleteTraining(training.id)
            .onIO()
            .catch { th -> javaClass.wtf(screenName, th, "create word failed") }
            .inViewModel()
    }

    fun onDialogCancelClick(progressTraining: ProgressTraining) {
        _action.tryEmit(
            RecentTrainingsAction.Reload(
                _uiState.value?.indexOfOrNull(progressTraining) ?: return
            )
        )
    }
}