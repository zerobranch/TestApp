package com.github.zerobranch.beebox.training

sealed interface TrainingAction {
    data object OpenEditWordDialog : TrainingAction
}