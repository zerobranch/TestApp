package com.github.zerobranch.beebox.commons_view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import java.util.Locale
import kotlin.math.roundToInt

internal val Int.dp: Int
    @JvmSynthetic inline get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()

internal val Float.dp: Float
    @JvmSynthetic inline get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this,
        Resources.getSystem().displayMetrics
    )

internal fun TextView.setTextOrHide(text: CharSequence?) {
    isVisible = !text.isNullOrBlank()
    setText(text)
}

internal fun Context.colorInt(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

@JvmSynthetic
internal inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T): T =
    getInt(index, -1).let { if (it >= 0) enumValues<T>()[it] else default }

internal fun View.expand(
    duration: Long = 200,
    startDelay: Long = 0,
    withEndAction: Runnable? = null
) = animate()
    .setStartDelay(startDelay)
    .scaleX(1f)
    .scaleY(1f)
    .setDuration(duration)
    .withEndAction(withEndAction)
    .start()

internal fun View.collapse(
    duration: Long = 200,
    startDelay: Long = 0,
    withEndAction: Runnable? = null
) = animate()
    .setStartDelay(startDelay)
    .scaleX(0f)
    .scaleY(0f)
    .setDuration(duration)
    .withEndAction(withEndAction)
    .start()

@ColorInt
internal fun transparentColor(
    @ColorInt colorId: Int,
    @FloatRange(from = 0.0, to = 1.0) transparent: Float
): Int {
    fun convert(@FloatRange(from = 0.0, to = 1.0) transparent: Float): String {
        val hexString = Integer.toHexString((255 * transparent).roundToInt())
        return (if (hexString.length < 2) "0" else "") + hexString
    }

    val color = Integer.toHexString(colorId).uppercase(Locale.getDefault()).substring(2)
    return Color.parseColor(
        if (color.trim { it <= ' ' }.length == 6) {
            "#" + convert(transparent) + color
        } else {
            convert(transparent) + color
        }
    )
}

internal fun View.getActivity(): Activity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

internal val Activity.rootContent: ViewGroup
    get() = window.findViewById(android.R.id.content)
