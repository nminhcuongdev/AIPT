package com.example.aipt.feature.chat.domain.model

import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.example.aipt.feature.workout.domain.model.WorkoutProgressEntry
import com.google.gson.annotations.SerializedName

data class AiTrainerChatRequest(
    @SerializedName("sent_at") val sentAt: Long,
    @SerializedName("message") val message: String,
    @SerializedName("conversation_history") val conversationHistory: List<AiTrainerChatMessage>,
    @SerializedName("context") val context: AiTrainerChatContext,
)

data class AiTrainerChatMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String,
    @SerializedName("created_at") val createdAt: Long,
)

data class AiTrainerChatContext(
    @SerializedName("profile") val profile: AiTrainerProfileContext?,
    @SerializedName("equipment") val equipment: List<AiTrainerEquipmentContext>,
    @SerializedName("current_plan") val currentPlan: AiTrainerPlanContext?,
    @SerializedName("today_workout") val todayWorkout: WorkoutDay?,
    @SerializedName("recent_session_states") val recentSessionStates: List<AiTrainerSessionStateContext>,
    @SerializedName("recent_workout_logs") val recentWorkoutLogs: List<WorkoutProgressEntry>,
    @SerializedName("data_notes") val dataNotes: List<String>,
)

data class AiTrainerProfileContext(
    @SerializedName("name") val name: String,
    @SerializedName("age") val age: Int?,
    @SerializedName("height_cm") val heightCm: Int?,
    @SerializedName("weight_kg") val weightKg: Int?,
    @SerializedName("body_fat_percentage") val bodyFatPercentage: Double?,
    @SerializedName("skeletal_muscle_mass_kg") val skeletalMuscleMassKg: Double?,
    @SerializedName("body_water_liters") val bodyWaterLiters: Double?,
    @SerializedName("visceral_fat_level") val visceralFatLevel: Int?,
    @SerializedName("bmr_kcal") val bmrKcal: Int?,
    @SerializedName("waist_to_hip_ratio") val waistToHipRatio: Double?,
    @SerializedName("left_arm_muscle_kg") val leftArmMuscleKg: Double?,
    @SerializedName("right_arm_muscle_kg") val rightArmMuscleKg: Double?,
    @SerializedName("trunk_muscle_kg") val trunkMuscleKg: Double?,
    @SerializedName("left_leg_muscle_kg") val leftLegMuscleKg: Double?,
    @SerializedName("right_leg_muscle_kg") val rightLegMuscleKg: Double?,
    @SerializedName("training_goal") val trainingGoal: String,
    @SerializedName("days_per_week") val daysPerWeek: Int?,
    @SerializedName("session_duration_minutes") val sessionDurationMinutes: Int?,
    @SerializedName("experience_level") val experienceLevel: String,
    @SerializedName("injuries_or_limitations") val injuriesOrLimitations: String,
    @SerializedName("preferred_language") val preferredLanguage: String,
)

data class AiTrainerEquipmentContext(
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
)

data class AiTrainerPlanContext(
    @SerializedName("weekly_schedule") val weeklySchedule: List<WorkoutDay>,
)

data class AiTrainerSessionStateContext(
    @SerializedName("date") val date: String,
    @SerializedName("day") val day: Int,
    @SerializedName("status") val status: String,
    @SerializedName("updated_at") val updatedAt: Long,
)

data class AiTrainerChatResponse(
    @SerializedName("reply") val reply: String,
    @SerializedName("recommendations") val recommendations: List<String>?,
    @SerializedName("safety_notes") val safetyNotes: List<String>?,
    @SerializedName("suggested_actions") val suggestedActions: List<AiTrainerSuggestedAction>?,
    @SerializedName("needs_medical_attention") val needsMedicalAttention: Boolean?,
    @SerializedName("plan_adjustment") val planAdjustment: WorkoutDay?,
    @SerializedName("model") val model: String?,
)

data class AiTrainerSuggestedAction(
    @SerializedName("type") val type: String,
    @SerializedName("label") val label: String,
    @SerializedName("details") val details: String?,
)
