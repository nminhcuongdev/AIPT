package com.example.aipt.feature.workout.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aipt.feature.workout.domain.model.PlannedExercise
import com.example.aipt.feature.workout.domain.model.WorkoutDay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "workout_days")
data class WorkoutDayEntity(
    @PrimaryKey val day: Int,
    val title: String,
    val focus: String,
    val warmupJson: String,
    val exercisesJson: String,
    val cooldownJson: String,
    val updatedAt: Long,
)

private val gson = Gson()
private val stringListType = object : TypeToken<List<String>>() {}.type
private val plannedExerciseListType = object : TypeToken<List<PlannedExercise>>() {}.type

fun WorkoutDayEntity.toDomain(): WorkoutDay = WorkoutDay(
    day = day,
    title = title,
    focus = focus,
    warmup = gson.fromJson(warmupJson, stringListType),
    exercises = gson.fromJson(exercisesJson, plannedExerciseListType),
    cooldown = gson.fromJson(cooldownJson, stringListType),
)

fun WorkoutDay.toEntity(): WorkoutDayEntity = WorkoutDayEntity(
    day = day,
    title = title,
    focus = focus,
    warmupJson = gson.toJson(warmup),
    exercisesJson = gson.toJson(exercises),
    cooldownJson = gson.toJson(cooldown),
    updatedAt = System.currentTimeMillis(),
)