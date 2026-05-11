package nminhcuong.aipt.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import nminhcuong.aipt.feature.profile.domain.model.EquipmentStatus
import nminhcuong.aipt.feature.profile.domain.model.GymEquipment
import nminhcuong.aipt.feature.profile.domain.model.UserProfile
import nminhcuong.aipt.feature.profile.domain.usecase.DeleteProfileUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.ObserveEquipmentUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.ObserveProfileUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.ResetEquipmentChoicesUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.SaveProfileUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.SeedEquipmentUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.SetEquipmentStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileSetupUiState(
    val name: String = "",
    val age: String = "",
    val heightCm: String = "",
    val weightKg: String = "",
    val bodyFatPercent: String = "",
    val skeletalMuscleMassKg: String = "",
    val bodyWaterLiters: String = "",
    val visceralFatLevel: String = "",
    val basalMetabolicRateKcal: String = "",
    val waistHipRatio: String = "",
    val leftArmMuscleKg: String = "",
    val rightArmMuscleKg: String = "",
    val trunkMuscleKg: String = "",
    val leftLegMuscleKg: String = "",
    val rightLegMuscleKg: String = "",
    val selectedGoal: String = TrainingGoals.first().label,
    val daysPerWeek: String = "5",
    val sessionDurationMinutes: String = "60",
    val experienceLevel: String = ExperienceLevels[1],
    val injuriesOrLimitations: String = "None",
    val preferredLanguage: String = PreferredLanguages.first().code,
    val equipment: List<GymEquipment> = emptyList(),
    val hasProfile: Boolean = false,
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
    private val observeProfile: ObserveProfileUseCase,
    private val observeEquipment: ObserveEquipmentUseCase,
    private val seedEquipment: SeedEquipmentUseCase,
    private val saveProfile: SaveProfileUseCase,
    private val deleteProfile: DeleteProfileUseCase,
    private val resetEquipmentChoices: ResetEquipmentChoicesUseCase,
    private val setEquipmentStatus: SetEquipmentStatusUseCase,
) : ViewModel() {
    private val name = MutableStateFlow("")
    private val age = MutableStateFlow("")
    private val heightCm = MutableStateFlow("")
    private val weightKg = MutableStateFlow("")
    private val bodyFatPercent = MutableStateFlow("")
    private val skeletalMuscleMassKg = MutableStateFlow("")
    private val bodyWaterLiters = MutableStateFlow("")
    private val visceralFatLevel = MutableStateFlow("")
    private val basalMetabolicRateKcal = MutableStateFlow("")
    private val waistHipRatio = MutableStateFlow("")
    private val leftArmMuscleKg = MutableStateFlow("")
    private val rightArmMuscleKg = MutableStateFlow("")
    private val trunkMuscleKg = MutableStateFlow("")
    private val leftLegMuscleKg = MutableStateFlow("")
    private val rightLegMuscleKg = MutableStateFlow("")
    private val selectedGoal = MutableStateFlow(TrainingGoals.first().label)
    private val daysPerWeek = MutableStateFlow("5")
    private val sessionDurationMinutes = MutableStateFlow("60")
    private val experienceLevel = MutableStateFlow(ExperienceLevels[1])
    private val injuriesOrLimitations = MutableStateFlow("None")
    private val preferredLanguage = MutableStateFlow(PreferredLanguages.first().code)
    private val hasProfile = MutableStateFlow(false)
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
        observeEquipment(),
        hasProfile,
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
            hasProfile = values[22] as Boolean,
            saved = values[23] as Boolean,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileSetupUiState(),
    )

    init {
        viewModelScope.launch { seedEquipment() }
        viewModelScope.launch {
            observeProfile().collect { profile ->
                hasProfile.value = profile != null
                if (profile == null) {
                    clearForm()
                } else {
                    populateFromProfile(profile)
                    saved.value = true
                }
            }
        }
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
            setEquipmentStatus(
                id = equipment.id,
                status = if (available) EquipmentStatus.Available else EquipmentStatus.Unavailable,
            )
        }
        saved.value = false
    }

    fun onResetEquipment() {
        viewModelScope.launch { resetEquipmentChoices() }
        saved.value = false
    }

    fun onCreateNewProfile() {
        clearForm()
        hasProfile.value = false
        saved.value = false
        viewModelScope.launch { resetEquipmentChoices() }
    }

    fun onDeleteProfile() {
        viewModelScope.launch {
            deleteProfile()
            resetEquipmentChoices()
            clearForm()
            hasProfile.value = false
            saved.value = false
        }
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
            saveProfile(profile)
            hasProfile.value = true
            saved.value = true
        }
    }

    private fun populateFromProfile(profile: UserProfile) {
        name.value = profile.name
        age.value = profile.age?.toString().orEmpty()
        heightCm.value = profile.heightCm?.toString().orEmpty()
        weightKg.value = profile.weightKg?.toString().orEmpty()
        bodyFatPercent.value = profile.bodyFatPercent?.display().orEmpty()
        skeletalMuscleMassKg.value = profile.skeletalMuscleMassKg?.display().orEmpty()
        bodyWaterLiters.value = profile.bodyWaterLiters?.display().orEmpty()
        visceralFatLevel.value = profile.visceralFatLevel?.toString().orEmpty()
        basalMetabolicRateKcal.value = profile.basalMetabolicRateKcal?.toString().orEmpty()
        waistHipRatio.value = profile.waistHipRatio?.display().orEmpty()
        leftArmMuscleKg.value = profile.leftArmMuscleKg?.display().orEmpty()
        rightArmMuscleKg.value = profile.rightArmMuscleKg?.display().orEmpty()
        trunkMuscleKg.value = profile.trunkMuscleKg?.display().orEmpty()
        leftLegMuscleKg.value = profile.leftLegMuscleKg?.display().orEmpty()
        rightLegMuscleKg.value = profile.rightLegMuscleKg?.display().orEmpty()
        selectedGoal.value = profile.trainingGoal.toGoalLabel()
        daysPerWeek.value = profile.daysPerWeek?.toString() ?: "5"
        sessionDurationMinutes.value = profile.sessionDurationMinutes?.toString() ?: "60"
        experienceLevel.value = profile.experienceLevel.takeIf { it in ExperienceLevels } ?: ExperienceLevels[1]
        injuriesOrLimitations.value = profile.injuriesOrLimitations.ifBlank { "None" }
        preferredLanguage.value = profile.preferredLanguage.takeIf { code -> PreferredLanguages.any { it.code == code } } ?: PreferredLanguages.first().code
    }

    private fun clearForm() {
        name.value = ""
        age.value = ""
        heightCm.value = ""
        weightKg.value = ""
        bodyFatPercent.value = ""
        skeletalMuscleMassKg.value = ""
        bodyWaterLiters.value = ""
        visceralFatLevel.value = ""
        basalMetabolicRateKcal.value = ""
        waistHipRatio.value = ""
        leftArmMuscleKg.value = ""
        rightArmMuscleKg.value = ""
        trunkMuscleKg.value = ""
        leftLegMuscleKg.value = ""
        rightLegMuscleKg.value = ""
        selectedGoal.value = TrainingGoals.first().label
        daysPerWeek.value = "5"
        sessionDurationMinutes.value = "60"
        experienceLevel.value = ExperienceLevels[1]
        injuriesOrLimitations.value = "None"
        preferredLanguage.value = PreferredLanguages.first().code
    }

    private fun String.toGoalApiValue(): String = TrainingGoals.firstOrNull { it.label == this }?.apiValue ?: this

    private fun String.toGoalLabel(): String = TrainingGoals.firstOrNull { it.apiValue == this }?.label ?: this

    private fun Double.display(): String = if (this % 1.0 == 0.0) toInt().toString() else toString()

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
