package com.example.aipt.feature.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.workout.data.remote.WorkoutPlanApi
import com.example.aipt.feature.workout.domain.model.WorkoutProgressAnalysisRequest
import com.example.aipt.feature.workout.domain.model.WorkoutProgressAnalysisResponse
import com.example.aipt.feature.workout.domain.model.WorkoutProgressEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressTrackingUiState(
    val exerciseName: String = "",
    val weightKg: String = "",
    val sets: String = "",
    val reps: String = "",
    val notes: String = "",
    val entries: List<WorkoutProgressEntry> = emptyList(),
    val isLoading: Boolean = false,
    val response: WorkoutProgressAnalysisResponse? = null,
    val errorMessage: String? = null,
) {
    val canAdd: Boolean = exerciseName.isNotBlank() && (weightKg.isNotBlank() || sets.isNotBlank() || reps.isNotBlank())
    val canAnalyze: Boolean = entries.isNotEmpty() && !isLoading
}

@HiltViewModel
class ProgressTrackingViewModel @Inject constructor(
    private val workoutPlanApi: WorkoutPlanApi,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProgressTrackingUiState())
    val uiState: StateFlow<ProgressTrackingUiState> = _uiState.asStateFlow()
    private var analyzeJob: Job? = null

    fun onExerciseNameChanged(value: String) = update { copy(exerciseName = value.take(80), errorMessage = null) }
    fun onWeightKgChanged(value: String) = update { copy(weightKg = value.onlyDecimal().take(6), errorMessage = null) }
    fun onSetsChanged(value: String) = update { copy(sets = value.onlyDigits().take(2), errorMessage = null) }
    fun onRepsChanged(value: String) = update { copy(reps = value.onlyDigits().take(3), errorMessage = null) }
    fun onNotesChanged(value: String) = update { copy(notes = value.take(180), errorMessage = null) }

    fun addEntry() {
        val state = _uiState.value
        if (!state.canAdd) return
        val entry = WorkoutProgressEntry(
            exerciseName = state.exerciseName.trim(),
            weightKg = state.weightKg.toDoubleOrNull(),
            sets = state.sets.toIntOrNull(),
            reps = state.reps.toIntOrNull(),
            notes = state.notes.trim().ifBlank { null },
        )
        _uiState.update {
            it.copy(
                exerciseName = "",
                weightKg = "",
                sets = "",
                reps = "",
                notes = "",
                entries = it.entries + entry,
                response = null,
                errorMessage = null,
            )
        }
    }

    fun removeEntry(index: Int) {
        _uiState.update { state ->
            state.copy(entries = state.entries.filterIndexed { entryIndex, _ -> entryIndex != index })
        }
    }

    fun analyzeProgress() {
        val entries = _uiState.value.entries
        if (entries.isEmpty()) return
        analyzeJob?.cancel()
        analyzeJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, response = null, errorMessage = null) }
            val request = WorkoutProgressAnalysisRequest(
                performedAt = System.currentTimeMillis(),
                entries = entries,
            )
            runCatching { workoutPlanApi.analyzeWorkoutProgress(request) }
                .onSuccess { response ->
                    _uiState.update { it.copy(isLoading = false, response = response) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to analyze workout progress.",
                        )
                    }
                }
        }
    }

    private fun update(block: ProgressTrackingUiState.() -> ProgressTrackingUiState) {
        _uiState.update { it.block() }
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