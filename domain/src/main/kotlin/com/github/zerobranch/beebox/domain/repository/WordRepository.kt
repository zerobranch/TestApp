package com.github.zerobranch.beebox.domain.repository

import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.Word
import com.github.zerobranch.beebox.domain.models.WordCategory
import com.github.zerobranch.beebox.domain.models.WordType
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun search(word: String): Flow<List<Word>>
    fun getAll(): Flow<List<Word>>
    fun getAll(ignoreWordCategory: WordCategory?, ignoreWordType: WordType?): Flow<List<Word>>
    fun getAllByFilter(
        wordCategories: List<WordCategory>,
        wordTypes: List<WordType>
    ): Flow<List<Word>>
    fun getAllByType(wordType: WordType): Flow<List<Word>>
    fun getAllByCategory(category: WordCategory): Flow<List<Word>>
    fun create(draftWord: DraftWord): Flow<Unit>
    fun createAll(drafts: List<DraftWord>): Flow<Unit>
    fun change(draftWord: DraftWord): Flow<Unit>
    fun softDeleteWord(word: Word): Flow<Unit>
    fun hardDeleteWordWithChildren(word: Word): Flow<Unit>
    fun hardDeleteWordWithoutChildren(word: Word): Flow<Unit>
    fun addCategoryToWord(word: Word, category: WordCategory): Flow<Unit>
    fun addTypeToWord(word: Word, type: WordType): Flow<Unit>
    fun deleteCategoryFromWord(word: Word, category: WordCategory): Flow<Unit>
    fun deleteTypeFromWord(word: Word, type: WordType): Flow<Unit>
    fun getWordCount(): Flow<Int>
    fun getWordTypesCount(): Flow<Int>
    fun getWordCategoriesCount(): Flow<Int>
}