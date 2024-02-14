package com.github.zerobranch.beebox.commons_app.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController

abstract class BaseViewPagerFragment(@LayoutRes layoutRes: Int) : BaseMainFragment(layoutRes) {

    override val getParentFragmentManager: FragmentManager
        get() = requireParentFragment().parentFragmentManager

    override fun navigate(navCommand: NavCommand) {
        requireParentFragment()
            .findNavController()
            .navigate(navCommand.action, navCommand.args, navCommand.navOptions)
    }
}
