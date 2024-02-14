package com.github.zerobranch.beebox.add_word_dialog

import com.github.zerobranch.beebox.commons_app.base.NavCommand

interface AddWordNavProvider {
    val toChooseCategoryScreen: NavCommand
    val toChooseWordTypeScreen: NavCommand
    val toLocalSearchScreen: NavCommand
}