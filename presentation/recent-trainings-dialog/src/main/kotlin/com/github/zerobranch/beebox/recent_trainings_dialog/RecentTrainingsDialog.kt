package com.github.zerobranch.beebox.recent_trainings_dialog

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.commons_android.base.DialogDispatcher
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.attachSwipeToDelete
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainBottomSheetDialog
import com.github.zerobranch.beebox.commons_app.utils.title
import com.github.zerobranch.beebox.domain.models.training.ProgressTraining
import com.github.zerobranch.beebox.recent_trainings_dialog.databinding.DialogRecentTrainingsBinding
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class RecentTrainingsDialog : BaseMainBottomSheetDialog(R.layout.dialog_recent_trainings), DialogDispatcher {
    private companion object {
        const val RECENT_TRAININGS_DELETE_RESULT_KEY = "recent_trainings_delete_result_key"
    }

    override val isExpanded: Boolean = true
    override val isFullScreen: Boolean = false

    override fun getTheme(): Int = CommonR.style.CommonBaseBottomSheetDialogTheme_Large

    @Inject
    internal lateinit var navProvider: RecentTrainingsNavProvider

    private val viewModel: RecentTrainingsViewModel by viewModels()

    private val binding by viewBinding(DialogRecentTrainingsBinding::bind)
    private val itemsAdapter: GroupieAdapter by groupAdapter { binding.rvTraining }

    override fun initUi() = with(binding) {
        initEdgeToEdgeMode()
        initRvTraining()
        ibClose.setOnClickListener { viewModel.onCloseClick() }
    }

    override fun observeOnStates() {
        viewModel.uiState
            .onEach { state ->
                itemsAdapter.updateAsync(
                    state.map { item ->
                        RecentTrainingItem(item)
                    }
                )
            }
            .launchWhenCreated()

        viewModel.action.onEach { action ->
            when (action) {
                RecentTrainingsAction.Exit -> exit()
                is RecentTrainingsAction.Reload -> itemsAdapter.notifyItemChanged(action.position)
                is RecentTrainingsAction.GoToTraining -> {
                    navigate(navProvider.toTrainingScreen(action.progressTraining))
                }
                is RecentTrainingsAction.OpenDeleteDialog -> navigate(
                    navProvider.toDeleteDialog(
                        resultKey = RECENT_TRAININGS_DELETE_RESULT_KEY,
                        progressTraining = action.progressTraining,
                        title = getString(
                            CommonR.string.common_contains_words_title,
                            getString(action.progressTraining.mainTrainingType.title)
                        ),
                        actionBtnText = getString(CommonR.string.common_delete),
                        cancelBtnText = getString(CommonR.string.common_cancel)
                    )
                )
            }
        }.launchWhenCreated()
    }

    override fun onDialogAction(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogAction(tag, resultKey, data)
        if (resultKey == RECENT_TRAININGS_DELETE_RESULT_KEY) {
            viewModel.onDialogDeleteClick(data as? ProgressTraining ?: return)
        }
    }

    override fun onDialogCancel(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogCancel(tag, resultKey, data)
        viewModel.onDialogCancelClick(data as? ProgressTraining ?: return)
    }

    private fun initRvTraining() = with(binding.rvTraining) {
        layoutManager = LinearLayoutManager(requireContext())

        adapter = itemsAdapter
        itemsAdapter.setOnItemClickListener { item, _ ->
            when (item) {
                is RecentTrainingItem -> viewModel.onRecentTrainingClick(item.training)
            }
        }

        attachSwipeToDelete(
            endIcon = CommonR.drawable.common_ic_delete,
            backgroundColor = colorInt(CommonR.color.common_alternative_blue_05),
            directions = ItemTouchHelper.LEFT
        ) { item, direction ->
            val training = (item as RecentTrainingItem).training
            if (direction == ItemTouchHelper.LEFT) {
                viewModel.onSwipeDelete(training)
            }
        }
    }

    private fun initEdgeToEdgeMode() {
        binding.root.applyInsetter {
            type(navigationBars = true) { padding(bottom = true) }
        }
    }
}