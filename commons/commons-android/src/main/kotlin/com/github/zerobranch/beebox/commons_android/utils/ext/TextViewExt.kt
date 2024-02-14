package com.github.zerobranch.beebox.commons_android.utils.ext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.github.zerobranch.beebox.commons_android.utils.PaddingBackgroundColorSpan

fun TextView.setTextColorList(@ColorRes color: Int) {
    setTextColor(ContextCompat.getColorStateList(context, color))
}

fun TextView.setTextOrHide(text: CharSequence?) {
    isVisible = !text.isNullOrBlank()
    setText(text)
}

fun TextView.setPaddingBackgroundColor(str: String, @ColorRes color: Int, @DimenRes paddingRes: Int) {
    val padding = context.resources.getDimensionPixelSize(paddingRes)
    val background = ContextCompat.getColor(context, color)
    val spannable = SpannableString(str)

    spannable.setSpan(
        PaddingBackgroundColorSpan(background, padding),
        0,
        str.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    setShadowLayer(padding.toFloat(), 0f, 0f, 0)
    setPadding(padding, padding, 0, padding)
    includeFontPadding = false
    text = spannable
}

fun TextView.setDrawables(
    @DrawableRes left: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes right: Int = 0,
    @DrawableRes bottom: Int = 0
) = setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)

fun TextView.drawableTint(@ColorRes color: Int) =
    TextViewCompat.setCompoundDrawableTintList(
        this,
        ContextCompat.getColorStateList(context, color)
    )

fun TextView.setDrawables(
    left: Drawable? = null,
    top: Drawable? = null,
    right: Drawable? = null,
    bottom: Drawable? = null
) = setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)

fun Drawable.withTint(
    context: Context,
    @ColorRes color: Int,
    @IntRange(from = 0, to = 255) alpha: Int? = null
): Drawable = DrawableCompat.wrap(this).mutate().apply {
    DrawableCompat.setTint(this, ContextCompat.getColor(context, color))
    alpha?.let { this.alpha = alpha }
}

fun View.animateOnPress() {
    this.animate()
        .scaleX(0.85f)
        .scaleY(0.85f)
        .setDuration(75L)
        .start()
}

fun View.animateOnRelease() {
    this.animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(75L)
        .start()
}

@SuppressLint("ClickableViewAccessibility")
fun View.setScaleClick(action: (() -> Unit)? = null) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> animateOnPress()
            MotionEvent.ACTION_CANCEL -> animateOnRelease()
            MotionEvent.ACTION_UP -> {
                animateOnRelease()
                action?.invoke()
            }

            else -> {}
        }
        return@setOnTouchListener true
    }
}

fun AppCompatTextView.textSizeRes(@DimenRes sizeResId: Int) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, toPx(sizeResId).toFloat())
}

fun AppCompatTextView.lineHeightRes(@DimenRes sizeResId: Int) {
    lineHeight = toPx(sizeResId)
}

fun TextView.setOrIgnore(newText: String?) {
    if (text?.toString() == newText) return
    text = newText
}

fun EditText.appendOrIgnore(newText: String?) {
    if (text?.toString() == newText) return
    text = null
    append(newText)
}