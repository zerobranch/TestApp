package com.github.zerobranch.beebox.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class HintEntity(
    val id: String,
    val text: String,
)