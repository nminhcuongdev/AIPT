package nminhcuong.aipt.feature.workout.domain.model

data class WorkoutProgressLog(
    val id: Long = 0,
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
