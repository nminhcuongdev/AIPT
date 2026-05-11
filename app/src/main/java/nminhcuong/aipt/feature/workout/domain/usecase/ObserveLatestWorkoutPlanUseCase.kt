package nminhcuong.aipt.feature.workout.domain.usecase

import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanResponse
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutPlanSessionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class ObserveLatestWorkoutPlanUseCase @Inject constructor(
    private val repository: WorkoutPlanSessionRepository,
) {
    operator fun invoke(): StateFlow<WorkoutPlanResponse?> = repository.latestPlan
}
