package com.example.aipt.feature.profile.domain.repository

import com.example.aipt.feature.profile.domain.model.BodyMetricSnapshot
import com.example.aipt.feature.profile.domain.model.EquipmentStatus
import com.example.aipt.feature.profile.domain.model.GymEquipment
import com.example.aipt.feature.profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<UserProfile?>
    fun observeEquipment(): Flow<List<GymEquipment>>
    fun observeBodyMetricSnapshots(): Flow<List<BodyMetricSnapshot>>
    suspend fun seedEquipmentIfNeeded()
    suspend fun saveProfile(profile: UserProfile)
    suspend fun deleteProfile()
    suspend fun setEquipmentStatus(id: Int, status: EquipmentStatus)
    suspend fun resetEquipmentChoices()
}
