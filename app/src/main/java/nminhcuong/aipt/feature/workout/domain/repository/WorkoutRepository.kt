package nminhcuong.aipt.feature.workout.domain.repository

import nminhcuong.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanResponse

interface WorkoutRepository {
    suspend fun createWorkoutPlan(request: WorkoutPlanRequest): WorkoutPlanResponse
    suspend fun analyzeWorkoutDayProgress(request: WorkoutDayProgressAnalysisRequest): WorkoutDayProgressAnalysisResponse
}