package com.example.aipt.feature.workout.domain.model

import com.google.gson.annotations.SerializedName

data class WorkoutPlanRequest(
    @SerializedName("basic_profile") val basicProfile: BasicProfile,
    @SerializedName("body_composition") val bodyComposition: BodyComposition,
    @SerializedName("segmental_muscle_mass") val segmentalMuscleMass: SegmentalMuscleMass,
    @SerializedName("training_goal") val trainingGoal: String,
    @SerializedName("equipment") val equipment: List<EquipmentAvailability>,
    @SerializedName("preferences") val preferences: WorkoutPreferences,
)

data class BasicProfile(
    @SerializedName("name") val name: String,
    @SerializedName("age") val age: Int?,
    @SerializedName("height_cm") val heightCm: Int?,
    @SerializedName("weight_kg") val weightKg: Int?,
)

data class BodyComposition(
    @SerializedName("body_fat_percentage") val bodyFatPercentage: Double?,
    @SerializedName("skeletal_muscle_mass_kg") val skeletalMuscleMassKg: Double?,
    @SerializedName("body_water_liters") val bodyWaterLiters: Double?,
    @SerializedName("visceral_fat_level") val visceralFatLevel: Int?,
    @SerializedName("bmr_kcal") val bmrKcal: Int?,
    @SerializedName("waist_to_hip_ratio") val waistToHipRatio: Double?,
)

data class SegmentalMuscleMass(
    @SerializedName("left_arm_muscle_kg") val leftArmMuscleKg: Double?,
    @SerializedName("right_arm_muscle_kg") val rightArmMuscleKg: Double?,
    @SerializedName("trunk_muscle_kg") val trunkMuscleKg: Double?,
    @SerializedName("left_leg_muscle_kg") val leftLegMuscleKg: Double?,
    @SerializedName("right_leg_muscle_kg") val rightLegMuscleKg: Double?,
)

data class EquipmentAvailability(
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
)

data class WorkoutPreferences(
    @SerializedName("days_per_week") val daysPerWeek: Int,
    @SerializedName("session_duration_minutes") val sessionDurationMinutes: Int,
    @SerializedName("experience_level") val experienceLevel: String,
    @SerializedName("injuries_or_limitations") val injuriesOrLimitations: String,
    @SerializedName("preferred_language") val preferredLanguage: String,
)

data class WorkoutPlanResponse(
    @SerializedName("plan") val plan: WorkoutPlan,
    @SerializedName("model") val model: String,
)

data class WorkoutPlan(
    @SerializedName("analysis_summary") val analysisSummary: String,
    @SerializedName("follow_up_questions") val followUpQuestions: List<String>,
    @SerializedName("assumptions") val assumptions: List<String>,
    @SerializedName("weekly_schedule") val weeklySchedule: List<WorkoutDay>,
    @SerializedName("progression_plan") val progressionPlan: List<ProgressionWeek>,
    @SerializedName("nutrition_guidance") val nutritionGuidance: NutritionGuidance,
    @SerializedName("recovery_guidance") val recoveryGuidance: List<String>,
    @SerializedName("safety_notes") val safetyNotes: List<String>,
)

data class WorkoutDay(
    @SerializedName("day") val day: Int,
    @SerializedName("title") val title: String,
    @SerializedName("focus") val focus: String,
    @SerializedName("warmup") val warmup: List<String>,
    @SerializedName("exercises") val exercises: List<PlannedExercise>,
    @SerializedName("cooldown") val cooldown: List<String>,
)

data class PlannedExercise(
    @SerializedName("name") val name: String,
    @SerializedName("sets") val sets: Int?,
    @SerializedName("reps") val reps: String?,
    @SerializedName("duration_minutes") val durationMinutes: Int?,
    @SerializedName("rest_seconds") val restSeconds: Int?,
    @SerializedName("intensity") val intensity: String,
    @SerializedName("equipment") val equipment: List<String>,
    @SerializedName("notes") val notes: String,
)

data class ProgressionWeek(
    @SerializedName("week") val week: Int,
    @SerializedName("instructions") val instructions: String,
)

data class NutritionGuidance(
    @SerializedName("calorie_guidance") val calorieGuidance: String,
    @SerializedName("protein_guidance") val proteinGuidance: String,
    @SerializedName("hydration_guidance") val hydrationGuidance: String,
)

data class WorkoutProgressAnalysisRequest(
    @SerializedName("performed_at") val performedAt: Long,
    @SerializedName("entries") val entries: List<WorkoutProgressEntry>,
)

data class WorkoutProgressEntry(
    @SerializedName("exercise_name") val exerciseName: String,
    @SerializedName("weight_kg") val weightKg: Double?,
    @SerializedName("sets") val sets: Int?,
    @SerializedName("reps") val reps: Int?,
    @SerializedName("notes") val notes: String?,
)

data class WorkoutProgressAnalysisResponse(
    @SerializedName("analysis_summary") val analysisSummary: String?,
    @SerializedName("advice") val advice: String?,
    @SerializedName("recommendations") val recommendations: List<String>?,
    @SerializedName("next_steps") val nextSteps: List<String>?,
    @SerializedName("safety_notes") val safetyNotes: List<String>?,
    @SerializedName("model") val model: String?,
)