package com.github.zerobranch.beebox.training.items.child

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemDisplayRuWordBinding
import com.xwray.groupie.Item

class DisplayRuWordItem(
    private val word: String
) : BaseBindableItem<ItemDisplayRuWordBinding>() {

    override fun getLayout(): Int = R.layout.item_display_ru_word

    override fun initializeViewBinding(view: View) = ItemDisplayRuWordBinding.bind(view)

    override fun bind(viewBinding: ItemDisplayRuWordBinding, position: Int) = with(viewBinding) {
        tvWord.text = word
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DisplayRuWordItem && word == other.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DisplayRuWordItem && word == other.word
}