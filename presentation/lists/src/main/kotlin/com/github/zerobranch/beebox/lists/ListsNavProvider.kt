package com.github.zerobranch.beebox.lists

import com.github.zerobranch.beebox.commons_app.base.NavCommand

interface ListsNavProvider {
    val toSearchScreen: NavCommand
    val toCreateCategoryDialog: NavCommand
    val toCreateWordTypeDialog: NavCommand
}