package com.example.aipt.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aipt.feature.exercise.data.local.ExerciseDao
import com.example.aipt.feature.exercise.data.local.ExerciseEntity
import com.example.aipt.feature.profile.data.local.GymEquipmentDao
import com.example.aipt.feature.profile.data.local.GymEquipmentEntity
import com.example.aipt.feature.profile.data.local.UserProfileDao
import com.example.aipt.feature.profile.data.local.UserProfileEntity

@Database(
    entities = [
        ExerciseEntity::class,
        UserProfileEntity::class,
        GymEquipmentEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class AiptDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun gymEquipmentDao(): GymEquipmentDao
}
