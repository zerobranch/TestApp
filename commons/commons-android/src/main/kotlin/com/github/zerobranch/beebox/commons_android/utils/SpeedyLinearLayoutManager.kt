package com.github.zerobranch.beebox.commons_android.utils

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpeedyLinearLayoutManager : LinearLayoutManager {
    private companion object {
        const val FAST_SCROLLING_OFFSET = 10000
    }

    constructor(context: Context) : super(context)

    constructor(
        context: Context,
        @RecyclerView.Orientation orientation: Int,
        reverseLayout: Boolean
    ) : super(context, orientation, reverseLayout)

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val offset = computeVerticalScrollOffset(state)
        if (offset > FAST_SCROLLING_OFFSET) {
            recyclerView.scrollToPosition(15)
        }

        super.smoothScrollToPosition(recyclerView, state, position)
    }
}