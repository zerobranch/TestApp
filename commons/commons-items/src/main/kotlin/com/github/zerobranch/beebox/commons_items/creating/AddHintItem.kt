package com.github.zerobranch.beebox.commons_items.creating

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_items.R
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemAddSimpleBinding
import com.xwray.groupie.Item

class AddHintItem : BaseBindableItem<CommonItemAddSimpleBinding>() {

    override fun getLayout(): Int = R.layout.common_item_add_simple

    override fun initializeViewBinding(view: View) = CommonItemAddSimpleBinding.bind(view)

    override fun bind(viewBinding: CommonItemAddSimpleBinding, position: Int) {}

    override fun isSameAs(other: Item<*>): Boolean = other is AddHintItem

    override fun hasSameContentAs(other: Item<*>): Boolean = other is AddHintItem
}