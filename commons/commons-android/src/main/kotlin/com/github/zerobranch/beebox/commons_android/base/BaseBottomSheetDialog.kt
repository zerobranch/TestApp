package com.github.zerobranch.beebox.commons_android.base

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import com.github.zerobranch.beebox.commons_android.R
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.dp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import com.google.android.material.R as MaterialR

@Deprecated(message = "Don't override", level = DeprecationLevel.WARNING)
abstract class BaseBottomSheetDialog(
    @LayoutRes private val layoutRes: Int
) : BottomSheetDialogFragment() {
    private companion object {
        const val MIN_FOREGROUND_ALPHA = 0f
        const val MAX_FOREGROUND_ALPHA = 0.75f
        const val FOREGROUND_SHOWING_DURATION = 200L
    }

    protected val dialogDispatcher: DialogDispatcher? by lazy {
        parentFragmentManager.fragments.let {
            (it.getOrNull(it.size - 2) as? DialogDispatcher)
        }
    }

    protected open val isFullScreen: Boolean = false
    protected open val withMargin: Boolean = false
    protected open val isExpanded: Boolean = false
    protected open val isCancelableMode: Boolean = true

    protected open val shadowOffsetY: Int = 12
    protected open val rootElevation: Float = 25F
    protected open val horizontalMargin: Int = 5
    protected open val dialogTopOffset: Int = 90.dp

    protected open val tintInterpolator: TimeInterpolator by lazy(LazyThreadSafetyMode.NONE) {
        LinearInterpolator()
    }

    protected open val logger: ((msg: String) -> Unit)
        get() = {}

    protected val bottomSheetDialog: BottomSheetDialog
        get() = (dialog as BottomSheetDialog)

    protected val behavior: BottomSheetBehavior<FrameLayout>
        get() = bottomSheetDialog.behavior

    protected val bottomSheetLayout: FrameLayout?
        get() = bottomSheetDialog.findViewById(MaterialR.id.design_bottom_sheet)

    protected val decorView: View?
        get() = activity?.window?.decorView

    protected val isViewDestroyed: Boolean
        get() = view == null

    private var tintAnimator: ValueAnimator? = null
    private val isRootDialog by lazy { decorView?.foreground == null }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (isFullScreen && newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            val alphaRange = MAX_FOREGROUND_ALPHA - MIN_FOREGROUND_ALPHA
            tintAnimator?.cancel()

            val alpha =
                if (slideOffset < 0) (1 - slideOffset.absoluteValue) * alphaRange else alphaRange
            updateAlpha(min(max((MIN_FOREGROUND_ALPHA + alpha), 0F), 1F))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger.invoke("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.invoke("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutRes, container, false)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        object : BottomSheetDialog(requireContext(), theme) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()
                dismissWithAnimation = true

                setCancellableMode()
                setExpand()
                setFullScreen()
                initNavigationBar(this)
                setMargin()
                setForeground()
                setShadowColor()
                setBottomSheetCallback()
                setTint()
                setupDialogBehavior()
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
        behavior.removeBottomSheetCallback(bottomSheetCallback)

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

    protected fun onDispatchResume() {
        dialogDispatcher?.onDialogResume(tag)
    }

    protected fun onDispatchDismiss() {
        dialogDispatcher?.onDialogDismiss(tag)
    }

    protected fun onDispatchCancel() {
        dialogDispatcher?.onDialogCancel(tag)
    }

    protected open fun setupDialogBehavior() = with(behavior) {
        if (isFullScreen) {
            skipCollapsed = true
            isFitToContents = false
            expandedOffset = dialogTopOffset
        }
    }

    protected open fun hideKeyboard() {
        view?.clearFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(
            view?.rootView?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    protected open fun setTint() {
        tintAnimator = ValueAnimator.ofFloat(
            MIN_FOREGROUND_ALPHA,
            MAX_FOREGROUND_ALPHA
        ).apply {
            duration = FOREGROUND_SHOWING_DURATION
            interpolator = tintInterpolator
            addUpdateListener { updateAlpha(min(it.animatedValue as Float, 1F)) }
            start()
        }
    }

    protected open fun initNavigationBar(dialog: BottomSheetDialog) = with(dialog) {
        window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            it.navigationBarColor = colorInt(R.color.utils_android_almost_transparent)
        }

        findViewById<View>(MaterialR.id.container)?.fitsSystemWindows = false
        findViewById<View>(MaterialR.id.coordinator)?.fitsSystemWindows = false
    }

    protected open fun setExpand() {
        if (isExpanded) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    protected open fun setFullScreen() {
        if (isFullScreen) {
            bottomSheetLayout?.layoutParams?.height = MATCH_PARENT
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    protected open fun setCancellableMode() {
        isCancelable = isCancelableMode
        behavior.isDraggable = isCancelableMode
    }

    protected open fun setBottomSheetCallback() {
        behavior.addBottomSheetCallback(bottomSheetCallback)
    }

    protected open fun updateAlpha(alpha: Float) {
        if (isRootDialog) {
            decorView?.foreground?.alpha = (alpha * 255F).toInt()
        }
    }

    protected open fun setMargin() {
        if (withMargin) {
            setHorizontalMargins()
        }
    }

    protected open fun setHorizontalMargins() {
        bottomSheetLayout?.apply {
            val lp = layoutParams as ViewGroup.MarginLayoutParams
            lp.marginStart = horizontalMargin
            lp.marginEnd = horizontalMargin
            layoutParams = lp
        }
    }

    protected open fun setForeground() {
        if (isRootDialog) {
            decorView?.foreground = createForegroundImage()?.toDrawable(resources)
        }
    }

    protected open fun setShadowColor() {}

    protected open fun createForegroundImage(): Bitmap? = null
}
