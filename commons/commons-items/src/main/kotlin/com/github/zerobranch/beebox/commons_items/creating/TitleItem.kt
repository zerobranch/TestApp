package com.github.zerobranch.beebox.commons_items.creating

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_items.R
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemTitleBinding
import com.xwray.groupie.Item

class TitleItem(
    private val title: String
) : BaseBindableItem<CommonItemTitleBinding>() {

    override fun getLayout(): Int = R.layout.common_item_title

    override fun initializeViewBinding(view: View) = CommonItemTitleBinding.bind(view)

    override fun bind(viewBinding: CommonItemTitleBinding, position: Int) {
        viewBinding.tvTitle.text = title
    }

    override fun isSameAs(other: Item<*>): Boolean = other is TitleItem && other.title == title

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is TitleItem && other.title == title
}