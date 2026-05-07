package com.example.aipt.core.navigation

import com.example.aipt.feature.profile.domain.model.BodyMetricSnapshot
import com.example.aipt.feature.profile.domain.model.EquipmentStatus
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import com.example.aipt.testutil.MainDispatcherRule
import com.example.aipt.testutil.testEquipment
import com.example.aipt.testutil.testUserProfile
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StartupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `routes to profile flow when profile is missing`() = runTest {
        val profile = MutableStateFlow<com.example.aipt.feature.profile.domain.model.UserProfile?>(null)
        val viewModel = StartupViewModel(mockProfileRepository(profile))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.destination.collect {} }
        runCurrent()

        assertEquals(StartupDestination.ProfileFlow, viewModel.destination.value)
    }

    @Test
    fun `routes to dashboard when profile exists`() = runTest {
        val profile = MutableStateFlow<com.example.aipt.feature.profile.domain.model.UserProfile?>(testUserProfile())
        val viewModel = StartupViewModel(mockProfileRepository(profile))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.destination.collect {} }
        runCurrent()

        assertEquals(StartupDestination.TodayDashboard, viewModel.destination.value)
    }

    @Test
    fun `updates destination when profile changes`() = runTest {
        val profile = MutableStateFlow<com.example.aipt.feature.profile.domain.model.UserProfile?>(null)
        val viewModel = StartupViewModel(mockProfileRepository(profile))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.destination.collect {} }
        runCurrent()
        assertEquals(StartupDestination.ProfileFlow, viewModel.destination.value)

        profile.value = testUserProfile()
        runCurrent()

        assertEquals(StartupDestination.TodayDashboard, viewModel.destination.value)
    }

    private fun mockProfileRepository(profile: MutableStateFlow<com.example.aipt.feature.profile.domain.model.UserProfile?>): ProfileRepository {
        val repository = mockk<ProfileRepository>()
        every { repository.observeProfile() } returns profile
        every { repository.observeEquipment() } returns MutableStateFlow(listOf(testEquipment(status = EquipmentStatus.Available)))
        every { repository.observeBodyMetricSnapshots() } returns MutableStateFlow(emptyList<BodyMetricSnapshot>())
        coEvery { repository.seedEquipmentIfNeeded() } returns Unit
        coEvery { repository.saveProfile(any()) } returns Unit
        coEvery { repository.deleteProfile() } returns Unit
        coEvery { repository.setEquipmentStatus(any(), any()) } returns Unit
        coEvery { repository.resetEquipmentChoices() } returns Unit
        return repository
    }
}
