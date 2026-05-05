package com.example.aipt.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.profile.domain.model.EquipmentStatus
import com.example.aipt.feature.profile.domain.model.GymEquipment
import com.example.aipt.feature.profile.domain.model.UserProfile
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileSetupUiState(
    val name: String = "",
    val age: String = "",
    val heightCm: String = "",
    val weightKg: String = "",
    val bodyFatPercent: String = "18.9",
    val skeletalMuscleMassKg: String = "28.5",
    val bodyWaterLiters: String = "37",
    val visceralFatLevel: String = "4",
    val basalMetabolicRateKcal: String = "1460",
    val waistHipRatio: String = "0.80",
    val leftArmMuscleKg: String = "2.62",
    val rightArmMuscleKg: String = "2.59",
    val trunkMuscleKg: String = "22.1",
    val leftLegMuscleKg: String = "8.19",
    val rightLegMuscleKg: String = "7.89",
    val selectedGoal: String = TrainingGoals.first().label,
    val daysPerWeek: String = "5",
    val sessionDurationMinutes: String = "60",
    val experienceLevel: String = ExperienceLevels[1],
    val injuriesOrLimitations: String = "None",
    val preferredLanguage: String = PreferredLanguages.first().code,
    val equipment: List<GymEquipment> = emptyList(),
    val saved: Boolean = false,
) {
    val currentEquipment: GymEquipment? = equipment.firstOrNull { it.status == EquipmentStatus.Unknown }
    val availableCount: Int = equipment.count { it.status == EquipmentStatus.Available }
    val unavailableCount: Int = equipment.count { it.status == EquipmentStatus.Unavailable }
    val remainingCount: Int = equipment.count { it.status == EquipmentStatus.Unknown }
    val canSave: Boolean = name.isNotBlank()
}

data class TrainingGoalOption(val label: String, val apiValue: String)
data class LanguageOption(val label: String, val code: String)

val TrainingGoals = listOf(
    TrainingGoalOption("Build muscle", "build_muscle"),
    TrainingGoalOption("Lose fat", "lose_fat"),
    TrainingGoalOption("Gain strength", "gain_strength"),
    TrainingGoalOption("Improve endurance", "improve_endurance"),
    TrainingGoalOption("Maintain fitness", "maintain_fitness"),
)

val ExperienceLevels = listOf("beginner", "intermediate", "advanced")
val PreferredLanguages = listOf(
    LanguageOption("English", "en"),
    LanguageOption("Vietnamese", "vi"),
)

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {
    private val name = MutableStateFlow("")
    private val age = MutableStateFlow("")
    private val heightCm = MutableStateFlow("")
    private val weightKg = MutableStateFlow("")
    private val bodyFatPercent = MutableStateFlow("18.9")
    private val skeletalMuscleMassKg = MutableStateFlow("28.5")
    private val bodyWaterLiters = MutableStateFlow("37")
    private val visceralFatLevel = MutableStateFlow("4")
    private val basalMetabolicRateKcal = MutableStateFlow("1460")
    private val waistHipRatio = MutableStateFlow("0.80")
    private val leftArmMuscleKg = MutableStateFlow("2.62")
    private val rightArmMuscleKg = MutableStateFlow("2.59")
    private val trunkMuscleKg = MutableStateFlow("22.1")
    private val leftLegMuscleKg = MutableStateFlow("8.19")
    private val rightLegMuscleKg = MutableStateFlow("7.89")
    private val selectedGoal = MutableStateFlow(TrainingGoals.first().label)
    private val daysPerWeek = MutableStateFlow("5")
    private val sessionDurationMinutes = MutableStateFlow("60")
    private val experienceLevel = MutableStateFlow(ExperienceLevels[1])
    private val injuriesOrLimitations = MutableStateFlow("None")
    private val preferredLanguage = MutableStateFlow(PreferredLanguages.first().code)
    private val saved = MutableStateFlow(false)

    val uiState: StateFlow<ProfileSetupUiState> = combine(
        name,
        age,
        heightCm,
        weightKg,
        bodyFatPercent,
        skeletalMuscleMassKg,
        bodyWaterLiters,
        visceralFatLevel,
        basalMetabolicRateKcal,
        waistHipRatio,
        leftArmMuscleKg,
        rightArmMuscleKg,
        trunkMuscleKg,
        leftLegMuscleKg,
        rightLegMuscleKg,
        selectedGoal,
        daysPerWeek,
        sessionDurationMinutes,
        experienceLevel,
        injuriesOrLimitations,
        preferredLanguage,
        repository.observeEquipment(),
        saved,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        ProfileSetupUiState(
            name = values[0] as String,
            age = values[1] as String,
            heightCm = values[2] as String,
            weightKg = values[3] as String,
            bodyFatPercent = values[4] as String,
            skeletalMuscleMassKg = values[5] as String,
            bodyWaterLiters = values[6] as String,
            visceralFatLevel = values[7] as String,
            basalMetabolicRateKcal = values[8] as String,
            waistHipRatio = values[9] as String,
            leftArmMuscleKg = values[10] as String,
            rightArmMuscleKg = values[11] as String,
            trunkMuscleKg = values[12] as String,
            leftLegMuscleKg = values[13] as String,
            rightLegMuscleKg = values[14] as String,
            selectedGoal = values[15] as String,
            daysPerWeek = values[16] as String,
            sessionDurationMinutes = values[17] as String,
            experienceLevel = values[18] as String,
            injuriesOrLimitations = values[19] as String,
            preferredLanguage = values[20] as String,
            equipment = values[21] as List<GymEquipment>,
            saved = values[22] as Boolean,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileSetupUiState(),
    )

    init {
        viewModelScope.launch { repository.seedEquipmentIfNeeded() }
    }

    fun onNameChanged(value: String) { name.value = value; saved.value = false }
    fun onAgeChanged(value: String) { age.value = value.onlyDigits().take(3); saved.value = false }
    fun onHeightChanged(value: String) { heightCm.value = value.onlyDigits().take(3); saved.value = false }
    fun onWeightChanged(value: String) { weightKg.value = value.onlyDigits().take(3); saved.value = false }

    fun onBodyFatPercentChanged(value: String) = updateDecimal(bodyFatPercent, value, maxLength = 5)
    fun onSkeletalMuscleMassChanged(value: String) = updateDecimal(skeletalMuscleMassKg, value, maxLength = 5)
    fun onBodyWaterLitersChanged(value: String) = updateDecimal(bodyWaterLiters, value, maxLength = 5)
    fun onVisceralFatLevelChanged(value: String) = updateInteger(visceralFatLevel, value, maxLength = 2)
    fun onBasalMetabolicRateChanged(value: String) = updateInteger(basalMetabolicRateKcal, value, maxLength = 4)
    fun onWaistHipRatioChanged(value: String) = updateDecimal(waistHipRatio, value, maxLength = 4)
    fun onLeftArmMuscleChanged(value: String) = updateDecimal(leftArmMuscleKg, value, maxLength = 4)
    fun onRightArmMuscleChanged(value: String) = updateDecimal(rightArmMuscleKg, value, maxLength = 4)
    fun onTrunkMuscleChanged(value: String) = updateDecimal(trunkMuscleKg, value, maxLength = 5)
    fun onLeftLegMuscleChanged(value: String) = updateDecimal(leftLegMuscleKg, value, maxLength = 5)
    fun onRightLegMuscleChanged(value: String) = updateDecimal(rightLegMuscleKg, value, maxLength = 5)

    fun onGoalSelected(goal: String) { selectedGoal.value = goal; saved.value = false }
    fun onDaysPerWeekChanged(value: String) { daysPerWeek.value = value.onlyDigits().take(1); saved.value = false }
    fun onSessionDurationChanged(value: String) { sessionDurationMinutes.value = value.onlyDigits().take(3); saved.value = false }
    fun onExperienceLevelSelected(value: String) { experienceLevel.value = value; saved.value = false }
    fun onInjuriesChanged(value: String) { injuriesOrLimitations.value = value.take(180); saved.value = false }
    fun onPreferredLanguageSelected(value: String) { preferredLanguage.value = value; saved.value = false }

    fun onEquipmentSwiped(equipment: GymEquipment, available: Boolean) {
        viewModelScope.launch {
            repository.setEquipmentStatus(
                id = equipment.id,
                status = if (available) EquipmentStatus.Available else EquipmentStatus.Unavailable,
            )
        }
        saved.value = false
    }

    fun onResetEquipment() {
        viewModelScope.launch { repository.resetEquipmentChoices() }
        saved.value = false
    }

    fun onSaveProfile() {
        val profile = UserProfile(
            name = name.value.trim(),
            age = age.value.toIntOrNull(),
            heightCm = heightCm.value.toIntOrNull(),
            weightKg = weightKg.value.toIntOrNull(),
            bodyFatPercent = bodyFatPercent.value.toDoubleOrNull(),
            skeletalMuscleMassKg = skeletalMuscleMassKg.value.toDoubleOrNull(),
            bodyWaterLiters = bodyWaterLiters.value.toDoubleOrNull(),
            visceralFatLevel = visceralFatLevel.value.toIntOrNull(),
            basalMetabolicRateKcal = basalMetabolicRateKcal.value.toIntOrNull(),
            waistHipRatio = waistHipRatio.value.toDoubleOrNull(),
            leftArmMuscleKg = leftArmMuscleKg.value.toDoubleOrNull(),
            rightArmMuscleKg = rightArmMuscleKg.value.toDoubleOrNull(),
            trunkMuscleKg = trunkMuscleKg.value.toDoubleOrNull(),
            leftLegMuscleKg = leftLegMuscleKg.value.toDoubleOrNull(),
            rightLegMuscleKg = rightLegMuscleKg.value.toDoubleOrNull(),
            trainingGoal = selectedGoal.value.toGoalApiValue(),
            daysPerWeek = daysPerWeek.value.toIntOrNull()?.coerceIn(2, 7),
            sessionDurationMinutes = sessionDurationMinutes.value.toIntOrNull()?.coerceIn(20, 180),
            experienceLevel = experienceLevel.value,
            injuriesOrLimitations = injuriesOrLimitations.value.ifBlank { "None" },
            preferredLanguage = preferredLanguage.value,
        )
        if (profile.name.isBlank()) return
        viewModelScope.launch {
            repository.saveProfile(profile)
            saved.value = true
        }
    }

    private fun String.toGoalApiValue(): String = TrainingGoals.firstOrNull { it.label == this }?.apiValue ?: this

    private fun updateDecimal(target: MutableStateFlow<String>, value: String, maxLength: Int) {
        target.value = value.onlyDecimal().take(maxLength)
        saved.value = false
    }

    private fun updateInteger(target: MutableStateFlow<String>, value: String, maxLength: Int) {
        target.value = value.onlyDigits().take(maxLength)
        saved.value = false
    }

    private fun String.onlyDigits(): String = filter { it.isDigit() }

    private fun String.onlyDecimal(): String {
        var dotUsed = false
        return filter { char ->
            when {
                char.isDigit() -> true
                char == '.' && !dotUsed -> { dotUsed = true; true }
                else -> false
            }
        }
    }
}
