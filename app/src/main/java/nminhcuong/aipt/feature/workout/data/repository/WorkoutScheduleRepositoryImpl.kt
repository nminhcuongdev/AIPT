package nminhcuong.aipt.feature.workout.data.repository

import nminhcuong.aipt.feature.workout.data.local.WorkoutDayDao
import nminhcuong.aipt.feature.workout.data.local.toDomain
import nminhcuong.aipt.feature.workout.data.local.toEntity
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDay
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutScheduleRepositoryImpl @Inject constructor(
    private val dao: WorkoutDayDao,
) : WorkoutScheduleRepository {
    override fun observeWorkoutDays(): Flow<List<WorkoutDay>> =
        dao.observeWorkoutDays().map { days -> days.map { it.toDomain() } }

    override suspend fun saveWorkoutDay(day: WorkoutDay) {
        dao.saveWorkoutDay(day.toEntity())
    }

    override suspend fun saveWorkoutDays(days: List<WorkoutDay>) {
        dao.saveWorkoutDays(days.map { it.toEntity() })
    }
}