package com.github.zerobranch.beebox.training.items.child

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.context
import com.github.zerobranch.beebox.commons_java.ext.isNull
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemDisplayEngWordBinding
import com.xwray.groupie.Item
import zerobranch.lightspanner.Spanner
import zerobranch.lightspanner.Spans
import com.github.zerobranch.beebox.commons_app.R as CommonR

class DisplayEngWordItem(
    private val word: String,
    private val rank: Int? = null
) : BaseBindableItem<ItemDisplayEngWordBinding>() {

    override fun getLayout(): Int = R.layout.item_display_eng_word

    override fun initializeViewBinding(view: View) = ItemDisplayEngWordBinding.bind(view)

    override fun bind(viewBinding: ItemDisplayEngWordBinding, position: Int) = with(viewBinding) {
        if (rank.isNull()) {
            tvWord.text = word
        } else {
            Spanner()
                .append(word)
                .append(
                    " $rank",
                    Spans.superscript(),
                    Spans.relativeSize(0.6F),
                    Spans.textColor(context.colorInt(CommonR.color.common_text_secondary))
                )
                .installTo(tvWord)
        }
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DisplayEngWordItem && word == other.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DisplayEngWordItem && word == other.word && rank == other.rank
}