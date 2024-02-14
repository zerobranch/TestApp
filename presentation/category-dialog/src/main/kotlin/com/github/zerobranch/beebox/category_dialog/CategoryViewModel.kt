package com.github.zerobranch.beebox.category_dialog

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.WordCategoryUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach

class CategoryViewModel @AssistedInject constructor(
    private val categoryUseCase: WordCategoryUseCase,
    private val draftWordEvents: DraftWordUseCase,
    @Assisted private val wordCategory: WordCategory?
) : BaseMainViewModel() {

    override val screenName: String = "Category"

    private val _action = OnceFlow<CategoryAction>()
    val action = _action.asSharedFlow()

    fun onCreateClick(name: String, description: String) {
        if (name.isBlank()) return

        categoryUseCase.create(name, description)
            .onIO()
            .onEach { _action.tryEmit(CategoryAction.Dismiss) }
            .catch { th -> javaClass.error(screenName, th, "category create failed") }
            .inViewModel()
    }

    fun onEditClick(categoryId: Long, name: String, description: String) {
        if (name.isBlank()) return

        categoryUseCase.change(categoryId, name, description)
            .onIO()
            .onEach {
                wordCategory?.run {
                    draftWordEvents.onCategoryChanged(
                        wordCategory.copy(name = name, description = description)
                    )
                }
            }
            .onEach { _action.tryEmit(CategoryAction.Dismiss) }
            .catch { th -> javaClass.error(screenName, th, "category change failed") }
            .inViewModel()
    }
}
