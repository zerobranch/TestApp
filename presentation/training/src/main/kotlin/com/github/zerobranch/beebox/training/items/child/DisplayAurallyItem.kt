package com.github.zerobranch.beebox.training.items.child

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.drawableTint
import com.github.zerobranch.beebox.commons_java.ext.isNull
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemDisplayAurallyBinding
import com.xwray.groupie.Item
import com.github.zerobranch.beebox.commons_app.R as CommonR

class DisplayAurallyItem(
    private val word: Word,
    private val onTranscriptionAudioClick: (String) -> Unit
) : BaseBindableItem<ItemDisplayAurallyBinding>() {

    override fun getLayout(): Int = R.layout.item_display_aurally

    override fun initializeViewBinding(view: View) = ItemDisplayAurallyBinding.bind(view)

    override fun bind(viewBinding: ItemDisplayAurallyBinding, position: Int) = with(viewBinding) {
        val mainUkAudio = word.mainUkAudio
        val mainUsAudio = word.mainUsAudio

        if (mainUkAudio.isNull()) {
            tvUkTitle.drawableTint(CommonR.color.common_primary_disable)
            vUkSpace.setOnClickListener(null)
        } else {
            tvUkTitle.drawableTint(CommonR.color.common_text_primary)
            vUkSpace.setOnClickListener { onTranscriptionAudioClick.invoke(mainUkAudio) }
        }

        if (mainUsAudio.isNull()) {
            tvUsTitle.drawableTint(CommonR.color.common_primary_disable)
            vUsSpace.setOnClickListener(null)
        } else {
            tvUsTitle.drawableTint(CommonR.color.common_text_primary)
            vUsSpace.setOnClickListener { onTranscriptionAudioClick.invoke(mainUsAudio) }
        }
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DisplayAurallyItem && word.word == other.word.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DisplayAurallyItem && word.word == other.word.word
}
