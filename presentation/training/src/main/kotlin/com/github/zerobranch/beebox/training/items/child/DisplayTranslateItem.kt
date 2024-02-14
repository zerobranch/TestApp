package com.github.zerobranch.beebox.training.items.child

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemDisplayTranslateBinding
import com.xwray.groupie.Item

class DisplayTranslateItem(
    private val translates: String
) : BaseBindableItem<ItemDisplayTranslateBinding>() {

    override fun getLayout(): Int = R.layout.item_display_translate

    override fun initializeViewBinding(view: View) = ItemDisplayTranslateBinding.bind(view)

    override fun bind(viewBinding: ItemDisplayTranslateBinding, position: Int) = with(viewBinding) {
        tvTranslates.text = translates
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DisplayTranslateItem && translates == other.translates

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DisplayTranslateItem && translates == other.translates
}