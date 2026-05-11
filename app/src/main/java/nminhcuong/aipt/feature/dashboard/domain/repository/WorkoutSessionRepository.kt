package nminhcuong.aipt.feature.dashboard.domain.repository

import nminhcuong.aipt.feature.dashboard.domain.model.WorkoutSessionState
import kotlinx.coroutines.flow.Flow

interface WorkoutSessionRepository {
    fun observeSession(date: String): Flow<WorkoutSessionState?>
    fun observeRecentSessions(limit: Int): Flow<List<WorkoutSessionState>>
    suspend fun saveSession(state: WorkoutSessionState)
}
