package com.github.zerobranch.beebox.commons_items.creating

import android.view.View
import androidx.core.widget.doOnTextChanged
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.getString
import com.github.zerobranch.beebox.commons_android.utils.ext.setOrIgnore
import com.github.zerobranch.beebox.commons_items.R
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemTranscriptionBinding
import com.github.zerobranch.beebox.domain.models.words.Transcription
import com.xwray.groupie.Item
import com.github.zerobranch.beebox.commons_app.R as CommonR

class TranscriptionItem(
    private val transcription: Transcription,
    private val onTranscriptionTitleChanged: (String, String) -> Unit,
    private val onFirstTranscriptionHintChanged: (String, String) -> Unit,
    private val onFirstTranscriptionChanged: (String, String) -> Unit,
    private val onSecondTranscriptionHintChanged: (String, String) -> Unit,
    private val onSecondTranscriptionChanged: (String, String) -> Unit,
    private val onDeleteClick: (String) -> Unit,
    private val onTranscriptionAudioClick: (String) -> Unit,
) : BaseBindableItem<CommonItemTranscriptionBinding>() {
    companion object {
        private const val TRANSCRIPTION_TITLE_CHANGED_PAYLOAD = "transcription_title_changed_payload"
        private const val FIRST_HINT_CHANGED_PAYLOAD = "first_hint_changed_payload"
        private const val FIRST_TRANSCRIPTION_CHANGED_PAYLOAD = "first_transcription_changed_payload"
        private const val SECOND_HINT_CHANGED_PAYLOAD = "second_hint_changed_payload"
        private const val SECOND_TRANSCRIPTION_CHANGED_PAYLOAD = "second_transcription_changed_payload"
    }

    override fun getLayout(): Int = R.layout.common_item_transcription

    override fun initializeViewBinding(view: View) = CommonItemTranscriptionBinding.bind(view)

    override fun bind(
        viewBinding: CommonItemTranscriptionBinding,
        position: Int,
        payload: String
    ) = with(viewBinding) {
        if (payload.contains(TRANSCRIPTION_TITLE_CHANGED_PAYLOAD)) setTitleState()
        if (payload.contains(FIRST_HINT_CHANGED_PAYLOAD)) setFirstHintState()
        if (payload.contains(FIRST_TRANSCRIPTION_CHANGED_PAYLOAD)) setFirstTranscriptionState()
        if (payload.contains(SECOND_HINT_CHANGED_PAYLOAD)) setSecondHintState()
        if (payload.contains(SECOND_TRANSCRIPTION_CHANGED_PAYLOAD)) setSecondTranscriptionState()
    }

    override fun bind(viewBinding: CommonItemTranscriptionBinding, position: Int) = with(viewBinding) {
        ibDelete.setOnClickListener { onDeleteClick.invoke(transcription.id) }

        setTranscriptionsAudioState()
        setTitleState()
        setFirstHintState()
        setFirstTranscriptionState()
        setSecondHintState()
        setSecondTranscriptionState()
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is TranscriptionItem && other.transcription.id == transcription.id

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is TranscriptionItem && other.transcription == transcription

    override fun setChangePayload(newItem: Item<*>, payloads: MutableSet<String>) {
        if (newItem !is TranscriptionItem) return
        if (newItem.transcription.title != transcription.title) payloads.add(TRANSCRIPTION_TITLE_CHANGED_PAYLOAD)
        if (newItem.transcription.firstTranscriptionHint != transcription.firstTranscriptionHint) payloads.add(FIRST_HINT_CHANGED_PAYLOAD)
        if (newItem.transcription.firstTranscription != transcription.firstTranscription) payloads.add(FIRST_TRANSCRIPTION_CHANGED_PAYLOAD)
        if (newItem.transcription.secondTranscriptionHint != transcription.secondTranscriptionHint) payloads.add(SECOND_HINT_CHANGED_PAYLOAD)
        if (newItem.transcription.secondTranscription != transcription.secondTranscription) payloads.add(SECOND_TRANSCRIPTION_CHANGED_PAYLOAD)
    }

    private fun CommonItemTranscriptionBinding.setTranscriptionsAudioState() {
        ivFirstTranscriptionAudio.isEnabled = transcription.firstTranscriptionAudio.isNotBlank()
        ivSecondTranscriptionAudio.isEnabled = transcription.secondTranscriptionAudio.isNotBlank()

        ivFirstTranscriptionAudio.setOnClickListener {
            onTranscriptionAudioClick.invoke(transcription.firstTranscriptionAudio)
        }

        ivSecondTranscriptionAudio.setOnClickListener {
            onTranscriptionAudioClick.invoke(transcription.secondTranscriptionAudio)
        }
    }

    private fun CommonItemTranscriptionBinding.setTitleState() {
        etTitle.setOrIgnore(transcription.title)
        etTitle.doOnTextChanged { text, _, _, _ ->
            onTranscriptionTitleChanged.invoke(transcription.id, text.toString())
        }
    }

    private fun CommonItemTranscriptionBinding.setFirstHintState() {
        etFirstHint.setOrIgnore(
            transcription.firstTranscriptionHint.takeIf { it.isNotBlank() }
                ?: getString(CommonR.string.common_usa_flag)
        )
        etFirstHint.doOnTextChanged { text, _, _, _ ->
            onFirstTranscriptionHintChanged.invoke(transcription.id, text.toString())
        }
    }

    private fun CommonItemTranscriptionBinding.setFirstTranscriptionState() {
        etFirstTranscription.setOrIgnore(transcription.firstTranscription)
        etFirstTranscription.doOnTextChanged { text, _, _, _ ->
            onFirstTranscriptionChanged.invoke(transcription.id, text.toString())
        }
    }

    private fun CommonItemTranscriptionBinding.setSecondHintState() {
        etSecondHint.setOrIgnore(
            transcription.secondTranscriptionHint.takeIf { it.isNotBlank() }
                ?: getString(CommonR.string.common_brith_flag)
        )
        etSecondHint.doOnTextChanged { text, _, _, _ ->
            onSecondTranscriptionHintChanged.invoke(transcription.id, text.toString())
        }
    }

    private fun CommonItemTranscriptionBinding.setSecondTranscriptionState() {
        etSecondTranscription.setOrIgnore(transcription.secondTranscription)
        etSecondTranscription.doOnTextChanged { text, _, _, _ ->
            onSecondTranscriptionChanged.invoke(transcription.id, text.toString())
        }
    }
}
