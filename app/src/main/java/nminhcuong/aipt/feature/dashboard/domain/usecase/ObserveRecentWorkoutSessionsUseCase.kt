package nminhcuong.aipt.feature.dashboard.domain.usecase

import nminhcuong.aipt.feature.dashboard.domain.repository.WorkoutSessionRepository
import javax.inject.Inject

class ObserveRecentWorkoutSessionsUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository,
) {
    operator fun invoke(limit: Int = 10) = repository.observeRecentSessions(limit)
}
