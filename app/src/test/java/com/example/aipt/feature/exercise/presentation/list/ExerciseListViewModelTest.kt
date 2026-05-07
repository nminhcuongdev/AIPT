package com.example.aipt.feature.exercise.presentation.list

import com.example.aipt.feature.exercise.domain.model.Exercise
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import com.example.aipt.feature.exercise.domain.usecase.ObserveExercisesUseCase
import com.example.aipt.testutil.MainDispatcherRule
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
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads exercises and builds sorted muscle groups`() = runTest {
        val exercises = MutableStateFlow(
            listOf(
                exercise(id = 1, name = "Bench Press", muscleGroup = "Chest"),
                exercise(id = 2, name = "Lat Pulldown", muscleGroup = "Back"),
                exercise(id = 3, name = "Push Up", muscleGroup = "Chest"),
            ),
        )
        val repository = mockExerciseRepository(exercises)
        val viewModel = ExerciseListViewModel(ObserveExercisesUseCase(repository), repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        runCurrent()

        coVerify(exactly = 1) { repository.seedIfNeeded() }
        assertEquals(listOf("All", "Back", "Chest"), viewModel.uiState.value.muscleGroups)
        assertEquals(listOf("Bench Press", "Lat Pulldown", "Push Up"), viewModel.uiState.value.exercises.map { it.name })
    }

    @Test
    fun `filters by query muscle group and favorites`() = runTest {
        val exercises = MutableStateFlow(
            listOf(
                exercise(id = 1, name = "Bench Press", muscleGroup = "Chest", equipment = "Barbell", isFavorite = true),
                exercise(id = 2, name = "Goblet Squat", muscleGroup = "Legs", equipment = "Dumbbell"),
                exercise(id = 3, name = "Cable Fly", muscleGroup = "Chest", equipment = "Cable"),
            ),
        )
        val repository = mockExerciseRepository(exercises)
        val viewModel = ExerciseListViewModel(ObserveExercisesUseCase(repository), repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        viewModel.onMuscleGroupSelected("Chest")
        viewModel.onSearchQueryChanged("bench")
        runCurrent()

        assertEquals(listOf("Bench Press"), viewModel.uiState.value.exercises.map { it.name })

        viewModel.onSearchQueryChanged("")
        viewModel.onFavoritesOnlyChanged()
        runCurrent()

        assertEquals(listOf("Bench Press"), viewModel.uiState.value.exercises.map { it.name })
    }

    @Test
    fun `favorite click toggles repository and updates state`() = runTest {
        val exercises = MutableStateFlow(listOf(exercise(id = 1, name = "Bench Press", isFavorite = false)))
        val repository = mockExerciseRepository(exercises)
        val viewModel = ExerciseListViewModel(ObserveExercisesUseCase(repository), repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        viewModel.onFavoriteClicked(viewModel.uiState.value.exercises.first())
        runCurrent()

        coVerify(exactly = 1) { repository.setFavorite(1, true) }
        assertEquals(true, viewModel.uiState.value.exercises.first().isFavorite)
    }

    private fun mockExerciseRepository(exercises: MutableStateFlow<List<Exercise>>): ExerciseRepository {
        val repository = mockk<ExerciseRepository>()
        every { repository.observeExercises() } returns exercises
        coEvery { repository.seedIfNeeded() } returns Unit
        coEvery { repository.setFavorite(any(), any()) } coAnswers {
            val id = firstArg<Int>()
            val isFavorite = secondArg<Boolean>()
            exercises.value = exercises.value.map { exercise ->
                if (exercise.id == id) exercise.copy(isFavorite = isFavorite) else exercise
            }
        }
        coEvery { repository.markViewed(any()) } returns Unit
        return repository
    }
}

private fun exercise(
    id: Int,
    name: String,
    muscleGroup: String = "Chest",
    equipment: String = "Bodyweight",
    isFavorite: Boolean = false,
): Exercise = Exercise(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    equipment = equipment,
    description = "Description for $name",
    videoUrl = "https://example.com/$id",
    isFavorite = isFavorite,
)
