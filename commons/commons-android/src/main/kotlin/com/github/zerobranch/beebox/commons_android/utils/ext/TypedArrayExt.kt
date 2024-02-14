package com.github.zerobranch.beebox.commons_android.utils.ext

import android.content.res.TypedArray

@JvmSynthetic
fun TypedArray.getIntArray(resourceId: Int, default: IntArray): IntArray {
    return getResourceId(resourceId, -1).let {
        if (it >= 0) resources.getIntArray(it) else default
    }
}

@JvmSynthetic
inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T): T {
    return getInt(index, -1).let {
        if (it >= 0) enumValues<T>()[it] else default
    }
}

val IntArray.arrayGradientPositions: FloatArray
    @JvmSynthetic inline get() {
        val positions = arrayListOf(0f)
        val interval = 1f / this.size
        for (i in 1 until size) {
            positions.add(positions[i - 1] + interval)
        }
        return positions.toFloatArray()
    }