package com.github.zerobranch.beebox.commons_android.utils.ext

import android.view.MotionEvent
import kotlin.math.abs

fun MotionEvent.isMoveTop(oldY: Float, touchSlop: Int) = y - oldY > touchSlop

fun MotionEvent.isMoveBottom(oldY: Float, touchSlop: Int) = oldY - y > touchSlop

val MotionEvent.isClickDelay: Boolean
    get() {
        val defaultClickDelay = 350
        return (eventTime - downTime) <= defaultClickDelay
    }

fun MotionEvent.xDiff(touchX: Float): Int = abs(touchX - x).toInt()

fun MotionEvent.yDiff(touchY: Float): Int = abs(touchY - y).toInt()
