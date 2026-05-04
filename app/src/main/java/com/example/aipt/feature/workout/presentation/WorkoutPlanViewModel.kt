package com.example.aipt.feature.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import com.example.aipt.feature.workout.data.remote.WorkoutPlanApi
import com.example.aipt.feature.workout.domain.WorkoutPlanGenerator
import com.example.aipt.feature.workout.domain.model.WorkoutPlanRequest
import com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutPlanUiState(
    val isLoading: Boolean = false,
    val isProfileMissing: Boolean = false,
    val request: WorkoutPlanRequest? = null,
    val response: WorkoutPlanResponse? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class WorkoutPlanViewModel @Inject constructor(
    profileRepository: ProfileRepository,
    exerciseRepository: ExerciseRepository,
    private val workoutPlanApi: WorkoutPlanApi,
    private val generator: WorkoutPlanGenerator,
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
                if (profile == null) {
                    null
                } else {
                    generator.buildRequest(profile, equipment)
                }
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

    private fun fetchWorkoutPlan(request: WorkoutPlanRequest) {
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            remoteState.value = WorkoutPlanUiState(
                isLoading = true,
                request = request,
            )
            runCatching { workoutPlanApi.createWorkoutPlan(request) }
                .onSuccess { response ->
                    remoteState.value = WorkoutPlanUiState(
                        request = request,
                        response = response,
                    )
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
