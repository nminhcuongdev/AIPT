package com.example.aipt.feature.workout.domain.usecase

import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisRequest
import com.example.aipt.feature.workout.domain.model.WorkoutDayProgressAnalysisResponse
import com.example.aipt.feature.workout.domain.repository.WorkoutRepository
import javax.inject.Inject

class AnalyzeWorkoutDayProgressUseCase @Inject constructor(
    private val repository: WorkoutRepository,
) {
    suspend operator fun invoke(request: WorkoutDayProgressAnalysisRequest): WorkoutDayProgressAnalysisResponse =
        repository.analyzeWorkoutDayProgress(request)
}