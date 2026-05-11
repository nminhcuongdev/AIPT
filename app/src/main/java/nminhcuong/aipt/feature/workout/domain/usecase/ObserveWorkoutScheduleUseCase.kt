package nminhcuong.aipt.feature.workout.domain.usecase

import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveWorkoutScheduleUseCase @Inject constructor(
    private val repository: WorkoutScheduleRepository,
) {
    operator fun invoke(): Flow<List<WorkoutDay>> = repository.observeWorkoutDays()
}