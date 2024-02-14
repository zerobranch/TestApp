package com.github.zerobranch.beebox.commons_items

import android.view.View
import androidx.core.view.isVisible
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemCategoryBinding
import com.github.zerobranch.beebox.commons_java.ext.stringWithFormat
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.xwray.groupie.Item

class CategoryItem(
    val item: WordCategory
) : BaseBindableItem<CommonItemCategoryBinding>() {

    override fun getLayout(): Int = R.layout.common_item_category

    override fun initializeViewBinding(view: View) = CommonItemCategoryBinding.bind(view)

    override fun bind(viewBinding: CommonItemCategoryBinding, position: Int) = with(viewBinding) {
        tvTitle.text = item.name
        tvDescription.text = item.description
        tvCreationDate.text = item.createdDate.stringWithFormat("dd.MM.yyyy")
        vSelector.isVisible = item.isSelected
        vSelectorEnd.isVisible = item.isSelected
        tvWordCount.text = item.wordsCount.toString()
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is CategoryItem && other.item.id == item.id

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is CategoryItem && other.item == item
}