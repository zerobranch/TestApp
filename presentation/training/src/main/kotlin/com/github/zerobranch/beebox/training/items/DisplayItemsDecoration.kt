package com.github.zerobranch.beebox.training.items

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.zerobranch.beebox.commons_android.utils.ext.toPx
import com.github.zerobranch.beebox.training.items.child.DisplayCommentItem
import com.github.zerobranch.beebox.training.items.child.DisplayEngWordItem
import com.github.zerobranch.beebox.training.items.child.DisplayEnterWordItem
import com.github.zerobranch.beebox.training.items.child.DisplayHintItem
import com.github.zerobranch.beebox.training.items.child.DisplayParentWordItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.github.zerobranch.beebox.commons_app.R as CommonR

class DisplayItemsDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapter = parent.adapter as GroupAdapter

        val holder = parent.getChildViewHolder(view)
        val position = holder.absoluteAdapterPosition
            .takeIf { it != RecyclerView.NO_POSITION } ?: holder.oldPosition
            .takeIf { it != RecyclerView.NO_POSITION } ?: return

        val item: Item<*> = runCatching { adapter.getItem(position) }.getOrNull() ?: return
        val prevItem: Item<*>? = runCatching { adapter.getItem(position - 1) }.getOrNull()

        when {
            item is DisplayHintItem && prevItem !is DisplayHintItem -> {
                outRect.top = parent.toPx(CommonR.dimen.common_spacing_15)
            }
            item is DisplayCommentItem -> {
                outRect.top = parent.toPx(CommonR.dimen.common_spacing_15)
            }
            item is DisplayParentWordItem -> {
                outRect.top = parent.toPx(CommonR.dimen.common_spacing_10)
            }
            item is DisplayEnterWordItem -> {
                outRect.top = parent.toPx(CommonR.dimen.common_spacing_10)
            }
            item is DisplayEngWordItem && prevItem is DisplayEnterWordItem -> {
                outRect.top = parent.toPx(CommonR.dimen.common_spacing_10)
            }
            else -> {}
        }
    }
}