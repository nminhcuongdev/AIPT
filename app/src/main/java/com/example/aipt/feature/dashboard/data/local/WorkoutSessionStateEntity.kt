package com.example.aipt.feature.dashboard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionState
import com.example.aipt.feature.dashboard.domain.model.WorkoutSessionStatus

@Entity(tableName = "workout_session_states")
data class WorkoutSessionStateEntity(
    @PrimaryKey val date: String,
    val day: Int,
    val status: String,
    val updatedAt: Long,
)

fun WorkoutSessionStateEntity.toDomain(): WorkoutSessionState = WorkoutSessionState(
    date = date,
    day = day,
    status = when (status) {
        "in_progress" -> WorkoutSessionStatus.InProgress
        "completed" -> WorkoutSessionStatus.Completed
        else -> WorkoutSessionStatus.NotStarted
    },
    updatedAt = updatedAt,
)

fun WorkoutSessionState.toEntity(): WorkoutSessionStateEntity = WorkoutSessionStateEntity(
    date = date,
    day = day,
    status = when (status) {
        WorkoutSessionStatus.NotStarted -> "not_started"
        WorkoutSessionStatus.InProgress -> "in_progress"
        WorkoutSessionStatus.Completed -> "completed"
    },
    updatedAt = updatedAt,
)
