package com.example.aipt.feature.dashboard.data.repository

import com.example.aipt.feature.dashboard.data.local.WorkoutSessionStateDao
import com.example.aipt.feature.dashboard.data.local.toDomain
import com.example.aipt.feature.dashboard.data.local.toEntity
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionState
import com.example.aipt.feature.dashboard.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkoutSessionRepositoryImpl @Inject constructor(
    private val dao: WorkoutSessionStateDao,
) : WorkoutSessionRepository {
    override fun observeSession(date: String): Flow<WorkoutSessionState?> =
        dao.observeByDate(date).map { it?.toDomain() }

    override fun observeRecentSessions(limit: Int): Flow<List<WorkoutSessionState>> =
        dao.observeRecent(limit).map { states -> states.map { it.toDomain() } }

    override suspend fun saveSession(state: WorkoutSessionState) {
        dao.save(state.toEntity())
    }
}
