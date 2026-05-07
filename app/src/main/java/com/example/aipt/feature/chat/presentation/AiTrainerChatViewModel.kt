package com.example.aipt.feature.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.chat.domain.model.AiTrainerChatMessage
import com.example.aipt.feature.chat.domain.model.AiTrainerChatResponse
import com.example.aipt.feature.chat.domain.usecase.BuildAiTrainerChatRequestUseCase
import com.example.aipt.feature.chat.domain.usecase.SendAiTrainerChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiTrainerChatBubbleUiState(
    val role: String,
    val content: String,
    val createdAt: Long,
    val recommendations: List<String> = emptyList(),
    val safetyNotes: List<String> = emptyList(),
    val suggestedActions: List<String> = emptyList(),
    val model: String? = null,
)

data class AiTrainerChatUiState(
    val input: String = "",
    val messages: List<AiTrainerChatBubbleUiState> = emptyList(),
    val isSending: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSend: Boolean = input.isNotBlank() && !isSending
}

@HiltViewModel
class AiTrainerChatViewModel @Inject constructor(
    private val buildChatRequest: BuildAiTrainerChatRequestUseCase,
    private val sendAiTrainerChat: SendAiTrainerChatUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AiTrainerChatUiState())
    val uiState: StateFlow<AiTrainerChatUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = AiTrainerChatUiState(
            messages = listOf(
                AiTrainerChatBubbleUiState(
                    role = "assistant",
                    content = "Ask me about today's workout, pain, recovery, nutrition, or how to adjust your current plan.",
                    createdAt = System.currentTimeMillis(),
                ),
            ),
        )
    }

    fun onInputChanged(value: String) {
        _uiState.update { it.copy(input = value.take(600), errorMessage = null) }
    }

    fun sendMessage() {
        val message = _uiState.value.input.trim()
        if (message.isBlank() || _uiState.value.isSending) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val userBubble = AiTrainerChatBubbleUiState(role = "user", content = message, createdAt = now)
            _uiState.update { it.copy(input = "", isSending = true, messages = it.messages + userBubble, errorMessage = null) }
            runCatching {
                val request = buildChatRequest(
                    message = message,
                    sentAt = now,
                    conversationHistory = _uiState.value.messages.map {
                        AiTrainerChatMessage(role = it.role, content = it.content, createdAt = it.createdAt)
                    },
                )
                sendAiTrainerChat(request)
            }.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isSending = false,
                        messages = it.messages + response.toBubble(),
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSending = false,
                        errorMessage = throwable.message ?: "Unable to reach AI trainer.",
                    )
                }
            }
        }
    }

    private fun AiTrainerChatResponse.toBubble(): AiTrainerChatBubbleUiState = AiTrainerChatBubbleUiState(
        role = "assistant",
        content = reply,
        createdAt = System.currentTimeMillis(),
        recommendations = recommendations.orEmpty(),
        safetyNotes = safetyNotes.orEmpty(),
        suggestedActions = suggestedActions.orEmpty().map { action ->
            listOf(action.label, action.details).filterNot { it.isNullOrBlank() }.joinToString(": ")
        },
        model = model,
    )


}
