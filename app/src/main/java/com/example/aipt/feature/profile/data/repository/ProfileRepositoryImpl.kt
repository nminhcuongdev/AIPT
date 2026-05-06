package com.example.aipt.feature.profile.data.repository

import com.example.aipt.feature.profile.data.local.GymEquipmentDao
import com.example.aipt.feature.profile.data.local.GymEquipmentSeedData
import com.example.aipt.feature.profile.data.local.UserProfileDao
import com.example.aipt.feature.profile.data.local.toDomain
import com.example.aipt.feature.profile.data.local.toEntity
import com.example.aipt.feature.profile.domain.model.EquipmentStatus
import com.example.aipt.feature.profile.domain.model.GymEquipment
import com.example.aipt.feature.profile.domain.model.UserProfile
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val gymEquipmentDao: GymEquipmentDao,
) : ProfileRepository {
    override fun observeProfile(): Flow<UserProfile?> =
        userProfileDao.observeProfile().map { it?.toDomain() }

    override fun observeEquipment(): Flow<List<GymEquipment>> =
        gymEquipmentDao.observeEquipment().map { equipment -> equipment.map { it.toDomain() } }

    override suspend fun seedEquipmentIfNeeded() {
        if (gymEquipmentDao.count() == 0) {
            gymEquipmentDao.insertAll(GymEquipmentSeedData.equipment)
        } else {
            gymEquipmentDao.upsertAll(GymEquipmentSeedData.equipment)
        }
    }

    override suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.saveProfile(profile.toEntity())
    }

    override suspend fun setEquipmentStatus(id: Int, status: EquipmentStatus) {
        gymEquipmentDao.updateStatus(id = id, status = status.name)
    }

    override suspend fun resetEquipmentChoices() {
        gymEquipmentDao.updateAllStatus(EquipmentStatus.Unknown.name)
    }
}


