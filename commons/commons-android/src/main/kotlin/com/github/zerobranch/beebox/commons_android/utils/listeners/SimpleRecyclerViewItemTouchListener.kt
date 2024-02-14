package com.github.zerobranch.beebox.commons_android.utils.listeners

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

open class SimpleRecyclerViewItemTouchListener : RecyclerView.OnItemTouchListener {
    override fun onInterceptTouchEvent(rv: RecyclerView, ev: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
