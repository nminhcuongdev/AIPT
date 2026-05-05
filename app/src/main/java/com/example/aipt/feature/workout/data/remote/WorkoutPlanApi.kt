package com.example.aipt.feature.workout.data.remote

import com.example.aipt.feature.workout.domain.model.WorkoutPlanRequest
import com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse
import com.example.aipt.feature.workout.domain.model.WorkoutProgressAnalysisRequest
import com.example.aipt.feature.workout.domain.model.WorkoutProgressAnalysisResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WorkoutPlanApi {
    @POST("api/v1/workout-plans")
    suspend fun createWorkoutPlan(
        @Body request: WorkoutPlanRequest,
    ): WorkoutPlanResponse

    @POST("api/v1/workout-progress/analyze")
    suspend fun analyzeWorkoutProgress(
        @Body request: WorkoutProgressAnalysisRequest,
    ): WorkoutProgressAnalysisResponse
}