package com.example.aipt.feature.profile.domain.model

data class UserProfile(
    val name: String,
    val age: Int?,
    val heightCm: Int?,
    val weightKg: Int?,
    val trainingGoal: String,
)
