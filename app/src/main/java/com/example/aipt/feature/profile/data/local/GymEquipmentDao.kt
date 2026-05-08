package com.example.aipt.feature.profile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GymEquipmentDao {
    @Query("SELECT * FROM gym_equipment ORDER BY id ASC")
    fun observeEquipment(): Flow<List<GymEquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(equipment: List<GymEquipmentEntity>)

    @Query("UPDATE gym_equipment SET name = :name, imageUrl = :imageUrl WHERE id = :id")
    suspend fun updateSeedMetadata(id: Int, name: String, imageUrl: String)

    @Query("UPDATE gym_equipment SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("UPDATE gym_equipment SET status = :status")
    suspend fun updateAllStatus(status: String)
}

