package com.github.zerobranch.beebox.commons_items

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemEmptyBinding
import com.xwray.groupie.Item

class EmptyItem : BaseBindableItem<CommonItemEmptyBinding>() {

    override fun getLayout(): Int = R.layout.common_item_empty

    override fun initializeViewBinding(view: View) = CommonItemEmptyBinding.bind(view)

    override fun bind(viewBinding: CommonItemEmptyBinding, position: Int) = with(viewBinding) {}

    override fun isSameAs(other: Item<*>): Boolean = other is EmptyItem

    override fun hasSameContentAs(other: Item<*>): Boolean = other is EmptyItem
}