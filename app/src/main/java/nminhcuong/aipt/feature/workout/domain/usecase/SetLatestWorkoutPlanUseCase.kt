package nminhcuong.aipt.feature.workout.domain.usecase

import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanResponse
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutPlanSessionRepository
import javax.inject.Inject

class SetLatestWorkoutPlanUseCase @Inject constructor(
    private val repository: WorkoutPlanSessionRepository,
) {
    fun invoke(response: WorkoutPlanResponse) = repository.setLatestPlan(response)
}
