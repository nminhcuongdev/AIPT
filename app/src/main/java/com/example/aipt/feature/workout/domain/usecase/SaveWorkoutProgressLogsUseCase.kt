package com.example.aipt.feature.workout.domain.usecase

import com.example.aipt.feature.workout.domain.model.WorkoutProgressLog
import com.example.aipt.feature.workout.domain.repository.WorkoutProgressRepository
import javax.inject.Inject

class SaveWorkoutProgressLogsUseCase @Inject constructor(
    private val repository: WorkoutProgressRepository,
) {
    suspend operator fun invoke(logs: List<WorkoutProgressLog>) = repository.saveLogs(logs)
}
