package com.github.zerobranch.beebox.commons_android.base

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.LayoutRes
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import kotlin.math.min

@Deprecated(message = "Don't override", level = DeprecationLevel.WARNING)
abstract class BaseDialogFragment(@LayoutRes layoutRes: Int) : DialogFragment(layoutRes) {
    private companion object {
        const val MIN_FOREGROUND_ALPHA = 0.4f
        const val MAX_FOREGROUND_ALPHA = 0.75f
        const val FOREGROUND_SHOWING_DURATION = 200L
    }

    protected open val logger: ((msg: String) -> Unit)
        get() = {}

    protected open val tintInterpolator: TimeInterpolator by lazy(LazyThreadSafetyMode.NONE) {
        LinearInterpolator()
    }

    protected val decorView: View?
        get() = activity?.window?.decorView

    protected val isViewDestroyed: Boolean
        get() = view == null

    protected val dialogDispatcher: DialogDispatcher? by lazy {
        parentFragmentManager.fragments.let {
            (it.getOrNull(it.size - 2) as? DialogDispatcher)
        }
    }

    private var tintAnimator: ValueAnimator? = null
    private val isRootDialog by lazy { decorView?.foreground == null }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger.invoke("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.invoke("onCreate")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        object : Dialog(requireContext(), theme) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                setForeground()
                setTint()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.invoke("onViewCreated")
        initUi()
        observeOnStates()
    }

    protected open fun initUi() {}

    protected open fun observeOnStates() {}

    override fun onStart() {
        super.onStart()
        logger.invoke("onStart")
    }

    override fun onResume() {
        super.onResume()
        logger.invoke("onResume")
        onDispatchResume()
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

        tintAnimator?.cancel()
        tintAnimator?.removeAllUpdateListeners()
        tintAnimator = null

        if (isRootDialog) {
            decorView?.foreground = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.invoke("onDestroy")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        logger.invoke("onDismiss")
        onDispatchDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        logger.invoke("onCancel")
        onDispatchCancel()
    }

    protected open fun onDispatchResume() {
        dialogDispatcher?.onDialogResume(tag)
    }

    protected open fun onDispatchDismiss() {
        dialogDispatcher?.onDialogDismiss(tag)
    }

    protected open fun onDispatchCancel() {
        dialogDispatcher?.onDialogCancel(tag)
    }

    protected open fun setTint() {
        tintAnimator = ValueAnimator.ofFloat(MIN_FOREGROUND_ALPHA, MAX_FOREGROUND_ALPHA).apply {
            duration = FOREGROUND_SHOWING_DURATION
            interpolator = tintInterpolator
            addUpdateListener { updateAlpha(min(it.animatedValue as Float, 1F)) }
            start()
        }
    }

    protected open fun setForeground() {
        if (isRootDialog) {
            decorView?.foreground = createForegroundImage()?.toDrawable(resources)
        }
    }

    protected open fun updateAlpha(alpha: Float) {
        decorView?.foreground?.alpha = (alpha * 255F).toInt()
    }

    protected open fun createForegroundImage(): Bitmap? = null

    protected fun firstTimeCreated(savedInstanceState: Bundle?) = savedInstanceState == null

    protected fun wasRecreated(savedInstanceState: Bundle?) = savedInstanceState != null
}