package com.github.zerobranch.beebox.training.items.child

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemDisplayCommentBinding
import com.xwray.groupie.Item

class DisplayCommentItem(
    private val comment: String
) : BaseBindableItem<ItemDisplayCommentBinding>() {

    override fun getLayout(): Int = R.layout.item_display_comment

    override fun initializeViewBinding(view: View) = ItemDisplayCommentBinding.bind(view)

    override fun bind(viewBinding: ItemDisplayCommentBinding, position: Int) = with(viewBinding) {
        tvComment.text = comment
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DisplayCommentItem && comment == other.comment

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DisplayCommentItem && comment == other.comment
}
