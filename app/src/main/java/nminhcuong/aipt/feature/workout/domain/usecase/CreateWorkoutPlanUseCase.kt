package nminhcuong.aipt.feature.workout.domain.usecase

import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanResponse
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutRepository
import javax.inject.Inject

class CreateWorkoutPlanUseCase @Inject constructor(
    private val repository: WorkoutRepository,
) {
    suspend operator fun invoke(request: WorkoutPlanRequest): WorkoutPlanResponse =
        repository.createWorkoutPlan(request)
}