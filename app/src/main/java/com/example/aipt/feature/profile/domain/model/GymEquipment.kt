package com.example.aipt.feature.profile.domain.model

data class GymEquipment(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val status: EquipmentStatus,
)

enum class EquipmentStatus {
    Unknown,
    Available,
    Unavailable,
}
