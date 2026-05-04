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
    val bodyFatPercent: Double?,
    val skeletalMuscleMassKg: Double?,
    val bodyWaterPercent: Double?,
    val visceralFatLevel: Int?,
    val basalMetabolicRateKcal: Int?,
    val waistHipRatio: Double?,
    val leftArmMuscleKg: Double?,
    val rightArmMuscleKg: Double?,
    val trunkMuscleKg: Double?,
    val leftLegMuscleKg: Double?,
    val rightLegMuscleKg: Double?,
    val trainingGoal: String,
    val updatedAt: Long,
)

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    name = name,
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    bodyFatPercent = bodyFatPercent,
    skeletalMuscleMassKg = skeletalMuscleMassKg,
    bodyWaterPercent = bodyWaterPercent,
    visceralFatLevel = visceralFatLevel,
    basalMetabolicRateKcal = basalMetabolicRateKcal,
    waistHipRatio = waistHipRatio,
    leftArmMuscleKg = leftArmMuscleKg,
    rightArmMuscleKg = rightArmMuscleKg,
    trunkMuscleKg = trunkMuscleKg,
    leftLegMuscleKg = leftLegMuscleKg,
    rightLegMuscleKg = rightLegMuscleKg,
    trainingGoal = trainingGoal,
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    name = name,
    age = age,
    heightCm = heightCm,
    weightKg = weightKg,
    bodyFatPercent = bodyFatPercent,
    skeletalMuscleMassKg = skeletalMuscleMassKg,
    bodyWaterPercent = bodyWaterPercent,
    visceralFatLevel = visceralFatLevel,
    basalMetabolicRateKcal = basalMetabolicRateKcal,
    waistHipRatio = waistHipRatio,
    leftArmMuscleKg = leftArmMuscleKg,
    rightArmMuscleKg = rightArmMuscleKg,
    trunkMuscleKg = trunkMuscleKg,
    leftLegMuscleKg = leftLegMuscleKg,
    rightLegMuscleKg = rightLegMuscleKg,
    trainingGoal = trainingGoal,
    updatedAt = System.currentTimeMillis(),
)
