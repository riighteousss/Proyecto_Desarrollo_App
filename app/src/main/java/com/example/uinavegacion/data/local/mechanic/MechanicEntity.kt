package com.example.uinavegacion.data.local.mechanic

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mechanics")
data class MechanicEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Float,
    val distance: String,
    val phone: String,
    val isAvailable: Boolean,
    val address: String,
    val experience: Int, // años de experiencia
    val pricePerHour: Int, // precio por hora en CLP
    val profileImage: String? = null
)

