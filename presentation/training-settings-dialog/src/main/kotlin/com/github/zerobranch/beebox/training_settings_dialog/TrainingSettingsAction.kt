package com.github.zerobranch.beebox.training_settings_dialog

import com.github.zerobranch.beebox.domain.models.training.ProgressTraining

sealed interface TrainingSettingsAction {
    data object GoToChooseCategory : TrainingSettingsAction
    data object GoToChooseWordType : TrainingSettingsAction
    data class GoToTraining(val training: ProgressTraining) : TrainingSettingsAction
    data object Exit : TrainingSettingsAction
}