package nminhcuong.aipt.feature.chat.domain.repository

import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatRequest
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatResponse

interface AiTrainerChatRepository {
    suspend fun chat(request: AiTrainerChatRequest): AiTrainerChatResponse
}
