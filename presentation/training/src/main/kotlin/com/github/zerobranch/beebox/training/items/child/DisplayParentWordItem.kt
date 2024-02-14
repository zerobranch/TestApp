package com.github.zerobranch.beebox.training.items.child

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.getString
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemDisplayParentWordBinding
import com.xwray.groupie.Item
import zerobranch.lightspanner.Spanner
import zerobranch.lightspanner.Spans
import com.github.zerobranch.beebox.commons_app.R as CommonR

class DisplayParentWordItem(
    private val parentWord: Word,
    private val onParentWordClick: () -> Unit
) : BaseBindableItem<ItemDisplayParentWordBinding>() {

    override fun getLayout(): Int = R.layout.item_display_parent_word

    override fun initializeViewBinding(view: View) = ItemDisplayParentWordBinding.bind(view)

    override fun bind(viewBinding: ItemDisplayParentWordBinding, position: Int) =
        with(viewBinding) {
            tvParentWord.setOnClickListener { onParentWordClick.invoke() }
            Spanner()
                .append(getString(CommonR.string.common_word_parent_title))
                .append(" ")
                .append(
                    parentWord.word,
                    Spans.textColor(colorInt(CommonR.color.common_primary))
                )
                .installTo(tvParentWord)
        }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DisplayParentWordItem && parentWord.word == other.parentWord.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DisplayParentWordItem && parentWord == other.parentWord
}