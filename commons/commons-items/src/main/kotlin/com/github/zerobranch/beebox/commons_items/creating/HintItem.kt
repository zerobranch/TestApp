package com.github.zerobranch.beebox.commons_items.creating

import android.view.View
import androidx.core.widget.doOnTextChanged
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.setOrIgnore
import com.github.zerobranch.beebox.commons_items.R
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemHintBinding
import com.github.zerobranch.beebox.domain.models.words.Hint
import com.xwray.groupie.Item

class HintItem(
    private val hint: Hint,
    private val onHintChanged: (String, String) -> Unit,
    private val onDeleteClick: (String) -> Unit,
) : BaseBindableItem<CommonItemHintBinding>() {
    companion object {
        private const val HINT_CHANGED_PAYLOAD = "hint_changed_payload"
    }

    override fun getLayout(): Int = R.layout.common_item_hint

    override fun initializeViewBinding(view: View) = CommonItemHintBinding.bind(view)

    override fun bind(
        viewBinding: CommonItemHintBinding,
        position: Int,
        payload: String
    ) = with(viewBinding) {
        if (payload.contains(HINT_CHANGED_PAYLOAD)) {
            setHintState()
        }
    }

    override fun bind(viewBinding: CommonItemHintBinding, position: Int) = with(viewBinding) {
        ibDelete.setOnClickListener { onDeleteClick.invoke(hint.id) }

        setHintState()
    }

    override fun setChangePayload(newItem: Item<*>, payloads: MutableSet<String>) {
        if (newItem !is HintItem) return
        if (hint != newItem.hint) payloads.add(HINT_CHANGED_PAYLOAD)
    }

    override fun isSameAs(other: Item<*>): Boolean = other is HintItem && other.hint.id == hint.id

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is HintItem && other.hint == hint

    private fun CommonItemHintBinding.setHintState() {
        etHint.setOrIgnore(hint.text)
        etHint.doOnTextChanged { text, _, _, _ -> onHintChanged.invoke(hint.id, text.toString()) }
    }
}