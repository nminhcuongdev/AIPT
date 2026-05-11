package nminhcuong.aipt.feature.dashboard.domain.usecase

import nminhcuong.aipt.feature.dashboard.domain.model.WorkoutSessionState
import nminhcuong.aipt.feature.dashboard.domain.repository.WorkoutSessionRepository
import javax.inject.Inject

class SaveWorkoutSessionUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository,
) {
    suspend operator fun invoke(state: WorkoutSessionState) = repository.saveSession(state)
}
