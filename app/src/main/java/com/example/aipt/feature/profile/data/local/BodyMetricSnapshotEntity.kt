package com.example.aipt.feature.profile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aipt.feature.profile.domain.model.BodyMetricSnapshot

@Entity(tableName = "body_metric_snapshots")
data class BodyMetricSnapshotEntity(
    @PrimaryKey val dateKey: String,
    val capturedAt: Long,
    val weightKg: Double?,
    val bodyFatPercent: Double?,
    val skeletalMuscleMassKg: Double?,
)

fun BodyMetricSnapshotEntity.toDomain(): BodyMetricSnapshot = BodyMetricSnapshot(
    dateKey = dateKey,
    capturedAt = capturedAt,
    weightKg = weightKg,
    bodyFatPercent = bodyFatPercent,
    skeletalMuscleMassKg = skeletalMuscleMassKg,
)

fun BodyMetricSnapshot.toEntity(): BodyMetricSnapshotEntity = BodyMetricSnapshotEntity(
    dateKey = dateKey,
    capturedAt = capturedAt,
    weightKg = weightKg,
    bodyFatPercent = bodyFatPercent,
    skeletalMuscleMassKg = skeletalMuscleMassKg,
)
