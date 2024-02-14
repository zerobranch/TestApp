package com.github.zerobranch.beebox.add_word_dialog

import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.add_word_dialog.databinding.DialogAddWordBinding
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
import com.github.zerobranch.beebox.domain.models.words.Word
import com.xwray.groupie.Group
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class AddWordDialog : BaseMainBottomSheetDialog(R.layout.dialog_add_word) {
    companion object {
        const val IS_EDIT_MODE_KEY = "is_edit_mode_key"
    }

    override val isExpanded: Boolean = true
    override val isFullScreen: Boolean = true

    override fun getTheme(): Int = CommonR.style.CommonBaseBottomSheetDialogTheme_Large

    @Inject
    internal lateinit var navProvider: AddWordNavProvider

    @Inject
    internal lateinit var factory: ViewModelFactory
    private val viewModel: AddWordViewModel by lazy { factory.get(isEditMode) }

    private val binding by viewBinding(DialogAddWordBinding::bind)
    private val isEditMode by args<Boolean>(IS_EDIT_MODE_KEY)
    private val itemsAdapter: GroupieAdapter by groupAdapter { binding.rvItems }

    override fun initUi() {
        super.initUi()
        initList()
        subscribeToNavResult()
        initEdgeToEdgeMode()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun observeOnStates() {
        observeOnWordState()

        viewModel.action.onEach { action ->
            when (action) {
                is AddWordAction.OpenInBrowser -> openWordInBrowser(action.url)
                AddWordAction.ValidationError -> showError(CommonR.string.common_create_word_validation_error)
                AddWordAction.BaseError -> showError(CommonR.string.common_error_default)
                AddWordAction.Exit -> exit()
                AddWordAction.GoToChooseCategory -> navigate(navProvider.toChooseCategoryScreen)
                AddWordAction.GoToChooseWordType -> navigate(navProvider.toChooseWordTypeScreen)
                AddWordAction.GoToLocalSearch -> navigate(navProvider.toLocalSearchScreen)
            }
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

        binding.ibApply.setOnClickListener { viewModel.onApplyClick() }
    }

    private fun subscribeToNavResult() {
        (getNavResult<Word>(BundleKeys.SEARCH_WORD_RESULT_KEY) ?: return)
            .onEach { word -> viewModel.onWordParentSelected(word) }
            .launchWhenCreated()
    }

    private fun observeOnWordState() {
        viewModel.wordState.onEach { wordState ->
            wordState ?: return@onEach

            binding.ibApply.setImageResource(
                if (wordState.isEditMode) CommonR.drawable.common_ic_simple_check
                else CommonR.drawable.common_ic_simple_add
            )

            itemsAdapter.updateAsync(
                mutableListOf<Group>().apply {
                    add(
                        AddingWordItem(
                            draftWord = wordState.draftWord,
                            isEditMode = wordState.isEditMode,
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
                        wordState.draftWord.hints.map { hint ->
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
                        wordState.draftWord.transcriptions.map { transcription ->
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
                    add(CommentItem(wordState.draftWord, viewModel::onCommentChanged))
                }
            )
        }.launchWhenCreated()
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

