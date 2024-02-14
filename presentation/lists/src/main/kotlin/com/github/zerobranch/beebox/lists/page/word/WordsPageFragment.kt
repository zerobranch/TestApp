package com.github.zerobranch.beebox.lists.page.word

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
import com.github.zerobranch.beebox.commons_android.utils.ext.safePost
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseViewPagerFragment
import com.github.zerobranch.beebox.commons_app.base.BundleKeys
import com.github.zerobranch.beebox.commons_items.EmptyItem
import com.github.zerobranch.beebox.commons_items.WordItem
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.lists.R
import com.github.zerobranch.beebox.lists.databinding.FragmentWordPageBinding
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class WordsPageFragment : BaseViewPagerFragment(R.layout.fragment_word_page), DialogDispatcher {

    @Inject
    internal lateinit var navProvider: WordPageNavProvider

    private val viewModel: WordsPageViewModel by viewModels()
    private val binding by viewBinding(FragmentWordPageBinding::bind)
    private val wordsAdapter: GroupieAdapter by groupAdapter { binding.rvWords }

    override fun initUi() {
        super.initUi()
        initRvCategories()
        initEdgeToEdgeMode()
    }

    override fun observeOnStates() {
        viewModel.action.onEach { action ->
            when (action) {
                is WordsPageAction.OpenDeleteDialog ->
                    navigate(
                        navProvider.toDeleteDialog(
                            resultKey = BundleKeys.WORD_PAGE_DELETE_RESULT_KEY,
                            word = action.word,
                            title = getString(
                                CommonR.string.common_contains_words_title,
                                action.word.word
                            ),
                            actionBtnText = getString(CommonR.string.common_delete),
                            cancelBtnText = getString(CommonR.string.common_cancel)
                        )
                    )
                is WordsPageAction.OpenEditWordDialog -> {
                    navigate(navProvider.toEditWordScreen)
                }
                is WordsPageAction.Reload -> {
                    wordsAdapter.notifyItemChanged(action.position)
                }
                is WordsPageAction.OpenConfirmDeleteDialog -> {
                    safePost {
                        navigate(
                            navProvider.toConfirmDeleteDialog(
                                resultKey = BundleKeys.WORD_PAGE_CONFIRM_DELETE_RESULT_KEY,
                                word = action.word,
                                title = getString(
                                    CommonR.string.common_contains_child_words_title,
                                    action.word.word
                                ),
                                actionBtnText = getString(CommonR.string.common_delete),
                                cancelBtnText = getString(CommonR.string.common_cancel)
                            )
                        )
                    }
                }
            }
        }.launchWhenCreated()

        viewModel.wordsState.onEach { words ->
            words ?: return@onEach

            if (words.isEmpty()) {
                wordsAdapter.clear()
                wordsAdapter.add(EmptyItem())
            } else {
                wordsAdapter.updateAsync(
                    words.map { WordItem(it) }
                )
            }
        }.launchWhenCreated()
    }

    override fun onDialogAction(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogAction(tag, resultKey, data)

        when (resultKey) {
            BundleKeys.WORD_PAGE_DELETE_RESULT_KEY -> {
                viewModel.onDialogDeleteClick(data as? Word ?: return)
            }
            BundleKeys.WORD_PAGE_CONFIRM_DELETE_RESULT_KEY -> {
                viewModel.onDialogConfirmDeleteClick(data as? Word ?: return)
            }
        }
    }

    override fun onDialogCancel(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogCancel(tag, resultKey, data)
        viewModel.onDialogCancelClick(data as? Word ?: return)
    }

    private fun initRvCategories() = with(binding.rvWords) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = wordsAdapter
        addDividerDecoration(dividerInsetStart = 0, dividerInsetEnd = 20.dp) { it !is EmptyItem }
        wordsAdapter.setOnItemClickListener { item, _ ->
            when (item) {
                is WordItem -> viewModel.onWordClick(item.item)
            }
        }

        attachSwipeToDelete(
            endIcon = CommonR.drawable.common_ic_delete,
            backgroundColor = colorInt(CommonR.color.common_alternative_blue_05),
            directions = ItemTouchHelper.LEFT,
            swipePredicate = { it !is EmptyItem }
        ) { item, direction ->
            val word = (item as WordItem).item
            if (direction == ItemTouchHelper.LEFT) {
                viewModel.onSwipeDelete(word)
            }
        }
    }

    private fun initEdgeToEdgeMode() {
        binding.rvWords.applyInsetter {
            type(navigationBars = true) { padding(bottom = true) }
        }
    }
}