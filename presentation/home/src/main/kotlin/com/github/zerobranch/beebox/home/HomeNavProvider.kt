package com.github.zerobranch.beebox.home

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.TrainingType

interface HomeNavProvider {
    val toRecentTrainingsDialog: NavCommand
    fun toTrainingSettingsDialog(trainingType: TrainingType): NavCommand
}