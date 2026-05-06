package com.example.aipt.feature.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.chat.domain.model.AiTrainerChatContext
import com.example.aipt.feature.chat.domain.model.AiTrainerChatMessage
import com.example.aipt.feature.chat.domain.model.AiTrainerChatRequest
import com.example.aipt.feature.chat.domain.model.AiTrainerChatResponse
import com.example.aipt.feature.chat.domain.model.AiTrainerEquipmentContext
import com.example.aipt.feature.chat.domain.model.AiTrainerPlanContext
import com.example.aipt.feature.chat.domain.model.AiTrainerProfileContext
import com.example.aipt.feature.chat.domain.model.AiTrainerSessionStateContext
import com.example.aipt.feature.chat.domain.usecase.SendAiTrainerChatUseCase
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionState
import com.example.aipt.feature.dashboard.domain.usecase.ObserveRecentWorkoutSessionsUseCase
import com.example.aipt.feature.profile.domain.model.GymEquipment
import com.example.aipt.feature.profile.domain.model.UserProfile
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.example.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val profileRepository: ProfileRepository,
    private val observeWorkoutSchedule: ObserveWorkoutScheduleUseCase,
    private val observeRecentWorkoutSessions: ObserveRecentWorkoutSessionsUseCase,
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
                val request = buildRequest(message = message, sentAt = now)
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

    private suspend fun buildRequest(message: String, sentAt: Long): AiTrainerChatRequest {
        val profile = profileRepository.observeProfile().first()
        val equipment = profileRepository.observeEquipment().first()
        val schedule = observeWorkoutSchedule().first()
        val recentSessions = observeRecentWorkoutSessions(10).first()
        return AiTrainerChatRequest(
            sentAt = sentAt,
            message = message,
            conversationHistory = _uiState.value.messages
                .takeLast(12)
                .map { AiTrainerChatMessage(role = it.role, content = it.content, createdAt = it.createdAt) },
            context = AiTrainerChatContext(
                profile = profile?.toChatContext(),
                equipment = equipment.map { it.toChatContext() },
                currentPlan = schedule.takeIf { it.isNotEmpty() }?.let { AiTrainerPlanContext(weeklySchedule = it) },
                todayWorkout = schedule.todayWorkoutOrNull(),
                recentSessionStates = recentSessions.map { it.toChatContext() },
                recentWorkoutLogs = emptyList(),
                dataNotes = listOf("Detailed set logs are not persisted yet; recent_workout_logs is currently empty."),
            ),
        )
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

    private fun UserProfile.toChatContext(): AiTrainerProfileContext = AiTrainerProfileContext(
        name = name,
        age = age,
        heightCm = heightCm,
        weightKg = weightKg,
        bodyFatPercentage = bodyFatPercent,
        skeletalMuscleMassKg = skeletalMuscleMassKg,
        bodyWaterLiters = bodyWaterLiters,
        visceralFatLevel = visceralFatLevel,
        bmrKcal = basalMetabolicRateKcal,
        waistToHipRatio = waistHipRatio,
        leftArmMuscleKg = leftArmMuscleKg,
        rightArmMuscleKg = rightArmMuscleKg,
        trunkMuscleKg = trunkMuscleKg,
        leftLegMuscleKg = leftLegMuscleKg,
        rightLegMuscleKg = rightLegMuscleKg,
        trainingGoal = trainingGoal,
        daysPerWeek = daysPerWeek,
        sessionDurationMinutes = sessionDurationMinutes,
        experienceLevel = experienceLevel,
        injuriesOrLimitations = injuriesOrLimitations,
        preferredLanguage = preferredLanguage,
    )

    private fun GymEquipment.toChatContext(): AiTrainerEquipmentContext = AiTrainerEquipmentContext(
        name = name,
        status = status.name,
    )

    private fun WorkoutSessionState.toChatContext(): AiTrainerSessionStateContext = AiTrainerSessionStateContext(
        date = date,
        day = day,
        status = status.name,
        updatedAt = updatedAt,
    )

    private fun List<WorkoutDay>.todayWorkoutOrNull(): WorkoutDay? {
        if (isEmpty()) return null
        val javaDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        val mondayBased = when (javaDay) {
            java.util.Calendar.MONDAY -> 1
            java.util.Calendar.TUESDAY -> 2
            java.util.Calendar.WEDNESDAY -> 3
            java.util.Calendar.THURSDAY -> 4
            java.util.Calendar.FRIDAY -> 5
            java.util.Calendar.SATURDAY -> 6
            else -> 7
        }
        val maxDay = maxOfOrNull { it.day } ?: 1
        val targetDay = ((mondayBased - 1) % maxDay) + 1
        return firstOrNull { it.day == targetDay } ?: firstOrNull()
    }
}
