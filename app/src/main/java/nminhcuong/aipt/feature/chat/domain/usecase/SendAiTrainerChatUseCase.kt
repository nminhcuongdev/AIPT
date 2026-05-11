package nminhcuong.aipt.feature.chat.domain.usecase

import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatRequest
import nminhcuong.aipt.feature.chat.domain.repository.AiTrainerChatRepository
import javax.inject.Inject

class SendAiTrainerChatUseCase @Inject constructor(
    private val repository: AiTrainerChatRepository,
) {
    suspend operator fun invoke(request: AiTrainerChatRequest) = repository.chat(request)
}
