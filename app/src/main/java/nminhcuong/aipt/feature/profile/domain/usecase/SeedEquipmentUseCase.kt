package nminhcuong.aipt.feature.profile.domain.usecase

import nminhcuong.aipt.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class SeedEquipmentUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke() = repository.seedEquipmentIfNeeded()
}
