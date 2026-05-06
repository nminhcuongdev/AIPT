package com.example.aipt.feature.dashboard.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionStateDao {
    @Query("SELECT * FROM workout_session_states WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<WorkoutSessionStateEntity?>

    @Upsert
    suspend fun save(state: WorkoutSessionStateEntity)
}
