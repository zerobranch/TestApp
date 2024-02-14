package com.github.zerobranch.beebox.lists

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.zerobranch.beebox.lists.page.category.CategoriesPageFragment
import com.github.zerobranch.beebox.lists.page.type.WordTypesPageFragment
import com.github.zerobranch.beebox.lists.page.word.WordsPageFragment

class ListsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> WordsPageFragment()
            1 -> CategoriesPageFragment()
            else -> WordTypesPageFragment()
        }
}