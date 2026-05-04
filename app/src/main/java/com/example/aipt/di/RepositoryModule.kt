package com.example.aipt.di

import com.example.aipt.feature.exercise.data.repository.ExerciseRepositoryImpl
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import com.example.aipt.feature.profile.data.repository.ProfileRepositoryImpl
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
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

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        implementation: ProfileRepositoryImpl,
    ): ProfileRepository
}
