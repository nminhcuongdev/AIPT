package com.example.aipt.feature.workout.data.remote

import com.example.aipt.feature.workout.domain.model.WorkoutPlanRequest
import com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface WorkoutPlanApi {
    @POST("api/v1/workout-plans")
    suspend fun createWorkoutPlan(
        @Body request: WorkoutPlanRequest,
    ): WorkoutPlanResponse
}
