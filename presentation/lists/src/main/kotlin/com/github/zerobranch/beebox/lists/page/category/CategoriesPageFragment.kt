package com.github.zerobranch.beebox.lists.page.category

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
import com.github.zerobranch.beebox.commons_items.CategoryItem
import com.github.zerobranch.beebox.commons_items.EmptyItem
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.lists.R
import com.github.zerobranch.beebox.lists.databinding.FragmentCategoryPageBinding
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import com.github.zerobranch.beebox.commons_app.R as CommonR

@AndroidEntryPoint
class CategoriesPageFragment : BaseViewPagerFragment(R.layout.fragment_category_page), DialogDispatcher {
    private companion object {
        const val CATEGORIES_PAGE_DELETE_RESULT_KEY = "categories_page_delete_result_key"
    }

    @Inject
    internal lateinit var navProvider: CategoriesPageNavProvider

    private val viewModel: CategoriesPageViewModel by viewModels()
    private val binding by viewBinding(FragmentCategoryPageBinding::bind)
    private val categoriesAdapter: GroupieAdapter by groupAdapter { binding.rvCategories }

    override fun initUi() {
        super.initUi()
        initRvCategories()
        initEdgeToEdgeMode()
    }

    override fun observeOnStates() {
        viewModel.action.onEach { action ->
            when (action) {
                is CategoriesPageAction.OpenDeleteDialog -> navigate(
                    navProvider.toDeleteDialog(
                        resultKey = CATEGORIES_PAGE_DELETE_RESULT_KEY,
                        wordCategory = action.wordCategory,
                        title = getString(
                            CommonR.string.common_contains_words_title,
                            action.wordCategory.name
                        ),
                        description = getString(CommonR.string.common_contains_words_in_category_description),
                        actionBtnText = getString(CommonR.string.common_delete),
                        cancelBtnText = getString(CommonR.string.common_cancel)
                    )
                )
                is CategoriesPageAction.OpenEditCategoryDialog -> {
                    navigate(navProvider.toEditCategoryDialog(action.wordCategory))
                }
                is CategoriesPageAction.Reload -> {
                    categoriesAdapter.notifyItemChanged(action.position)
                }
                is CategoriesPageAction.ToCategoryWordList -> {
                    navigate(navProvider.toCategoryWordList(action.wordCategory))
                }
            }
        }.launchWhenCreated()

        viewModel.categoriesState.onEach { categories ->
            categories ?: return@onEach

            if (categories.isEmpty()) {
                categoriesAdapter.clear()
                categoriesAdapter.add(EmptyItem())
            } else {
                categoriesAdapter.updateAsync(
                    categories.map { CategoryItem(it) }
                )
            }
        }.launchWhenCreated()
    }

    override fun onDialogAction(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogAction(tag, resultKey, data)
        if (resultKey == CATEGORIES_PAGE_DELETE_RESULT_KEY) {
            viewModel.onDialogDeleteClick(data as? WordCategory ?: return)
        }
    }

    override fun onDialogCancel(tag: String?, resultKey: String?, data: Any?) {
        super.onDialogCancel(tag, resultKey, data)
        viewModel.onDialogCancelClick(data as? WordCategory ?: return)
    }

    private fun initRvCategories() = with(binding.rvCategories) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = categoriesAdapter
        addDividerDecoration(dividerInsetStart = 0, dividerInsetEnd = 20.dp) { it !is EmptyItem }
        categoriesAdapter.setOnItemClickListener { item, _ ->
            when (item) {
                is CategoryItem -> viewModel.onCategoryClick(item.item)
            }
        }

        attachSwipeToDelete(
            endIcon = CommonR.drawable.common_ic_delete,
            startIcon = CommonR.drawable.common_ic_edit,
            backgroundColor = colorInt(CommonR.color.common_alternative_blue_05),
            directions = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            swipePredicate = { it !is EmptyItem }
        ) { item, direction ->
            val category = (item as CategoryItem).item
            if (direction == ItemTouchHelper.LEFT) {
                viewModel.onSwipeDelete(category)
            } else if (direction == ItemTouchHelper.RIGHT) {
                viewModel.onSwipeEdit(category)
            }
        }
    }

    private fun initEdgeToEdgeMode() {
        binding.rvCategories.applyInsetter {
            type(navigationBars = true) { padding(bottom = true) }
        }
    }
}