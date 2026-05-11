package nminhcuong.aipt.testutil

import nminhcuong.aipt.feature.profile.domain.model.EquipmentStatus
import nminhcuong.aipt.feature.profile.domain.model.GymEquipment
import nminhcuong.aipt.feature.profile.domain.model.UserProfile
import nminhcuong.aipt.feature.workout.domain.model.BodyComposition
import nminhcuong.aipt.feature.workout.domain.model.BasicProfile
import nminhcuong.aipt.feature.workout.domain.model.EquipmentAvailability
import nminhcuong.aipt.feature.workout.domain.model.NutritionGuidance
import nminhcuong.aipt.feature.workout.domain.model.PlannedExercise
import nminhcuong.aipt.feature.workout.domain.model.SegmentalMuscleMass
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlan
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanResponse
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPreferences

fun testUserProfile(name: String = "Minh"): UserProfile = UserProfile(
    name = name,
    age = 30,
    heightCm = 170,
    weightKg = 70,
    bodyFatPercent = 18.0,
    skeletalMuscleMassKg = 30.0,
    bodyWaterLiters = 39.0,
    visceralFatLevel = 4,
    basalMetabolicRateKcal = 1600,
    waistHipRatio = 0.8,
    leftArmMuscleKg = 2.7,
    rightArmMuscleKg = 2.8,
    trunkMuscleKg = 23.0,
    leftLegMuscleKg = 8.3,
    rightLegMuscleKg = 8.4,
    trainingGoal = "build_muscle",
    daysPerWeek = 5,
    sessionDurationMinutes = 60,
    experienceLevel = "intermediate",
    injuriesOrLimitations = "None",
    preferredLanguage = "en",
)

fun testEquipment(
    id: Int = 1,
    name: String = "Dumbbell",
    status: EquipmentStatus = EquipmentStatus.Available,
): GymEquipment = GymEquipment(
    id = id,
    name = name,
    imageUrl = "https://example.com/$id.png",
    status = status,
)

fun testWorkoutPlanRequest(name: String = "Minh"): WorkoutPlanRequest = WorkoutPlanRequest(
    basicProfile = BasicProfile(name = name, age = 30, heightCm = 170, weightKg = 70),
    bodyComposition = BodyComposition(
        bodyFatPercentage = 18.0,
        skeletalMuscleMassKg = 30.0,
        bodyWaterPercentage = 39.0,
        visceralFatLevel = 4,
        bmrKcal = 1600,
        waistToHipRatio = 0.8,
    ),
    segmentalMuscleMass = SegmentalMuscleMass(
        leftArmMuscleKg = 2.7,
        rightArmMuscleKg = 2.8,
        trunkMuscleKg = 23.0,
        leftLegMuscleKg = 8.3,
        rightLegMuscleKg = 8.4,
    ),
    trainingGoal = "build_muscle",
    equipment = listOf(EquipmentAvailability(name = "dumbbells", status = "available")),
    preferences = WorkoutPreferences(
        daysPerWeek = 5,
        sessionDurationMinutes = 60,
        experienceLevel = "intermediate",
        injuriesOrLimitations = "None",
        preferredLanguage = "en",
    ),
)

fun testWorkoutDay(day: Int = 1, title: String = "Push Day"): WorkoutDay = WorkoutDay(
    day = day,
    title = title,
    focus = "Chest and triceps",
    warmup = emptyList(),
    exercises = listOf(
        PlannedExercise(
            name = "Bench Press",
            sets = 4,
            reps = "8",
            durationMinutes = null,
            restSeconds = 90,
            intensity = "RPE 8",
            equipment = listOf("Barbell"),
            notes = "Pause reps",
        ),
    ),
    cooldown = emptyList(),
)

fun testWorkoutPlanResponse(day: WorkoutDay = testWorkoutDay()): WorkoutPlanResponse = WorkoutPlanResponse(
    plan = WorkoutPlan(
        analysisSummary = "Plan summary",
        followUpQuestions = emptyList(),
        assumptions = emptyList(),
        weeklySchedule = listOf(day),
        progressionPlan = emptyList(),
        nutritionGuidance = NutritionGuidance(
            calorieGuidance = "Maintenance",
            proteinGuidance = "High protein",
            hydrationGuidance = "Drink water",
        ),
        recoveryGuidance = emptyList(),
        safetyNotes = emptyList(),
    ),
    model = "test-model",
)
