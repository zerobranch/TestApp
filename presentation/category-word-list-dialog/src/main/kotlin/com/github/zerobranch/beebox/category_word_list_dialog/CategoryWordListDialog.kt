package com.github.zerobranch.beebox.category_word_list_dialog

import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.category_word_list_dialog.databinding.DialogCategoryWordListBinding
import com.github.zerobranch.beebox.commons_android.base.DialogDispatcher
import com.github.zerobranch.beebox.commons_android.utils.delegates.args
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.addDividerDecoration
import com.github.zerobranch.beebox.commons_android.utils.ext.attachSwipeToDelete
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.dp
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.ext.navigationBarHeight
import com.github.zerobranch.beebox.commons_android.utils.ext.safePost
import com.github.zerobranch.beebox.commons_android.utils.ext.updateMargins
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainBottomSheetDialog
import com.github.zerobranch.beebox.commons_app.base.BundleKeys
import com.github.zerobranch.beebox.commons_items.EmptyItem
import com.github.zerobranch.beebox.commons_items.WordItem
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.words.Word
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.Insetter
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class CategoryWordListDialog : BaseMainBottomSheetDialog(R.layout.dialog_category_word_list), DialogDispatcher {
    companion object {
        const val WORD_CATEGORY_KEY = "word_category_key"
    }

    override val isExpanded: Boolean = true
    override val isFullScreen: Boolean = true

    override fun getTheme(): Int = CommonR.style.CommonBaseBottomSheetDialogTheme_Large

    @Inject
    internal lateinit var navProvider: CategoryWordListNavProvider

    @Inject
    internal lateinit var factory: ViewModelFactory
    private val viewModel: CategoryWordListViewModel by lazy { factory.get(wordCategory) }

    private val binding by viewBinding(DialogCategoryWordListBinding::bind)
    private val wordsAdapter: GroupieAdapter by groupAdapter { binding.rvWords }
    private val wordCategory by args<WordCategory>(WORD_CATEGORY_KEY)

    override fun initUi() {
        super.initUi()
        initRvCategories()
        initEdgeToEdgeMode()

        binding.fabAdd.setOnClickListener { viewModel.onAddClick() }
    }

    override fun observeOnStates() {
        observeOnAction()

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

    private fun observeOnAction() {
        viewModel.action.onEach { action ->
            when (action) {
                is CategoryWordListAction.OpenDeleteDialog ->
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
                is CategoryWordListAction.OpenEditWordDialog -> {
                    navigate(navProvider.toEditWordScreen)
                }
                is CategoryWordListAction.Reload -> {
                    wordsAdapter.notifyItemChanged(action.position)
                }
                is CategoryWordListAction.OpenConfirmDeleteDialog -> {
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
                is CategoryWordListAction.ToAddWord -> {
                    navigate(navProvider.toAddWordScreen(action.wordCategory))
                }
            }
        }.launchWhenCreated()
    }

    private fun initRvCategories() = with(binding.rvWords) {
        updatePadding(bottom = dialogTopOffset + paddingBottom)
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

        Insetter
            .builder()
            .setOnApplyInsetsListener { _, insets, state ->
                binding.fabAdd.updateMargins(
                    bottom = state.margins.bottom + dialogTopOffset + insets.navigationBarHeight
                )
            }
            .consume(Insetter.CONSUME_NONE)
            .build()
            .applyToView(binding.fabAdd)
    }
}