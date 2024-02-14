package com.github.zerobranch.beebox.local_search

import android.widget.EditText
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.zerobranch.beebox.commons_android.utils.delegates.groupAdapter
import com.github.zerobranch.beebox.commons_android.utils.ext.launchWhenCreated
import com.github.zerobranch.beebox.commons_android.utils.ext.safePostDelayed
import com.github.zerobranch.beebox.commons_android.utils.ext.setDrawables
import com.github.zerobranch.beebox.commons_android.utils.ext.setOnDoneClickListener
import com.github.zerobranch.beebox.commons_android.utils.ext.setOnEndDrawableClickListener
import com.github.zerobranch.beebox.commons_android.utils.ext.showKeyboard
import com.github.zerobranch.beebox.commons_android.utils.viewBinding
import com.github.zerobranch.beebox.commons_app.base.BaseMainBottomSheetDialog
import com.github.zerobranch.beebox.commons_app.base.BundleKeys
import com.github.zerobranch.beebox.commons_items.WordItem
import com.github.zerobranch.beebox.local_search.databinding.DialogLocalSearchBinding
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class LocalSearchDialog : BaseMainBottomSheetDialog(R.layout.dialog_local_search) {
    override val isExpanded: Boolean = true
    override val isFullScreen: Boolean = true

    override fun getTheme(): Int = CommonR.style.CommonBaseBottomSheetDialogTheme_Large

    private val viewModel: LocalSearchViewModel by viewModels()
    private val binding by viewBinding(DialogLocalSearchBinding::bind)
    private val searchAdapter: GroupieAdapter by groupAdapter { binding.rvSearch }

    override fun initUi() {
        super.initUi()
        initRvSearch()
        initSearchField()
        initEdgeToEdgeMode()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
    }

    override fun observeOnStates() {
        viewModel.action
            .onEach { action ->
                when (action) {
                    is LocalSearchAction.ReturnResult -> {
                        setNavResult(BundleKeys.SEARCH_WORD_RESULT_KEY, action.word)
                        exit()
                    }
                }
            }
            .launchWhenCreated()

        viewModel.searchState
            .onEach { words -> searchAdapter.replaceAll(words.map { WordItem(it) }) }
            .launchWhenCreated()
    }

    private fun initSearchField() = with(binding.etSearch) {
        safePostDelayed(300) { showKeyboard() }
        changeSearchEndDrawable()

        doOnTextChanged { text, _, _, _ ->
            changeSearchEndDrawable()
            viewModel.onQueryChanged(text.toString())
        }

        setOnEndDrawableClickListener {
            setText("")
            showKeyboard()
        }

        setOnDoneClickListener { clearFocus() }
    }

    private fun initRvSearch() = with(binding.rvSearch) {
        updatePadding(bottom = dialogTopOffset + paddingBottom)
        layoutManager = LinearLayoutManager(requireContext())
        adapter = searchAdapter
        searchAdapter.setOnItemClickListener { item, _ ->
            when (item) {
                is WordItem -> viewModel.onWordClick(item.item)
            }
        }
    }

    private fun EditText.changeSearchEndDrawable() {
        if (text.isNullOrBlank()) setDrawables(right = CommonR.drawable.common_ic_search)
        else setDrawables(right = CommonR.drawable.common_ic_close_large)
    }

    private fun initEdgeToEdgeMode() {
        binding.rvSearch.applyInsetter {
            type(navigationBars = true, ime = true) { padding(bottom = true) }
        }
    }
}

