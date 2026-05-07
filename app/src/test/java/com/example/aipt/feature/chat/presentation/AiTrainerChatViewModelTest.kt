package com.example.aipt.feature.chat.presentation

import com.example.aipt.feature.chat.domain.model.AiTrainerChatRequest
import com.example.aipt.feature.chat.domain.model.AiTrainerChatResponse
import com.example.aipt.feature.chat.domain.model.AiTrainerSuggestedAction
import com.example.aipt.feature.chat.domain.repository.AiTrainerChatRepository
import com.example.aipt.feature.chat.domain.usecase.SendAiTrainerChatUseCase
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionState
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionStatus
import com.example.aipt.feature.dashboard.domain.repository.WorkoutSessionRepository
import com.example.aipt.feature.dashboard.domain.usecase.ObserveRecentWorkoutSessionsUseCase
import com.example.aipt.feature.profile.domain.model.BodyMetricSnapshot
import com.example.aipt.feature.profile.domain.model.EquipmentStatus
import com.example.aipt.feature.profile.domain.model.GymEquipment
import com.example.aipt.feature.profile.domain.model.UserProfile
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import com.example.aipt.feature.workout.domain.model.PlannedExercise
import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.example.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
import com.example.aipt.feature.workout.domain.usecase.ObserveWorkoutScheduleUseCase
import com.example.aipt.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
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
class AiTrainerChatViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `starts with assistant greeting and limits input length`() = runTest {
        val viewModel = createViewModel()

        assertEquals(1, viewModel.uiState.value.messages.size)
        assertEquals("assistant", viewModel.uiState.value.messages.first().role)

        viewModel.onInputChanged("x".repeat(700))

        assertEquals(600, viewModel.uiState.value.input.length)
        assertTrue(viewModel.uiState.value.canSend)
    }

    @Test
    fun `send message builds context and appends assistant response`() = runTest {
        val requestSlot = slot<AiTrainerChatRequest>()
        val chatRepository = mockk<AiTrainerChatRepository>()
        coEvery { chatRepository.chat(capture(requestSlot)) } returns AiTrainerChatResponse(
            reply = "Keep two reps in reserve.",
            recommendations = listOf("Add warmup sets"),
            safetyNotes = listOf("Stop if pain increases"),
            suggestedActions = listOf(AiTrainerSuggestedAction(type = "plan", label = "Adjust load", details = "Reduce 5%")),
            needsMedicalAttention = false,
            planAdjustment = null,
            model = "test-model",
        )
        val viewModel = createViewModel(chatRepository = chatRepository)

        viewModel.onInputChanged("How should I adjust today?")
        viewModel.sendMessage()
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isSending)
        assertEquals("", state.input)
        assertEquals(listOf("assistant", "user", "assistant"), state.messages.map { it.role })
        assertEquals("Keep two reps in reserve.", state.messages.last().content)
        assertEquals(listOf("Add warmup sets"), state.messages.last().recommendations)
        assertEquals(listOf("Stop if pain increases"), state.messages.last().safetyNotes)
        assertEquals(listOf("Adjust load: Reduce 5%"), state.messages.last().suggestedActions)
        assertEquals("test-model", state.messages.last().model)

        val request = requestSlot.captured
        assertEquals("How should I adjust today?", request.message)
        assertEquals("Minh", request.context.profile!!.name)
        assertEquals(listOf("Dumbbell"), request.context.equipment.map { it.name })
        assertEquals(1, request.context.currentPlan!!.weeklySchedule.size)
        assertEquals(1, request.context.recentSessionStates.size)
        assertTrue(request.context.dataNotes.isNotEmpty())
        coVerify(exactly = 1) { chatRepository.chat(any()) }
    }

    @Test
    fun `send failure keeps user message and exposes error`() = runTest {
        val chatRepository = mockk<AiTrainerChatRepository>()
        coEvery { chatRepository.chat(any()) } throws IllegalStateException("network down")
        val viewModel = createViewModel(chatRepository = chatRepository)

        viewModel.onInputChanged("Help")
        viewModel.sendMessage()
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isSending)
        assertEquals("network down", state.errorMessage)
        assertEquals(listOf("assistant", "user"), state.messages.map { it.role })
    }

    private fun createViewModel(
        chatRepository: AiTrainerChatRepository = mockk<AiTrainerChatRepository>().also {
            coEvery { it.chat(any()) } returns AiTrainerChatResponse(
                reply = "OK",
                recommendations = null,
                safetyNotes = null,
                suggestedActions = null,
                needsMedicalAttention = false,
                planAdjustment = null,
                model = null,
            )
        },
    ): AiTrainerChatViewModel {
        val profileRepository = mockk<ProfileRepository>()
        every { profileRepository.observeProfile() } returns MutableStateFlow(profile())
        every { profileRepository.observeEquipment() } returns MutableStateFlow(listOf(equipment()))
        every { profileRepository.observeBodyMetricSnapshots() } returns MutableStateFlow(emptyList<BodyMetricSnapshot>())
        coEvery { profileRepository.seedEquipmentIfNeeded() } returns Unit
        coEvery { profileRepository.saveProfile(any()) } returns Unit
        coEvery { profileRepository.deleteProfile() } returns Unit
        coEvery { profileRepository.setEquipmentStatus(any(), any()) } returns Unit
        coEvery { profileRepository.resetEquipmentChoices() } returns Unit

        val scheduleRepository = mockk<WorkoutScheduleRepository>()
        every { scheduleRepository.observeWorkoutDays() } returns MutableStateFlow(listOf(workoutDay()))
        coEvery { scheduleRepository.saveWorkoutDay(any()) } returns Unit
        coEvery { scheduleRepository.saveWorkoutDays(any()) } returns Unit

        val sessionRepository = mockk<WorkoutSessionRepository>()
        every { sessionRepository.observeSession(any()) } returns MutableStateFlow(null)
        every { sessionRepository.observeRecentSessions(10) } returns MutableStateFlow(
            listOf(WorkoutSessionState(date = "2026-05-07", day = 1, status = WorkoutSessionStatus.Completed, updatedAt = 123L)),
        )
        coEvery { sessionRepository.saveSession(any()) } returns Unit

        return AiTrainerChatViewModel(
            profileRepository = profileRepository,
            observeWorkoutSchedule = ObserveWorkoutScheduleUseCase(scheduleRepository),
            observeRecentWorkoutSessions = ObserveRecentWorkoutSessionsUseCase(sessionRepository),
            sendAiTrainerChat = SendAiTrainerChatUseCase(chatRepository),
        )
    }
}

private fun profile(): UserProfile = UserProfile(
    name = "Minh",
    age = 30,
    heightCm = 170,
    weightKg = 70,
    bodyFatPercent = 18.0,
    skeletalMuscleMassKg = 30.0,
    bodyWaterLiters = 39.0,
    visceralFatLevel = 4,
    basalMetabolicRateKcal = 1600,
    waistHipRatio = 0.8,
    leftArmMuscleKg = 2.7,
    rightArmMuscleKg = 2.8,
    trunkMuscleKg = 23.0,
    leftLegMuscleKg = 8.3,
    rightLegMuscleKg = 8.4,
    trainingGoal = "build_muscle",
    daysPerWeek = 5,
    sessionDurationMinutes = 60,
    experienceLevel = "intermediate",
    injuriesOrLimitations = "None",
    preferredLanguage = "en",
)

private fun equipment(): GymEquipment = GymEquipment(
    id = 1,
    name = "Dumbbell",
    imageUrl = "https://example.com/dumbbell.png",
    status = EquipmentStatus.Available,
)

private fun workoutDay(): WorkoutDay = WorkoutDay(
    day = 1,
    title = "Push Day",
    focus = "Chest and triceps",
    warmup = emptyList(),
    exercises = listOf(
        PlannedExercise(
            name = "Bench Press",
            sets = 4,
            reps = "8",
            durationMinutes = null,
            restSeconds = 90,
            intensity = "RPE 8",
            equipment = listOf("Barbell"),
            notes = "Pause reps",
        ),
    ),
    cooldown = emptyList(),
)
