package com.github.zerobranch.beebox.commons_android.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

@Deprecated(message = "Don't override", level = DeprecationLevel.WARNING)
abstract class BaseFragment(@LayoutRes private val layoutRes: Int) : Fragment(layoutRes) {

    protected abstract val getParentFragmentManager: FragmentManager

    protected open val logger: ((msg: String) -> Unit)
        get() = {}

    protected val isViewDestroyed: Boolean
        get() = view == null

    protected var firstTimeCreated: Boolean = true

    protected var wasRecreated: Boolean = false

    protected val decorView: ViewGroup
        get() = requireActivity().window.decorView as ViewGroup

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger.invoke("onAttach")
        requireActivity().onBackPressedDispatcher.addCallback(this) { handleBackPressed() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        firstTimeCreated = savedInstanceState == null
        wasRecreated = !firstTimeCreated

        super.onCreate(savedInstanceState)
        logger.invoke("onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.invoke("onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        logger.invoke("onStart")
    }

    override fun onResume() {
        super.onResume()
        logger.invoke("onResume")
    }

    override fun onPause() {
        super.onPause()
        logger.invoke("onPause")
    }

    override fun onStop() {
        super.onStop()
        logger.invoke("onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.invoke("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.invoke("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logger.invoke("onDetach")
    }

    protected open fun onBackPressed() {
        logger.invoke("onBackPressed")
    }

    protected fun firstTimeCreated(savedInstanceState: Bundle?) = savedInstanceState == null

    protected fun wasRecreated(savedInstanceState: Bundle?) = savedInstanceState != null

    private fun handleBackPressed() {
        if (getParentFragmentManager.backStackEntryCount > 0) {
            onBackPressed()
        } else {
            requireActivity().finish()
        }
    }
}