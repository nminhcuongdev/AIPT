package com.example.aipt.feature.workout.domain.usecase

import com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse
import com.example.aipt.feature.workout.domain.repository.WorkoutPlanSessionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class ObserveLatestWorkoutPlanUseCase @Inject constructor(
    private val repository: WorkoutPlanSessionRepository,
) {
    operator fun invoke(): StateFlow<WorkoutPlanResponse?> = repository.latestPlan
}
