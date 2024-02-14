package com.github.zerobranch.beebox.home

import com.github.zerobranch.beebox.domain.models.TrainingType

sealed interface HomeState {
    data object RecentTraining : HomeState
    data class Training(val training: TrainingType) : HomeState
}