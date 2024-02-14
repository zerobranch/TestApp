package com.github.zerobranch.beebox.commons_items.creating

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.size
import androidx.core.widget.doOnTextChanged
import com.github.zerobranch.beebox.commons_android.utils.BaseBindableItem
import com.github.zerobranch.beebox.commons_android.utils.ext.context
import com.github.zerobranch.beebox.commons_android.utils.ext.gone
import com.github.zerobranch.beebox.commons_android.utils.ext.setOrIgnore
import com.github.zerobranch.beebox.commons_android.utils.ext.show
import com.github.zerobranch.beebox.commons_items.R
import com.github.zerobranch.beebox.commons_items.databinding.CommonItemAddingWordBinding
import com.github.zerobranch.beebox.commons_java.ext.deepEquals
import com.github.zerobranch.beebox.commons_java.ext.isNotNull
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.xwray.groupie.Item

class AddingWordItem(
    private val draftWord: DraftWord,
    private val isEditMode: Boolean,
    private val onWordChanged: (String) -> Unit,
    private val onTranslateChanged: (String) -> Unit,
    private val onAddWordCategoryClick: () -> Unit,
    private val onAddWordTypeClick: () -> Unit,
    private val onAddWordParentClick: () -> Unit,
    private val onCategoryDeleteClick: (WordCategory) -> Unit,
    private val onWordCategoryClick: () -> Unit,
    private val onWordTypeDeleteClick: (WordType) -> Unit,
    private val onWordTypeClick: () -> Unit,
    private val onDeleteWordParentClick: () -> Unit,
    private val onWebClick: (String) -> Unit,
) : BaseBindableItem<CommonItemAddingWordBinding>() {
    companion object {
        private const val WORD_CHANGED_PAYLOAD = "word_changed_payload"
        private const val TRANSLATE_CHANGED_PAYLOAD = "translate_changed_payload"
        private const val WORD_TYPE_CHANGED_PAYLOAD = "word_type_changed_payload"
        private const val CATEGORY_TYPE_CHANGED_PAYLOAD = "category_type_changed_payload"
    }

    private var inflater: LayoutInflater? = null

    override fun getLayout(): Int = R.layout.common_item_adding_word

    override fun initializeViewBinding(view: View) = CommonItemAddingWordBinding.bind(view)

    override fun onViewBindingCreated(viewBinding: CommonItemAddingWordBinding) = with(viewBinding) {
        super.onViewBindingCreated(viewBinding)
        ibAddCategory.setOnClickListener { onAddWordCategoryClick.invoke() }
        ibAddWordType.setOnClickListener { onAddWordTypeClick.invoke() }
        ibAddWordParent.setOnClickListener { onAddWordParentClick.invoke() }
    }

    override fun bind(
        viewBinding: CommonItemAddingWordBinding,
        position: Int,
        payload: String
    ) = with(viewBinding) {
        initLayoutInflater()

        if (payload.contains(WORD_CHANGED_PAYLOAD)) setWordState()
        if (payload.contains(TRANSLATE_CHANGED_PAYLOAD)) setTranslateState()
        if (payload.contains(WORD_TYPE_CHANGED_PAYLOAD)) setWordTypes()
        if (payload.contains(CATEGORY_TYPE_CHANGED_PAYLOAD)) setCategories()
    }

    override fun bind(viewBinding: CommonItemAddingWordBinding, position: Int): Unit = with(viewBinding) {
        etWord.isEnabled = !isEditMode

        initLayoutInflater()
        setWordState()
        setTranslateState()
        setCategories()
        setWordTypes()
        initParentWord()
    }

    private fun CommonItemAddingWordBinding.initLayoutInflater() {
        if (inflater == null) {
            inflater = LayoutInflater.from(context)
        }
    }

    override fun setChangePayload(newItem: Item<*>, payloads: MutableSet<String>) {
        if (newItem !is AddingWordItem) return
        if (draftWord.word != newItem.draftWord.word) payloads.add(WORD_CHANGED_PAYLOAD)
        if (draftWord.translates != newItem.draftWord.translates) payloads.add(TRANSLATE_CHANGED_PAYLOAD)
        if (!draftWord.categories.deepEquals(newItem.draftWord.categories)) payloads.add(CATEGORY_TYPE_CHANGED_PAYLOAD)
        if (!draftWord.wordTypes.deepEquals(newItem.draftWord.wordTypes)) payloads.add(WORD_TYPE_CHANGED_PAYLOAD)
    }

    override fun isSameAs(other: Item<*>): Boolean = other is AddingWordItem

    override fun hasSameContentAs(other: Item<*>): Boolean =
        other is AddingWordItem && isAddingWordItem(other)

    private fun isAddingWordItem(other: AddingWordItem): Boolean =
        other.draftWord.word == draftWord.word
                && other.draftWord.translates == draftWord.translates
                && other.draftWord.categories.deepEquals(draftWord.categories)
                && other.draftWord.wordTypes.deepEquals(draftWord.wordTypes)
                && other.draftWord.parentWord == draftWord.parentWord

    private fun CommonItemAddingWordBinding.setWordState() {
        etWord.setOrIgnore(draftWord.word)
        etWord.doOnTextChanged { text, _, _, _ -> onWordChanged.invoke(text.toString()) }

        ibWeb.isEnabled = draftWord.word.isNotBlank()
        ibWeb.setOnClickListener { onWebClick.invoke(draftWord.word) }
    }

    private fun CommonItemAddingWordBinding.setTranslateState() {
        etTranslate.setOrIgnore(draftWord.translates)
        etTranslate.doOnTextChanged { text, _, _, _ -> onTranslateChanged.invoke(text.toString()) }
    }

    private fun CommonItemAddingWordBinding.initParentWord() {
        val inflater = inflater ?: return
        chipGroupWordParent.removeViews(0, chipGroupWordParent.size - 1)
        if (draftWord.parentWord.isNotNull()) {
            ibAddWordParent.gone()
            chipGroupWordParent.addChip(
                inflater = inflater,
                text = draftWord.parentWord?.word,
                closeIconClickListener = { onDeleteWordParentClick.invoke() },
                chipClickListener = { }
            )
        } else {
            ibAddWordParent.show()
        }
    }

    private fun CommonItemAddingWordBinding.setWordTypes() {
        val inflater = inflater ?: return
        chipGroupWordType.removeViews(0, chipGroupWordType.size - 1)
        draftWord.wordTypes.forEach { type ->
            chipGroupWordType.addChip(
                inflater = inflater,
                text = type.name,
                closeIconClickListener = { onWordTypeDeleteClick.invoke(type) },
                chipClickListener = { onWordTypeClick.invoke() }
            )
        }
    }

    private fun CommonItemAddingWordBinding.setCategories() {
        val inflater = inflater ?: return
        chipGroupCategory.removeViews(0, chipGroupCategory.size - 1)
        draftWord.categories.forEach { category ->
            chipGroupCategory.addChip(
                inflater = inflater,
                text = category.name,
                closeIconClickListener = { onCategoryDeleteClick.invoke(category) },
                chipClickListener = { onWordCategoryClick.invoke() }
            )
        }
    }

    private fun ChipGroup.addChip(
        inflater: LayoutInflater,
        text: String?,
        closeIconClickListener: View.OnClickListener,
        chipClickListener: View.OnClickListener
    ) {
        val chip = inflater.inflate(R.layout.item_chip, this, false) as Chip
        chip.text = text
        chip.setOnCloseIconClickListener(closeIconClickListener)
        chip.setOnClickListener(chipClickListener)
        addView(chip, childCount - 1)
    }
}