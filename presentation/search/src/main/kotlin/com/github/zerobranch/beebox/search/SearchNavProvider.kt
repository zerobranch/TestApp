package com.github.zerobranch.beebox.search

import com.github.zerobranch.beebox.commons_app.base.NavCommand
import com.github.zerobranch.beebox.domain.models.words.DraftWord

interface SearchNavProvider {
    val toAddWordScreen: NavCommand
    val toEditWordScreen: NavCommand
    fun toWordGroupScreen(draftWord: List<DraftWord>): NavCommand
}