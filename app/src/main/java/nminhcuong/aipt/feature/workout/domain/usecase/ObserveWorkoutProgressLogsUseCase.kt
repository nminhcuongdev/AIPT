package nminhcuong.aipt.feature.workout.domain.usecase

import nminhcuong.aipt.feature.workout.domain.model.WorkoutProgressLog
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutProgressRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveWorkoutProgressLogsUseCase @Inject constructor(
    private val repository: WorkoutProgressRepository,
) {
    operator fun invoke(): Flow<List<WorkoutProgressLog>> = repository.observeLogs()
}
