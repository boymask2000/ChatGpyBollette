package com.boymask.myapplication.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface OpenAIApi {
    @Headers(
        "Content-Type: application/json", //
        "Authorization: Bearer YOUR_API_KEY"
    )
    @POST("v1/chat/completions")
    fun chat(@Body request: ChatRequest?): Call<ChatResponse?>?
}