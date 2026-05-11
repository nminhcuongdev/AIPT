package nminhcuong.aipt.feature.chat.data.repository

import nminhcuong.aipt.feature.chat.data.remote.AiTrainerChatApi
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatRequest
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatResponse
import nminhcuong.aipt.feature.chat.domain.repository.AiTrainerChatRepository
import javax.inject.Inject

class AiTrainerChatRepositoryImpl @Inject constructor(
    private val api: AiTrainerChatApi,
) : AiTrainerChatRepository {
    override suspend fun chat(request: AiTrainerChatRequest): AiTrainerChatResponse = api.chat(request)
}
