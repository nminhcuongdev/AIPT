package com.example.aipt.feature.profile.data.repository

import com.example.aipt.feature.profile.data.local.BodyMetricSnapshotDao
import com.example.aipt.feature.profile.data.local.GymEquipmentDao
import com.example.aipt.feature.profile.data.local.GymEquipmentSeedData
import com.example.aipt.feature.profile.data.local.UserProfileDao
import com.example.aipt.feature.profile.data.local.toDomain
import com.example.aipt.feature.profile.data.local.toEntity
import com.example.aipt.feature.profile.domain.model.BodyMetricSnapshot
import com.example.aipt.feature.profile.domain.model.EquipmentStatus
import com.example.aipt.feature.profile.domain.model.GymEquipment
import com.example.aipt.feature.profile.domain.model.UserProfile
import com.example.aipt.feature.profile.domain.repository.ProfileRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val gymEquipmentDao: GymEquipmentDao,
    private val bodyMetricSnapshotDao: BodyMetricSnapshotDao,
) : ProfileRepository {
    override fun observeProfile(): Flow<UserProfile?> =
        userProfileDao.observeProfile().map { it?.toDomain() }

    override fun observeEquipment(): Flow<List<GymEquipment>> =
        gymEquipmentDao.observeEquipment().map { equipment -> equipment.map { it.toDomain() } }

    override fun observeBodyMetricSnapshots(): Flow<List<BodyMetricSnapshot>> =
        bodyMetricSnapshotDao.observeSnapshots().map { snapshots -> snapshots.map { it.toDomain() } }

    override suspend fun seedEquipmentIfNeeded() {
        gymEquipmentDao.seedEquipment(GymEquipmentSeedData.equipment)
    }

    override suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.saveProfile(profile.toEntity())
        bodyMetricSnapshotDao.saveSnapshot(profile.toBodyMetricSnapshot().toEntity())
    }

    override suspend fun deleteProfile() {
        userProfileDao.deleteProfile()
    }

    override suspend fun setEquipmentStatus(id: Int, status: EquipmentStatus) {
        gymEquipmentDao.updateStatus(id = id, status = status.name)
    }

    override suspend fun resetEquipmentChoices() {
        gymEquipmentDao.updateAllStatus(EquipmentStatus.Unknown.name)
    }

    private fun UserProfile.toBodyMetricSnapshot(): BodyMetricSnapshot {
        val now = System.currentTimeMillis()
        return BodyMetricSnapshot(
            dateKey = DateKeyFormat.format(Date(now)),
            capturedAt = now,
            weightKg = weightKg?.toDouble(),
            bodyFatPercent = bodyFatPercent,
            skeletalMuscleMassKg = skeletalMuscleMassKg,
        )
    }

    private companion object {
        val DateKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }
}


