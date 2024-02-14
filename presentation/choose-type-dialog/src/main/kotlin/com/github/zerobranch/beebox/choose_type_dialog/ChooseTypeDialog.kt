package com.github.zerobranch.beebox.choose_type_dialog

import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.choose_type_dialog.databinding.DialogChooseTypeBinding
import com.github.zerobranch.beebox.commons_android.base.DialogDispatcher
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.addDividerDecoration
import com.github.zerobranch.beebox.commons_android.utils.ext.attachSwipeToDelete
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.dp
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainBottomSheetDialog
import com.github.zerobranch.beebox.commons_items.EmptyItem
import com.github.zerobranch.beebox.commons_items.WordTypeItem
import com.github.zerobranch.beebox.domain.models.WordType
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class ChooseTypeDialog : BaseMainBottomSheetDialog(R.layout.dialog_choose_type), DialogDispatcher {
    private companion object {
        const val CHOOSE_TYPE_DELETE_RESULT_KEY = "choose_type_delete_result_key"
    }

    override val isExpanded: Boolean = true
    override val isFullScreen: Boolean = false

    override fun getTheme(): Int = CommonR.style.CommonBaseBottomSheetDialogTheme_Large

    @Inject
    internal lateinit var navProvider: ChooseTypeNavProvider

    private val viewModel: ChooseTypeViewModel by viewModels()
    private val binding by viewBinding(DialogChooseTypeBinding::bind)
    private val wordTypesAdapter: GroupieAdapter by groupAdapter { binding.rvTypes }

    override fun initUi() {
        super.initUi()
        initRvTypes()
        initClickListeners()
        initEdgeToEdgeMode()
    }

    override fun observeOnStates() {
        viewModel.action
            .onEach { action ->
                when (action) {
                    is ChooseTypeAction.OpenDeleteDialog -> navigate(
                        navProvider.toDeleteDialog(
                            resultKey = CHOOSE_TYPE_DELETE_RESULT_KEY,
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
                    is ChooseTypeAction.OpenCreateWordTypeDialog -> {
                        navigate(navProvider.toCreateWordTypeDialog)
                    }
                    is ChooseTypeAction.OpenEditWordTypeDialog -> {
                        navigate(navProvider.toEditWordTypeDialog(action.wordType))
                    }
                    is ChooseTypeAction.Reload -> {
                        wordTypesAdapter.notifyItemChanged(action.position)
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
        if (resultKey == CHOOSE_TYPE_DELETE_RESULT_KEY) {
            viewModel.onDialogDeleteClick(data as? WordType ?: return)
        }
    }

    override fun onDialogCancel(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogCancel(tag, resultKey, data)
        viewModel.onDialogCancelClick(data as? WordType ?: return)
    }

    private fun initRvTypes() = with(binding.rvTypes) {
        updatePadding(bottom = dialogTopOffset + paddingBottom)
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

    private fun initClickListeners() = with(binding) {
        ibAdd.setOnClickListener { viewModel.onAddWordTypeClick() }
    }

    private fun initEdgeToEdgeMode() {
        binding.rvTypes.applyInsetter {
            type(navigationBars = true) { padding(bottom = true) }
        }
    }
}

