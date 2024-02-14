package com.github.zerobranch.beebox.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SearchWordDataDto(
    @Json(name = "w")
    val word: String,

    @Json(name = "t")
    val translate: String
)