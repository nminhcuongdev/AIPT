package com.example.aipt.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aipt.feature.exercise.data.local.ExerciseDao
import com.example.aipt.feature.exercise.data.local.ExerciseEntity

@Database(
    entities = [ExerciseEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AiptDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
}
