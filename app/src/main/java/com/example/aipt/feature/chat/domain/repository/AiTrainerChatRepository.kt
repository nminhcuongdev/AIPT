package com.example.aipt.feature.chat.domain.repository

import com.example.aipt.feature.chat.domain.model.AiTrainerChatRequest
import com.example.aipt.feature.chat.domain.model.AiTrainerChatResponse

interface AiTrainerChatRepository {
    suspend fun chat(request: AiTrainerChatRequest): AiTrainerChatResponse
}
