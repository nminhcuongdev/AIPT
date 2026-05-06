package com.example.aipt.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionState
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionStatus
import com.example.aipt.feature.dashboard.domain.usecase.ObserveWorkoutSessionUseCase
import com.example.aipt.feature.dashboard.domain.usecase.SaveWorkoutSessionUseCase
import com.example.aipt.feature.workout.domain.model.PlannedExercise
import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.example.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodayExerciseUiState(
    val name: String,
    val sets: String,
    val reps: String,
    val targetWeight: String,
    val equipment: List<String>,
    val notes: String,
)

data class TodayDashboardUiState(
    val isLoading: Boolean = true,
    val date: String = todayDateKey(),
    val dayLabel: String = "Today",
    val workoutDay: Int? = null,
    val title: String = "No workout scheduled",
    val focus: String = "Confirm a workout plan first to build today's dashboard.",
    val exercises: List<TodayExerciseUiState> = emptyList(),
    val status: WorkoutSessionStatus = WorkoutSessionStatus.NotStarted,
    val isPlanMissing: Boolean = false,
) {
    val statusLabel: String = when (status) {
        WorkoutSessionStatus.NotStarted -> "Not started"
        WorkoutSessionStatus.InProgress -> "In progress"
        WorkoutSessionStatus.Completed -> "Completed"
    }

    val canStart: Boolean = !isPlanMissing && status == WorkoutSessionStatus.NotStarted
    val canResume: Boolean = !isPlanMissing && status == WorkoutSessionStatus.InProgress
    val canComplete: Boolean = !isPlanMissing && status == WorkoutSessionStatus.InProgress
}

@HiltViewModel
class TodayDashboardViewModel @Inject constructor(
    private val observeWorkoutSchedule: ObserveWorkoutScheduleUseCase,
    private val observeWorkoutSession: ObserveWorkoutSessionUseCase,
    private val saveWorkoutSession: SaveWorkoutSessionUseCase,
) : ViewModel() {
    private val dateKey = todayDateKey()
    private val _uiState = MutableStateFlow(TodayDashboardUiState(date = dateKey))
    val uiState: StateFlow<TodayDashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeWorkoutSchedule()
                .combine(observeWorkoutSession(dateKey)) { days, session -> days to session }
                .collect { (days, session) ->
                    _uiState.value = buildState(days, session)
                }
        }
    }

    fun startWorkout() {
        saveStatus(WorkoutSessionStatus.InProgress)
    }

    fun completeWorkout() {
        saveStatus(WorkoutSessionStatus.Completed)
    }

    private fun saveStatus(status: WorkoutSessionStatus) {
        val day = _uiState.value.workoutDay ?: return
        viewModelScope.launch {
            saveWorkoutSession(
                WorkoutSessionState(
                    date = dateKey,
                    day = day,
                    status = status,
                    updatedAt = System.currentTimeMillis(),
                ),
            )
        }
    }

    private fun buildState(days: List<WorkoutDay>, session: WorkoutSessionState?): TodayDashboardUiState {
        if (days.isEmpty()) {
            return TodayDashboardUiState(
                isLoading = false,
                date = dateKey,
                dayLabel = todayDisplayLabel(),
                isPlanMissing = true,
            )
        }
        val sortedDays = days.sortedBy { it.day }
        val todayPlan = sortedDays.firstOrNull { it.day == workoutDayNumber(sortedDays) } ?: sortedDays.first()
        return TodayDashboardUiState(
            isLoading = false,
            date = dateKey,
            dayLabel = todayDisplayLabel(),
            workoutDay = todayPlan.day,
            title = todayPlan.title,
            focus = todayPlan.focus,
            exercises = todayPlan.exercises.map { it.toUiState() },
            status = session?.status ?: WorkoutSessionStatus.NotStarted,
            isPlanMissing = false,
        )
    }

    private fun workoutDayNumber(days: List<WorkoutDay>): Int {
        val calendar = Calendar.getInstance()
        val mondayBasedDay = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> 7
        }
        val maxDay = days.maxOfOrNull { it.day } ?: 1
        return ((mondayBasedDay - 1) % maxDay) + 1
    }

    private fun PlannedExercise.toUiState(): TodayExerciseUiState = TodayExerciseUiState(
        name = name,
        sets = sets?.toString() ?: "-",
        reps = reps ?: durationMinutes?.let { "$it min" } ?: "-",
        targetWeight = "Not set",
        equipment = equipment,
        notes = notes,
    )
}

private fun todayDateKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(System.currentTimeMillis())

private fun todayDisplayLabel(): String = SimpleDateFormat("EEE, MMM d", Locale.US).format(System.currentTimeMillis())

