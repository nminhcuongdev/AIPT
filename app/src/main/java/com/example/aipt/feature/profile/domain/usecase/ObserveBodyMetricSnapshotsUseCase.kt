package com.example.aipt.feature.profile.domain.usecase

import com.example.aipt.feature.profile.domain.model.BodyMetricSnapshot
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveBodyMetricSnapshotsUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    operator fun invoke(): Flow<List<BodyMetricSnapshot>> = repository.observeBodyMetricSnapshots()
}
