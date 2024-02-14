package com.github.zerobranch.beebox.training.items.child

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.domain.models.words.Hint
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemDisplayHintBinding
import com.xwray.groupie.Item

class DisplayHintItem(
    private val hint: Hint
) : BaseBindableItem<ItemDisplayHintBinding>() {

    override fun getLayout(): Int = R.layout.item_display_hint

    override fun initializeViewBinding(view: View) = ItemDisplayHintBinding.bind(view)

    override fun bind(viewBinding: ItemDisplayHintBinding, position: Int) = with(viewBinding) {
        tvHint.text = hint.text
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DisplayHintItem && hint.id == other.hint.id

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DisplayHintItem && hint == other.hint
}
