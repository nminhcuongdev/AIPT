package com.example.aipt.feature.exercise.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.example.aipt.feature.exercise.domain.model.Exercise
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import com.example.aipt.feature.exercise.domain.usecase.ObserveExerciseDetailUseCase
import com.example.aipt.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `marks exercise viewed after detail is available`() = runTest {
        val exercises = MutableStateFlow(listOf(exercise(id = 7, name = "Romanian Deadlift")))
        val repository = mockExerciseRepository(exercises)

        ExerciseDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("exerciseId" to 7)),
            observeExerciseDetail = ObserveExerciseDetailUseCase(repository),
            repository = repository,
        )
        runCurrent()

        coVerify(exactly = 1) { repository.markViewed(7) }
    }

    @Test
    fun `favorite click toggles exercise favorite state`() = runTest {
        val exercises = MutableStateFlow(listOf(exercise(id = 7, name = "Romanian Deadlift", isFavorite = false)))
        val repository = mockExerciseRepository(exercises)
        val viewModel = ExerciseDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("exerciseId" to 7)),
            observeExerciseDetail = ObserveExerciseDetailUseCase(repository),
            repository = repository,
        )
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.exercise.collect {} }
        runCurrent()

        viewModel.onFavoriteClicked(viewModel.exercise.value!!)
        runCurrent()

        coVerify(exactly = 1) { repository.setFavorite(7, true) }
        assertTrue(viewModel.exercise.value!!.isFavorite)
    }

    private fun mockExerciseRepository(exercises: MutableStateFlow<List<Exercise>>): ExerciseRepository {
        val repository = mockk<ExerciseRepository>()
        every { repository.observeExercises() } returns exercises
        every { repository.observeExerciseById(any()) } answers {
            val id = firstArg<Int>()
            exercises.map { list -> list.firstOrNull { it.id == id } }
        }
        coEvery { repository.seedIfNeeded() } returns Unit
        coEvery { repository.markViewed(any()) } returns Unit
        coEvery { repository.setFavorite(any(), any()) } coAnswers {
            val id = firstArg<Int>()
            val isFavorite = secondArg<Boolean>()
            exercises.value = exercises.value.map { exercise ->
                if (exercise.id == id) exercise.copy(isFavorite = isFavorite) else exercise
            }
        }
        return repository
    }
}

private fun exercise(
    id: Int,
    name: String,
    isFavorite: Boolean = false,
): Exercise = Exercise(
    id = id,
    name = name,
    muscleGroup = "Legs",
    equipment = "Barbell",
    description = "Description for $name",
    videoUrl = "https://example.com/$id",
    isFavorite = isFavorite,
)
