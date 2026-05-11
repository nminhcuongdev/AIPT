package nminhcuong.aipt.feature.workout.presentation

import nminhcuong.aipt.feature.dashboard.domain.model.WorkoutSessionState
import nminhcuong.aipt.feature.dashboard.domain.model.WorkoutSessionStatus
import nminhcuong.aipt.feature.dashboard.domain.repository.WorkoutSessionRepository
import nminhcuong.aipt.feature.dashboard.domain.usecase.SaveWorkoutSessionUseCase
import nminhcuong.aipt.feature.workout.domain.model.PlannedExercise
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import nminhcuong.aipt.feature.workout.domain.model.WorkoutProgressLog
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutProgressRepository
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
import nminhcuong.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import nminhcuong.aipt.feature.workout.domain.usecase.SaveWorkoutProgressLogsUseCase
import nminhcuong.aipt.feature.workout.domain.usecase.SelectWorkoutDayUseCase
import nminhcuong.aipt.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutSessionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `shows missing plan when schedule is empty`() = runTest {
        val scheduleRepository = mockScheduleRepository(MutableStateFlow(emptyList()))
        val sessionRepository = mockSessionRepository()
        val progressRepository = mockProgressRepository()

        val viewModel = createViewModel(scheduleRepository, sessionRepository, progressRepository)
        runCurrent()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isPlanMissing)
        coVerify(exactly = 0) { sessionRepository.saveSession(any()) }
    }

    @Test
    fun `loads workout and marks session in progress`() = runTest {
        val scheduleRepository = mockScheduleRepository(MutableStateFlow(listOf(workoutDay(day = 1))))
        val sessionRepository = mockSessionRepository()
        val progressRepository = mockProgressRepository()

        val viewModel = createViewModel(scheduleRepository, sessionRepository, progressRepository)
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.day)
        assertEquals("Workout 1", state.title)
        assertEquals("1/2", state.progressLabel)
        assertEquals("Squat", state.currentExercise!!.name)
        coVerify(exactly = 1) { sessionRepository.saveSession(match { it.day == 1 && it.status == WorkoutSessionStatus.InProgress }) }
    }

    @Test
    fun `sanitizes current exercise log and moves to next exercise`() = runTest {
        val viewModel = createViewModel(
            mockScheduleRepository(MutableStateFlow(listOf(workoutDay(day = 1)))),
            mockSessionRepository(),
            mockProgressRepository(),
        )
        runCurrent()

        viewModel.onWeightKgChanged("80.5.9kg")
        viewModel.onSetsChanged("12x")
        viewModel.onRepsChanged("9999")
        viewModel.onNoteChanged("a".repeat(220))
        runCurrent()

        val logged = viewModel.uiState.value.currentExercise!!
        assertEquals("80.59", logged.weightKg)
        assertEquals("12", logged.sets)
        assertEquals("999", logged.reps)
        assertEquals(180, logged.note.length)

        viewModel.nextExercise()
        runCurrent()

        assertEquals(1, viewModel.uiState.value.currentIndex)
        assertEquals("Bench Press", viewModel.uiState.value.currentExercise!!.name)
    }

    @Test
    fun `rest timer counts down and can stop`() = runTest {
        val viewModel = createViewModel(
            mockScheduleRepository(MutableStateFlow(listOf(workoutDay(day = 1)))),
            mockSessionRepository(),
            mockProgressRepository(),
        )
        runCurrent()

        viewModel.startRestTimer()
        runCurrent()
        assertTrue(viewModel.uiState.value.isResting)
        assertEquals(2, viewModel.uiState.value.restRemainingSeconds)

        advanceTimeBy(1_000)
        runCurrent()
        assertEquals(1, viewModel.uiState.value.restRemainingSeconds)

        viewModel.stopRestTimer()
        runCurrent()
        assertFalse(viewModel.uiState.value.isResting)
        assertEquals(0, viewModel.uiState.value.restRemainingSeconds)
    }

    @Test
    fun `finish workout saves logged progress and completed status`() = runTest {
        val sessionRepository = mockSessionRepository()
        val progressRepository = mockProgressRepository()
        val viewModel = createViewModel(
            mockScheduleRepository(MutableStateFlow(listOf(workoutDay(day = 1)))),
            sessionRepository,
            progressRepository,
        )
        runCurrent()

        viewModel.onWeightKgChanged("80")
        viewModel.onSetsChanged("3")
        viewModel.onRepsChanged("10")
        viewModel.onNoteChanged("Good form")
        viewModel.finishWorkout()
        runCurrent()

        coVerify(exactly = 1) {
            progressRepository.saveLogs(match<List<WorkoutProgressLog>> { logs ->
                logs.size == 1 &&
                    logs.first().exerciseName == "Squat" &&
                    logs.first().sets == 3 &&
                    logs.first().reps == 10 &&
                    logs.first().weightKg == 80.0 &&
                    logs.first().volumeKg == 2400.0 &&
                    logs.first().notes == "Good form"
            })
        }
        coVerify(exactly = 1) { sessionRepository.saveSession(match { it.day == 1 && it.status == WorkoutSessionStatus.Completed }) }
        assertTrue(viewModel.uiState.value.isFinished)
    }

    private fun createViewModel(
        scheduleRepository: WorkoutScheduleRepository,
        sessionRepository: WorkoutSessionRepository,
        progressRepository: WorkoutProgressRepository,
    ): WorkoutSessionViewModel = WorkoutSessionViewModel(
        observeWorkoutSchedule = ObserveWorkoutScheduleUseCase(scheduleRepository),
        saveWorkoutSession = SaveWorkoutSessionUseCase(sessionRepository),
        saveWorkoutProgressLogs = SaveWorkoutProgressLogsUseCase(progressRepository),
        selectWorkoutDay = SelectWorkoutDayUseCase(),
    )

    private fun mockScheduleRepository(days: MutableStateFlow<List<WorkoutDay>>): WorkoutScheduleRepository {
        val repository = mockk<WorkoutScheduleRepository>()
        every { repository.observeWorkoutDays() } returns days
        coEvery { repository.saveWorkoutDay(any()) } returns Unit
        coEvery { repository.saveWorkoutDays(any()) } returns Unit
        return repository
    }

    private fun mockSessionRepository(): WorkoutSessionRepository {
        val repository = mockk<WorkoutSessionRepository>()
        every { repository.observeSession(any()) } returns MutableStateFlow(null)
        every { repository.observeRecentSessions(any()) } returns MutableStateFlow(emptyList())
        coEvery { repository.saveSession(any()) } returns Unit
        return repository
    }

    private fun mockProgressRepository(): WorkoutProgressRepository {
        val repository = mockk<WorkoutProgressRepository>()
        every { repository.observeLogs() } returns MutableStateFlow(emptyList())
        coEvery { repository.saveLogs(any()) } returns Unit
        return repository
    }
}

private fun workoutDay(day: Int): WorkoutDay = WorkoutDay(
    day = day,
    title = "Workout $day",
    focus = "Strength",
    warmup = emptyList(),
    exercises = listOf(
        PlannedExercise(
            name = "Squat",
            sets = 4,
            reps = "8",
            durationMinutes = null,
            restSeconds = 2,
            intensity = "RPE 8",
            equipment = listOf("Barbell"),
            notes = "Stay braced",
        ),
        PlannedExercise(
            name = "Bench Press",
            sets = 3,
            reps = "10",
            durationMinutes = null,
            restSeconds = 90,
            intensity = "RPE 7",
            equipment = listOf("Barbell"),
            notes = "Pause reps",
        ),
    ),
    cooldown = emptyList(),
)
