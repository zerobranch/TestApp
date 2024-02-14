package com.github.zerobranch.beebox.training.items

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.context
import com.github.zerobranch.beebox.commons_android.utils.ext.removeAllItemDecorations
import com.github.zerobranch.beebox.commons_app.utils.setPrimaryShadowColor
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.training.R
import com.github.zerobranch.beebox.training.databinding.ItemTrainingSimpleBinding
import com.github.zerobranch.beebox.training.items.child.DisplayCommentItem
import com.github.zerobranch.beebox.training.items.child.DisplayEngWordItem
import com.github.zerobranch.beebox.training.items.child.DisplayEnterWordItem
import com.github.zerobranch.beebox.training.items.child.DisplayHintItem
import com.github.zerobranch.beebox.training.items.child.DisplayParentWordItem
import com.github.zerobranch.beebox.training.items.child.DisplayRuWordItem
import com.github.zerobranch.beebox.training.items.child.DisplayTranscriptionItem
import com.github.zerobranch.beebox.training.items.child.DisplayTranslateItem
import com.xwray.groupie.Group
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Item

class EnterWordTrainingItem(
    private val word: Word,
    private val onEditClick: (Word) -> Unit,
    private val onTranscriptionAudioClick: (String) -> Unit,
) : BaseBindableItem<ItemTrainingSimpleBinding>() {

    override fun getLayout(): Int = R.layout.item_training_simple

    override fun initializeViewBinding(view: View) = ItemTrainingSimpleBinding.bind(view)

    override fun onViewBindingCreated(viewBinding: ItemTrainingSimpleBinding) = with(viewBinding) {
        super.onViewBindingCreated(viewBinding)
        cvContainer.setPrimaryShadowColor()
    }

    override fun bind(viewBinding: ItemTrainingSimpleBinding, position: Int) = with(viewBinding) {
        ibEdit.setOnClickListener { onEditClick.invoke(word) }

        rvItems.removeAllItemDecorations()
        rvItems.addItemDecoration(DisplayItemsDecoration())
        rvItems.layoutManager = LinearLayoutManager(context)
        rvItems.adapter = GroupieAdapter()
        rvItems.setFirstStepItems()
    }

    private fun RecyclerView.setFirstStepItems() {
        groupAdapter?.replaceAll(
            mutableListOf<Group>().apply {
                val splitTranslates = word.translates.split(", ")

                add(DisplayRuWordItem(word = splitTranslates.firstOrNull() ?: ""))
                add(
                    DisplayEnterWordItem(word = word) { enteredWord ->
                        setSecondStepItems(enteredWord)
                    }
                )
            }
        )
    }

    private fun RecyclerView.setSecondStepItems(enteredWord: String) {
        groupAdapter?.replaceAll(
            mutableListOf<Group>().apply {
                add(DisplayRuWordItem(word = word.translates))
                add(DisplayEnterWordItem(word = word, enteredWord = enteredWord))
                add(DisplayEngWordItem(word = word.word, word.rank))
                add(DisplayTranslateItem(translates = word.translates))
                addAll(
                    word.transcriptions.map {
                        DisplayTranscriptionItem(
                            transcription = it,
                            onTranscriptionAudioClick = onTranscriptionAudioClick,
                        )
                    }
                )
                addAll(
                    word.hints.map {
                        DisplayHintItem(hint = it)
                    }
                )

                if (word.comment.isNotBlank()) {
                    add(DisplayCommentItem(comment = word.comment))
                }

                val parentWord = word.parentWord
                if (parentWord.isNotNull()) {
                    add(
                        DisplayParentWordItem(
                            parentWord = parentWord,
                            onParentWordClick = { onEditClick.invoke(parentWord) }
                        )
                    )
                }
            }
        )
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is EnterWordTrainingItem && word.word == other.word.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is EnterWordTrainingItem && word == other.word
}