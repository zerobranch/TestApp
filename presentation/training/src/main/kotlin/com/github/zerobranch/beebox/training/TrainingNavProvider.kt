package com.github.zerobranch.beebox.training

import com.github.zerobranch.beebox.commons_app.base.NavCommand

interface TrainingNavProvider {
    val toEditWordScreen: NavCommand
    val toLocalSearchScreen: NavCommand
}