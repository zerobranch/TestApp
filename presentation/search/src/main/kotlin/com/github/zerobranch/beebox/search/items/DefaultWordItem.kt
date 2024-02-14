package com.github.zerobranch.beebox.search.items

import android.view.View
import androidx.core.view.isVisible
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.getString
import com.github.zerobranch.beebox.commons_android.utils.ext.setTextColorList
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.search.R
import com.github.zerobranch.beebox.search.databinding.ItemDefaultWordBinding
import com.xwray.groupie.Item
import com.github.zerobranch.beebox.commons_app.R as CommonR

class DefaultWordItem(
    val word: Word,
    private val isLocalWords: Boolean
) : BaseBindableItem<ItemDefaultWordBinding>() {

    override fun getLayout(): Int = R.layout.item_default_word

    override fun initializeViewBinding(view: View) = ItemDefaultWordBinding.bind(view)

    override fun bind(viewBinding: ItemDefaultWordBinding, position: Int) = with(viewBinding) {
        tvWord.text = word.word
        tvTranslate.text = word.translates
        tvState.isVisible = isLocalWords

        tvState.text = getString(
            if (word.isSelected) CommonR.string.common_title_added
            else CommonR.string.common_title_not
        )

        tvState.setTextColorList(
            if (word.isSelected) CommonR.color.common_dark_green
            else CommonR.color.common_dark_red
        )
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DefaultWordItem && other.word.word == word.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DefaultWordItem && other.word == word && other.isLocalWords == isLocalWords
}