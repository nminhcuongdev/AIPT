package com.example.aipt.feature.exercise.data.repository

import com.example.aipt.feature.exercise.data.local.ExerciseDao
import com.example.aipt.feature.exercise.data.local.ExerciseSeedData
import com.example.aipt.feature.exercise.data.local.toDomain
import com.example.aipt.feature.exercise.domain.model.Exercise
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
) : ExerciseRepository {
    override fun observeExercises(): Flow<List<Exercise>> =
        exerciseDao.observeExercises().map { exercises -> exercises.map { it.toDomain() } }

    override fun observeExerciseById(id: Int): Flow<Exercise?> =
        exerciseDao.observeExerciseById(id).map { it?.toDomain() }

    override suspend fun seedIfNeeded() {
        if (exerciseDao.count() == 0) {
            exerciseDao.insertAll(ExerciseSeedData.exercises)
        }
    }

    override suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        exerciseDao.setFavorite(id = id, isFavorite = isFavorite)
    }

    override suspend fun markViewed(id: Int) {
        exerciseDao.markViewed(id = id, lastViewedAt = System.currentTimeMillis())
    }
}
