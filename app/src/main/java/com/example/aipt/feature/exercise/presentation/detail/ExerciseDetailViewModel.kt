package com.example.aipt.feature.exercise.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aipt.feature.exercise.domain.model.Exercise
import com.example.aipt.feature.exercise.domain.usecase.MarkExerciseViewedUseCase
import com.example.aipt.feature.exercise.domain.usecase.ObserveExerciseDetailUseCase
import com.example.aipt.feature.exercise.domain.usecase.SetExerciseFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeExerciseDetail: ObserveExerciseDetailUseCase,
    private val markExerciseViewed: MarkExerciseViewedUseCase,
    private val setExerciseFavorite: SetExerciseFavoriteUseCase,
) : ViewModel() {
    private val exerciseId: Int = checkNotNull(savedStateHandle["exerciseId"])

    val exercise: StateFlow<Exercise?> = observeExerciseDetail(exerciseId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    init {
        viewModelScope.launch {
            observeExerciseDetail(exerciseId).filterNotNull().first()
            markExerciseViewed(exerciseId)
        }
    }

    fun onFavoriteClicked(exercise: Exercise) {
        viewModelScope.launch {
            setExerciseFavorite(exercise.id, !exercise.isFavorite)
        }
    }
}
