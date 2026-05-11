package nminhcuong.aipt.feature.profile.domain.usecase

import nminhcuong.aipt.feature.profile.domain.model.EquipmentStatus
import nminhcuong.aipt.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class SetEquipmentStatusUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(id: Int, status: EquipmentStatus) = repository.setEquipmentStatus(id, status)
}
