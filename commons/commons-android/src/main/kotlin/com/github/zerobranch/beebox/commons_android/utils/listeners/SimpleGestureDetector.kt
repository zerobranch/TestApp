package com.github.zerobranch.beebox.commons_android.utils.listeners

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent

class SimpleGestureDetector(
    context: Context,
    private val onSingleTap: () -> Unit = {},
    private val onLongPress: () -> Unit = {},
    private val onDoubleTap: () -> Unit = {}
) : GestureDetector.SimpleOnGestureListener() {
    private val gestureDetector = GestureDetector(context, this)

    override fun onLongPress(e: MotionEvent) {
        onLongPress.invoke()
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        onDoubleTap.invoke()
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        onSingleTap.invoke()
        return false
    }

    fun onTouchEvent(motionEvent: MotionEvent): Boolean = gestureDetector.onTouchEvent(motionEvent)
}
