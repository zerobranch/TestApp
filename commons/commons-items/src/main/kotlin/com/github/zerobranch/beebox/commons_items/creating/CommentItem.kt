package com.github.zerobranch.beebox.commons_items.creating

import android.view.View
import androidx.core.widget.doOnTextChanged
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.setOrIgnore
import com.github.zerobranch.beebox.commons_items.R
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemCommentBinding
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.xwray.groupie.Item

class CommentItem(
    private val draftWord: DraftWord,
    private val onCommentChanged: (String) -> Unit,
) : BaseBindableItem<CommonItemCommentBinding>() {
    companion object {
        private const val COMMENT_CHANGED_PAYLOAD = "comment_changed_payload"
    }

    override fun getLayout(): Int = R.layout.common_item_comment

    override fun initializeViewBinding(view: View) = CommonItemCommentBinding.bind(view)

    override fun bind(
        viewBinding: CommonItemCommentBinding,
        position: Int,
        payload: String
    ) = with(viewBinding) {
        if (payload.contains(COMMENT_CHANGED_PAYLOAD)) {
            setCommentState()
        }
    }

    override fun bind(viewBinding: CommonItemCommentBinding, position: Int) = with(viewBinding) {
        setCommentState()
    }

    override fun setChangePayload(newItem: Item<*>, payloads: MutableSet<String>) {
        if (newItem !is CommentItem) return
        if (draftWord.comment != newItem.draftWord.comment) payloads.add(COMMENT_CHANGED_PAYLOAD)
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is CommentItem && other.draftWord.word == draftWord.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is CommentItem && other.draftWord.comment == draftWord.comment

    private fun CommonItemCommentBinding.setCommentState() {
        etComment.setOrIgnore(draftWord.comment)
        etComment.doOnTextChanged { text, _, _, _ -> onCommentChanged.invoke(text.toString()) }
    }
}