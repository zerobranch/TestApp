package com.github.zerobranch.beebox.home

import com.github.zerobranch.beebox.domain.models.TrainingType

sealed interface HomeAction {
    data class GoToTrainingSettingsDialog(val trainingType: TrainingType) : HomeAction
    data object GoToRecentTrainingDialog : HomeAction
}