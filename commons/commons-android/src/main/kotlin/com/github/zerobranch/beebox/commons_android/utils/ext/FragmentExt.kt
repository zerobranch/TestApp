package com.github.zerobranch.beebox.commons_android.utils.ext

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

val Fragment.isTablet: Boolean
    get() = resources.isTablet

val Fragment.isLandscape: Boolean
    get() = resources.isLandscape

fun Fragment.toPx(@DimenRes dimenReId: Int) = resources.getDimensionPixelSize(dimenReId)

fun Fragment.colorInt(@ColorRes colorId: Int) = requireContext().colorInt(colorId)

fun Fragment.drawable(@DrawableRes drawableResId: Int): Drawable? =
    requireContext().drawable(drawableResId)

fun Fragment.getPlurals(
    @PluralsRes pluralsResId: Int,
    value: Int,
    formattedValue: Any? = null,
    @StringRes zeroValueStringResId: Int? = null
): String = requireContext().getPlurals(pluralsResId, value, formattedValue, zeroValueStringResId)

var Fragment.isAppearanceLightNavigationBars: Boolean
    get() {
        val activity = activity ?: return false
        val rootView = view ?: return false
        return WindowInsetsControllerCompat(
            activity.window,
            rootView
        ).isAppearanceLightNavigationBars
    }
    set(value) {
        val activity = activity ?: return
        val rootView = view ?: return
        WindowInsetsControllerCompat(
            activity.window,
            rootView
        ).isAppearanceLightNavigationBars = value
    }

var Fragment.isAppearanceLightStatusBars: Boolean
    get() {
        val activity = activity ?: return false
        val rootView = view ?: return false
        return WindowInsetsControllerCompat(
            activity.window,
            rootView
        ).isAppearanceLightStatusBars
    }
    set(value) {
        val activity = activity ?: return
        val rootView = view ?: return
        WindowInsetsControllerCompat(
            activity.window,
            rootView
        ).isAppearanceLightStatusBars = value
    }

val Fragment.packageName: String
    get() = requireContext().packageName

fun Fragment.changeOrientation() {
    requireActivity().requestedOrientation =
        if (isLandscape) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
}

fun Fragment.setPortrait() {
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun Fragment.removeFragment() = childFragmentManager.popBackStack()

fun Fragment.replaceFragment(container: Int, fragment: Fragment) {
    childFragmentManager.popBackStack()
    with(childFragmentManager.beginTransaction()) {
        replace(
            container,
            fragment,
            fragment::class.java.name
        )
        addToBackStack(fragment::class.java.name)
        commit()
    }
}

fun Fragment.hideSystemBars(autoClose: Boolean) {
    val activity = activity ?: return
    val rootView = view ?: return
    val controller = WindowInsetsControllerCompat(activity.window, rootView)
    controller.hide(WindowInsetsCompat.Type.systemBars())

    if (autoClose) {
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun Fragment.showSystemBars() {
    val activity = activity ?: return
    val rootView = view ?: return

    WindowInsetsControllerCompat(activity.window, rootView)
        .show(WindowInsetsCompat.Type.systemBars())

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun Fragment.setSystemBarsVisibility(visible: Boolean, autoClose: Boolean = true) {
    if (visible) {
        showSystemBars()
    } else {
        hideSystemBars(autoClose)
    }
}

fun Fragment.enableOrientationSensor() {
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
}

fun Fragment.disableOrientationSensor() {
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
}

fun Fragment.waitForTransition(targetView: View) {
    postponeEnterTransition()
    targetView.doOnPreDraw { startPostponedEnterTransition() }
}

fun Fragment.navigateToDetailsSettings() {
    requireActivity().navigateToDetailsSettings()
}

fun Fragment.registerActivityResults(
    activityResult: ActivityResultCallback<ActivityResult>
): ActivityResultLauncher<Intent> = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult(),
    activityResult
)

fun Fragment.share(
    text: String,
    targetPackage: String? = null,
    chooserTitle: String? = null,
    attachFiles: List<Uri> = emptyList()
) = requireContext().share(text, targetPackage, chooserTitle, attachFiles)

context(Fragment)
fun <T> Flow<T>.launchWhenCreated() {
    viewLifecycleOwner.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) { collect() }
    }
}

context(Fragment)
fun <T> Flow<T>.launchWhenStarted() {
    viewLifecycleOwner.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) { collect() }
    }
}

context(Fragment)
fun <T> Flow<T>.launchWhenResumed() {
    viewLifecycleOwner.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) { collect() }
    }
}

fun Fragment.longToast(@StringRes title: Int) {
    requireContext().longToast(title)
}

fun Fragment.shortToast(@StringRes title: Int) {
    requireContext().shortToast(title)
}

fun Fragment.safePost(action: () -> Unit) =
    view?.post {
        if (view == null) return@post
        else action.invoke()
    }

fun Fragment.safePostDelayed(
    delay: Long,
    action: () -> Unit
): Runnable? = view?.postDelayed(delay) {
    if (view == null) return@postDelayed
    else action.invoke()
}
