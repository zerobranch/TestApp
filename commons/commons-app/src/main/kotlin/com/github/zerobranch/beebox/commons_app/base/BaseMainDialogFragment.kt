package com.github.zerobranch.beebox.commons_app.base

import android.animation.TimeInterpolator
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import com.github.zerobranch.beebox.commons_android.base.BaseDialogFragment
import com.github.zerobranch.beebox.commons_android.utils.CubicBezierInterpolator
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_app.R
import com.github.zerobranch.beebox.logging.info
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

abstract class BaseMainDialogFragment(@LayoutRes layoutRes: Int) : BaseDialogFragment(layoutRes) {
    private companion object {
        const val TAG = "DialogLifecycle"
    }

    override val tintInterpolator: TimeInterpolator by lazy(LazyThreadSafetyMode.NONE) {
        CubicBezierInterpolator.EASE_IN_OUT_QUAD
    }

    override val logger: (String) -> Unit
        get() = { msg -> javaClass.info(TAG, msg) }

    override fun getTheme(): Int = R.style.CommonBaseAlertDialogTheme

    override fun createForegroundImage(): Bitmap {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(colorInt(R.color.common_dark_carbone))
        return bitmap
    }

    protected fun navigate(navCommand: NavCommand) {
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
}