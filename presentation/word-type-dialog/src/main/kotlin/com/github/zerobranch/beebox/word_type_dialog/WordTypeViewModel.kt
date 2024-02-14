package com.github.zerobranch.beebox.word_type_dialog

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import com.github.zerobranch.beebox.commons_java.delegates.OnceFlow
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.usecase.DraftWordUseCase
import com.github.zerobranch.beebox.domain.usecase.WordTypeUseCase
import com.github.zerobranch.beebox.logging.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach

class WordTypeViewModel @AssistedInject constructor(
    private val wordTypeUseCase: WordTypeUseCase,
    private val draftWordEvents: DraftWordUseCase,
    @Assisted private val wordType: WordType?
) : BaseMainViewModel() {

    override val screenName: String = "WordType"

    private val _action = OnceFlow<WordTypeAction>()
    val action = _action.asSharedFlow()

    fun onCreateClick(name: String, description: String) {
        if (name.isBlank()) return

        wordTypeUseCase.create(name, description)
            .onIO()
            .onEach { _action.tryEmit(WordTypeAction.Dismiss) }
            .catch { th -> javaClass.error(screenName, th, "wordType create failed") }
            .inViewModel()
    }

    fun onEditClick(categoryId: Long, name: String, description: String) {
        if (name.isBlank()) return

        wordTypeUseCase.change(categoryId, name, description)
            .onIO()
            .onEach {
                wordType?.run {
                    draftWordEvents.onWordTypeChanged(
                        wordType.copy(name = name, description = description)
                    )
                }
            }
            .onEach { _action.tryEmit(WordTypeAction.Dismiss) }
            .catch { th -> javaClass.error(screenName, th, "wordType change failed") }
            .inViewModel()
    }
}
