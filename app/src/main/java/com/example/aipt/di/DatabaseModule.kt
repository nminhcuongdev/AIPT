package com.example.aipt.di

import android.content.Context
import androidx.room.Room
import com.example.aipt.core.data.local.AiptDatabase
import com.example.aipt.feature.exercise.data.local.ExerciseDao
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
        ).build()

    @Provides
    fun provideExerciseDao(database: AiptDatabase): ExerciseDao = database.exerciseDao()
}
