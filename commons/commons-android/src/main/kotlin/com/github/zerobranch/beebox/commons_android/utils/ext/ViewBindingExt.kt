package com.github.zerobranch.beebox.commons_android.utils.ext

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FractionRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding

val ViewBinding.context: Context
    get() = root.context

val ViewBinding.isTablet: Boolean
    get() = root.resources.isTablet

val ViewBinding.resources: Resources
    get() = root.resources

fun ViewBinding.getFraction(@FractionRes fractionResId: Int): Float = root.getFraction(fractionResId)

fun ViewBinding.toPx(@DimenRes dimenReId: Int) = root.toPx(dimenReId)

fun ViewBinding.colorInt(@ColorRes colorId: Int): Int = root.colorInt(colorId)

fun ViewBinding.drawable(@DrawableRes drawableResId: Int): Drawable? = root.drawable(drawableResId)

fun ViewBinding.getString(@StringRes stringResId: Int): String = root.context.getString(stringResId)

fun ViewBinding.getString(@StringRes stringResId: Int, vararg formatArgs: Any): String =
    root.context.getString(stringResId, *formatArgs)

fun ViewBinding.getStringArray(@ArrayRes arrayResId: Int): Array<String> =
    root.resources.getStringArray(arrayResId)

fun ViewBinding.getPlurals(
    @PluralsRes pluralsResId: Int,
    value: Int,
    formattedValue: Any? = null,
    @StringRes zeroValueStringResId: Int? = null
): String = root.context.getPlurals(pluralsResId, value, formattedValue, zeroValueStringResId)
