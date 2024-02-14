package com.github.zerobranch.beebox.training.items.child

import android.view.View
import androidx.core.view.isVisible
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.backgroundTint
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemDisplayEnterWordBinding
import com.xwray.groupie.Item
import com.github.zerobranch.beebox.commons_app.R as CommonR

class DisplayEnterWordItem(
    private val word: Word,
    private val enteredWord: String? = null,
    private val onDoneClick: ((String) -> Unit)? = null,
) : BaseBindableItem<ItemDisplayEnterWordBinding>() {

    override fun getLayout(): Int = R.layout.item_display_enter_word

    override fun initializeViewBinding(view: View) = ItemDisplayEnterWordBinding.bind(view)

    override fun bind(viewBinding: ItemDisplayEnterWordBinding, position: Int) = with(viewBinding) {
        etWord.setText("")

        if (enteredWord.isNotNull()) {
            etWord.append(enteredWord)
        }

        if (enteredWord == null) {
            etWord.setBackgroundResource(CommonR.drawable.common_bg_rounded_corners)
            etWord.backgroundTint(CommonR.color.common_bg_primary_edittext)
        } else {
            etWord.backgroundTintList = null
            etWord.setBackgroundResource(
                if (enteredWord == word.word) {
                    CommonR.drawable.common_bg_success_rounded_corners
                } else {
                    CommonR.drawable.common_bg_error_rounded_corners
                }
            )
        }

        ibCheck.isVisible = onDoneClick.isNotNull()
        ibCheck.setOnClickListener {
            onDoneClick?.invoke(etWord.text.toString().trim())
        }
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DisplayEnterWordItem && word.word == other.word.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DisplayEnterWordItem && word.word == other.word.word && enteredWord == other.enteredWord
}
