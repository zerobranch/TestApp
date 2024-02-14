package com.github.zerobranch.beebox.recent_trainings_dialog

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining

interface RecentTrainingsNavProvider {
    fun toTrainingScreen(training: ProgressTraining): NavCommand
    fun toDeleteDialog(
        resultKey: String,
        progressTraining: ProgressTraining,
        title: String,
        actionBtnText: String,
        cancelBtnText: String
    ): NavCommand
}