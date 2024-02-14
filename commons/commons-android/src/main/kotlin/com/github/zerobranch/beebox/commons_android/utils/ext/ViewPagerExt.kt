package com.github.zerobranch.beebox.commons_android.utils.ext

import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

fun Fragment.currentViewPagerFragment(currentItem: Int): Fragment? {
    return runCatching { childFragmentManager.findFragmentByTag("f$currentItem") }.getOrNull()
}

fun ViewPager2.fakeDrag(forward: Boolean, duration: Long = 400L) {
    var prevDragPosition = 0
    if (prevDragPosition == 0 && beginFakeDrag()) {
        ValueAnimator.ofInt(0, width).apply {
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
            val onFinishFakeDragging = {
                removeAllUpdateListeners()
                if (isFakeDragging) endFakeDrag()
                prevDragPosition = 0
            }
            doOnEnd { onFinishFakeDragging.invoke() }
            doOnCancel { onFinishFakeDragging.invoke() }

            addUpdateListener { animator ->
                if (isFakeDragging.not()) return@addUpdateListener
                val dragPosition: Int = animator.animatedValue.toString().toInt()
                val dragOffset: Float = (dragPosition - prevDragPosition) * if (forward) -1f else 1f
                prevDragPosition = dragPosition
                fakeDragBy(dragOffset)
            }
        }.start()
    }
}

fun ViewPager2.registerOnPageChangeCallback(
    onPageSelected: ((position: Int) -> Unit)? = null,
    onPageScrollStateChanged: ((state: Int) -> Unit)? = null,
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onPageSelected?.invoke(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            onPageScrollStateChanged?.invoke(state)
        }
    })
}

fun ViewPager2.addBottomPagingListener(
    visibleThreshold: Int = 5,
    action: () -> Unit
) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val itemCount = adapter?.itemCount ?: return

            if (position > itemCount - visibleThreshold) {
                action.invoke()
            }
        }
    })
}

fun ViewPager2.disableNestedScrolling() {
    children.forEach {
        (it as? RecyclerView)?.apply {
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
    }
}