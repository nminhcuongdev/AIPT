package nminhcuong.aipt.feature.dashboard.domain.usecase

import nminhcuong.aipt.feature.dashboard.domain.repository.WorkoutSessionRepository
import javax.inject.Inject

class ObserveWorkoutSessionUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository,
) {
    operator fun invoke(date: String) = repository.observeSession(date)
}
