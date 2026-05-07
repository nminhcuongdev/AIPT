package com.example.aipt.feature.profile.domain.model

data class BodyMetricSnapshot(
    val dateKey: String,
    val capturedAt: Long,
    val weightKg: Double?,
    val bodyFatPercent: Double?,
    val skeletalMuscleMassKg: Double?,
)
