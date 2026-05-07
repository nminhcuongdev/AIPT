package com.example.aipt.feature.exercise.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.exercise.domain.model.Exercise
import com.example.aipt.feature.exercise.domain.usecase.ObserveExercisesUseCase
import com.example.aipt.feature.exercise.domain.usecase.SeedExercisesUseCase
import com.example.aipt.feature.exercise.domain.usecase.SetExerciseFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseListUiState(
    val exercises: List<Exercise> = emptyList(),
    val muscleGroups: List<String> = emptyList(),
    val searchQuery: String = "",
    val selectedMuscleGroup: String = "All",
    val showFavoritesOnly: Boolean = false,
)

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    observeExercises: ObserveExercisesUseCase,
    private val seedExercises: SeedExercisesUseCase,
    private val setExerciseFavorite: SetExerciseFavoriteUseCase,
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val selectedMuscleGroup = MutableStateFlow("All")
    private val showFavoritesOnly = MutableStateFlow(false)

    val uiState: StateFlow<ExerciseListUiState> = combine(
        observeExercises(),
        searchQuery,
        selectedMuscleGroup,
        showFavoritesOnly,
    ) { exercises, query, muscleGroup, favoritesOnly ->
        val groups = listOf("All") + exercises.map { it.muscleGroup }.distinct().sorted()
        val filtered = exercises
            .filter { exercise -> muscleGroup == "All" || exercise.muscleGroup == muscleGroup }
            .filter { exercise -> !favoritesOnly || exercise.isFavorite }
            .filter { exercise ->
                query.isBlank() || exercise.name.contains(query, ignoreCase = true) ||
                    exercise.muscleGroup.contains(query, ignoreCase = true) ||
                    exercise.equipment.contains(query, ignoreCase = true)
            }

        ExerciseListUiState(
            exercises = filtered,
            muscleGroups = groups,
            searchQuery = query,
            selectedMuscleGroup = muscleGroup,
            showFavoritesOnly = favoritesOnly,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ExerciseListUiState(),
    )

    init {
        viewModelScope.launch {
            seedExercises()
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onMuscleGroupSelected(muscleGroup: String) {
        selectedMuscleGroup.value = muscleGroup
    }

    fun onFavoritesOnlyChanged() {
        showFavoritesOnly.update { !it }
    }

    fun onFavoriteClicked(exercise: Exercise) {
        viewModelScope.launch {
            setExerciseFavorite(exercise.id, !exercise.isFavorite)
        }
    }
}
