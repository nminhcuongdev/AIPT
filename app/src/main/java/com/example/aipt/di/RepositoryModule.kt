package com.example.aipt.di

import com.example.aipt.feature.exercise.data.repository.ExerciseRepositoryImpl
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        implementation: ExerciseRepositoryImpl,
    ): ExerciseRepository
}
