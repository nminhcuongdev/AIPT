package com.example.aipt.feature.workout.presentation

import com.example.aipt.feature.exercise.domain.model.Exercise
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import com.example.aipt.feature.exercise.domain.usecase.SeedExercisesUseCase
import com.example.aipt.feature.profile.domain.model.BodyMetricSnapshot
import com.example.aipt.feature.profile.domain.model.UserProfile
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import com.example.aipt.feature.profile.domain.usecase.ObserveBodyMetricSnapshotsUseCase
import com.example.aipt.feature.profile.domain.usecase.ObserveEquipmentUseCase
import com.example.aipt.feature.profile.domain.usecase.ObserveProfileUseCase
import com.example.aipt.feature.workout.domain.WorkoutPlanGenerator
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import com.example.aipt.feature.workout.domain.model.WorkoutProgressLog
import com.example.aipt.feature.workout.domain.repository.WorkoutPlanSessionRepository
import com.example.aipt.feature.workout.domain.repository.WorkoutProgressRepository
import com.example.aipt.feature.workout.domain.repository.WorkoutRepository
import com.example.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
import com.example.aipt.feature.workout.domain.usecase.AnalyzeWorkoutDayProgressUseCase
import com.example.aipt.feature.workout.domain.usecase.CreateWorkoutPlanUseCase
import com.example.aipt.feature.workout.domain.usecase.ObserveLatestWorkoutPlanUseCase
import com.example.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import com.example.aipt.feature.workout.domain.usecase.SaveWorkoutDayUseCase
import com.example.aipt.feature.workout.domain.usecase.ObserveWorkoutProgressLogsUseCase
import com.example.aipt.feature.workout.domain.usecase.SaveWorkoutProgressLogsUseCase
import com.example.aipt.feature.workout.domain.usecase.SetLatestWorkoutPlanUseCase
import com.example.aipt.testutil.MainDispatcherRule
import com.example.aipt.testutil.testEquipment
import com.example.aipt.testutil.testUserProfile
import com.example.aipt.testutil.testWorkoutDay
import com.example.aipt.testutil.testWorkoutPlanResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressTrackingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads latest plan into schedule`() = runTest {
        val sessionRepository = WorkoutPlanSessionRepository()
        sessionRepository.setLatestPlan(testWorkoutPlanResponse(testWorkoutDay(day = 1)))
        val viewModel = createViewModel(sessionRepository = sessionRepository)
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isPlanMissing)
        assertEquals(listOf("Bench Press"), state.schedule.map { it.exerciseName })
    }

    @Test
    fun `builds charts from progress logs and body metrics`() = runTest {
        val progressLogs = MutableStateFlow(
            listOf(
                progressLog(exerciseName = "Bench Press", dateKey = "2026-05-01", weekStartDate = "2026-04-27", weightKg = 70.0, volumeKg = 2800.0),
                progressLog(exerciseName = "Bench Press", dateKey = "2026-05-02", weekStartDate = "2026-04-27", weightKg = 72.5, volumeKg = 2900.0),
                progressLog(exerciseName = "Squat", dateKey = "2026-05-03", weekStartDate = "2026-04-27", weightKg = 100.0, volumeKg = 3000.0),
            ),
        )
        val bodyMetrics = MutableStateFlow(
            listOf(BodyMetricSnapshot(dateKey = "2026-05-01", capturedAt = 1L, weightKg = 70.0, bodyFatPercent = 18.0, skeletalMuscleMassKg = 30.0)),
        )
        val viewModel = createViewModel(progressLogs = progressLogs, bodyMetrics = bodyMetrics)
        runCurrent()

        val charts = viewModel.uiState.value.charts
        assertEquals(listOf("Bench Press", "Squat"), charts.exerciseOptions)
        assertEquals("Bench Press", charts.selectedExerciseName)
        assertEquals(listOf(70.0, 72.5), charts.exerciseWeightPoints.map { it.value })
        assertEquals(listOf(8700.0), charts.weeklyVolumePoints.map { it.value })
        assertEquals(70.0, charts.bodyMetricPoints.single().weightKg ?: 0.0, 0.0)

        viewModel.onExerciseChartSelected("Squat")
        runCurrent()

        assertEquals("Squat", viewModel.uiState.value.charts.selectedExerciseName)
        assertEquals(listOf(100.0), viewModel.uiState.value.charts.exerciseWeightPoints.map { it.value })
    }

    @Test
    fun `updates logs with sanitized values and clears stale day status`() = runTest {
        val viewModel = createViewModel(sessionRepository = WorkoutPlanSessionRepository().also { it.setLatestPlan(testWorkoutPlanResponse(testWorkoutDay(day = 1))) })
        runCurrent()
        val id = viewModel.uiState.value.schedule.first().id

        viewModel.onWeightKgChanged(id, "72.5.9kg")
        viewModel.onSetsChanged(id, "1234")
        viewModel.onRepsChanged(id, "9999")
        viewModel.onNoteChanged(id, "n".repeat(220))
        runCurrent()

        val log = viewModel.uiState.value.schedule.first()
        assertEquals("72.59", log.weightKg)
        assertEquals("123", log.sets)
        assertEquals("999", log.reps)
        assertEquals(180, log.note.length)
        assertTrue(viewModel.uiState.value.canAnalyzeDay(1))
    }

    @Test
    fun `analyze day saves progress logs and stores response`() = runTest {
        val response = WorkoutDayProgressAnalysisResponse(
            analysisSummary = "Good work",
            advice = "Add load slowly",
            recommendations = listOf("Sleep more"),
            nextSteps = listOf("Repeat"),
            safetyNotes = emptyList(),
            nextWeekDay = testWorkoutDay(day = 1, title = "Updated Push Day"),
            model = "test-model",
        )
        val workoutRepository = mockWorkoutRepository(analysisResponse = response)
        val progressRepository = mockProgressRepository(MutableStateFlow(emptyList()))
        val viewModel = createViewModel(
            sessionRepository = WorkoutPlanSessionRepository().also { it.setLatestPlan(testWorkoutPlanResponse(testWorkoutDay(day = 1))) },
            workoutRepository = workoutRepository,
            progressRepository = progressRepository,
        )
        runCurrent()
        val id = viewModel.uiState.value.schedule.first().id
        viewModel.onWeightKgChanged(id, "80")
        viewModel.onSetsChanged(id, "4")
        viewModel.onRepsChanged(id, "8")
        runCurrent()

        viewModel.analyzeDay(1)
        runCurrent()

        coVerify(exactly = 1) { progressRepository.saveLogs(match<List<WorkoutProgressLog>> { it.single().volumeKg == 2560.0 }) }
        coVerify(exactly = 1) { workoutRepository.analyzeWorkoutDayProgress(match { it.day == 1 && it.entries.single().weightKg == 80.0 }) }
        assertEquals("Good work", viewModel.uiState.value.dayStatuses[1]!!.response!!.analysisSummary)
        assertTrue(viewModel.uiState.value.canConfirmDay(1))
    }

    @Test
    fun `confirm suggested day saves updated day and marks status confirmed`() = runTest {
        val scheduleRepository = mockScheduleRepository(MutableStateFlow(emptyList()))
        val response = WorkoutDayProgressAnalysisResponse(
            analysisSummary = "Good work",
            advice = null,
            recommendations = null,
            nextSteps = null,
            safetyNotes = null,
            nextWeekDay = testWorkoutDay(day = 1, title = "Updated Push Day"),
            model = null,
        )
        val viewModel = createViewModel(
            sessionRepository = WorkoutPlanSessionRepository().also { it.setLatestPlan(testWorkoutPlanResponse(testWorkoutDay(day = 1))) },
            workoutRepository = mockWorkoutRepository(analysisResponse = response),
            scheduleRepository = scheduleRepository,
        )
        runCurrent()
        val id = viewModel.uiState.value.schedule.first().id
        viewModel.onWeightKgChanged(id, "80")
        viewModel.analyzeDay(1)
        runCurrent()

        viewModel.confirmSuggestedDay(1)
        runCurrent()

        coVerify(exactly = 1) { scheduleRepository.saveWorkoutDay(match { it.title == "Updated Push Day" }) }
        assertTrue(viewModel.uiState.value.dayStatuses[1]!!.isConfirmed)
        assertEquals("Updated Push Day", viewModel.uiState.value.schedule.first().dayTitle)
    }

    private fun createViewModel(
        sessionRepository: WorkoutPlanSessionRepository = WorkoutPlanSessionRepository(),
        progressLogs: MutableStateFlow<List<WorkoutProgressLog>> = MutableStateFlow(emptyList()),
        bodyMetrics: MutableStateFlow<List<BodyMetricSnapshot>> = MutableStateFlow(emptyList()),
        workoutRepository: WorkoutRepository = mockWorkoutRepository(),
        scheduleRepository: WorkoutScheduleRepository = mockScheduleRepository(MutableStateFlow(emptyList())),
        progressRepository: WorkoutProgressRepository = mockProgressRepository(progressLogs),
    ): ProgressTrackingViewModel {
        val profileRepository = mockk<ProfileRepository>()
        every { profileRepository.observeProfile() } returns MutableStateFlow<UserProfile?>(testUserProfile())
        every { profileRepository.observeEquipment() } returns MutableStateFlow(listOf(testEquipment()))
        every { profileRepository.observeBodyMetricSnapshots() } returns bodyMetrics
        coEvery { profileRepository.seedEquipmentIfNeeded() } returns Unit
        coEvery { profileRepository.saveProfile(any()) } returns Unit
        coEvery { profileRepository.deleteProfile() } returns Unit
        coEvery { profileRepository.setEquipmentStatus(any(), any()) } returns Unit
        coEvery { profileRepository.resetEquipmentChoices() } returns Unit

        val exerciseRepository = mockk<ExerciseRepository>()
        every { exerciseRepository.observeExercises() } returns MutableStateFlow(emptyList<Exercise>())
        every { exerciseRepository.observeExerciseById(any()) } returns MutableStateFlow(null)
        coEvery { exerciseRepository.seedIfNeeded() } returns Unit
        coEvery { exerciseRepository.setFavorite(any(), any()) } returns Unit
        coEvery { exerciseRepository.markViewed(any()) } returns Unit

        return ProgressTrackingViewModel(
            observeProfile = ObserveProfileUseCase(profileRepository),
            observeEquipment = ObserveEquipmentUseCase(profileRepository),
            observeBodyMetricSnapshots = ObserveBodyMetricSnapshotsUseCase(profileRepository),
            seedExercises = SeedExercisesUseCase(exerciseRepository),
            generator = WorkoutPlanGenerator(),
            createWorkoutPlan = CreateWorkoutPlanUseCase(workoutRepository),
            analyzeWorkoutDayProgress = AnalyzeWorkoutDayProgressUseCase(workoutRepository),
            observeWorkoutSchedule = ObserveWorkoutScheduleUseCase(scheduleRepository),
            saveWorkoutDay = SaveWorkoutDayUseCase(scheduleRepository),
            observeWorkoutProgressLogs = ObserveWorkoutProgressLogsUseCase(progressRepository),
            saveWorkoutProgressLogs = SaveWorkoutProgressLogsUseCase(progressRepository),
            observeLatestWorkoutPlan = ObserveLatestWorkoutPlanUseCase(sessionRepository),
            setLatestWorkoutPlan = SetLatestWorkoutPlanUseCase(sessionRepository),
        )
    }
}

private fun mockWorkoutRepository(
    analysisResponse: WorkoutDayProgressAnalysisResponse = WorkoutDayProgressAnalysisResponse(
        analysisSummary = "OK",
        advice = null,
        recommendations = null,
        nextSteps = null,
        safetyNotes = null,
        nextWeekDay = null,
        model = null,
    ),
): WorkoutRepository {
    val repository = mockk<WorkoutRepository>()
    coEvery { repository.createWorkoutPlan(any()) } returns testWorkoutPlanResponse()
    coEvery { repository.analyzeWorkoutDayProgress(any()) } returns analysisResponse
    return repository
}

private fun mockScheduleRepository(days: MutableStateFlow<List<com.example.aipt.feature.workout.domain.model.WorkoutDay>>): WorkoutScheduleRepository {
    val repository = mockk<WorkoutScheduleRepository>()
    every { repository.observeWorkoutDays() } returns days
    coEvery { repository.saveWorkoutDay(any()) } coAnswers {
        val day = firstArg<com.example.aipt.feature.workout.domain.model.WorkoutDay>()
        days.value = days.value.filterNot { it.day == day.day } + day
    }
    coEvery { repository.saveWorkoutDays(any()) } coAnswers { days.value = firstArg() }
    return repository
}

private fun mockProgressRepository(logs: MutableStateFlow<List<WorkoutProgressLog>>): WorkoutProgressRepository {
    val repository = mockk<WorkoutProgressRepository>()
    every { repository.observeLogs() } returns logs
    coEvery { repository.saveLogs(any()) } coAnswers { logs.value = logs.value + firstArg<List<WorkoutProgressLog>>() }
    return repository
}

private fun progressLog(
    exerciseName: String,
    dateKey: String,
    weekStartDate: String,
    weightKg: Double,
    volumeKg: Double,
): WorkoutProgressLog = WorkoutProgressLog(
    performedAt = 1L,
    dateKey = dateKey,
    weekStartDate = weekStartDate,
    day = 1,
    dayTitle = "Push Day",
    exerciseName = exerciseName,
    sets = 4,
    reps = 8,
    weightKg = weightKg,
    volumeKg = volumeKg,
    notes = "",
)
