package com.github.zerobranch.beebox.data.source.remote

import com.github.zerobranch.beebox.data.dto.SearchWordDto
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("openscripts/forjs/get_tips.php")
    suspend fun search(@Query("abc") word: String): SearchWordDto

    @GET("word/{word}")
    suspend fun getWordInfoHtml(@Path("word") word: String): ResponseBody
}