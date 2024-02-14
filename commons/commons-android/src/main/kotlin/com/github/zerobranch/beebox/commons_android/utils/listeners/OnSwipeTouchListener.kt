package com.github.zerobranch.beebox.commons_android.utils.listeners

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.github.zerobranch.beebox.commons_android.utils.ext.isClick
import com.github.zerobranch.beebox.commons_android.utils.ext.isClickDelay
import com.github.zerobranch.beebox.commons_android.utils.ext.xDiff
import com.github.zerobranch.beebox.commons_android.utils.ext.yDiff
import kotlin.math.abs

open class OnSwipeTouchListener(context: Context) : View.OnTouchListener {

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    private val gestureDetector: GestureDetector
    private var startX: Float = 0f
    private var startY: Float = 0f
    private val viewConfiguration by lazy(LazyThreadSafetyMode.NONE) { ViewConfiguration.get(context) }

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        view ?: return false
        event ?: return false

        onTouchView(view, event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_UP -> {
                if (viewConfiguration.isClick(event.xDiff(startX), event.yDiff(startY)) && event.isClickDelay) {
                    onClick(view)
                }
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            e1 ?: return false
            var result = false
            try {
                val diffY = e2.y - e1.y
                if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) onSwipeBottom()
                    else onSwipeTop()
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }

        override fun onLongPress(e: MotionEvent) {
            onLongPress()
        }
    }

    open fun onClick(view: View) {}

    open fun onLongPress() {}

    open fun onTouchView(view: View, event: MotionEvent): Boolean {
        return false
    }

    open fun onSwipeTop() {}

    open fun onSwipeBottom() {}
}