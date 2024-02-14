package com.github.zerobranch.beebox.lists.page.category

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.commons_java.ext.indexOfOrNull
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.usecase.WordCategoryUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class CategoriesPageViewModel @Inject constructor(
    private val categoryUseCase: WordCategoryUseCase
) : BaseMainViewModel() {

    override val screenName: String = "CategoriesPage"

    private val _categoriesState = MutableStateFlow<List<WordCategory>?>(null)
    val categoriesState = _categoriesState.asStateFlow()

    private val _action = OnceFlow<CategoriesPageAction>()
    val action = _action.asSharedFlow()

    init {
        categoryUseCase.getAllWithWordsCount()
            .onIO()
            .onEach { _categoriesState.fresh { it } }
            .catch { th -> javaClass.error(screenName, th, "category getAll failed") }
            .inViewModel()
    }

    fun onCategoryClick(category: WordCategory) {
        _action.tryEmit(CategoriesPageAction.ToCategoryWordList(category))
    }

    fun onSwipeDelete(category: WordCategory) {
        categoryUseCase.containsWordsIsCategory(category.id)
            .onIO()
            .onEach { contains ->
                if (contains) {
                    _action.tryEmit(CategoriesPageAction.OpenDeleteDialog(category))
                } else {
                    hardDeleteCategory(category)
                }
            }
            .catch { th -> javaClass.error(screenName, th, "containsWordsIsCategory failed") }
            .inViewModel()
    }

    fun onSwipeEdit(item: WordCategory) {
        _action.tryEmit(CategoriesPageAction.OpenEditCategoryDialog(item))
    }

    fun onDialogDeleteClick(category: WordCategory) {
        hardDeleteCategory(category)
    }

    fun onDialogCancelClick(category: WordCategory) {
        _action.tryEmit(
            CategoriesPageAction.Reload(
                _categoriesState.value?.indexOfOrNull(category) ?: return
            )
        )
    }

    private fun hardDeleteCategory(category: WordCategory) {
        categoryUseCase.delete(category)
            .onIO()
            .catch { th -> javaClass.error(screenName, th, "category delete failed") }
            .inViewModel()
    }
}
