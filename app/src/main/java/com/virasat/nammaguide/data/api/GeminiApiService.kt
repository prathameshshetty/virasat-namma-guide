package com.virasat.nammaguide.data.api

import com.virasat.nammaguide.data.model.GeminiRequest
import com.virasat.nammaguide.data.model.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.0-flash-lite:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
