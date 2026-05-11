package nminhcuong.aipt.feature.workout.domain.usecase

import nminhcuong.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import nminhcuong.aipt.feature.workout.domain.repository.WorkoutRepository
import javax.inject.Inject

class AnalyzeWorkoutDayProgressUseCase @Inject constructor(
    private val repository: WorkoutRepository,
) {
    suspend operator fun invoke(request: WorkoutDayProgressAnalysisRequest): WorkoutDayProgressAnalysisResponse =
        repository.analyzeWorkoutDayProgress(request)
}