package com.github.zerobranch.beebox.search

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.delegates.nullableArgs
import com.github.zerobranch.beebox.commons_android.utils.ext.attachSwipeToDelete
import com.github.zerobranch.beebox.commons_android.utils.ext.colorInt
import com.github.zerobranch.beebox.commons_android.utils.ext.gone
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.ext.safePostDelayed
import com.github.zerobranch.beebox.commons_android.utils.ext.setDrawables
import com.github.zerobranch.beebox.commons_android.utils.ext.setOnEndDrawableClickListener
import com.github.zerobranch.beebox.commons_android.utils.ext.show
import com.github.zerobranch.beebox.commons_android.utils.ext.showKeyboard
import com.github.zerobranch.beebox.commons_android.utils.ext.updateMargins
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainBottomSheetDialog
import com.github.zerobranch.beebox.commons_app.utils.showBaseError
import com.github.zerobranch.beebox.commons_app.utils.showNetworkError
import com.github.zerobranch.beebox.commons_java.Status
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.SearchWord
import com.github.zerobranch.beebox.domain.models.words.SimpleWord
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.search.databinding.DialogSearchBinding
import com.github.zerobranch.beebox.search.items.DefaultWordItem
import com.github.zerobranch.beebox.search.items.DoneButtonItem
import com.github.zerobranch.beebox.search.items.PreWordItem
import com.github.zerobranch.beebox.search.items.SearchItem
import com.xwray.groupie.Group
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class SearchDialog : BaseMainBottomSheetDialog(R.layout.dialog_search) {
    companion object {
        const val WORD_TYPE_KEY = "word_type_key"
        const val WORD_CATEGORY_KEY = "word_category_key"
    }

    override val isExpanded: Boolean = true
    override val isFullScreen: Boolean = true

    override fun getTheme(): Int = CommonR.style.CommonBaseBottomSheetDialogTheme_Large

    @Inject
    internal lateinit var navProvider: SearchNavProvider

    @Inject
    internal lateinit var factory: ViewModelFactory
    private val viewModel: SearchViewModel by lazy { factory.get(wordType, wordCategory) }

    private val wordType by nullableArgs<WordType>(WORD_TYPE_KEY)
    private val wordCategory by nullableArgs<WordCategory>(WORD_CATEGORY_KEY)
    private val binding by viewBinding(DialogSearchBinding::bind)
    private val searchAdapter: GroupieAdapter by groupAdapter { binding.rvSearch }
    private val wordsGroupAdapter: GroupieAdapter by groupAdapter { binding.rvWords }
    private var oldItemTouchHelper: ItemTouchHelper? = null

    override fun initUi() {
        super.initUi()
        initRvSearch()
        initRvWords()
        initSearchField()
        initClicksListeners()
        initEdgeToEdgeMode()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
    }

    override fun observeOnStates() {
        observeOnWordsGroupState()
        observeOnAction()
        observeOnSearchState()
    }

    private fun observeOnWordsGroupState() {
        viewModel.wordsGroupState
            .onEach { state ->
                binding.ibAdd.isEnabled = !state.isWordGroupActive

                wordsGroupAdapter.replaceAll(
                    mutableListOf<Group>().apply {
                        addAll(
                            if (state.isWordGroupActive) {
                                state.words.map { word -> PreWordItem(word) }.reversed()
                            } else {
                                state.words.map { word ->
                                    DefaultWordItem(word as Word, state.isLocalWords)
                                }
                            }
                        )

                        if (state.isWordGroupActive) {
                            add(
                                DoneButtonItem(
                                    isEnable = state.words.isNotEmpty(),
                                    onDoneClick = viewModel::onCreateWordGroupClick
                                )
                            )
                        }
                    }
                )

                if (state.isLocalWords) {
                    oldItemTouchHelper?.attachToRecyclerView(null)
                    oldItemTouchHelper = binding.rvWords.attachSwipeToDelete(
                        endIcon = if (state.isWordGroupActive) CommonR.drawable.common_ic_delete else CommonR.drawable.common_ic_switch,
                        backgroundColor = colorInt(CommonR.color.common_alternative_blue_05),
                        directions = ItemTouchHelper.LEFT,
                        swipePredicate = { it !is DoneButtonItem }
                    ) { item, direction ->
                        if (direction == ItemTouchHelper.LEFT) {
                            when (item) {
                                is PreWordItem -> viewModel.onSwipeDelete(item.word)
                                is DefaultWordItem -> viewModel.onSwitchWord(item.word)
                            }
                        }
                    }
                }
            }
            .launchWhenCreated()
    }

    private fun observeOnSearchState() {
        viewModel.searchState.onEach { state ->
            when (state.status) {
                is Status.Idle -> {
                    binding.pbWords.hide()
                    setIdleState(state.data)
                }

                is Status.Loading -> binding.pbWords.show()
                is Status.Failure -> binding.pbWords.hide()
                else -> {}
            }
        }.launchWhenCreated()
    }

    private fun observeOnAction() {
        viewModel.action
            .onEach { action ->
                when (action) {
                    is SearchAction.ShowBaseError -> binding.root.showBaseError()
                    is SearchAction.ShowNetworkError -> binding.root.showNetworkError()
                    is SearchAction.GoToAddWord -> navigate(navProvider.toAddWordScreen)
                    is SearchAction.ToEditWordDialog -> navigate(navProvider.toEditWordScreen)
                    is SearchAction.GoToWordGroup -> navigate(navProvider.toWordGroupScreen(action.drafts))
                    is SearchAction.ClearQuery -> {
                        binding.etSearch.setText("")
                        binding.etSearch.showKeyboard()
                    }
                }
            }
            .launchWhenCreated()
    }

    private fun initClicksListeners() = with(binding) {
        ibAdd.setOnClickListener { viewModel.onAddWordClick() }
        ibAddList.onCheckedChangeListener = { _, isChecked -> viewModel.onWordGroupCheckedChange(isChecked) }
    }

    private fun initSearchField() = with(binding.etSearch) {
        safePostDelayed(300) { showKeyboard() }
        changeSearchEndDrawable()

        doOnTextChanged { text, _, _, _ ->
            changeSearchEndDrawable()
            viewModel.onQueryChanged(text.toString())
        }

        setOnEndDrawableClickListener { viewModel.onClearQueryClick() }

        setOnEditorActionListener { _, actionId, keyEvent ->
            if (keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.onSearchEnterClick()
                safePostDelayed(300L) { requestFocus() }
            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearFocus()
            }
            false
        }
    }

    private fun initRvSearch() = with(binding) {
        pbWords.updatePadding(bottom = dialogTopOffset + pbWords.paddingBottom)
        rvSearch.updatePadding(bottom = dialogTopOffset + rvSearch.paddingBottom)

        rvSearch.layoutManager = LinearLayoutManager(requireContext())
        rvSearch.adapter = searchAdapter
        searchAdapter.setOnItemClickListener { baseItem, _ ->
            when (val item = (baseItem as SearchItem).item) {
                is Word -> viewModel.onWordClick(item)
                is SearchWord -> viewModel.onSearchWordClick(item)
            }
        }
    }

    private fun initRvWords() = with(binding.rvWords) {
        updateMargins(bottom = dialogTopOffset)
        layoutManager = LinearLayoutManager(requireContext())
        adapter = wordsGroupAdapter
        itemAnimator = null

        wordsGroupAdapter.setOnItemClickListener { item, _ ->
            if (item is DefaultWordItem) {
                viewModel.onWordClick(item.word)
            }
        }
    }

    private fun setIdleState(words: List<SimpleWord>? = null) {
        searchAdapter.clear()

        if (words == null) {
            binding.etSearch.showKeyboard()
            binding.rvSearch.gone()
            return
        }

        if (words.isEmpty()) {
            binding.rvSearch.gone()
        } else {
            binding.rvSearch.show()

            searchAdapter.addAll(
                words.map { word -> SearchItem(word) }
            )
        }
    }

    private fun EditText.changeSearchEndDrawable() {
        if (text.isNullOrBlank()) setDrawables(right = CommonR.drawable.common_ic_search)
        else setDrawables(right = CommonR.drawable.common_ic_close_large)
    }

    private fun initEdgeToEdgeMode() {
        binding.pbWords.applyInsetter {
            type(navigationBars = true, ime = true) { margin(bottom = true) }
        }
        binding.rvSearch.applyInsetter {
            type(navigationBars = true, ime = true) { padding(bottom = true) }
        }
        binding.rvWords.applyInsetter {
            type(navigationBars = true, ime = true) { padding(bottom = true) }
        }
    }
}
