package com.github.zerobranch.beebox.search.items

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.context
import com.github.zerobranch.beebox.commons_android.utils.ext.getStyleByAttr
import com.github.zerobranch.beebox.domain.models.words.SimpleWord
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.search.R
import com.github.zerobranch.beebox.search.databinding.ItemSearchBinding
import com.xwray.groupie.Item
import zerobranch.lightspanner.Spanner
import zerobranch.lightspanner.Spans
import com.github.zerobranch.beebox.commons_app.R as CommonR

class SearchItem(val item: SimpleWord) : BaseBindableItem<ItemSearchBinding>() {

    override fun getLayout(): Int = R.layout.item_search

    override fun initializeViewBinding(view: View) = ItemSearchBinding.bind(view)

    override fun bind(viewBinding: ItemSearchBinding, position: Int) = with(viewBinding) {
        Spanner()
            .append(
                item.word,
                Spans.textAppearance(
                    context,
                    context.getStyleByAttr(CommonR.attr.DefaultBold)
                )
            )
            .append(" — ")
            .append(
                item.translates,
                Spans.textAppearance(
                    context,
                    context.getStyleByAttr(CommonR.attr.DefaultSemiBold)
                )
            )
            .installTo(root)

        root.setBackgroundColor(
            colorInt(
                if (item is Word) R.color.search_bg_contains_item
                else CommonR.color.common_bg_primary
            )
        )
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is SearchItem && other.item.word == item.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is SearchItem && other.item == item
}