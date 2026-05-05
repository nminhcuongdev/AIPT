package com.example.aipt.feature.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import com.example.aipt.feature.workout.domain.WorkoutPlanGenerator
import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisRequest
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import com.example.aipt.feature.workout.domain.model.WorkoutPlan
import com.example.aipt.feature.workout.domain.model.WorkoutProgressEntry
import com.example.aipt.feature.workout.domain.repository.WorkoutPlanSessionRepository
import com.example.aipt.feature.workout.domain.usecase.AnalyzeWorkoutDayProgressUseCase
import com.example.aipt.feature.workout.domain.usecase.CreateWorkoutPlanUseCase
import com.example.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import com.example.aipt.feature.workout.domain.usecase.SaveWorkoutDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressExerciseLogUiState(
    val id: String,
    val day: Int,
    val dayTitle: String,
    val exerciseName: String,
    val plannedPrescription: String,
    val equipment: List<String>,
    val plannedNotes: String,
    val weightKg: String = "",
    val reps: String = "",
    val note: String = "",
) {
    val hasLog: Boolean = weightKg.isNotBlank() || reps.isNotBlank() || note.isNotBlank()
}

data class ProgressDayStatusUiState(
    val isLoading: Boolean = false,
    val response: WorkoutDayProgressAnalysisResponse? = null,
    val errorMessage: String? = null,
    val isConfirmed: Boolean = false,
)

data class ProgressTrackingUiState(
    val schedule: List<ProgressExerciseLogUiState> = emptyList(),
    val dayStatuses: Map<Int, ProgressDayStatusUiState> = emptyMap(),
    val isPlanMissing: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    fun canAnalyzeDay(day: Int): Boolean = schedule.any { it.day == day && it.hasLog } && dayStatuses[day]?.isLoading != true
    fun canConfirmDay(day: Int): Boolean = dayStatuses[day]?.response?.nextWeekDay != null && dayStatuses[day]?.isConfirmed != true
}

@HiltViewModel
class ProgressTrackingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val exerciseRepository: ExerciseRepository,
    private val generator: WorkoutPlanGenerator,
    private val createWorkoutPlan: CreateWorkoutPlanUseCase,
    private val analyzeWorkoutDayProgress: AnalyzeWorkoutDayProgressUseCase,
    private val observeWorkoutSchedule: ObserveWorkoutScheduleUseCase,
    private val saveWorkoutDay: SaveWorkoutDayUseCase,
    private val planSessionRepository: WorkoutPlanSessionRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProgressTrackingUiState(isLoading = true))
    val uiState: StateFlow<ProgressTrackingUiState> = _uiState.asStateFlow()
    private val dayJobs = mutableMapOf<Int, Job>()
    private var planJob: Job? = null

    init {
        viewModelScope.launch {
            planSessionRepository.latestPlan.collect { latestPlan ->
                if (latestPlan == null) {
                    ensurePlanLoaded()
                } else {
                    _uiState.update { state ->
                        if (state.schedule.isEmpty() || state.isPlanMissing) {
                            state.copy(
                                schedule = latestPlan.plan.toProgressSchedule(),
                                isPlanMissing = false,
                                isLoading = false,
                                errorMessage = null,
                            )
                        } else {
                            state.copy(isPlanMissing = false, isLoading = false)
                        }
                    }
                }
            }
        }
    }

    fun onWeightKgChanged(id: String, value: String) = updateLog(id) {
        copy(weightKg = value.onlyDecimal().take(6))
    }

    fun onRepsChanged(id: String, value: String) = updateLog(id) {
        copy(reps = value.onlyDigits().take(3))
    }

    fun onNoteChanged(id: String, value: String) = updateLog(id) {
        copy(note = value.take(180))
    }

    fun analyzeDay(day: Int) {
        val state = _uiState.value
        val dayLogs = state.schedule.filter { it.day == day }
        val loggedEntries = dayLogs.filter { it.hasLog }
        if (loggedEntries.isEmpty()) return
        dayJobs[day]?.cancel()
        dayJobs[day] = viewModelScope.launch {
            updateDayStatus(day) { ProgressDayStatusUiState(isLoading = true) }
            val request = WorkoutDayProgressAnalysisRequest(
                performedAt = System.currentTimeMillis(),
                day = day,
                dayTitle = dayLogs.firstOrNull()?.dayTitle.orEmpty(),
                entries = loggedEntries.map { it.toProgressEntry() },
            )
            runCatching { analyzeWorkoutDayProgress(request) }
                .onSuccess { response ->
                    updateDayStatus(day) { ProgressDayStatusUiState(response = response) }
                }
                .onFailure { throwable ->
                    updateDayStatus(day) {
                        ProgressDayStatusUiState(errorMessage = throwable.message ?: "Unable to analyze day $day.")
                    }
                }
        }
    }

    fun confirmSuggestedDay(day: Int) {
        val suggestedDay = _uiState.value.dayStatuses[day]?.response?.nextWeekDay ?: return
        viewModelScope.launch {
            saveWorkoutDay(suggestedDay)
            _uiState.update { current ->
                current.copy(
                    schedule = (current.schedule.filterNot { it.day == day } + suggestedDay.toProgressSchedule())
                        .sortedWith(compareBy<ProgressExerciseLogUiState> { it.day }.thenBy { it.id }),
                    dayStatuses = current.dayStatuses + (day to (current.dayStatuses[day]?.copy(isConfirmed = true) ?: ProgressDayStatusUiState(isConfirmed = true))),
                )
            }
        }
    }

    private fun ensurePlanLoaded() {
        if (planJob?.isActive == true) return
        planJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isPlanMissing = false, errorMessage = null) }
            exerciseRepository.seedIfNeeded()
            val savedSchedule = observeWorkoutSchedule().first()
            if (savedSchedule.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        schedule = savedSchedule.flatMap { day -> day.toProgressSchedule() },
                        isPlanMissing = false,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
                return@launch
            }
            val profile = profileRepository.observeProfile().first()
            if (profile == null) {
                _uiState.update { it.copy(isLoading = false, isPlanMissing = true) }
                return@launch
            }
            val equipment = profileRepository.observeEquipment().first()
            val request = generator.buildRequest(profile, equipment)
            runCatching { createWorkoutPlan(request) }
                .onSuccess { response ->
                    planSessionRepository.setLatestPlan(response)
                    _uiState.update {
                        it.copy(
                            schedule = response.plan.toProgressSchedule(),
                            isPlanMissing = false,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPlanMissing = true,
                            errorMessage = throwable.message ?: "Unable to create workout plan.",
                        )
                    }
                }
        }
    }

    private fun updateLog(id: String, block: ProgressExerciseLogUiState.() -> ProgressExerciseLogUiState) {
        _uiState.update { state ->
            val changedDay = state.schedule.firstOrNull { it.id == id }?.day
            state.copy(
                schedule = state.schedule.map { item -> if (item.id == id) item.block() else item },
                dayStatuses = if (changedDay == null) state.dayStatuses else state.dayStatuses - changedDay,
                errorMessage = null,
            )
        }
    }

    private fun updateDayStatus(day: Int, block: () -> ProgressDayStatusUiState) {
        _uiState.update { state -> state.copy(dayStatuses = state.dayStatuses + (day to block())) }
    }

    private fun WorkoutPlan.toProgressSchedule(): List<ProgressExerciseLogUiState> = weeklySchedule.flatMap { it.toProgressSchedule() }

    private fun WorkoutDay.toProgressSchedule(): List<ProgressExerciseLogUiState> =
        exercises.mapIndexed { index, exercise ->
            ProgressExerciseLogUiState(
                id = "$day-$index-${exercise.name}",
                day = day,
                dayTitle = title,
                exerciseName = exercise.name,
                plannedPrescription = buildList {
                    exercise.sets?.let { add("$it sets") }
                    exercise.reps?.let { add("$it reps") }
                    exercise.restSeconds?.let { add("${it}s rest") }
                    add(exercise.intensity)
                }.joinToString(" - "),
                equipment = exercise.equipment,
                plannedNotes = exercise.notes,
            )
        }

    private fun ProgressExerciseLogUiState.toProgressEntry(): WorkoutProgressEntry {
        val contextNote = buildList {
            add("Day $day - $dayTitle")
            if (plannedPrescription.isNotBlank()) add("Planned: $plannedPrescription")
            if (plannedNotes.isNotBlank()) add("Plan note: $plannedNotes")
            if (note.isNotBlank()) add("User note: $note")
        }.joinToString(" | ")
        return WorkoutProgressEntry(
            exerciseName = exerciseName,
            weightKg = weightKg.toDoubleOrNull(),
            sets = null,
            reps = reps.toIntOrNull(),
            notes = contextNote,
        )
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