package nminhcuong.aipt.feature.chat.domain.usecase

import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatContext
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatMessage
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerChatRequest
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerEquipmentContext
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerPlanContext
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerProfileContext
import nminhcuong.aipt.feature.chat.domain.model.AiTrainerSessionStateContext
import nminhcuong.aipt.feature.dashboard.domain.model.WorkoutSessionState
import nminhcuong.aipt.feature.dashboard.domain.usecase.ObserveRecentWorkoutSessionsUseCase
import nminhcuong.aipt.feature.profile.domain.model.GymEquipment
import nminhcuong.aipt.feature.profile.domain.model.UserProfile
import nminhcuong.aipt.feature.profile.domain.usecase.ObserveEquipmentUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.ObserveProfileUseCase
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import nminhcuong.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class BuildAiTrainerChatRequestUseCase @Inject constructor(
    private val observeProfile: ObserveProfileUseCase,
    private val observeEquipment: ObserveEquipmentUseCase,
    private val observeWorkoutSchedule: ObserveWorkoutScheduleUseCase,
    private val observeRecentWorkoutSessions: ObserveRecentWorkoutSessionsUseCase,
) {
    suspend operator fun invoke(
        message: String,
        sentAt: Long,
        conversationHistory: List<AiTrainerChatMessage>,
    ): AiTrainerChatRequest {
        val profile = observeProfile().first()
        val equipment = observeEquipment().first()
        val schedule = observeWorkoutSchedule().first()
        val recentSessions = observeRecentWorkoutSessions(10).first()
        return AiTrainerChatRequest(
            sentAt = sentAt,
            message = message,
            conversationHistory = conversationHistory.takeLast(12),
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
