package com.github.zerobranch.beebox.recent_trainings_dialog

import com.github.zerobranch.beebox.domain.models.training.ProgressTraining

sealed interface RecentTrainingsAction {
    data object Exit : RecentTrainingsAction
    data class GoToTraining(val progressTraining: ProgressTraining) : RecentTrainingsAction
    data class OpenDeleteDialog(val progressTraining: ProgressTraining) : RecentTrainingsAction
    data class Reload(val position: Int) : RecentTrainingsAction
}