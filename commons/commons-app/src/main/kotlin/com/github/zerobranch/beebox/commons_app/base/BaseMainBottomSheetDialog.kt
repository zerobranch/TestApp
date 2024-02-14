package com.github.zerobranch.beebox.commons_app.base

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.animation.Interpolator
import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import com.github.zerobranch.beebox.commons_android.base.BaseBottomSheetDialog
import com.github.zerobranch.beebox.commons_android.utils.CubicBezierInterpolator
import com.github.zerobranch.beebox.commons_android.utils.CustomOutlineProvider
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.dp
import com.github.zerobranch.beebox.commons_android.utils.ext.isLandscape
import com.github.zerobranch.beebox.commons_android.utils.ext.isTablet
import com.github.zerobranch.beebox.commons_android.utils.ext.screenWidth
import com.github.zerobranch.beebox.commons_app.R
import com.github.zerobranch.beebox.logging.info
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

abstract class BaseMainBottomSheetDialog(
    @LayoutRes layoutRes: Int
) : BaseBottomSheetDialog(layoutRes) {
    private companion object {
        const val TAG = "BottomSheetLifecycle"
    }

    override val shadowOffsetY: Int by lazy(LazyThreadSafetyMode.NONE) { 12.dp }
    override val rootElevation: Float by lazy(LazyThreadSafetyMode.NONE) { 25.dp.toFloat() }

    override val tintInterpolator: Interpolator by lazy(LazyThreadSafetyMode.NONE) {
        CubicBezierInterpolator.EASE_IN_OUT_QUAD
    }

    override val horizontalMargin: Int by lazy(LazyThreadSafetyMode.NONE) {
        val screenWidth = requireContext().screenWidth
        when {
            isTablet && isLandscape -> (screenWidth * 0.25).toInt()
            isTablet -> (screenWidth * 0.1).toInt()
            else -> 5.dp
        }
    }

    override val logger: (String) -> Unit
        get() = { msg -> javaClass.info(TAG, msg) }

    override fun getTheme(): Int = R.style.CommonBaseBottomSheetDialogTheme_Small

    override fun setShadowColor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return
        bottomSheetLayout?.apply {
            elevation = rootElevation
            outlineProvider = CustomOutlineProvider(
                yShift = -shadowOffsetY,
                cornerRadius = R.dimen.common_spacing_16
            )
            outlineAmbientShadowColor = colorInt(R.color.common_primary)
            outlineSpotShadowColor = colorInt(R.color.common_primary)
        }
    }

    override fun createForegroundImage(): Bitmap? {
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