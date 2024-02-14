package com.github.zerobranch.beebox.training.items.child

import android.view.View
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.getString
import com.github.zerobranch.beebox.commons_android.utils.ext.setOrIgnore
import com.github.zerobranch.beebox.commons_android.utils.ext.setTextOrHide
import com.github.zerobranch.beebox.domain.models.words.Transcription
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemDisplayTranscriptionBinding
import com.xwray.groupie.Item
import com.github.zerobranch.beebox.commons_app.R as CommonR

class DisplayTranscriptionItem(
    private val transcription: Transcription,
    private val onTranscriptionAudioClick: (String) -> Unit,
) : BaseBindableItem<ItemDisplayTranscriptionBinding>() {

    override fun getLayout(): Int = R.layout.item_display_transcription

    override fun initializeViewBinding(view: View) = ItemDisplayTranscriptionBinding.bind(view)

    override fun bind(viewBinding: ItemDisplayTranscriptionBinding, position: Int) = with(viewBinding) {
        setTitle()
        setHint()
        setTranscription()
        setTranscriptionAudio()
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is DisplayTranscriptionItem && transcription.id == other.transcription.id

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is DisplayTranscriptionItem && transcription == other.transcription

    private fun ItemDisplayTranscriptionBinding.setTitle() {
        etTitle.isSelected = true
        etTitle.setTextOrHide(transcription.title)
    }

    private fun ItemDisplayTranscriptionBinding.setHint() {
        etSecondHint.isSelected = true
        etSecondHint.setOrIgnore(
            transcription.firstTranscriptionHint.takeIf { it.isNotBlank() }
                ?: getString(CommonR.string.common_usa_flag)
        )

        etFirstHint.isSelected = true
        etFirstHint.setOrIgnore(
            transcription.secondTranscriptionHint.takeIf { it.isNotBlank() }
                ?: getString(CommonR.string.common_brith_flag)
        )
    }

    private fun ItemDisplayTranscriptionBinding.setTranscription() {
        etSecondTranscription.isSelected = true
        etSecondTranscription.setOrIgnore(transcription.firstTranscription)

        etFirstTranscription.isSelected = true
        etFirstTranscription.setOrIgnore(transcription.secondTranscription)
    }

    private fun ItemDisplayTranscriptionBinding.setTranscriptionAudio() {
        ivSecondTranscriptionAudio.isEnabled = transcription.firstTranscriptionAudio.isNotBlank()
        ivFirstTranscriptionAudio.isEnabled = transcription.secondTranscriptionAudio.isNotBlank()

        ivSecondTranscriptionAudio.setOnClickListener {
            onTranscriptionAudioClick.invoke(transcription.firstTranscriptionAudio)
        }

        ivFirstTranscriptionAudio.setOnClickListener {
            onTranscriptionAudioClick.invoke(transcription.secondTranscriptionAudio)
        }
    }
}
