package com.example.aipt.feature.workout.data.repository

import com.example.aipt.feature.workout.data.remote.WorkoutPlanApi
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisRequest
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import com.example.aipt.feature.workout.domain.model.WorkoutPlanRequest
import com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse
import com.example.aipt.feature.workout.domain.repository.WorkoutRepository
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val api: WorkoutPlanApi,
) : WorkoutRepository {
    override suspend fun createWorkoutPlan(request: WorkoutPlanRequest): WorkoutPlanResponse =
        api.createWorkoutPlan(request)

    override suspend fun analyzeWorkoutDayProgress(request: WorkoutDayProgressAnalysisRequest): WorkoutDayProgressAnalysisResponse =
        api.analyzeWorkoutDayProgress(request)
}