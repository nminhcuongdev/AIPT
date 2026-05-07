package com.example.aipt.feature.workout.domain.usecase

import com.example.aipt.feature.profile.domain.model.GymEquipment
import com.example.aipt.feature.profile.domain.model.UserProfile
import com.example.aipt.feature.workout.domain.WorkoutPlanGenerator
import com.example.aipt.feature.workout.domain.model.WorkoutPlanRequest
import javax.inject.Inject

class BuildWorkoutPlanRequestUseCase @Inject constructor(
    private val generator: WorkoutPlanGenerator,
) {
    operator fun invoke(profile: UserProfile, equipment: List<GymEquipment>): WorkoutPlanRequest =
        generator.buildRequest(profile, equipment)
}
