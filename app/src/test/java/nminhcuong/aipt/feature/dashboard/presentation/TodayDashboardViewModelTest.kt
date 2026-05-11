package nminhcuong.aipt.feature.dashboard.presentation

import nminhcuong.aipt.feature.dashboard.domain.model.WorkoutSessionState
import nminhcuong.aipt.feature.dashboard.domain.model.WorkoutSessionStatus
import nminhcuong.aipt.feature.dashboard.domain.repository.WorkoutSessionRepository
import nminhcuong.aipt.feature.dashboard.domain.usecase.ObserveWorkoutSessionUseCase
import nminhcuong.aipt.feature.dashboard.domain.usecase.SaveWorkoutSessionUseCase
import nminhcuong.aipt.feature.workout.domain.model.PlannedExercise
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
import nminhcuong.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import nminhcuong.aipt.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodayDashboardViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `shows missing plan state when schedule is empty`() = runTest {
        val days = MutableStateFlow(emptyList<WorkoutDay>())
        val session = MutableStateFlow<WorkoutSessionState?>(null)
        val scheduleRepository = mockWorkoutScheduleRepository(days)
        val sessionRepository = mockWorkoutSessionRepository(session)

        val viewModel = createViewModel(scheduleRepository, sessionRepository)
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isPlanMissing)
        assertEquals(null, state.workoutDay)
        assertFalse(state.canStart)
    }

    @Test
    fun `loads today's workout from schedule`() = runTest {
        val days = MutableStateFlow(listOf(workoutDay(day = 1)))
        val session = MutableStateFlow<WorkoutSessionState?>(null)
        val scheduleRepository = mockWorkoutScheduleRepository(days)
        val sessionRepository = mockWorkoutSessionRepository(session)

        val viewModel = createViewModel(scheduleRepository, sessionRepository)
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isPlanMissing)
        assertEquals(1, state.workoutDay)
        assertEquals("Upper Strength", state.title)
        assertEquals(listOf("Bench Press"), state.exercises.map { it.name })
        assertTrue(state.canStart)
    }

    @Test
    fun `start and complete workout persist session status`() = runTest {
        val days = MutableStateFlow(listOf(workoutDay(day = 1)))
        val session = MutableStateFlow<WorkoutSessionState?>(null)
        val scheduleRepository = mockWorkoutScheduleRepository(days)
        val sessionRepository = mockWorkoutSessionRepository(session)
        val viewModel = createViewModel(scheduleRepository, sessionRepository)
        runCurrent()

        viewModel.startWorkout()
        runCurrent()

        coVerify(exactly = 1) { sessionRepository.saveSession(match { it.status == WorkoutSessionStatus.InProgress && it.day == 1 }) }
        assertEquals(WorkoutSessionStatus.InProgress, viewModel.uiState.value.status)
        assertTrue(viewModel.uiState.value.canComplete)

        viewModel.completeWorkout()
        runCurrent()

        coVerify(exactly = 1) { sessionRepository.saveSession(match { it.status == WorkoutSessionStatus.Completed && it.day == 1 }) }
        assertEquals(WorkoutSessionStatus.Completed, viewModel.uiState.value.status)
    }

    private fun createViewModel(
        scheduleRepository: WorkoutScheduleRepository,
        sessionRepository: WorkoutSessionRepository,
    ): TodayDashboardViewModel = TodayDashboardViewModel(
        observeWorkoutSchedule = ObserveWorkoutScheduleUseCase(scheduleRepository),
        observeWorkoutSession = ObserveWorkoutSessionUseCase(sessionRepository),
        saveWorkoutSession = SaveWorkoutSessionUseCase(sessionRepository),
    )

    private fun mockWorkoutScheduleRepository(days: MutableStateFlow<List<WorkoutDay>>): WorkoutScheduleRepository {
        val repository = mockk<WorkoutScheduleRepository>()
        every { repository.observeWorkoutDays() } returns days
        coEvery { repository.saveWorkoutDay(any()) } coAnswers {
            val day = firstArg<WorkoutDay>()
            days.value = days.value.filterNot { it.day == day.day } + day
        }
        coEvery { repository.saveWorkoutDays(any()) } coAnswers {
            days.value = firstArg()
        }
        return repository
    }

    private fun mockWorkoutSessionRepository(session: MutableStateFlow<WorkoutSessionState?>): WorkoutSessionRepository {
        val repository = mockk<WorkoutSessionRepository>()
        every { repository.observeSession(any()) } returns session
        every { repository.observeRecentSessions(any()) } returns MutableStateFlow(emptyList())
        coEvery { repository.saveSession(any()) } coAnswers {
            session.value = firstArg()
        }
        return repository
    }
}

private fun workoutDay(day: Int): WorkoutDay = WorkoutDay(
    day = day,
    title = "Upper Strength",
    focus = "Build pressing strength",
    warmup = listOf("Band pull-aparts"),
    exercises = listOf(
        PlannedExercise(
            name = "Bench Press",
            sets = 4,
            reps = "8",
            durationMinutes = null,
            restSeconds = 90,
            intensity = "RPE 8",
            equipment = listOf("Barbell"),
            notes = "Control the eccentric",
        ),
    ),
    cooldown = listOf("Chest stretch"),
)
