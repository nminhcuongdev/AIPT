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
    val selectedGoal: String = TrainingGoals.first(),
    val equipment: List<GymEquipment> = emptyList(),
    val saved: Boolean = false,
) {
    val currentEquipment: GymEquipment? = equipment.firstOrNull { it.status == EquipmentStatus.Unknown }
    val availableCount: Int = equipment.count { it.status == EquipmentStatus.Available }
    val unavailableCount: Int = equipment.count { it.status == EquipmentStatus.Unavailable }
    val remainingCount: Int = equipment.count { it.status == EquipmentStatus.Unknown }
    val canSave: Boolean = name.isNotBlank()
}

val TrainingGoals = listOf(
    "Tang co",
    "Giam mo",
    "Tang suc manh",
    "Cai thien suc ben",
    "Duy tri suc khoe",
)

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {
    private val name = MutableStateFlow("")
    private val age = MutableStateFlow("")
    private val heightCm = MutableStateFlow("")
    private val weightKg = MutableStateFlow("")
    private val selectedGoal = MutableStateFlow(TrainingGoals.first())
    private val saved = MutableStateFlow(false)

    val uiState: StateFlow<ProfileSetupUiState> = combine(
        name,
        age,
        heightCm,
        weightKg,
        selectedGoal,
        repository.observeEquipment(),
        saved,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        ProfileSetupUiState(
            name = values[0] as String,
            age = values[1] as String,
            heightCm = values[2] as String,
            weightKg = values[3] as String,
            selectedGoal = values[4] as String,
            equipment = values[5] as List<GymEquipment>,
            saved = values[6] as Boolean,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileSetupUiState(),
    )

    init {
        viewModelScope.launch {
            repository.seedEquipmentIfNeeded()
        }
    }

    fun onNameChanged(value: String) {
        name.value = value
        saved.value = false
    }

    fun onAgeChanged(value: String) {
        age.value = value.filter { it.isDigit() }.take(3)
        saved.value = false
    }

    fun onHeightChanged(value: String) {
        heightCm.value = value.filter { it.isDigit() }.take(3)
        saved.value = false
    }

    fun onWeightChanged(value: String) {
        weightKg.value = value.filter { it.isDigit() }.take(3)
        saved.value = false
    }

    fun onGoalSelected(goal: String) {
        selectedGoal.value = goal
        saved.value = false
    }

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
        viewModelScope.launch {
            repository.resetEquipmentChoices()
        }
        saved.value = false
    }

    fun onSaveProfile() {
        val profile = UserProfile(
            name = name.value.trim(),
            age = age.value.toIntOrNull(),
            heightCm = heightCm.value.toIntOrNull(),
            weightKg = weightKg.value.toIntOrNull(),
            trainingGoal = selectedGoal.value,
        )
        if (profile.name.isBlank()) return

        viewModelScope.launch {
            repository.saveProfile(profile)
            saved.value = true
        }
    }
}
