package com.github.zerobranch.beebox.commons_android.utils.decorations

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OutMarginDecoration(
    @Px private val margin: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val childViewHolder = parent.getChildViewHolder(view)

        val position = childViewHolder.absoluteAdapterPosition
            .takeIf { it != RecyclerView.NO_POSITION } ?: childViewHolder.oldPosition
            .takeIf { it != RecyclerView.NO_POSITION } ?: return

        when (val lm = parent.layoutManager) {
            is GridLayoutManager -> {
                val spanCount = lm.spanCount
                val itemSpanIndex = lm.spanSizeLookup.getSpanIndex(position, spanCount)
                val itemSpanSize = lm.spanSizeLookup.getSpanSize(position)

                when (lm.orientation) {
                    RecyclerView.HORIZONTAL -> {
                        if (itemSpanIndex == 0) {
                            outRect.top = margin
                        }

                        if (itemSpanIndex == spanCount - itemSpanSize) {
                            outRect.bottom = margin
                        }
                    }
                    RecyclerView.VERTICAL -> {
                        if (itemSpanIndex == 0) {
                            outRect.left = margin
                        }

                        if (itemSpanIndex == spanCount - itemSpanSize) {
                            outRect.right = margin
                        }
                    }
                }
            }
            is LinearLayoutManager -> {
                when (lm.orientation) {
                    RecyclerView.HORIZONTAL -> {
                        outRect.top = margin
                        outRect.bottom = margin
                    }
                    RecyclerView.VERTICAL -> {
                        outRect.left = margin
                        outRect.right = margin
                    }
                }
            }
        }
    }
}