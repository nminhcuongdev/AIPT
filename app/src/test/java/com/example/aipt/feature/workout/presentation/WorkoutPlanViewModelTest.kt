package com.example.aipt.feature.workout.presentation

import com.example.aipt.feature.exercise.domain.model.Exercise
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import com.example.aipt.feature.exercise.domain.usecase.ObserveExercisesUseCase
import com.example.aipt.feature.exercise.domain.usecase.SeedExercisesUseCase
import com.example.aipt.feature.profile.domain.model.BodyMetricSnapshot
import com.example.aipt.feature.profile.domain.model.UserProfile
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import com.example.aipt.feature.profile.domain.usecase.ObserveEquipmentUseCase
import com.example.aipt.feature.profile.domain.usecase.ObserveProfileUseCase
import com.example.aipt.feature.workout.domain.WorkoutPlanGenerator
import com.example.aipt.feature.workout.domain.repository.WorkoutPlanSessionRepository
import com.example.aipt.feature.workout.domain.repository.WorkoutRepository
import com.example.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
import com.example.aipt.feature.workout.domain.usecase.BuildWorkoutPlanRequestUseCase
import com.example.aipt.feature.workout.domain.usecase.CreateWorkoutPlanUseCase
import com.example.aipt.feature.workout.domain.usecase.SaveWorkoutScheduleUseCase
import com.example.aipt.feature.workout.domain.usecase.SetLatestWorkoutPlanUseCase
import com.example.aipt.testutil.MainDispatcherRule
import com.example.aipt.testutil.testEquipment
import com.example.aipt.testutil.testUserProfile
import com.example.aipt.testutil.testWorkoutPlanRequest
import com.example.aipt.testutil.testWorkoutPlanResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutPlanViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `shows profile missing when no profile exists`() = runTest {
        val viewModel = createViewModel(profile = MutableStateFlow(null))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        assertTrue(viewModel.uiState.value.isProfileMissing)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `creates workout plan when profile request is available`() = runTest {
        val request = testWorkoutPlanRequest()
        val response = testWorkoutPlanResponse()
        val workoutRepository = mockWorkoutRepository(response = response)
        val generator = mockk<WorkoutPlanGenerator>()
        every { generator.buildRequest(any(), any()) } returns request
        val viewModel = createViewModel(
            buildWorkoutPlanRequest = BuildWorkoutPlanRequestUseCase(generator),
            workoutRepository = workoutRepository,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertSame(request, state.request)
        assertSame(response, state.response)
        assertTrue(state.canConfirmPlan)
        coVerify(exactly = 1) { workoutRepository.createWorkoutPlan(request) }
    }

    @Test
    fun `exposes create plan error and supports retry`() = runTest {
        val request = testWorkoutPlanRequest()
        val response = testWorkoutPlanResponse()
        val workoutRepository = mockk<WorkoutRepository>()
        coEvery { workoutRepository.createWorkoutPlan(request) } throws IllegalStateException("api down") andThen response
        val generator = mockk<WorkoutPlanGenerator>()
        every { generator.buildRequest(any(), any()) } returns request
        val viewModel = createViewModel(buildWorkoutPlanRequest = BuildWorkoutPlanRequestUseCase(generator), workoutRepository = workoutRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        assertEquals("api down", viewModel.uiState.value.errorMessage)

        viewModel.retry()
        runCurrent()

        assertSame(response, viewModel.uiState.value.response)
        coVerify(exactly = 2) { workoutRepository.createWorkoutPlan(request) }
    }

    @Test
    fun `confirm plan saves schedule and latest plan`() = runTest {
        val request = testWorkoutPlanRequest()
        val response = testWorkoutPlanResponse()
        val scheduleRepository = mockScheduleRepository()
        val sessionRepository = WorkoutPlanSessionRepository()
        val generator = mockk<WorkoutPlanGenerator>()
        every { generator.buildRequest(any(), any()) } returns request
        val viewModel = createViewModel(
            buildWorkoutPlanRequest = BuildWorkoutPlanRequestUseCase(generator),
            workoutRepository = mockWorkoutRepository(response),
            scheduleRepository = scheduleRepository,
            sessionRepository = sessionRepository,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        viewModel.confirmPlan()
        runCurrent()

        val state = viewModel.uiState.value
        assertTrue(state.isPlanConfirmed)
        assertTrue(state.canTrackProgress)
        assertEquals("Workout plan saved to database.", state.saveMessage)
        assertSame(response, sessionRepository.latestPlan.value)
        coVerify(exactly = 1) { scheduleRepository.saveWorkoutDays(response.plan.weeklySchedule) }
    }

    private fun createViewModel(
        profile: MutableStateFlow<UserProfile?> = MutableStateFlow(testUserProfile()),
        buildWorkoutPlanRequest: BuildWorkoutPlanRequestUseCase = BuildWorkoutPlanRequestUseCase(WorkoutPlanGenerator()),
        workoutRepository: WorkoutRepository = mockWorkoutRepository(testWorkoutPlanResponse()),
        scheduleRepository: WorkoutScheduleRepository = mockScheduleRepository(),
        sessionRepository: WorkoutPlanSessionRepository = WorkoutPlanSessionRepository(),
    ): WorkoutPlanViewModel {
        val profileRepository = mockk<ProfileRepository>()
        every { profileRepository.observeProfile() } returns profile
        every { profileRepository.observeEquipment() } returns MutableStateFlow(listOf(testEquipment()))
        every { profileRepository.observeBodyMetricSnapshots() } returns MutableStateFlow(emptyList<BodyMetricSnapshot>())
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

        return WorkoutPlanViewModel(
            observeProfile = ObserveProfileUseCase(profileRepository),
            observeEquipment = ObserveEquipmentUseCase(profileRepository),
            observeExercises = ObserveExercisesUseCase(exerciseRepository),
            seedExercises = SeedExercisesUseCase(exerciseRepository),
            buildWorkoutPlanRequest = buildWorkoutPlanRequest,
            createWorkoutPlan = CreateWorkoutPlanUseCase(workoutRepository),
            saveWorkoutSchedule = SaveWorkoutScheduleUseCase(scheduleRepository),
            setLatestWorkoutPlan = SetLatestWorkoutPlanUseCase(sessionRepository),
        )
    }

    private fun mockWorkoutRepository(response: com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse): WorkoutRepository {
        val repository = mockk<WorkoutRepository>()
        coEvery { repository.createWorkoutPlan(any()) } returns response
        coEvery { repository.analyzeWorkoutDayProgress(any()) } throws AssertionError("Not expected")
        return repository
    }

    private fun mockScheduleRepository(): WorkoutScheduleRepository {
        val repository = mockk<WorkoutScheduleRepository>()
        every { repository.observeWorkoutDays() } returns MutableStateFlow(emptyList())
        coEvery { repository.saveWorkoutDay(any()) } returns Unit
        coEvery { repository.saveWorkoutDays(any()) } returns Unit
        return repository
    }
}
