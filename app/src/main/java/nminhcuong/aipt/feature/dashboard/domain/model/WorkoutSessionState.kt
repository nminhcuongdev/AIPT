package nminhcuong.aipt.feature.dashboard.domain.model

enum class WorkoutSessionStatus {
    NotStarted,
    InProgress,
    Completed,
}

data class WorkoutSessionState(
    val date: String,
    val day: Int,
    val status: WorkoutSessionStatus,
    val updatedAt: Long,
)
