package nminhcuong.aipt.feature.workout.data.remote

import nminhcuong.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutPlanResponse
import nminhcuong.aipt.feature.workout.domain.model.WorkoutProgressAnalysisRequest
import nminhcuong.aipt.feature.workout.domain.model.WorkoutProgressAnalysisResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WorkoutPlanApi {
    @POST("api/v1/workout-plans")
    suspend fun createWorkoutPlan(
        @Body request: WorkoutPlanRequest,
    ): WorkoutPlanResponse

    @POST("api/v1/workout-progress/analyze-day")
    suspend fun analyzeWorkoutDayProgress(
        @Body request: WorkoutDayProgressAnalysisRequest,
    ): WorkoutDayProgressAnalysisResponse

    @POST("api/v1/workout-progress/analyze")
    suspend fun analyzeWorkoutProgress(
        @Body request: WorkoutProgressAnalysisRequest,
    ): WorkoutProgressAnalysisResponse
}