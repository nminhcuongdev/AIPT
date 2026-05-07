package com.example.aipt.feature.profile.domain.usecase

import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class ResetEquipmentChoicesUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke() = repository.resetEquipmentChoices()
}
