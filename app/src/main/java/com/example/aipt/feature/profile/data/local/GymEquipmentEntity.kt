package com.example.aipt.feature.profile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aipt.feature.profile.domain.model.EquipmentStatus
import com.example.aipt.feature.profile.domain.model.GymEquipment

@Entity(tableName = "gym_equipment")
data class GymEquipmentEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val status: String = EquipmentStatus.Unknown.name,
)

fun GymEquipmentEntity.toDomain(): GymEquipment = GymEquipment(
    id = id,
    name = name,
    imageUrl = imageUrl,
    status = EquipmentStatus.valueOf(status),
)
