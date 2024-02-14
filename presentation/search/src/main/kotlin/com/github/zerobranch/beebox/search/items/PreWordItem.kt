package com.github.zerobranch.beebox.search.items

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.domain.models.words.SimpleWord
import com.github.zerobranch.beebox.search.R
import com.github.zerobranch.beebox.search.databinding.ItemPreWordBinding
import com.xwray.groupie.Item

class PreWordItem(val word: SimpleWord) : BaseBindableItem<ItemPreWordBinding>() {

    override fun getLayout(): Int = R.layout.item_pre_word

    override fun initializeViewBinding(view: View) = ItemPreWordBinding.bind(view)

    override fun bind(viewBinding: ItemPreWordBinding, position: Int) = with(viewBinding) {
        tvWord.text = word.word
        tvTranslate.text = word.translates
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is PreWordItem && other.word.word == word.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is PreWordItem && other.word == word
}