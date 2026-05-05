package com.example.aipt.feature.workout.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDayDao {
    @Query("SELECT * FROM workout_days ORDER BY day ASC")
    fun observeWorkoutDays(): Flow<List<WorkoutDayEntity>>

    @Upsert
    suspend fun saveWorkoutDay(day: WorkoutDayEntity)

    @Upsert
    suspend fun saveWorkoutDays(days: List<WorkoutDayEntity>)
}