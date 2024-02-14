package com.github.zerobranch.beebox.word_groups_dialog

import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.commons_android.base.DialogDispatcher
import com.github.zerobranch.beebox.commons_android.utils.delegates.args
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainBottomSheetDialog
import com.github.zerobranch.beebox.commons_app.base.BundleKeys
import com.github.zerobranch.beebox.commons_app.utils.openWordInBrowser
import com.github.zerobranch.beebox.commons_app.utils.showError
import com.github.zerobranch.beebox.commons_items.creating.AddHintItem
import com.github.zerobranch.beebox.commons_items.creating.AddTranscriptionItem
import com.github.zerobranch.beebox.commons_items.creating.AddingWordItem
import com.github.zerobranch.beebox.commons_items.creating.CommentItem
import com.github.zerobranch.beebox.commons_items.creating.HintItem
import com.github.zerobranch.beebox.commons_items.creating.TitleItem
import com.github.zerobranch.beebox.commons_items.creating.TranscriptionItem
import com.github.zerobranch.beebox.commons_view.notification.NotificationBar
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.word_groups_dialog.databinding.DialogWordGroupBinding
import com.xwray.groupie.Group
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class WordGroupDialog : BaseMainBottomSheetDialog(R.layout.dialog_word_group), DialogDispatcher {
    companion object {
        const val DRAFT_WORDS_KEY = "draft_words_key"
        private const val CLOSE_DIALOG_RESULT_KEY = "close_dialog_result_key"
    }

    override val isExpanded: Boolean = true
    override val isFullScreen: Boolean = true
    override val isCancelableMode: Boolean = false

    override fun getTheme(): Int = CommonR.style.CommonBaseBottomSheetDialogTheme_Large

    @Inject
    internal lateinit var factory: ViewModelFactory
    private val viewModel: WordGroupViewModel by lazy { factory.get(drafts.toMutableList()) }

    private val drafts by args<List<DraftWord>>(DRAFT_WORDS_KEY)

    @Inject
    internal lateinit var navProvider: WordGroupNavProvider

    private val binding by viewBinding(DialogWordGroupBinding::bind)
    private val itemsAdapter: GroupieAdapter by groupAdapter { binding.rvItems }

    override fun initUi() {
        super.initUi()
        initList()
        subscribeToNavResult()
        initEdgeToEdgeMode()

        binding.ibClose.setOnClickListener { viewModel.onCloseClick() }
        binding.ibDelete.setOnClickListener { viewModel.onDeleteClick() }
        binding.btnNext.setOnClickListener { viewModel.onNextClick() }
        binding.btnPrev.setOnClickListener { viewModel.onPreviousClick() }
        binding.btnSave.setOnClickListener { viewModel.onSaveClick() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun observeOnStates() {
        observeOnUiState()
        observeOnActions()
    }

    override fun onDialogAction(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogAction(tag, resultKey, data)

        when (resultKey) {
            BundleKeys.WORD_PAGE_DELETE_RESULT_KEY -> {
                viewModel.onDialogDeleteClick(data as? DraftWord ?: return)
            }
            CLOSE_DIALOG_RESULT_KEY -> viewModel.onDialogDeleteClick()
        }
    }

    private fun observeOnActions() {
        viewModel.action.onEach { action ->
            when (action) {
                is WordGroupAction.OpenInBrowser -> openWordInBrowser(action.url)
                is WordGroupAction.OpenDeleteDialog -> {
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
                }
                WordGroupAction.ValidationError -> showError(CommonR.string.common_create_word_validation_error)
                WordGroupAction.BaseError -> showError(CommonR.string.common_error_default)
                WordGroupAction.Exit -> exit()
                WordGroupAction.GoToChooseCategory -> navigate(navProvider.toChooseCategoryScreen)
                WordGroupAction.GoToChooseWordType -> navigate(navProvider.toChooseWordTypeScreen)
                WordGroupAction.GoToLocalSearch -> navigate(navProvider.toLocalSearchScreen)
                WordGroupAction.OpenExitDialog -> {
                    navigate(
                        navProvider.toOpenExitDialog(
                            resultKey = CLOSE_DIALOG_RESULT_KEY,
                            title = getString(
                                CommonR.string.common_exit_without_save_title,
                            ),
                            actionBtnText = getString(CommonR.string.common_exit),
                            cancelBtnText = getString(CommonR.string.common_cancel)
                        )
                    )
                }
            }
        }.launchWhenCreated()
    }

    private fun observeOnUiState() {
        viewModel.state.onEach { state ->
            state ?: return@onEach
            setControlState(state)

            itemsAdapter.updateAsync(
                mutableListOf<Group>().apply {
                    add(
                        AddingWordItem(
                            state.draft,
                            isEditMode = true,
                            onWordChanged = viewModel::onWordChanged,
                            onTranslateChanged = viewModel::onTranslateChanged,
                            onAddWordCategoryClick = viewModel::onAddWordCategoryClick,
                            onAddWordTypeClick = viewModel::onAddWordTypeClick,
                            onAddWordParentClick = viewModel::onAddWordParentClick,
                            onCategoryDeleteClick = viewModel::onCategoryDeleteClick,
                            onWordCategoryClick = viewModel::onWordCategoryClick,
                            onWordTypeDeleteClick = viewModel::onWordTypeDeleteClick,
                            onWordTypeClick = viewModel::onWordTypeClick,
                            onDeleteWordParentClick = viewModel::onDeleteWordParentClick,
                            onWebClick = viewModel::onWebClick,
                        )
                    )
                    add(TitleItem(getString(CommonR.string.common_hint_title)))
                    addAll(
                        state.draft.hints.map { hint ->
                            HintItem(
                                hint = hint,
                                onHintChanged = viewModel::onHintChanged,
                                onDeleteClick = viewModel::onHintDeleteClick
                            )
                        }
                    )
                    add(AddHintItem())
                    add(TitleItem(getString(CommonR.string.common_transcription)))
                    addAll(
                        state.draft.transcriptions.map { transcription ->
                            TranscriptionItem(
                                transcription = transcription,
                                onTranscriptionTitleChanged = viewModel::onTranscriptionTitleChanged,
                                onFirstTranscriptionHintChanged = viewModel::onFirstTranscriptionHintChanged,
                                onFirstTranscriptionChanged = viewModel::onFirstTranscriptionChanged,
                                onSecondTranscriptionHintChanged = viewModel::onSecondTranscriptionHintChanged,
                                onSecondTranscriptionChanged = viewModel::onSecondTranscriptionChanged,
                                onDeleteClick = viewModel::onTranscriptionDeleteClick,
                                onTranscriptionAudioClick = viewModel::onTranscriptionAudioClick,
                            )
                        }
                    )
                    add(AddTranscriptionItem())

                    add(TitleItem(getString(CommonR.string.common_comment)))
                    add(CommentItem(state.draft, viewModel::onCommentChanged))
                }
            )
        }.launchWhenCreated()
    }

    private fun initList() = with(binding.rvItems) {
        updatePadding(bottom = dialogTopOffset + paddingBottom)
        layoutManager = LinearLayoutManager(requireContext())
        adapter = itemsAdapter
        itemsAdapter.setOnItemClickListener { item, _ ->
            when (item) {
                is AddTranscriptionItem -> viewModel.addTranscriptionClick()
                is AddHintItem -> viewModel.addHintClick()
            }
        }
    }

    private fun subscribeToNavResult() {
        (getNavResult<Word>(BundleKeys.SEARCH_WORD_RESULT_KEY) ?: return)
            .onEach { word -> viewModel.onWordParentSelected(word) }
            .launchWhenCreated()
    }

    private fun setControlState(state: WordGroupState) = with(binding) {
        when (state.controlState) {
            ControlState.START -> {
                btnNext.isEnabled = true
                btnPrev.isEnabled = false
                btnSave.isEnabled = false
            }
            ControlState.MIDDLE -> {
                btnNext.isEnabled = true
                btnPrev.isEnabled = true
                btnSave.isEnabled = false
            }
            ControlState.END -> {
                btnNext.isEnabled = false
                btnPrev.isEnabled = true
                btnSave.isEnabled = true
            }
            ControlState.SINGLE -> {
                btnNext.isEnabled = false
                btnPrev.isEnabled = false
                btnSave.isEnabled = true
            }
        }
    }

    private fun showError(text: Int) {
        binding.root.showError(getString(text), duration = NotificationBar.SHORT_DURATION)
    }

    private fun initEdgeToEdgeMode() {
        binding.rvItems.applyInsetter {
            type(navigationBars = true, ime = true) { padding(bottom = true) }
        }
    }
}

