package com.jpobi.xkcd_viewer

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET(value="{comicId}/info.0.json")
    suspend fun getComic(@Path("comicId") comicId: String): Response<Comic>

}