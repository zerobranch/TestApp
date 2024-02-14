package com.github.zerobranch.beebox.training_settings_dialog

import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType

data class UiState(
    val wordTypes: List<WordType>,
    val wordCategories: List<WordCategory>
)