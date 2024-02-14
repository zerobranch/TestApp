package com.github.zerobranch.beebox.commons_android.utils.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

val Activity.rootContent: ViewGroup
    get() = window.findViewById(android.R.id.content)

val Activity.isLandscape: Boolean
    get() = resources.isLandscape

val Activity.isTablet: Boolean
    get() = resources.isTablet

fun Activity?.closeKeyboard() {
    this?.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}

val Activity.mediaVolume
    get() = (getSystemService(Context.AUDIO_SERVICE) as? AudioManager)
        ?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0

fun FragmentActivity.hideKeyboard() = currentFocus?.hideKeyboard()

fun FragmentActivity.keepScreenOn(keep: Boolean) {
    if (keep) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Suppress("DEPRECATION")
val Activity.rotation: Int
    get() = with(this) {
        val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.rotation
        } else {
            windowManager.defaultDisplay.rotation
        }
        rotation ?: 0
    }

context(FragmentActivity)
val WindowInsetsCompat.isGestureNavigationBar: Boolean
    get() {
        val height = if (isLandscape) screenWidth else screenHeight
        return navigationBarHeight.toFloat() / height <= 0.03
    }

fun Activity.navigateToDetailsSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
    )
}

context(FragmentActivity)
fun <T> Flow<T>.launchWhenCreated() {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) { collect() }
    }
}

context(FragmentActivity)
fun <T> Flow<T>.launchWhenStarted() {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) { collect() }
    }
}

context(FragmentActivity)
fun <T> Flow<T>.launchWhenResumed() {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) { collect() }
    }
}

fun FragmentActivity.registerActivityResults(
    activityResult: ActivityResultCallback<ActivityResult>
): ActivityResultLauncher<Intent> = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult(),
    activityResult
)

fun Activity.enableSecureScreen() {
    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
}

fun Activity.disableSecureScreen() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
}