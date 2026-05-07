package com.example.aipt.feature.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionState
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionStatus
import com.example.aipt.feature.dashboard.domain.usecase.SaveWorkoutSessionUseCase
import com.example.aipt.feature.workout.domain.model.PlannedExercise
import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.example.aipt.feature.workout.domain.model.WorkoutProgressLog
import com.example.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import com.example.aipt.feature.workout.domain.usecase.SaveWorkoutProgressLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkoutSessionExerciseUiState(
    val id: String,
    val name: String,
    val prescription: String,
    val restSeconds: Int,
    val equipment: List<String>,
    val plannedNotes: String,
    val plannedSets: Int?,
    val sets: String = plannedSets?.toString().orEmpty(),
    val weightKg: String = "",
    val reps: String = "",
    val note: String = "",
) {
    val hasLog: Boolean = weightKg.isNotBlank() || reps.isNotBlank() || note.isNotBlank()
}

data class WorkoutSessionUiState(
    val isLoading: Boolean = true,
    val isPlanMissing: Boolean = false,
    val day: Int? = null,
    val title: String = "Workout session",
    val focus: String = "",
    val exercises: List<WorkoutSessionExerciseUiState> = emptyList(),
    val currentIndex: Int = 0,
    val restRemainingSeconds: Int = 0,
    val isResting: Boolean = false,
    val isFinished: Boolean = false,
    val errorMessage: String? = null,
) {
    val currentExercise: WorkoutSessionExerciseUiState? = exercises.getOrNull(currentIndex)
    val progressLabel: String = if (exercises.isEmpty()) "0/0" else "${currentIndex + 1}/${exercises.size}"
    val canGoNext: Boolean = currentIndex < exercises.lastIndex
    val canFinish: Boolean = exercises.isNotEmpty() && !isFinished
}

@HiltViewModel
class WorkoutSessionViewModel @Inject constructor(
    private val observeWorkoutSchedule: ObserveWorkoutScheduleUseCase,
    private val saveWorkoutSession: SaveWorkoutSessionUseCase,
    private val saveWorkoutProgressLogs: SaveWorkoutProgressLogsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutSessionUiState())
    val uiState: StateFlow<WorkoutSessionUiState> = _uiState.asStateFlow()
    private var restJob: Job? = null

    init {
        viewModelScope.launch {
            val days = observeWorkoutSchedule().first()
            if (days.isEmpty()) {
                _uiState.value = WorkoutSessionUiState(isLoading = false, isPlanMissing = true)
                return@launch
            }
            val todayPlan = selectTodayPlan(days)
            markStatus(todayPlan.day, WorkoutSessionStatus.InProgress)
            _uiState.value = WorkoutSessionUiState(
                isLoading = false,
                day = todayPlan.day,
                title = todayPlan.title,
                focus = todayPlan.focus,
                exercises = todayPlan.exercises.mapIndexed { index, exercise -> exercise.toUiState(todayPlan.day, index) },
            )
        }
    }

    fun onWeightKgChanged(value: String) = updateCurrentExercise { copy(weightKg = value.onlyDecimal().take(6)) }

    fun onSetsChanged(value: String) = updateCurrentExercise { copy(sets = value.onlyDigits().take(3)) }

    fun onRepsChanged(value: String) = updateCurrentExercise { copy(reps = value.onlyDigits().take(3)) }

    fun onNoteChanged(value: String) = updateCurrentExercise { copy(note = value.take(180)) }

    fun startRestTimer() {
        val seconds = _uiState.value.currentExercise?.restSeconds?.takeIf { it > 0 } ?: 60
        restJob?.cancel()
        _uiState.update { it.copy(restRemainingSeconds = seconds, isResting = true) }
        restJob = viewModelScope.launch {
            while (_uiState.value.restRemainingSeconds > 0) {
                delay(1_000)
                _uiState.update { state -> state.copy(restRemainingSeconds = (state.restRemainingSeconds - 1).coerceAtLeast(0)) }
            }
            _uiState.update { it.copy(isResting = false) }
        }
    }

    fun stopRestTimer() {
        restJob?.cancel()
        _uiState.update { it.copy(restRemainingSeconds = 0, isResting = false) }
    }

    fun nextExercise() {
        restJob?.cancel()
        _uiState.update { state ->
            if (!state.canGoNext) state else state.copy(
                currentIndex = state.currentIndex + 1,
                restRemainingSeconds = 0,
                isResting = false,
            )
        }
    }

    fun finishWorkout() {
        val state = _uiState.value
        val day = state.day ?: return
        restJob?.cancel()
        viewModelScope.launch {
            val performedAt = System.currentTimeMillis()
            saveWorkoutProgressLogs(
                state.exercises.filter { it.hasLog }.map { exercise ->
                    exercise.toProgressLog(
                        performedAt = performedAt,
                        day = day,
                        dayTitle = state.title,
                    )
                },
            )
            markStatus(day, WorkoutSessionStatus.Completed)
            _uiState.update { it.copy(isFinished = true, isResting = false, restRemainingSeconds = 0) }
        }
    }

    override fun onCleared() {
        restJob?.cancel()
        super.onCleared()
    }

    private suspend fun markStatus(day: Int, status: WorkoutSessionStatus) {
        saveWorkoutSession(
            WorkoutSessionState(
                date = todayDateKey(),
                day = day,
                status = status,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    private fun updateCurrentExercise(block: WorkoutSessionExerciseUiState.() -> WorkoutSessionExerciseUiState) {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises.mapIndexed { index, exercise ->
                    if (index == state.currentIndex) exercise.block() else exercise
                },
            )
        }
    }

    private fun selectTodayPlan(days: List<WorkoutDay>): WorkoutDay {
        val sortedDays = days.sortedBy { it.day }
        val mondayBasedDay = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> 7
        }
        val maxDay = sortedDays.maxOfOrNull { it.day } ?: 1
        val targetDay = ((mondayBasedDay - 1) % maxDay) + 1
        return sortedDays.firstOrNull { it.day == targetDay } ?: sortedDays.first()
    }

    private fun PlannedExercise.toUiState(day: Int, index: Int): WorkoutSessionExerciseUiState = WorkoutSessionExerciseUiState(
        id = "$day-$index-$name",
        name = name,
        prescription = buildList {
            sets?.let { add("$it sets") }
            reps?.let { add("$it reps") }
            durationMinutes?.let { add("$it min") }
            restSeconds?.let { add("${it}s rest") }
            add(intensity)
        }.joinToString(" - "),
        restSeconds = restSeconds ?: 60,
        equipment = equipment,
        plannedNotes = notes,
        plannedSets = sets,
    )

    private fun WorkoutSessionExerciseUiState.toProgressLog(
        performedAt: Long,
        day: Int,
        dayTitle: String,
    ): WorkoutProgressLog {
        val actualSets = sets.toIntOrNull() ?: plannedSets
        val actualReps = reps.toIntOrNull()
        val actualWeight = weightKg.toDoubleOrNull()
        val volume = if (actualSets != null && actualReps != null && actualWeight != null) {
            actualSets * actualReps * actualWeight
        } else {
            0.0
        }
        return WorkoutProgressLog(
            performedAt = performedAt,
            dateKey = todayDateKey(performedAt),
            weekStartDate = weekStartDateKey(performedAt),
            day = day,
            dayTitle = dayTitle,
            exerciseName = name,
            sets = actualSets,
            reps = actualReps,
            weightKg = actualWeight,
            volumeKg = volume,
            notes = note,
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

private fun todayDateKey(timeMillis: Long = System.currentTimeMillis()): String = WorkoutSessionDateKeyFormat.format(Date(timeMillis))

private fun weekStartDateKey(timeMillis: Long): String {
    val calendar = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        timeInMillis = timeMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        val dayOffset = (get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
        add(Calendar.DAY_OF_MONTH, -dayOffset)
    }
    return WorkoutSessionDateKeyFormat.format(Date(calendar.timeInMillis))
}

private val WorkoutSessionDateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
