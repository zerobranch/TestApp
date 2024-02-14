package com.github.zerobranch.beebox.commons_items

import android.view.View
import androidx.core.view.isVisible
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemWordTypeBinding
import com.github.zerobranch.beebox.commons_java.ext.stringWithFormat
import com.github.zerobranch.beebox.domain.models.WordType
import com.xwray.groupie.Item

class WordTypeItem(
    val item: WordType
) : BaseBindableItem<CommonItemWordTypeBinding>() {

    override fun getLayout(): Int = R.layout.common_item_word_type

    override fun initializeViewBinding(view: View) = CommonItemWordTypeBinding.bind(view)

    override fun bind(viewBinding: CommonItemWordTypeBinding, position: Int) = with(viewBinding) {
        tvTitle.text = item.name
        tvDescription.text = item.description
        tvCreationDate.text = item.createdDate.stringWithFormat("dd.MM.yyyy")
        vSelector.isVisible = item.isSelected
        vSelectorEnd.isVisible = item.isSelected
        tvWordCount.text = item.wordsCount.toString()
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is WordTypeItem && other.item.id == item.id

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is WordTypeItem && other.item == item
}