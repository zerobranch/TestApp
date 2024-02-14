package com.github.zerobranch.beebox.commons_app.selecting

import android.view.View
import com.github.zerobranch.beebox.commons_app.R
import com.github.zerobranch.beebox.commons_app.databinding.CommonItemSelectableBinding
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class SelectableItem(
    private val title: String,
    private val selected: Boolean,
    private val click: (Boolean) -> Unit
) : BindableItem<CommonItemSelectableBinding>() {

    override fun getLayout() = R.layout.common_item_selectable

    override fun initializeViewBinding(view: View) = CommonItemSelectableBinding.bind(view)

    override fun bind(binding: CommonItemSelectableBinding, position: Int) = with(binding.item) {
        text = title
        isChecked = selected

        setOnClickListener { click.invoke(isChecked) }
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is SelectableItem && other.title == title

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is SelectableItem && other.title == title && other.selected == selected
}