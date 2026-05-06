package com.example.aipt.feature.chat.domain.usecase

import com.example.aipt.feature.chat.domain.model.AiTrainerChatRequest
import com.example.aipt.feature.chat.domain.repository.AiTrainerChatRepository
import javax.inject.Inject

class SendAiTrainerChatUseCase @Inject constructor(
    private val repository: AiTrainerChatRepository,
) {
    suspend operator fun invoke(request: AiTrainerChatRequest) = repository.chat(request)
}
