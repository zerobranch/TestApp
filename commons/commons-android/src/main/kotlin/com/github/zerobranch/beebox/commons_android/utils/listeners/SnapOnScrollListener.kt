package com.github.zerobranch.beebox.commons_android.utils.listeners

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.zerobranch.beebox.commons_android.utils.ext.getSnapPosition

class SnapOnScrollListener(
    private val snapHelper: SnapHelper,
    private val behavior: Behavior = Behavior.NOTIFY_ON_SCROLL,
    private val onlyWhenChangingPosition: Boolean = true,
    private val onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
) : RecyclerView.OnScrollListener() {
    private companion object {
        const val FIRST_VISIBLE_ITEM = 0
    }

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            maybeNotifySnapPositionChange(recyclerView)
        }
        if (dx > 0 || dx < 0) {
            onSnapPositionChangeListener?.onScroll()
        } else {
            scrollPositionStop(recyclerView)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            scrollPositionStop(recyclerView)

            if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE) {
                maybeNotifySnapPositionChange(recyclerView)
            }
        }
    }

    private fun scrollPositionStop(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        val view = layoutManager?.getChildAt(FIRST_VISIBLE_ITEM)

        val currentPosition = when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findFirstVisibleItemPosition()
            else -> RecyclerView.NO_POSITION
        }

        onSnapPositionChangeListener?.onScrollStop(currentPosition, view)
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)

        if (onlyWhenChangingPosition) {
            if (this.snapPosition != snapPosition) {
                onSnapPositionChangeListener?.onSnapPositionChange(snapPosition)
                this.snapPosition = snapPosition
            }
        } else {
            if (snapPosition != RecyclerView.NO_POSITION) {
                onSnapPositionChangeListener?.onSnapPositionChange(snapPosition)
            }
        }
    }

    interface OnSnapPositionChangeListener {
        fun onScroll() {
            // Empty by default
        }

        fun onScrollStop(position: Int, childView: View?) {
            // Empty by default
        }

        fun onSnapPositionChange(position: Int) {
            // Empty by default
        }
    }
}
