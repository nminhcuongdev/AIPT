package com.example.aipt.di

import com.example.aipt.feature.exercise.data.repository.ExerciseRepositoryImpl
import com.example.aipt.feature.exercise.domain.repository.ExerciseRepository
import com.example.aipt.feature.profile.data.repository.ProfileRepositoryImpl
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import com.example.aipt.feature.workout.data.repository.WorkoutRepositoryImpl
import com.example.aipt.feature.workout.data.repository.WorkoutScheduleRepositoryImpl
import com.example.aipt.feature.workout.domain.repository.WorkoutRepository
import com.example.aipt.feature.workout.domain.repository.WorkoutScheduleRepository
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

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        implementation: WorkoutRepositoryImpl,
    ): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutScheduleRepository(
        implementation: WorkoutScheduleRepositoryImpl,
    ): WorkoutScheduleRepository
}
