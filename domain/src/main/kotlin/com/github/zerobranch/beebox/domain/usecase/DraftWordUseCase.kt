package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.commons_java.delegates.fresh
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.Hint
import com.github.zerobranch.beebox.domain.models.words.Transcription
import com.github.zerobranch.beebox.domain.models.words.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DraftWordUseCase @Inject constructor() {
    private val _draftWordState = MutableStateFlow(DraftWord())
    val draftWordState = _draftWordState.asStateFlow()

    val wordCategories: List<WordCategory>
        get() = _draftWordState.value.categories

    val wordTypes: List<WordType>
        get() = _draftWordState.value.wordTypes

    val draftWord: DraftWord
        get() = _draftWordState.value

    fun reset(): DraftWordUseCase = apply { _draftWordState.fresh { DraftWord() } }

    fun onWordTypeChanged(wordType: WordType?): DraftWordUseCase = apply {
        wordType ?: return@apply
        _draftWordState.fresh {
            val wordTypesMutable = wordTypes.toMutableList()

            if (!wordType.isSelected) {
                wordTypesMutable.removeAll { it.id == wordType.id }
            } else {
                val oldWordTypeIndex = wordTypesMutable.indexOfFirst { it.id == wordType.id }
                if (oldWordTypeIndex != -1) {
                    wordTypesMutable[oldWordTypeIndex] = wordType
                } else {
                    wordTypesMutable.add(wordType)
                }
            }
            copy(wordTypes = wordTypesMutable)
        }
    }

    fun onCategoryChanged(category: WordCategory?): DraftWordUseCase = apply {
        category ?: return@apply
        _draftWordState.fresh {
            val categoriesMutable = categories.toMutableList()

            if (!category.isSelected) {
                categoriesMutable.removeAll { it.id == category.id }
            } else {
                val oldCategoryIndex = categoriesMutable.indexOfFirst { it.id == category.id }
                if (oldCategoryIndex != -1) {
                    categoriesMutable[oldCategoryIndex] = category
                } else {
                    categoriesMutable.add(category)
                }
            }
            copy(categories = categoriesMutable)
        }
    }

    fun onAddHint(): DraftWordUseCase = apply {
        val hints = _draftWordState.value.hints.toMutableList()
        hints.add(
            Hint(id = UUID.randomUUID().toString())
        )
        _draftWordState.fresh { copy(hints = hints) }
    }

    fun onAddTranscription(): DraftWordUseCase = apply {
        val transcriptions = _draftWordState.value.transcriptions.toMutableList()
        transcriptions.add(
            Transcription(id = UUID.randomUUID().toString())
        )
        _draftWordState.fresh { copy(transcriptions = transcriptions) }
    }

    fun onDeleteTranscription(transcriptionId: String): DraftWordUseCase = apply {
        val transcriptions = _draftWordState.value.transcriptions.toMutableList()
        transcriptions.removeAll { it.id == transcriptionId }
        _draftWordState.fresh { copy(transcriptions = transcriptions) }
    }

    fun onDeleteHint(hintId: String): DraftWordUseCase = apply {
        val hints = _draftWordState.value.hints.toMutableList()
        hints.removeAll { it.id == hintId }
        _draftWordState.fresh { copy(hints = hints) }
    }

    fun onHintChanged(id: String, text: String): DraftWordUseCase = apply {
        _draftWordState.fresh {
            copy(
                hints = _draftWordState.value.hints.map { hint ->
                    hint.copy(text = text).takeIf { hint.id == id } ?: hint
                }
            )
        }
    }

    fun onDraftWordStateChange(
        word: String = _draftWordState.value.word,
        parentWord: Word? = _draftWordState.value.parentWord,
        transcription: List<Transcription> = _draftWordState.value.transcriptions,
        translates: String = _draftWordState.value.translates,
        comment: String = _draftWordState.value.comment,
        hints: List<Hint> = _draftWordState.value.hints,
        categories: List<WordCategory> = _draftWordState.value.categories,
        wordTypes: List<WordType> = _draftWordState.value.wordTypes,
    ): DraftWordUseCase = apply {
        _draftWordState.fresh {
            copy(
                word = word,
                parentWord = parentWord,
                transcriptions = transcription,
                translates = translates,
                comment = comment,
                hints = hints,
                categories = categories,
                wordTypes = wordTypes,
            )
        }
    }

    fun onTranscriptionChange(
        id: String,
        title: String? = null,
        firstTranscriptionHint: String? = null,
        firstTranscription: String? = null,
        secondTranscriptionHint: String? = null,
        secondTranscription: String? = null,
    ): DraftWordUseCase = apply {
        val updated = _draftWordState.value.transcriptions.map { tr ->
            tr.copy(
                title = title ?: tr.title,
                firstTranscriptionHint = firstTranscriptionHint ?: tr.firstTranscriptionHint,
                firstTranscription = firstTranscription ?: tr.firstTranscription,
                secondTranscriptionHint = secondTranscriptionHint ?: tr.secondTranscriptionHint,
                secondTranscription = secondTranscription ?: tr.secondTranscription,
            ).takeIf { tr.id == id } ?: tr
        }

        _draftWordState.fresh { copy(transcriptions = updated) }
    }

    fun set(word: Word): DraftWordUseCase = apply {
        _draftWordState.tryEmit(
            DraftWord(
                word = word.word,
                parentWord = word.parentWord,
                transcriptions = word.transcriptions,
                translates = word.translates,
                comment = word.comment,
                rank = word.rank,
                hints = word.hints,
                categories = word.categories,
                wordTypes = word.wordTypes,
            )
        )
    }

    fun set(draftWord: DraftWord): DraftWordUseCase = apply {
        _draftWordState.tryEmit(draftWord.copy())
    }
}