package com.example.aipt.di

import android.content.Context
import androidx.room.Room
import com.example.aipt.core.data.local.AiptDatabase
import com.example.aipt.feature.exercise.data.local.ExerciseDao
import com.example.aipt.feature.profile.data.local.GymEquipmentDao
import com.example.aipt.feature.profile.data.local.UserProfileDao
import com.example.aipt.feature.workout.data.local.WorkoutDayDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AiptDatabase =
        Room.databaseBuilder(
            context = context,
            klass = AiptDatabase::class.java,
            name = "aipt.db",
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

    @Provides
    fun provideExerciseDao(database: AiptDatabase): ExerciseDao = database.exerciseDao()

    @Provides
    fun provideUserProfileDao(database: AiptDatabase): UserProfileDao = database.userProfileDao()

    @Provides
    fun provideGymEquipmentDao(database: AiptDatabase): GymEquipmentDao = database.gymEquipmentDao()

    @Provides
    fun provideWorkoutDayDao(database: AiptDatabase): WorkoutDayDao = database.workoutDayDao()
}
