package com.example.aipt.feature.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.profile.domain.model.BodyMetricSnapshot
import com.example.aipt.feature.workout.domain.WorkoutPlanGenerator
import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisRequest
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import com.example.aipt.feature.workout.domain.model.WorkoutPlan
import com.example.aipt.feature.workout.domain.model.WorkoutProgressEntry
import com.example.aipt.feature.workout.domain.model.WorkoutProgressLog
import com.example.aipt.feature.exercise.domain.usecase.SeedExercisesUseCase
import com.example.aipt.feature.profile.domain.usecase.ObserveBodyMetricSnapshotsUseCase
import com.example.aipt.feature.profile.domain.usecase.ObserveEquipmentUseCase
import com.example.aipt.feature.profile.domain.usecase.ObserveProfileUseCase
import com.example.aipt.feature.workout.domain.usecase.AnalyzeWorkoutDayProgressUseCase
import com.example.aipt.feature.workout.domain.usecase.CreateWorkoutPlanUseCase
import com.example.aipt.feature.workout.domain.usecase.ObserveLatestWorkoutPlanUseCase
import com.example.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import com.example.aipt.feature.workout.domain.usecase.SaveWorkoutDayUseCase
import com.example.aipt.feature.workout.domain.usecase.ObserveWorkoutProgressLogsUseCase
import com.example.aipt.feature.workout.domain.usecase.SaveWorkoutProgressLogsUseCase
import com.example.aipt.feature.workout.domain.usecase.SetLatestWorkoutPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProgressExerciseLogUiState(
    val id: String,
    val day: Int,
    val dayTitle: String,
    val exerciseName: String,
    val plannedPrescription: String,
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

data class ChartPointUiState(
    val label: String,
    val value: Double,
)

data class BodyMetricChartPointUiState(
    val label: String,
    val weightKg: Double?,
    val bodyFatPercent: Double?,
    val skeletalMuscleMassKg: Double?,
)

data class ProgressChartsUiState(
    val selectedExerciseName: String? = null,
    val exerciseOptions: List<String> = emptyList(),
    val exerciseWeightPoints: List<ChartPointUiState> = emptyList(),
    val weeklyVolumePoints: List<ChartPointUiState> = emptyList(),
    val bodyMetricPoints: List<BodyMetricChartPointUiState> = emptyList(),
)

data class ProgressDayStatusUiState(
    val isLoading: Boolean = false,
    val response: WorkoutDayProgressAnalysisResponse? = null,
    val errorMessage: String? = null,
    val isConfirmed: Boolean = false,
)

data class ProgressTrackingUiState(
    val schedule: List<ProgressExerciseLogUiState> = emptyList(),
    val dayStatuses: Map<Int, ProgressDayStatusUiState> = emptyMap(),
    val charts: ProgressChartsUiState = ProgressChartsUiState(),
    val isPlanMissing: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    fun canAnalyzeDay(day: Int): Boolean = schedule.any { it.day == day && it.hasLog } && dayStatuses[day]?.isLoading != true
    fun canConfirmDay(day: Int): Boolean = dayStatuses[day]?.response?.nextWeekDay != null && dayStatuses[day]?.isConfirmed != true
}

@HiltViewModel
class ProgressTrackingViewModel @Inject constructor(
    private val observeProfile: ObserveProfileUseCase,
    private val observeEquipment: ObserveEquipmentUseCase,
    private val observeBodyMetricSnapshots: ObserveBodyMetricSnapshotsUseCase,
    private val seedExercises: SeedExercisesUseCase,
    private val generator: WorkoutPlanGenerator,
    private val createWorkoutPlan: CreateWorkoutPlanUseCase,
    private val analyzeWorkoutDayProgress: AnalyzeWorkoutDayProgressUseCase,
    private val observeWorkoutSchedule: ObserveWorkoutScheduleUseCase,
    private val saveWorkoutDay: SaveWorkoutDayUseCase,
    private val observeWorkoutProgressLogs: ObserveWorkoutProgressLogsUseCase,
    private val saveWorkoutProgressLogs: SaveWorkoutProgressLogsUseCase,
    private val observeLatestWorkoutPlan: ObserveLatestWorkoutPlanUseCase,
    private val setLatestWorkoutPlan: SetLatestWorkoutPlanUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProgressTrackingUiState(isLoading = true))
    val uiState: StateFlow<ProgressTrackingUiState> = _uiState.asStateFlow()
    private val dayJobs = mutableMapOf<Int, Job>()
    private var planJob: Job? = null
    private var latestProgressLogs: List<WorkoutProgressLog> = emptyList()
    private var latestBodyMetrics: List<BodyMetricSnapshot> = emptyList()

    init {
        viewModelScope.launch {
            observeLatestWorkoutPlan().collect { latestPlan ->
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
        viewModelScope.launch {
            combine(
                observeWorkoutProgressLogs(),
                observeBodyMetricSnapshots(),
            ) { logs, bodyMetrics -> logs to bodyMetrics }
                .collect { (logs, bodyMetrics) ->
                    latestProgressLogs = logs
                    latestBodyMetrics = bodyMetrics
                    refreshCharts()
                }
        }
    }

    fun onWeightKgChanged(id: String, value: String) = updateLog(id) {
        copy(weightKg = value.onlyDecimal().take(6))
    }

    fun onSetsChanged(id: String, value: String) = updateLog(id) {
        copy(sets = value.onlyDigits().take(3))
    }

    fun onRepsChanged(id: String, value: String) = updateLog(id) {
        copy(reps = value.onlyDigits().take(3))
    }

    fun onNoteChanged(id: String, value: String) = updateLog(id) {
        copy(note = value.take(180))
    }

    fun onExerciseChartSelected(exerciseName: String) {
        _uiState.update { state -> state.copy(charts = state.charts.copy(selectedExerciseName = exerciseName)) }
        refreshCharts()
    }

    fun analyzeDay(day: Int) {
        val state = _uiState.value
        val dayLogs = state.schedule.filter { it.day == day }
        val loggedEntries = dayLogs.filter { it.hasLog }
        if (loggedEntries.isEmpty()) return
        dayJobs[day]?.cancel()
        dayJobs[day] = viewModelScope.launch {
            val performedAt = System.currentTimeMillis()
            updateDayStatus(day) { ProgressDayStatusUiState(isLoading = true) }
            saveWorkoutProgressLogs(loggedEntries.map { it.toProgressLog(performedAt) })
            val request = WorkoutDayProgressAnalysisRequest(
                performedAt = performedAt,
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
            seedExercises()
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
            val profile = observeProfile().first()
            if (profile == null) {
                _uiState.update { it.copy(isLoading = false, isPlanMissing = true) }
                return@launch
            }
            val equipment = observeEquipment().first()
            val request = generator.buildRequest(profile, equipment)
            runCatching { createWorkoutPlan(request) }
                .onSuccess { response ->
                    setLatestWorkoutPlan.invoke(response)
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

    private fun refreshCharts() {
        _uiState.update { state ->
            state.copy(charts = buildCharts(latestProgressLogs, latestBodyMetrics, state.charts.selectedExerciseName))
        }
    }

    private fun buildCharts(
        logs: List<WorkoutProgressLog>,
        bodyMetrics: List<BodyMetricSnapshot>,
        currentSelection: String?,
    ): ProgressChartsUiState {
        val exerciseOptions = logs.map { it.exerciseName }.distinct().sorted()
        val selectedExercise = currentSelection?.takeIf { it in exerciseOptions } ?: exerciseOptions.firstOrNull()
        val exerciseWeightPoints = selectedExercise?.let { exercise ->
            logs.filter { it.exerciseName == exercise && it.weightKg != null }
                .groupBy { it.dateKey }
                .toSortedMap()
                .map { (dateKey, dateLogs) ->
                    ChartPointUiState(
                        label = dateKey.shortDateLabel(),
                        value = dateLogs.maxOf { it.weightKg ?: 0.0 },
                    )
                }
                .takeLast(12)
        }.orEmpty()
        val weeklyVolumePoints = logs
            .groupBy { it.weekStartDate }
            .toSortedMap()
            .map { (weekStart, weekLogs) ->
                ChartPointUiState(
                    label = weekStart.shortDateLabel(),
                    value = weekLogs.sumOf { it.volumeKg },
                )
            }
            .takeLast(12)
        val bodyMetricPoints = bodyMetrics.map { snapshot ->
            BodyMetricChartPointUiState(
                label = snapshot.dateKey.shortDateLabel(),
                weightKg = snapshot.weightKg,
                bodyFatPercent = snapshot.bodyFatPercent,
                skeletalMuscleMassKg = snapshot.skeletalMuscleMassKg,
            )
        }.takeLast(12)
        return ProgressChartsUiState(
            selectedExerciseName = selectedExercise,
            exerciseOptions = exerciseOptions,
            exerciseWeightPoints = exerciseWeightPoints,
            weeklyVolumePoints = weeklyVolumePoints,
            bodyMetricPoints = bodyMetricPoints,
        )
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
                plannedSets = exercise.sets,
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
            sets = sets.toIntOrNull() ?: plannedSets,
            reps = reps.toIntOrNull(),
            notes = contextNote,
        )
    }

    private fun ProgressExerciseLogUiState.toProgressLog(performedAt: Long): WorkoutProgressLog {
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
            dateKey = dateKey(performedAt),
            weekStartDate = weekStartDateKey(performedAt),
            day = day,
            dayTitle = dayTitle,
            exerciseName = exerciseName,
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

private fun dateKey(timeMillis: Long): String = DateKeyFormat.format(Date(timeMillis))

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
    return DateKeyFormat.format(Date(calendar.timeInMillis))
}

private fun String.shortDateLabel(): String = if (length >= 10) substring(5, 10).replace('-', '/') else this

private val DateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
