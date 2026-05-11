package nminhcuong.aipt.feature.chat.data.remote

import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatRequest
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AiTrainerChatApi {
    @POST("api/v1/workout-advice")
    suspend fun chat(
        @Body request: AiTrainerChatRequest,
    ): AiTrainerChatResponse
}
