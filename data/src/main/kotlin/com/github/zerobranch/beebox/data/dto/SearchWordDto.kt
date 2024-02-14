package com.github.zerobranch.beebox.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SearchWordDto(
    @Json(name = "tips")
    val tips: List<SearchWordDataDto>
)
