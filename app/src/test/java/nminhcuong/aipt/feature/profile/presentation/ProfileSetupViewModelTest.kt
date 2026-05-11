package nminhcuong.aipt.feature.profile.presentation

import nminhcuong.aipt.feature.profile.domain.model.BodyMetricSnapshot
import nminhcuong.aipt.feature.profile.domain.model.EquipmentStatus
import nminhcuong.aipt.feature.profile.domain.model.GymEquipment
import nminhcuong.aipt.feature.profile.domain.model.UserProfile
import nminhcuong.aipt.feature.profile.domain.repository.ProfileRepository
import nminhcuong.aipt.feature.profile.domain.usecase.DeleteProfileUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.ObserveEquipmentUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.ObserveProfileUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.ResetEquipmentChoicesUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.SaveProfileUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.SeedEquipmentUseCase
import nminhcuong.aipt.feature.profile.domain.usecase.SetEquipmentStatusUseCase
import nminhcuong.aipt.testutil.MainDispatcherRule
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileSetupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads saved profile into editable form`() = runTest {
        val profile = MutableStateFlow<UserProfile?>(savedProfile())
        val equipment = MutableStateFlow(listOf(equipment(1, EquipmentStatus.Available), equipment(2, EquipmentStatus.Unknown)))
        val repository = mockProfileRepository(profile, equipment)
        val viewModel = profileSetupViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        val state = viewModel.uiState.value
        assertTrue(state.hasProfile)
        assertTrue(state.saved)
        assertEquals("Minh", state.name)
        assertEquals("29", state.age)
        assertEquals("Build muscle", state.selectedGoal)
        assertEquals(1, state.availableCount)
        assertEquals(1, state.remainingCount)
        coVerify(exactly = 1) { repository.seedEquipmentIfNeeded() }
    }

    @Test
    fun `sanitizes profile input and marks state unsaved`() = runTest {
        val repository = mockProfileRepository(MutableStateFlow(null), MutableStateFlow(emptyList()))
        val viewModel = profileSetupViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        viewModel.onNameChanged("  Cuong  ")
        viewModel.onAgeChanged("29abc0")
        viewModel.onHeightChanged("1759")
        viewModel.onBodyFatPercentChanged("18.9.5kg")
        viewModel.onDaysPerWeekChanged("9")
        viewModel.onInjuriesChanged("x".repeat(220))
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals("  Cuong  ", state.name)
        assertEquals("290", state.age)
        assertEquals("175", state.heightCm)
        assertEquals("18.95", state.bodyFatPercent)
        assertEquals("9", state.daysPerWeek)
        assertEquals(180, state.injuriesOrLimitations.length)
        assertFalse(state.saved)
    }

    @Test
    fun `save profile trims name maps goal and coerces preferences`() = runTest {
        val profile = MutableStateFlow<UserProfile?>(null)
        val repository = mockProfileRepository(profile, MutableStateFlow(emptyList()))
        val viewModel = profileSetupViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        viewModel.onNameChanged("  Cuong  ")
        viewModel.onAgeChanged("32")
        viewModel.onWeightChanged("70")
        viewModel.onGoalSelected("Gain strength")
        viewModel.onDaysPerWeekChanged("9")
        viewModel.onSessionDurationChanged("999")
        viewModel.onSaveProfile()
        runCurrent()

        coVerify(exactly = 1) {
            repository.saveProfile(match {
                it.name == "Cuong" &&
                    it.age == 32 &&
                    it.weightKg == 70 &&
                    it.trainingGoal == "gain_strength" &&
                    it.daysPerWeek == 7 &&
                    it.sessionDurationMinutes == 180
            })
        }
        assertTrue(viewModel.uiState.value.hasProfile)
        assertTrue(viewModel.uiState.value.saved)
    }

    @Test
    fun `equipment actions and delete profile call repository`() = runTest {
        val profile = MutableStateFlow<UserProfile?>(savedProfile())
        val repository = mockProfileRepository(profile, MutableStateFlow(listOf(equipment(3, EquipmentStatus.Unknown))))
        val viewModel = profileSetupViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        viewModel.onEquipmentSwiped(viewModel.uiState.value.currentEquipment!!, available = false)
        viewModel.onResetEquipment()
        viewModel.onDeleteProfile()
        runCurrent()

        coVerify(exactly = 1) { repository.setEquipmentStatus(3, EquipmentStatus.Unavailable) }
        coVerify(exactly = 2) { repository.resetEquipmentChoices() }
        coVerify(exactly = 1) { repository.deleteProfile() }
        assertFalse(viewModel.uiState.value.hasProfile)
        assertFalse(viewModel.uiState.value.saved)
    }


    private fun profileSetupViewModel(repository: ProfileRepository): ProfileSetupViewModel = ProfileSetupViewModel(
        observeProfile = ObserveProfileUseCase(repository),
        observeEquipment = ObserveEquipmentUseCase(repository),
        seedEquipment = SeedEquipmentUseCase(repository),
        saveProfile = SaveProfileUseCase(repository),
        deleteProfile = DeleteProfileUseCase(repository),
        resetEquipmentChoices = ResetEquipmentChoicesUseCase(repository),
        setEquipmentStatus = SetEquipmentStatusUseCase(repository),
    )
    private fun mockProfileRepository(
        profile: MutableStateFlow<UserProfile?>,
        equipment: MutableStateFlow<List<GymEquipment>>,
    ): ProfileRepository {
        val repository = mockk<ProfileRepository>()
        every { repository.observeProfile() } returns profile
        every { repository.observeEquipment() } returns equipment
        every { repository.observeBodyMetricSnapshots() } returns MutableStateFlow(emptyList<BodyMetricSnapshot>())
        coEvery { repository.seedEquipmentIfNeeded() } returns Unit
        coEvery { repository.saveProfile(any()) } coAnswers { profile.value = firstArg() }
        coEvery { repository.deleteProfile() } coAnswers { profile.value = null }
        coEvery { repository.resetEquipmentChoices() } returns Unit
        coEvery { repository.setEquipmentStatus(any(), any()) } coAnswers {
            val id = firstArg<Int>()
            val status = secondArg<EquipmentStatus>()
            equipment.value = equipment.value.map { item -> if (item.id == id) item.copy(status = status) else item }
        }
        return repository
    }
}

private fun savedProfile(): UserProfile = UserProfile(
    name = "Minh",
    age = 29,
    heightCm = 172,
    weightKg = 68,
    bodyFatPercent = 18.5,
    skeletalMuscleMassKg = 29.0,
    bodyWaterLiters = 38.0,
    visceralFatLevel = 4,
    basalMetabolicRateKcal = 1500,
    waistHipRatio = 0.8,
    leftArmMuscleKg = 2.6,
    rightArmMuscleKg = 2.7,
    trunkMuscleKg = 22.0,
    leftLegMuscleKg = 8.1,
    rightLegMuscleKg = 8.2,
    trainingGoal = "build_muscle",
    daysPerWeek = 5,
    sessionDurationMinutes = 60,
    experienceLevel = "intermediate",
    injuriesOrLimitations = "None",
    preferredLanguage = "en",
)

private fun equipment(id: Int, status: EquipmentStatus): GymEquipment = GymEquipment(
    id = id,
    name = "Equipment $id",
    imageUrl = "https://example.com/$id.png",
    status = status,
)
