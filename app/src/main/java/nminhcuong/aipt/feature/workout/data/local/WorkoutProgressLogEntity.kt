package nminhcuong.aipt.feature.workout.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import nminhcuong.aipt.feature.workout.domain.model.WorkoutProgressLog

@Entity(tableName = "workout_progress_logs")
data class WorkoutProgressLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val performedAt: Long,
    val dateKey: String,
    val weekStartDate: String,
    val day: Int,
    val dayTitle: String,
    val exerciseName: String,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Double?,
    val volumeKg: Double,
    val notes: String,
)

fun WorkoutProgressLogEntity.toDomain(): WorkoutProgressLog = WorkoutProgressLog(
    id = id,
    performedAt = performedAt,
    dateKey = dateKey,
    weekStartDate = weekStartDate,
    day = day,
    dayTitle = dayTitle,
    exerciseName = exerciseName,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    volumeKg = volumeKg,
    notes = notes,
)

fun WorkoutProgressLog.toEntity(): WorkoutProgressLogEntity = WorkoutProgressLogEntity(
    id = id,
    performedAt = performedAt,
    dateKey = dateKey,
    weekStartDate = weekStartDate,
    day = day,
    dayTitle = dayTitle,
    exerciseName = exerciseName,
    sets = sets,
    reps = reps,
    weightKg = weightKg,
    volumeKg = volumeKg,
    notes = notes,
)
