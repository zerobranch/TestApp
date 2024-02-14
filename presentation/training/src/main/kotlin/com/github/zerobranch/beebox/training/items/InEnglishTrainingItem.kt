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
import com.github.zerobranch.beebox.training.items.child.DisplayHintItem
import com.github.zerobranch.beebox.training.items.child.DisplayParentWordItem
import com.github.zerobranch.beebox.training.items.child.DisplayTranscriptionItem
import com.github.zerobranch.beebox.training.items.child.DisplayTranslateItem
import com.xwray.groupie.Group
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Item

class InEnglishTrainingItem(
    private val word: Word,
    private val onEditClick: (Word) -> Unit,
    private val onTranscriptionAudioClick: (String) -> Unit,
) : BaseBindableItem<ItemTrainingSimpleBinding>() {
    private companion object {
        const val INIT_STEP = -1
        const val ZERO_STEP = 0
        const val FIRST_STEP = 1
        const val SECOND_STEP = 2
        const val OTHER_ITEMS_STEP = 3
    }

    private var step: Int = INIT_STEP

    override fun getLayout(): Int = R.layout.item_training_simple

    override fun initializeViewBinding(view: View) = ItemTrainingSimpleBinding.bind(view)

    override fun onViewBindingCreated(viewBinding: ItemTrainingSimpleBinding) = with(viewBinding) {
        super.onViewBindingCreated(viewBinding)
        cvContainer.setPrimaryShadowColor()
    }

    override fun bind(viewBinding: ItemTrainingSimpleBinding, position: Int) = with(viewBinding) {
        step = INIT_STEP

        rvItems.removeAllItemDecorations()
        rvItems.addItemDecoration(DisplayItemsDecoration())
        rvItems.layoutManager = LinearLayoutManager(context)
        rvItems.adapter = GroupieAdapter().apply {
            setOnItemClickListener { _, _ -> rvItems.updateItems() }
        }

        cvContainer.setOnClickListener { rvItems.updateItems() }
        ibEdit.setOnClickListener { onEditClick.invoke(word) }

        rvItems.updateItems()
    }

    private fun RecyclerView.updateItems() {
        step++

        if (step > OTHER_ITEMS_STEP) return

        groupAdapter?.replaceAll(
            mutableListOf<Group>().apply {
                if (step == ZERO_STEP) {
                    add(DisplayEngWordItem(word = word.word))
                } else if (step > ZERO_STEP) {
                    add(DisplayEngWordItem(word = word.word, word.rank))
                }

                if (step >= FIRST_STEP) {
                    add(DisplayTranslateItem(translates = word.translates))
                }

                if (step >= SECOND_STEP) {
                    addAll(
                        word.transcriptions.map {
                            DisplayTranscriptionItem(
                                transcription = it,
                                onTranscriptionAudioClick = onTranscriptionAudioClick,
                            )
                        }
                    )
                }

                if (step >= OTHER_ITEMS_STEP) {
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
            }
        )
    }

    override fun isSameAs(other: Item<*>): Boolean =
        other is InEnglishTrainingItem && word.word == other.word.word

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is InEnglishTrainingItem && word == other.word
}