package com.example.aipt.feature.exercise.domain.usecase

import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import javax.inject.Inject

class ObserveExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository,
) {
    operator fun invoke() = repository.observeExercises()
}
