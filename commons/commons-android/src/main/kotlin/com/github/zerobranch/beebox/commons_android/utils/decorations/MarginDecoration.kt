package com.github.zerobranch.beebox.commons_android.utils.decorations

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MarginDecoration(
    @Px private val margin: Int = 0,
    private val withSkipMarginOnBorders: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemCount = parent.adapter?.itemCount ?: return
        val childViewHolder = parent.getChildViewHolder(view)

        val position = childViewHolder.absoluteAdapterPosition
            .takeIf { it != RecyclerView.NO_POSITION } ?: childViewHolder.oldPosition
            .takeIf { it != RecyclerView.NO_POSITION } ?: return

        val lm = parent.layoutManager as? LinearLayoutManager ?: return

        when (lm.orientation) {
            RecyclerView.HORIZONTAL -> {
                outRect.left = when {
                    withSkipMarginOnBorders && position == 0 -> 0
                    position == 0 -> margin
                    else -> margin / 2
                }
                outRect.right = when {
                    withSkipMarginOnBorders && position == itemCount - 1 -> 0
                    position == itemCount - 1 -> margin
                    else -> margin / 2
                }
            }
            RecyclerView.VERTICAL -> {
                outRect.top = when {
                    withSkipMarginOnBorders && position == 0 -> 0
                    position == 0 -> margin
                    else -> margin / 2
                }
                outRect.bottom = when {
                    withSkipMarginOnBorders && position == itemCount - 1 -> 0
                    position == itemCount - 1 -> margin
                    else -> margin / 2
                }
            }
        }
    }
}