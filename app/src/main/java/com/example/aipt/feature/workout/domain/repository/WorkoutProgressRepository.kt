package com.example.aipt.feature.workout.domain.repository

import com.example.aipt.feature.workout.domain.model.WorkoutProgressLog
import kotlinx.coroutines.flow.Flow

interface WorkoutProgressRepository {
    fun observeLogs(): Flow<List<WorkoutProgressLog>>
    suspend fun saveLogs(logs: List<WorkoutProgressLog>)
}
