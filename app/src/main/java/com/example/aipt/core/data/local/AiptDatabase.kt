package com.example.aipt.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aipt.feature.dashboard.data.local.WorkoutSessionStateDao
import com.example.aipt.feature.dashboard.data.local.WorkoutSessionStateEntity
import com.example.aipt.feature.exercise.data.local.ExerciseDao
import com.example.aipt.feature.exercise.data.local.ExerciseEntity
import com.example.aipt.feature.profile.data.local.GymEquipmentDao
import com.example.aipt.feature.profile.data.local.GymEquipmentEntity
import com.example.aipt.feature.profile.data.local.UserProfileDao
import com.example.aipt.feature.profile.data.local.UserProfileEntity
import com.example.aipt.feature.workout.data.local.WorkoutDayDao
import com.example.aipt.feature.workout.data.local.WorkoutDayEntity

@Database(
    entities = [
        ExerciseEntity::class,
        UserProfileEntity::class,
        GymEquipmentEntity::class,
        WorkoutDayEntity::class,
        WorkoutSessionStateEntity::class,
    ],
    version = 7,
    exportSchema = false,
)
abstract class AiptDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun gymEquipmentDao(): GymEquipmentDao
    abstract fun workoutDayDao(): WorkoutDayDao
    abstract fun workoutSessionStateDao(): WorkoutSessionStateDao
}

