package nminhcuong.aipt.feature.exercise.domain.usecase

import nminhcuong.aipt.feature.exercise.domain.repository.ExerciseRepository
import javax.inject.Inject

class MarkExerciseViewedUseCase @Inject constructor(
    private val repository: ExerciseRepository,
) {
    suspend operator fun invoke(id: Int) = repository.markViewed(id)
}
