package com.github.zerobranch.beebox.commons_items

import android.view.View
import androidx.core.view.isVisible
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemWordBinding
import com.github.zerobranch.beebox.commons_java.ext.stringWithFormat
import com.github.zerobranch.beebox.domain.models.words.Word
import com.xwray.groupie.Item

class WordItem(
    val item: Word
) : BaseBindableItem<CommonItemWordBinding>() {

    override fun getLayout(): Int = R.layout.common_item_word

    override fun initializeViewBinding(view: View) = CommonItemWordBinding.bind(view)

    override fun bind(viewBinding: CommonItemWordBinding, position: Int) = with(viewBinding) {
        tvWord.text = item.word
        tvTranslate.text = item.translates
        tvCreationDate.text = item.createdDate.stringWithFormat("dd.MM.yyyy")
        vSelector.isVisible = item.isSelected
        vSelectorEnd.isVisible = item.isSelected
        tvTypeCount.text = item.typesCount.toString()
        tvCategoryCount.text = item.categoriesCount.toString()
        ivParent.isVisible = item.parentWord != null
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is WordItem && other.item.word == item.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is WordItem && other.item == item
}