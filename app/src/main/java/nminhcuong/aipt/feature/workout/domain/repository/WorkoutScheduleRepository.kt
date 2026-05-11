package nminhcuong.aipt.feature.workout.domain.repository

import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import kotlinx.coroutines.flow.Flow

interface WorkoutScheduleRepository {
    fun observeWorkoutDays(): Flow<List<WorkoutDay>>
    suspend fun saveWorkoutDay(day: WorkoutDay)
    suspend fun saveWorkoutDays(days: List<WorkoutDay>)
}