package com.example.aipt.feature.exercise.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun observeExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    fun observeExerciseById(id: Int): Flow<ExerciseEntity?>

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Query("UPDATE exercises SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean)

    @Query("UPDATE exercises SET lastViewedAt = :lastViewedAt WHERE id = :id")
    suspend fun markViewed(id: Int, lastViewedAt: Long)
}
