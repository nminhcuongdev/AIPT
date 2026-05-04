package com.example.aipt.feature.exercise.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aipt.feature.exercise.domain.model.Exercise

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val description: String,
    val videoUrl: String,
    val isFavorite: Boolean = false,
    val lastViewedAt: Long? = null,
)

fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    equipment = equipment,
    description = description,
    videoUrl = videoUrl,
    isFavorite = isFavorite,
    lastViewedAt = lastViewedAt,
)
