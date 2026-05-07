package com.example.aipt.feature.exercise.domain.usecase

import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import javax.inject.Inject

class SetExerciseFavoriteUseCase @Inject constructor(
    private val repository: ExerciseRepository,
) {
    suspend operator fun invoke(id: Int, isFavorite: Boolean) = repository.setFavorite(id, isFavorite)
}
