package com.github.zerobranch.beebox.commons_app.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.github.zerobranch.beebox.commons_android.base.BaseFragment
import com.github.zerobranch.beebox.commons_android.utils.ext.waitForTransition
import com.github.zerobranch.beebox.logging.info
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

abstract class BaseMainFragment(@LayoutRes layoutRes: Int) : BaseFragment(layoutRes) {
    private companion object {
        const val TAG = "FragmentLifecycle"
    }

    override val logger: (String) -> Unit
        get() = { msg -> javaClass.info(TAG, msg) }

    override val getParentFragmentManager: FragmentManager
        get() = parentFragmentManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        waitForTransition(view)
        initUi()
        observeOnStates()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exit()
    }

    protected open fun initUi() {}

    protected open fun observeOnStates() {}

    protected open fun navigate(
        navCommand: ActivityNavCommand,
        withFinish: Boolean = false
    ) {
        startActivity(
            Intent(requireContext(), navCommand.cls)
                .apply {
                    navCommand.args?.let { args -> putExtras(args) }
                }
        )

        if (withFinish) {
            requireActivity().finish()
        }
    }

    protected open fun navigate(navCommand: NavCommand) {
        findNavController().navigate(navCommand.action, navCommand.args, navCommand.navOptions)
    }

    protected fun exit() {
        findNavController().popBackStack()
    }

    protected fun <T : Any> getNavResult(key: String): Flow<T>? =
        findNavController()
            .currentBackStackEntry?.savedStateHandle?.getStateFlow<T?>(key, null)
            ?.filterNotNull()

    protected fun <T : Any> setNavResult(key: String, result: T?) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, result)
    }

    protected open fun showNavigationBar(isSmooth: Boolean = true) {
        (requireActivity() as? Paggable)?.showNavigationBar(isSmooth)
    }

    protected open fun hideNavigationBar(isSmooth: Boolean = true) {
        (requireActivity() as? Paggable)?.hideNavigationBar(isSmooth)
    }

    protected open fun setNavigationBarVisible(visible: Boolean, isSmooth: Boolean = true) {
        if (visible) {
            (requireActivity() as? Paggable)?.showNavigationBar(isSmooth)
        } else {
            (requireActivity() as? Paggable)?.hideNavigationBar(isSmooth)
        }
    }
}
