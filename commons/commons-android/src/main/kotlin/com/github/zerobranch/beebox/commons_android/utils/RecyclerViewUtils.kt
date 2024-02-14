package com.github.zerobranch.beebox.commons_android.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

object RecyclerViewUtils {

    /**
     * Reduces drag sensitivity of [RecyclerView] widget
     */
    fun RecyclerView.reduceDragSensitivity() {
        runCatching {
            val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(this) as Int
            touchSlopField.set(this, touchSlop * 8)     // "8" was obtained experimentally
        }
    }

    /**
     * Increase drag sensitivity of [RecyclerView] widget
     */
    fun RecyclerView.increaseDragSensitivity() {
        runCatching {
            val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(this) as Int
            touchSlopField.set(this, touchSlop / 8)     // "8" was obtained experimentally
        }
    }

    /**
     * Increase drag sensitivity of [ViewPager2] widget
     */
    fun ViewPager2.increaseDragSensitivity() {
        runCatching {
            val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView = recyclerViewField.get(this) as RecyclerView

            val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(recyclerView) as Int
            touchSlopField.set(recyclerView, touchSlop / 16) // "8" was obtained experimentally
        }
    }
}