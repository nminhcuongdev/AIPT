package com.example.aipt.feature.profile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Upsert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GymEquipmentDao {
    @Query("SELECT * FROM gym_equipment ORDER BY id ASC")
    fun observeEquipment(): Flow<List<GymEquipmentEntity>>

    @Query("SELECT COUNT(*) FROM gym_equipment")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(equipment: List<GymEquipmentEntity>)

    @Upsert
    suspend fun upsertAll(equipment: List<GymEquipmentEntity>)

    @Query("UPDATE gym_equipment SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)

    @Query("UPDATE gym_equipment SET status = :status")
    suspend fun updateAllStatus(status: String)
}


