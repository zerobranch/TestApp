package com.github.zerobranch.beebox.commons_android.utils.listeners

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class OnViewPagerPageChange(
    private val onPageScrollStateChanged: ((Int, Boolean) -> (Unit))? = null,
    private val onPageSelected: ((Int, Boolean) -> (Unit))? = null,
    private val onPageScrolled: ((Int, Float, Int) -> (Unit))? = null
) : ViewPager2.OnPageChangeCallback() {
    private var previousState: Int = ViewPager.SCROLL_STATE_IDLE
    private var isUserScroll: Boolean = false

    override fun onPageScrollStateChanged(state: Int) {
        if (previousState == ViewPager.SCROLL_STATE_DRAGGING && state == ViewPager.SCROLL_STATE_SETTLING) {
            isUserScroll = true
        } else if (previousState == ViewPager.SCROLL_STATE_SETTLING && state == ViewPager.SCROLL_STATE_IDLE) {
            isUserScroll = false
        }

        previousState = state

        onPageScrollStateChanged?.invoke(state, isUserScroll)
    }

    override fun onPageSelected(position: Int) {
        onPageSelected?.invoke(position, isUserScroll)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
    }
}