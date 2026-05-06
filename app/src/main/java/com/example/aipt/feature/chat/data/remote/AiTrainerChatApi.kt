package com.example.aipt.feature.chat.data.remote

import com.example.aipt.feature.chat.domain.model.AiTrainerChatRequest
import com.example.aipt.feature.chat.domain.model.AiTrainerChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AiTrainerChatApi {
    @POST("api/v1/workout-advice")
    suspend fun chat(
        @Body request: AiTrainerChatRequest,
    ): AiTrainerChatResponse
}
