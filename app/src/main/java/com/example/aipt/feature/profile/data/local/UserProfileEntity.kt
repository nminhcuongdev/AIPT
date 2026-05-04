package com.example.aipt.feature.profile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aipt.feature.profile.domain.model.UserProfile

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int?,
    val heightCm: Int?,
    val weightKg: Int?,
    val trainingGoal: String,
    val updatedAt: Long,
)

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    name = name,
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    trainingGoal = trainingGoal,
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    name = name,
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    trainingGoal = trainingGoal,
    updatedAt = System.currentTimeMillis(),
)
