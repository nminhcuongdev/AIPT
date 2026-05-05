package com.example.aipt.feature.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import com.example.aipt.feature.workout.domain.WorkoutPlanGenerator
import com.example.aipt.feature.workout.domain.model.WorkoutPlanRequest
import com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse
import com.example.aipt.feature.workout.domain.repository.WorkoutPlanSessionRepository
import com.example.aipt.feature.workout.domain.usecase.CreateWorkoutPlanUseCase
import com.example.aipt.feature.workout.domain.usecase.SaveWorkoutScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutPlanUiState(
    val isLoading: Boolean = false,
    val isProfileMissing: Boolean = false,
    val isSavingPlan: Boolean = false,
    val isPlanConfirmed: Boolean = false,
    val request: WorkoutPlanRequest? = null,
    val response: WorkoutPlanResponse? = null,
    val errorMessage: String? = null,
    val saveMessage: String? = null,
) {
    val canConfirmPlan: Boolean = response != null && !isSavingPlan && !isPlanConfirmed
    val canTrackProgress: Boolean = response != null && isPlanConfirmed && !isSavingPlan
}

@HiltViewModel
class WorkoutPlanViewModel @Inject constructor(
    profileRepository: ProfileRepository,
    exerciseRepository: ExerciseRepository,
    private val generator: WorkoutPlanGenerator,
    private val createWorkoutPlan: CreateWorkoutPlanUseCase,
    private val saveWorkoutSchedule: SaveWorkoutScheduleUseCase,
    private val planSessionRepository: WorkoutPlanSessionRepository,
) : ViewModel() {
    private val remoteState = MutableStateFlow(WorkoutPlanUiState(isLoading = true))
    private var requestJob: Job? = null

    val uiState: StateFlow<WorkoutPlanUiState> = remoteState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WorkoutPlanUiState(isLoading = true),
    )

    init {
        viewModelScope.launch { exerciseRepository.seedIfNeeded() }
        viewModelScope.launch {
            combine(
                profileRepository.observeProfile(),
                profileRepository.observeEquipment(),
                exerciseRepository.observeExercises(),
            ) { profile, equipment, _ ->
                if (profile == null) null else generator.buildRequest(profile, equipment)
            }.collect { request ->
                if (request == null) {
                    requestJob?.cancel()
                    remoteState.value = WorkoutPlanUiState(isProfileMissing = true)
                } else if (request != remoteState.value.request) {
                    fetchWorkoutPlan(request)
                }
            }
        }
    }

    fun retry() {
        remoteState.value.request?.let(::fetchWorkoutPlan)
    }

    fun confirmPlan() {
        val response = remoteState.value.response ?: return
        viewModelScope.launch {
            remoteState.update { it.copy(isSavingPlan = true, errorMessage = null, saveMessage = null) }
            runCatching { saveWorkoutSchedule(response.plan.weeklySchedule) }
                .onSuccess {
                    planSessionRepository.setLatestPlan(response)
                    remoteState.update {
                        it.copy(
                            isSavingPlan = false,
                            isPlanConfirmed = true,
                            saveMessage = "Workout plan saved to database.",
                        )
                    }
                }
                .onFailure { throwable ->
                    remoteState.update {
                        it.copy(
                            isSavingPlan = false,
                            errorMessage = throwable.message ?: "Unable to save workout plan.",
                        )
                    }
                }
        }
    }

    private fun fetchWorkoutPlan(request: WorkoutPlanRequest) {
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            remoteState.value = WorkoutPlanUiState(isLoading = true, request = request)
            runCatching { createWorkoutPlan(request) }
                .onSuccess { response ->
                    remoteState.value = WorkoutPlanUiState(request = request, response = response)
                }
                .onFailure { throwable ->
                    remoteState.value = WorkoutPlanUiState(
                        request = request,
                        errorMessage = throwable.message ?: "Unable to create workout plan.",
                    )
                }
        }
    }
}
