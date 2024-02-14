package com.github.zerobranch.beebox.domain.repository

import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.SearchWord
import com.github.zerobranch.beebox.domain.models.words.SimpleWord
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(query: String): Flow<List<SearchWord>>
    fun getWord(word: String): Flow<DraftWord?>
    fun parseDraftWords(words: List<SimpleWord>): Flow<List<DraftWord>>
}