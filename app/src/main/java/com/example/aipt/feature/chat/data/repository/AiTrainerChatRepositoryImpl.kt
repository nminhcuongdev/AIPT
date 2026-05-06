package com.example.aipt.feature.chat.data.repository

import com.example.aipt.feature.chat.data.remote.AiTrainerChatApi
import com.example.aipt.feature.chat.domain.model.AiTrainerChatRequest
import com.example.aipt.feature.chat.domain.model.AiTrainerChatResponse
import com.example.aipt.feature.chat.domain.repository.AiTrainerChatRepository
import javax.inject.Inject

class AiTrainerChatRepositoryImpl @Inject constructor(
    private val api: AiTrainerChatApi,
) : AiTrainerChatRepository {
    override suspend fun chat(request: AiTrainerChatRequest): AiTrainerChatResponse = api.chat(request)
}
