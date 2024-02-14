package com.github.zerobranch.beebox.commons_android.utils.ext

import android.os.SystemClock
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.zerobranch.beebox.commons_android.R
import com.github.zerobranch.beebox.commons_android.utils.SwipeToDeleteCallback
import com.github.zerobranch.beebox.commons_android.utils.decorations.DividerItemDecoration
import com.github.zerobranch.beebox.commons_android.utils.decorations.MarginDecoration
import com.github.zerobranch.beebox.commons_android.utils.decorations.OutMarginDecoration
import com.github.zerobranch.beebox.commons_android.utils.listeners.SingleScrollDirectionEnforcer
import com.github.zerobranch.beebox.commons_android.utils.listeners.SnapOnScrollListener
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter

fun RecyclerView.attachSwipeToDelete(
    @DrawableRes endIcon: Int? = null,
    @DrawableRes startIcon: Int? = null,
    @ColorInt backgroundColor: Int,
    @Px backgroundRadius: Float = 0F,
    directions: Int = ItemTouchHelper.LEFT,
    swipePredicate: (Group) -> Boolean = { true },
    onSwiped: (Group, direction: Int) -> Unit
): ItemTouchHelper = ItemTouchHelper(
    SwipeToDeleteCallback(
        context,
        endIcon ?: R.drawable.utils_android_swipe_to_delete_bg_icon,
        startIcon ?: R.drawable.utils_android_swipe_to_delete_bg_icon,
        backgroundColor,
        backgroundRadius,
        directions,
        swipePredicate,
        onSwiped
    )
).apply { attachToRecyclerView(this@attachSwipeToDelete) }

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    onlyWhenChangingPosition: Boolean = true,
    onSnapPositionChangeListener: SnapOnScrollListener.OnSnapPositionChangeListener? = null
) {
    onFlingListener = null
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener =
        SnapOnScrollListener(
            snapHelper,
            behavior,
            onlyWhenChangingPosition,
            onSnapPositionChangeListener
        )
    addOnScrollListener(snapOnScrollListener)
}

fun RecyclerView.removeAllItemDecorations() {
    while (itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }
}

fun RecyclerView.addMarginDecoration(
    @Px margin: Int = 0,
    withSkipMarginOnBorders: Boolean = false
): Unit = addItemDecoration(MarginDecoration(margin, withSkipMarginOnBorders))

fun RecyclerView.addOutMarginDecoration(@Px margin: Int = 0): Unit =
    addItemDecoration(OutMarginDecoration(margin))

fun RecyclerView.addDividerDecoration(
    defStyleAttr: Int = R.attr.dividerItemStyle,
    orientation: Int = DividerItemDecoration.VERTICAL,
    horizontalMargin: Int = 0,
    dividerInsetStart: Int = -1,
    dividerInsetEnd: Int = -1,
    isReverseDraw: Boolean = false,
    itemPredicate: (Group) -> Boolean = { true }
): Unit =
    addItemDecoration(
        DividerItemDecoration(
            context = context,
            defStyleAttr = defStyleAttr,
            orientation = orientation,
            horizontalMargin = horizontalMargin,
            dividerInsetStart = dividerInsetStart,
            dividerInsetEnd = dividerInsetEnd,
            isReverseDraw = isReverseDraw,
            itemPredicate = itemPredicate
        )
    )

fun RecyclerView.enforceSingleScrollDirection() {
    val enforcer = SingleScrollDirectionEnforcer()
    addOnItemTouchListener(enforcer)
    addOnScrollListener(enforcer)
}

fun RecyclerView.addDistinctVerticalScrollListener(action: (position: Int, fromUser: Boolean) -> Boolean) {
    addOnScrollListener(
        object : RecyclerView.OnScrollListener() {
            private var previousFirstVisibleItem = RecyclerView.NO_POSITION

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return

                val firstItemPosition = manager.findFirstVisibleItemPosition()
                val lastItemPosition = manager.findLastVisibleItemPosition()

                if (lastItemPosition != previousFirstVisibleItem) {
                    val isHandledScroll: Boolean =
                        if (firstItemPosition == 0) {
                            for (position in firstItemPosition..lastItemPosition) {
                                action.invoke(position, isUserScroll(dy))
                            }

                            true
                        } else {
                            action.invoke(lastItemPosition, isUserScroll(dy))
                        }

                    if (isHandledScroll) {
                        previousFirstVisibleItem = lastItemPosition
                    }
                }
            }

            private fun isUserScroll(dy: Int): Boolean = dy != 0
        }
    )
}

fun RecyclerView.addVerticalScrollListener(action: (position: Int, fromUser: Boolean) -> Unit) {
    addOnScrollListener(
        object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return

                action.invoke(manager.findLastVisibleItemPosition(), isUserScroll(dy))
            }

            private fun isUserScroll(dy: Int): Boolean = dy != 0
        }
    )
}

fun RecyclerView.addTopPagingListener(action: () -> Unit) {
    addVerticalPagingListener(-1, action)
}

fun RecyclerView.addBottomPagingListener(visibleThreshold: Int = 5, action: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        private var previousTotalItemCount: Int = 0
        private var loading: Boolean = true
        private var firstVisibleItem: Int = 0
        private var visibleItemCount: Int = 0
        private var totalItemCount: Int = 0

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if (loading) {
                if (totalItemCount != previousTotalItemCount) {
                    loading = false
                    previousTotalItemCount = totalItemCount
                }
            }

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                action.invoke()
                loading = true
            }
        }
    })
}

fun RecyclerView.addVerticalPagingListener(direction: Int = 1, action: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!canScrollVertically(direction) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                action.invoke()
            }
        }
    })
}

fun RecyclerView.addBeforehandHorizontalPagingListener(
    beforehandBuffer: Int = 1,
    debounceTime: Long = 3000L,
    action: () -> Unit
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        private var lastCallTime: Long = 0

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == RecyclerView.SCROLL_STATE_IDLE && isLoad()) {
                if (SystemClock.elapsedRealtime() - lastCallTime < debounceTime) return
                else action.invoke()

                lastCallTime = SystemClock.elapsedRealtime()
            }
        }

        private fun RecyclerView.isLoad(): Boolean {
            val lm = layoutManager as? LinearLayoutManager ?: return false
            val visibleItemPosition = lm.findLastVisibleItemPosition()
            return visibleItemPosition > lm.itemCount - beforehandBuffer
        }
    })
}

fun RecyclerView.addHorizontalPagingListener(direction: Int = 1, action: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!canScrollHorizontally(direction) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                action.invoke()
            }
        }
    })
}

fun RecyclerView.scrollToTop() = scrollToPosition(0)

fun RecyclerView.smoothScrollToTop() = smoothScrollToPosition(0)

inline fun <reified T> RecyclerView.addOnLastPositionBelowItemListener(crossinline action: () -> Unit) {
    val isInitialBelow = isLastVisibleItemBelowTarget<T>()
    if (isInitialBelow) action.invoke()

    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        var isFound: Boolean = isInitialBelow

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val isBelow = isLastVisibleItemBelowTarget<T>()
            if (isBelow) {
                if (isFound.not()) {
                    isFound = true
                    action.invoke()
                }
            } else {
                isFound = false
            }
        }
    })
}

inline fun <reified T> RecyclerView.isLastVisibleItemBelowTarget(): Boolean {
    val manager = layoutManager as? LinearLayoutManager ?: return false
    val groupAdapter = (adapter as? GroupAdapter<*>) ?: return false

    val lastVisibleItemPosition = manager.findLastVisibleItemPosition()

    for (position in 0 until groupAdapter.itemCount) {
        if (groupAdapter.getItem(position) is T) {
            return lastVisibleItemPosition >= position
        }
    }
    return false
}

fun RecyclerView.setOnSingleScrollListener(action: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            action.invoke()
            removeOnScrollListener(this)
        }
    })
}