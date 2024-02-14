package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    fun search(word: String): Flow<List<Word>> = repository.search(word)

    fun getAll(): Flow<List<Word>> = repository.getAll()

    fun getAllByFilter(
        wordCategories: List<WordCategory>,
        wordTypes: List<WordType>
    ): Flow<List<Word>> = repository.getAllByFilter(wordCategories, wordTypes)

    fun getAll(ignoreWordCategory: WordCategory?, ignoreWordType: WordType?): Flow<List<Word>> =
        repository.getAll(ignoreWordCategory, ignoreWordType)

    fun getAllByType(wordType: WordType): Flow<List<Word>> = repository.getAllByType(wordType)

    fun getAllByCategory(wordCategory: WordCategory): Flow<List<Word>> =
        repository.getAllByCategory(wordCategory)

    fun create(draftWord: DraftWord): Flow<Unit> =
        repository.create(
            draftWord.copy(
                word = draftWord.word.trim(),
                transcriptions = draftWord.transcriptions.filter {
                    it.firstTranscription.isNotBlank() || it.secondTranscription.isNotBlank()
                },
                translates = draftWord.translates.trim(),
                hints = draftWord.hints.filter { it.text.isNotBlank() }
            )
        )

    fun createAll(drafts: List<DraftWord>): Flow<Unit> =
        repository.createAll(
            drafts.map { draft ->
                draft.copy(
                    word = draft.word.trim(),
                    transcriptions = draft.transcriptions.filter {
                        it.firstTranscription.isNotBlank() || it.secondTranscription.isNotBlank()
                    },
                    translates = draft.translates.trim(),
                    hints = draft.hints.filter { it.text.isNotBlank() }
                )
            }
        )

    fun change(draftWord: DraftWord): Flow<Unit> =
        repository.change(
            draftWord.copy(
                word = draftWord.word.trim(),
                transcriptions = draftWord.transcriptions.filter {
                    it.firstTranscription.isNotBlank() || it.secondTranscription.isNotBlank()
                },
                translates = draftWord.translates.trim(),
                hints = draftWord.hints.filter { it.text.isNotBlank() }
            )
        )

    fun softDeleteWord(word: Word): Flow<Unit> = repository.softDeleteWord(word)

    fun hardDeleteWordWithChildren(word: Word): Flow<Unit> =
        repository.hardDeleteWordWithChildren(word)

    fun hardDeleteWordWithoutChildren(word: Word): Flow<Unit> =
        repository.hardDeleteWordWithoutChildren(word)

    fun addCategoryToWord(word: Word, category: WordCategory): Flow<Unit> =
        repository.addCategoryToWord(word, category)

    fun addTypeToWord(word: Word, type: WordType): Flow<Unit> = repository.addTypeToWord(word, type)

    fun deleteCategoryFromWord(word: Word, category: WordCategory): Flow<Unit> =
        repository.deleteCategoryFromWord(word, category)

    fun deleteTypeFromWord(word: Word, type: WordType): Flow<Unit> =
        repository.deleteTypeFromWord(word, type)

    fun getWordCount(): Flow<Int> = repository.getWordCount()

    fun getWordTypesCount(): Flow<Int> = repository.getWordTypesCount()

    fun getWordCategoriesCount(): Flow<Int> = repository.getWordCategoriesCount()
}