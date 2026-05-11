package nminhcuong.aipt.feature.exercise.domain.model

data class Exercise(
    val id: Int,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val description: String,
    val videoUrl: String,
    val isFavorite: Boolean = false,
    val lastViewedAt: Long? = null,
)
