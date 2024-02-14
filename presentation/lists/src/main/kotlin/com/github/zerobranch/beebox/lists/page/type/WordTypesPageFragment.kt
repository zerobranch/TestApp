package com.github.zerobranch.beebox.lists.page.type

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.commons_android.base.DialogDispatcher
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.addDividerDecoration
import com.github.zerobranch.beebox.commons_android.utils.ext.attachSwipeToDelete
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.dp
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseViewPagerFragment
import com.github.zerobranch.beebox.commons_items.EmptyItem
import com.github.zerobranch.beebox.commons_items.WordTypeItem
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.lists.R
import com.github.zerobranch.beebox.lists.databinding.FragmentWordTypePageBinding
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class WordTypesPageFragment : BaseViewPagerFragment(R.layout.fragment_word_type_page), DialogDispatcher {
    private companion object {
        const val WORD_TYPES_PAGE_DELETE_RESULT_KEY = "word_types_page_delete_result_key"
    }

    @Inject
    internal lateinit var navProvider: WordTypesPageNavProvider

    private val viewModel: WordTypesPageViewModel by viewModels()
    private val binding by viewBinding(FragmentWordTypePageBinding::bind)
    private val wordTypesAdapter: GroupieAdapter by groupAdapter { binding.rvTypes }

    override fun initUi() {
        super.initUi()
        initRvTypes()
        initEdgeToEdgeMode()
    }

    override fun observeOnStates() {
        viewModel.action
            .onEach { action ->
                when (action) {
                    is WordTypesPageAction.OpenDeleteDialog -> navigate(
                        navProvider.toDeleteDialog(
                            resultKey = WORD_TYPES_PAGE_DELETE_RESULT_KEY,
                            wordType = action.wordType,
                            title = getString(
                                CommonR.string.common_contains_words_title,
                                action.wordType.name
                            ),
                            description = getString(CommonR.string.common_contains_words_in_type_description),
                            actionBtnText = getString(CommonR.string.common_delete),
                            cancelBtnText = getString(CommonR.string.common_cancel)
                        )
                    )
                    is WordTypesPageAction.OpenEditWordTypeDialog -> {
                        navigate(navProvider.toEditWordTypeDialog(action.wordType))
                    }
                    is WordTypesPageAction.Reload -> {
                        wordTypesAdapter.notifyItemChanged(action.position)
                    }
                    is WordTypesPageAction.ToTypeWordList -> {
                        navigate(navProvider.toTypeWordList(action.wordType))
                    }
                }
            }
            .launchWhenCreated()

        viewModel.wordTypesState.onEach { wordTypes ->
            wordTypes ?: return@onEach

            if (wordTypes.isEmpty()) {
                wordTypesAdapter.clear()
                wordTypesAdapter.add(EmptyItem())
            } else {
                wordTypesAdapter.updateAsync(
                    wordTypes.map { WordTypeItem(it) }
                )
            }
        }.launchWhenCreated()
    }

    override fun onDialogAction(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogAction(tag, resultKey, data)
        if (resultKey == WORD_TYPES_PAGE_DELETE_RESULT_KEY) {
            viewModel.onDialogDeleteClick(data as? WordType ?: return)
        }
    }

    override fun onDialogCancel(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogCancel(tag, resultKey, data)
        viewModel.onDialogCancelClick(data as? WordType ?: return)
    }

    private fun initRvTypes() = with(binding.rvTypes) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = wordTypesAdapter
        addDividerDecoration(dividerInsetStart = 0, dividerInsetEnd = 20.dp) { it !is EmptyItem }
        wordTypesAdapter.setOnItemClickListener { item, _ ->
            when (item) {
                is WordTypeItem -> viewModel.onWordTypeClick(item.item)
            }
        }

        attachSwipeToDelete(
            endIcon = CommonR.drawable.common_ic_delete,
            startIcon = CommonR.drawable.common_ic_edit,
            backgroundColor = colorInt(CommonR.color.common_alternative_blue_05),
            directions = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            swipePredicate = { it !is EmptyItem }
        ) { item, direction ->
            val wordType = (item as WordTypeItem).item
            if (direction == ItemTouchHelper.LEFT) {
                viewModel.onSwipeDelete(wordType)
            } else if (direction == ItemTouchHelper.RIGHT) {
                viewModel.onSwipeEdit(wordType)
            }
        }
    }

    private fun initEdgeToEdgeMode() {
        binding.rvTypes.applyInsetter {
            type(navigationBars = true) { padding(bottom = true) }
        }
    }
}

