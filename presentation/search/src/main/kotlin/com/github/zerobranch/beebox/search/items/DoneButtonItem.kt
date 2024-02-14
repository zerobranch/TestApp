package com.github.zerobranch.beebox.search.items

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.search.R
import com.github.zerobranch.beebox.search.databinding.ItemDoneButtonBinding
import com.xwray.groupie.Item

class DoneButtonItem(
    private val isEnable: Boolean,
    private val onDoneClick: () -> Unit
) : BaseBindableItem<ItemDoneButtonBinding>() {

    override fun getLayout(): Int = R.layout.item_done_button

    override fun initializeViewBinding(view: View) = ItemDoneButtonBinding.bind(view)

    override fun bind(viewBinding: ItemDoneButtonBinding, position: Int) = with(viewBinding) {
        btnCreate.setOnClickListener { onDoneClick.invoke() }
        btnCreate.isEnabled = isEnable
    }

    override fun isSameAs(other: Item<*>): Boolean = this === other

    override fun hasSameContentAs(other: Item<*>): Boolean = this === other
}