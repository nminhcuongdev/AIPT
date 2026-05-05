package com.example.aipt.feature.workout.domain.repository

import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisRequest
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import com.example.aipt.feature.workout.domain.model.WorkoutPlanRequest
import com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse

interface WorkoutRepository {
    suspend fun createWorkoutPlan(request: WorkoutPlanRequest): WorkoutPlanResponse
    suspend fun analyzeWorkoutDayProgress(request: WorkoutDayProgressAnalysisRequest): WorkoutDayProgressAnalysisResponse
}