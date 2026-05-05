package com.example.aipt.feature.workout.domain.usecase

import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.example.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
import javax.inject.Inject

class SaveWorkoutScheduleUseCase @Inject constructor(
    private val repository: WorkoutScheduleRepository,
) {
    suspend operator fun invoke(days: List<WorkoutDay>) = repository.saveWorkoutDays(days)
}
