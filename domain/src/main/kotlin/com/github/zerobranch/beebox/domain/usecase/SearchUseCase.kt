package com.github.zerobranch.beebox.domain.usecase

import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.SearchWord
import com.github.zerobranch.beebox.domain.models.words.SimpleWord
import com.github.zerobranch.beebox.domain.repository.SearchRepository
import com.github.zerobranch.beebox.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: SearchRepository,
    private val wordRepository: WordRepository,
) {
    fun search(query: String): Flow<List<SimpleWord>> =
        repository.search(query).zip(wordRepository.search(query)) { searchWords, words ->
            searchWords.map { searchWord ->
                words.find { it.word == searchWord.word } ?: searchWord
            }
        }

    fun getWord(searchWord: SearchWord): Flow<DraftWord?> = repository.getWord(searchWord.word)

    fun parseDraftWords(words: List<SimpleWord>): Flow<List<DraftWord>> =
        repository.parseDraftWords(words)
}