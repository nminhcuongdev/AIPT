package com.example.aipt.feature.workout.domain.repository

import com.example.aipt.feature.workout.domain.model.WorkoutPlanResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutPlanSessionRepository @Inject constructor() {
    private val _latestPlan = MutableStateFlow<WorkoutPlanResponse?>(null)
    val latestPlan: StateFlow<WorkoutPlanResponse?> = _latestPlan

    fun setLatestPlan(response: WorkoutPlanResponse) {
        _latestPlan.value = response
    }
}