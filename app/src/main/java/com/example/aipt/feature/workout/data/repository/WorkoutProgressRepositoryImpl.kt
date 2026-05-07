package com.example.aipt.feature.workout.data.repository

import com.example.aipt.feature.workout.data.local.WorkoutProgressLogDao
import com.example.aipt.feature.workout.data.local.toDomain
import com.example.aipt.feature.workout.data.local.toEntity
import com.example.aipt.feature.workout.domain.model.WorkoutProgressLog
import com.example.aipt.feature.workout.domain.repository.WorkoutProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutProgressRepositoryImpl @Inject constructor(
    private val dao: WorkoutProgressLogDao,
) : WorkoutProgressRepository {
    override fun observeLogs(): Flow<List<WorkoutProgressLog>> =
        dao.observeLogs().map { logs -> logs.map { it.toDomain() } }

    override suspend fun saveLogs(logs: List<WorkoutProgressLog>) {
        if (logs.isNotEmpty()) {
            dao.insertAll(logs.map { it.toEntity() })
        }
    }
}
