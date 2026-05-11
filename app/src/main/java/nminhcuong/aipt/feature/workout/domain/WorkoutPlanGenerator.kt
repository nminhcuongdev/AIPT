package nminhcuong.aipt.feature.workout.domain

import nminhcuong.aipt.feature.exercise.domain.model.Exercise
import nminhcuong.aipt.feature.profile.domain.model.EquipmentStatus
import nminhcuong.aipt.feature.profile.domain.model.GymEquipment
import nminhcuong.aipt.feature.profile.domain.model.UserProfile
import nminhcuong.aipt.feature.workout.domain.model.BasicProfile
import nminhcuong.aipt.feature.workout.domain.model.BodyComposition
import nminhcuong.aipt.feature.workout.domain.model.EquipmentAvailability
import nminhcuong.aipt.feature.workout.domain.model.NutritionGuidance
import nminhcuong.aipt.feature.workout.domain.model.PlannedExercise
import nminhcuong.aipt.feature.workout.domain.model.ProgressionWeek
import nminhcuong.aipt.feature.workout.domain.model.SegmentalMuscleMass
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlan
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanResponse
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPreferences
import javax.inject.Inject
import kotlin.math.roundToInt

class WorkoutPlanGenerator @Inject constructor() {
    fun buildRequest(profile: UserProfile, equipment: List<GymEquipment>): WorkoutPlanRequest = WorkoutPlanRequest(
        basicProfile = BasicProfile(
            name = profile.name,
            age = profile.age ?: 0,
            heightCm = profile.heightCm ?: 0,
            weightKg = profile.weightKg ?: 0,
        ),
        bodyComposition = BodyComposition(
            bodyFatPercentage = profile.bodyFatPercent ?: 0.0,
            skeletalMuscleMassKg = profile.skeletalMuscleMassKg ?: 0.0,
            bodyWaterPercentage = profile.bodyWaterLiters ?: 0.0,
            visceralFatLevel = profile.visceralFatLevel ?: 0,
            bmrKcal = profile.basalMetabolicRateKcal ?: 0,
            waistToHipRatio = profile.waistHipRatio ?: 0.0,
        ),
        segmentalMuscleMass = SegmentalMuscleMass(
            leftArmMuscleKg = profile.leftArmMuscleKg ?: 0.0,
            rightArmMuscleKg = profile.rightArmMuscleKg ?: 0.0,
            trunkMuscleKg = profile.trunkMuscleKg ?: 0.0,
            leftLegMuscleKg = profile.leftLegMuscleKg ?: 0.0,
            rightLegMuscleKg = profile.rightLegMuscleKg ?: 0.0,
        ),
        trainingGoal = profile.trainingGoal,
        equipment = equipment.map { item ->
            EquipmentAvailability(
                name = item.name.toEquipmentSlug(),
                status = if (item.status == EquipmentStatus.Available) "available" else "unavailable",
            )
        },
        preferences = WorkoutPreferences(
            daysPerWeek = profile.daysPerWeek?.coerceIn(1, 7) ?: 1,
            sessionDurationMinutes = profile.sessionDurationMinutes?.coerceIn(20, 180) ?: 20,
            experienceLevel = profile.experienceLevel.ifBlank { "beginner" },
            injuriesOrLimitations = profile.injuriesOrLimitations.ifBlank { "None" },
            preferredLanguage = profile.preferredLanguage.ifBlank { "en" },
        ),
    )

    fun generate(request: WorkoutPlanRequest, exercises: List<Exercise>): WorkoutPlanResponse {
        val availableEquipment = request.equipment
            .filter { it.status == "available" }
            .map { it.name }
            .toSet()
        val days = request.preferences.daysPerWeek.coerceIn(2, 6)
        val split = splitFor(days)
        val filteredExercises = exercises.filter { exercise ->
            val required = exercise.equipment.toEquipmentSlug()
            required == "bodyweight" || required in availableEquipment
        }.ifEmpty { exercises.filter { it.equipment.equals("Bodyweight", ignoreCase = true) } }

        val schedule = split.take(days).mapIndexed { index, template ->
            val dayExercises = selectExercises(filteredExercises, template.groups).take(4)
            WorkoutDay(
                day = index + 1,
                title = template.title,
                focus = template.focus,
                warmup = warmupFor(template.title, availableEquipment),
                exercises = dayExercises.map { exercise -> exercise.toPlannedExercise(request.preferences.experienceLevel) },
                cooldown = listOf(
                    "Breathe slowly for 2 minutes.",
                    "Stretch the main trained muscle groups for 30-45 seconds each.",
                    "Log load, reps, and any joint discomfort.",
                ),
            )
        }

        return WorkoutPlanResponse(
            plan = WorkoutPlan(
                analysisSummary = buildAnalysis(request, availableEquipment),
                followUpQuestions = listOf(
                    "How long have you trained consistently?",
                    "Which exercises currently cause shoulder, lower-back, or knee discomfort?",
                    "Which muscle group should be prioritized over the next 8-12 weeks?",
                ),
                assumptions = listOf(
                    "No severe injury is present unless listed in limitations.",
                    "Each session lasts around ${request.preferences.sessionDurationMinutes} minutes.",
                    "The plan prioritizes ${request.trainingGoal.replace('_', ' ')} while managing fatigue.",
                ),
                weeklySchedule = schedule,
                progressionPlan = progressionFor(request.preferences.experienceLevel),
                nutritionGuidance = nutritionFor(request),
                recoveryGuidance = listOf(
                    "Sleep 7-8 hours per night.",
                    "Avoid hard loading for a muscle group that is still clearly sore.",
                    "Use light walking or mobility on rest days to support recovery.",
                ),
                safetyNotes = listOf(
                    "Stop any exercise that causes sharp joint, lower-back, chest, or radiating pain.",
                    "Do not increase load when technique breaks down.",
                    "Consult a clinician or qualified coach if you have medical history or unresolved injury.",
                ),
            ),
            model = "local-rule-based-planner",
        )
    }

    private fun buildAnalysis(request: WorkoutPlanRequest, availableEquipment: Set<String>): String {
        val profile = request.basicProfile
        val composition = request.bodyComposition
        val equipmentQuality = if (availableEquipment.size >= 8) "well-equipped" else "limited-equipment"
        return "${profile.name} is ${profile.age ?: "an unspecified age"}, ${profile.heightCm ?: "unknown"}cm, ${profile.weightKg ?: "unknown"}kg, with ${composition.skeletalMuscleMassKg ?: "unknown"}kg skeletal muscle mass and goal ${request.trainingGoal.replace('_', ' ')}. A ${request.preferences.daysPerWeek}-day plan with ${request.preferences.sessionDurationMinutes}-minute sessions fits a ${request.preferences.experienceLevel} trainee using a $equipmentQuality setup."
    }

    private fun selectExercises(exercises: List<Exercise>, groups: List<String>): List<Exercise> =
        groups.flatMap { group -> exercises.filter { it.muscleGroup.equals(group, ignoreCase = true) }.take(2) }
            .ifEmpty { exercises.take(4) }

    private fun Exercise.toPlannedExercise(experienceLevel: String): PlannedExercise {
        val isCompound = muscleGroup in setOf("Chest", "Back", "Legs", "Full Body")
        val sets = when (experienceLevel) {
            "beginner" -> if (isCompound) 3 else 2
            "advanced" -> if (isCompound) 4 else 3
            else -> if (isCompound) 4 else 3
        }
        return PlannedExercise(
            name = name,
            sets = sets,
            reps = if (isCompound) "6-10" else "10-15",
            durationMinutes = null,
            restSeconds = if (isCompound) 120 else 60,
            intensity = if (experienceLevel == "beginner") "RPE 6-7" else "RPE 7-8",
            equipment = listOf(equipment.toEquipmentSlug()),
            notes = "Prioritize controlled tempo and stop 1-3 reps before form breaks.",
        )
    }

    private fun warmupFor(title: String, availableEquipment: Set<String>): List<String> = buildList {
        if ("treadmill" in availableEquipment) add("Walk briskly on the treadmill for 5 minutes.") else add("Do 5 minutes of light full-body movement.")
        add("Run joint circles and dynamic mobility for the target areas.")
        add("Perform 1-2 lighter ramp-up sets before the first working exercise.")
    }

    private fun progressionFor(experienceLevel: String): List<ProgressionWeek> = listOf(
        ProgressionWeek(1, "Train at ${if (experienceLevel == "beginner") "RPE 6-7" else "RPE 7"}, focus on technique, and record loads."),
        ProgressionWeek(2, "Add 1-2 reps per set when form stays stable."),
        ProgressionWeek(3, "Increase load by 2.5-5% on main lifts after hitting the top of the rep range."),
        ProgressionWeek(4, "Reduce volume by 20-30% if fatigue, joint irritation, or performance drops accumulate."),
    )

    private fun nutritionFor(request: WorkoutPlanRequest): NutritionGuidance {
        val weight = request.basicProfile.weightKg ?: 70
        val proteinLow = (weight * 1.6).roundToInt()
        val proteinHigh = (weight * 2.2).roundToInt()
        val goal = request.trainingGoal
        return NutritionGuidance(
            calorieGuidance = if (goal == "build_muscle") "Use a small surplus of roughly 150-250 kcal/day." else "Start near maintenance and adjust weekly based on body weight trend.",
            proteinGuidance = "Target $proteinLow-$proteinHigh g protein/day based on current body weight.",
            hydrationGuidance = "Aim for 30-40 ml water/kg/day, plus more during high-sweat sessions.",
        )
    }

    private fun splitFor(days: Int): List<SplitTemplate> = when (days) {
        2 -> listOf(upper(), lower())
        3 -> listOf(push(), pull(), legs())
        4 -> listOf(upper(), lower(), push(), pull())
        5 -> listOf(push(), pull(), legs(), upper(), lower())
        else -> listOf(push(), pull(), legs(), upper(), lower(), fullBody())
    }

    private fun push() = SplitTemplate("Push - Chest, Shoulders, Triceps", "Upper-body pushing strength and hypertrophy.", listOf("Chest", "Shoulders", "Arms"))
    private fun pull() = SplitTemplate("Pull - Back, Biceps", "Back width, rowing strength, and arm work.", listOf("Back", "Arms", "Core"))
    private fun legs() = SplitTemplate("Legs - Quads, Hamstrings, Glutes", "Lower-body hypertrophy and posterior-chain balance.", listOf("Legs", "Hamstrings", "Glutes", "Calves"))
    private fun upper() = SplitTemplate("Upper Body", "Balanced upper-body volume.", listOf("Chest", "Back", "Shoulders", "Arms"))
    private fun lower() = SplitTemplate("Lower Body", "Squat, hinge, glute, and calf development.", listOf("Legs", "Hamstrings", "Glutes", "Calves"))
    private fun fullBody() = SplitTemplate("Full Body Conditioning", "Total-body strength with conditioning bias.", listOf("Full Body", "Core", "Legs"))

    private fun String.toEquipmentSlug(): String {
        val normalized = lowercase().replace("-", "_").replace(" ", "_")
        return when (normalized) {
            "dumbbell", "dumbbells" -> "dumbbells"
            "pullup_bar", "pull_up_bar" -> "pull_up_bar"
            "cable_machine" -> "cable_machine"
            "leg_press_machine" -> "leg_press_machine"
            "smith_machine" -> "smith_machine"
            "resistance_band", "resistance_bands" -> "resistance_bands"
            "medicine_ball" -> "medicine_ball"
            "rowing_machine" -> "rowing_machine"
            else -> normalized
        }
    }

    private data class SplitTemplate(
        val title: String,
        val focus: String,
        val groups: List<String>,
    )
}
