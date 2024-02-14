package com.github.zerobranch.beebox.commons_android.utils.ext

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt

val Int.dp: Int
    @JvmSynthetic inline get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()

val Float.dp: Float
    @JvmSynthetic inline get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this,
        Resources.getSystem().displayMetrics
    )

val Int.px: Int
    @JvmSynthetic inline get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX, this.toFloat(),
        Resources.getSystem().displayMetrics
    ).roundToInt()