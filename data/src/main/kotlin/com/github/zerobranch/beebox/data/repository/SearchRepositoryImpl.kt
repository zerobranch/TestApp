package com.github.zerobranch.beebox.data.repository

import com.github.zerobranch.beebox.data.base.NetworkErrorParser
import com.github.zerobranch.beebox.data.mappers.toDomain
import com.github.zerobranch.beebox.data.source.remote.Api
import com.github.zerobranch.beebox.domain.models.words.DraftWord
import com.github.zerobranch.beebox.domain.models.words.SearchWord
import com.github.zerobranch.beebox.domain.models.words.SimpleWord
import com.github.zerobranch.beebox.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: Api,
    private val networkErrorParser: NetworkErrorParser
) : SearchRepository {
    override fun search(query: String): Flow<List<SearchWord>> =
        flow { emit(api.search(query)) }
            .map { dto -> dto.tips.map { it.toDomain() } }
            .catch { networkErrorParser.invoke(it) }

    override fun getWord(word: String): Flow<DraftWord?> = flow {
        emit(
            WordParser.parse(
                word = word,
                body = api.getWordInfoHtml(word).string()
            )
        )
    }.catch { networkErrorParser.invoke(it) }

    override fun parseDraftWords(words: List<SimpleWord>): Flow<List<DraftWord>> = flow {
        emit(
            words.mapNotNull { word ->
                WordParser.parse(
                    word = word.word,
                    body = api.getWordInfoHtml(word.word).string()
                )
            }
        )
    }.catch { networkErrorParser.invoke(it) }
}