package com.github.zerobranch.beebox.training_settings_dialog

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining

interface TrainingSettingsNavProvider {
    val toChooseCategoryScreen: NavCommand
    val toChooseWordTypeScreen: NavCommand
    fun toTrainingScreen(training: ProgressTraining): NavCommand
}