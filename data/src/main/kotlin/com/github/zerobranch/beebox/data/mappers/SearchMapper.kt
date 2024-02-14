package com.github.zerobranch.beebox.data.mappers

import com.github.zerobranch.beebox.data.dto.SearchWordDataDto
import com.github.zerobranch.beebox.domain.models.words.SearchWord

fun SearchWordDataDto.toDomain(): SearchWord =
    SearchWord(
        word = word,
        translates = translate
    )
