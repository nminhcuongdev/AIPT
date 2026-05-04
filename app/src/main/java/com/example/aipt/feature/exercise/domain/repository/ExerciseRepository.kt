package com.example.aipt.feature.exercise.domain.repository

import com.example.aipt.feature.exercise.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeExercises(): Flow<List<Exercise>>
    fun observeExerciseById(id: Int): Flow<Exercise?>
    suspend fun seedIfNeeded()
    suspend fun setFavorite(id: Int, isFavorite: Boolean)
    suspend fun markViewed(id: Int)
}
