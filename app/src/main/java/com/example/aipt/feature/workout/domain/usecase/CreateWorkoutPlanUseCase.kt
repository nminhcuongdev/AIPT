package com.example.aipt.feature.workout.domain.usecase

import com.example.aipt.feature.workout.domain.model.WorkoutPlanRequest
import com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse
import com.example.aipt.feature.workout.domain.repository.WorkoutRepository
import javax.inject.Inject

class CreateWorkoutPlanUseCase @Inject constructor(
    private val repository: WorkoutRepository,
) {
    suspend operator fun invoke(request: WorkoutPlanRequest): WorkoutPlanResponse =
        repository.createWorkoutPlan(request)
}