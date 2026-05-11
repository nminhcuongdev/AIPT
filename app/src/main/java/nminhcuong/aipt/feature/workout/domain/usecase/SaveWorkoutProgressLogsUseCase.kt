package nminhcuong.aipt.feature.workout.domain.usecase

import nminhcuong.aipt.feature.workout.domain.model.WorkoutProgressLog
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutProgressRepository
import javax.inject.Inject

class SaveWorkoutProgressLogsUseCase @Inject constructor(
    private val repository: WorkoutProgressRepository,
) {
    suspend operator fun invoke(logs: List<WorkoutProgressLog>) = repository.saveLogs(logs)
}
