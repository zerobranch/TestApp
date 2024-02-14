package com.github.zerobranch.beebox.choose_category_dialog

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.commons_java.ext.indexOfOrNull
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.WordCategoryUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class ChooseCategoryViewModel @Inject constructor(
    private val draftWordEvents: DraftWordUseCase,
    private val categoryUseCase: WordCategoryUseCase
) : BaseMainViewModel() {

    override val screenName: String = "ChooseCategory"

    private val _categoriesState = MutableStateFlow<List<WordCategory>?>(null)
    val categoriesState = _categoriesState.asStateFlow()

    private val _action = OnceFlow<ChooseCategoryAction>()
    val action = _action.asSharedFlow()

    init {
        categoryUseCase.getAllWithWordsCount()
            .onIO()
            .map { categories ->
                val draftCategories = draftWordEvents.wordCategories
                categories.map { category ->
                    if (draftCategories.any { it.id == category.id }) {
                        category.copy(isSelected = true)
                    } else {
                        category
                    }
                }
            }
            .flowOn(Dispatchers.Default)
            .onEach { _categoriesState.fresh { it } }
            .catch { th -> javaClass.error(screenName, th, "category getAll failed") }
            .inViewModel()
    }

    fun onCategoryClick(category: WordCategory) {
        _categoriesState.fresh {
            this?.map {
                it.takeIf { it.id != category.id } ?: it.copy(isSelected = !it.isSelected)
            }
        }
        draftWordEvents.onCategoryChanged(category.copy(isSelected = !category.isSelected))
    }

    fun onAddCategoryClick() {
        _action.tryEmit(ChooseCategoryAction.OpenCreateCategoryDialog)
    }

    fun onSwipeDelete(category: WordCategory) {
        categoryUseCase.containsWordsIsCategory(category.id)
            .onIO()
            .onEach { contains ->
                if (contains) {
                    _action.tryEmit(ChooseCategoryAction.OpenDeleteDialog(category))
                } else {
                    hardDeleteCategory(category)
                }
            }
            .catch { th -> javaClass.error(screenName, th, "containsWordsIsCategory failed") }
            .inViewModel()
    }

    fun onSwipeEdit(item: WordCategory) {
        _action.tryEmit(ChooseCategoryAction.OpenEditCategoryDialog(item))
    }

    fun onDialogDeleteClick(category: WordCategory) {
        hardDeleteCategory(category)
    }

    fun onDialogCancelClick(category: WordCategory) {
        _action.tryEmit(
            ChooseCategoryAction.Reload(
                _categoriesState.value?.indexOfOrNull(category) ?: return
            )
        )
    }

    private fun hardDeleteCategory(category: WordCategory) {
        categoryUseCase.delete(category)
            .onIO()
            .onEach { draftWordEvents.onCategoryChanged(category.copy(isSelected = false)) }
            .catch { th -> javaClass.error(screenName, th, "category delete failed") }
            .inViewModel()
    }
}
