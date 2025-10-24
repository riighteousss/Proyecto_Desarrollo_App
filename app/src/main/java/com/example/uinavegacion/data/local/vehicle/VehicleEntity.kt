package com.example.uinavegacion.data.local.vehicle

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long, // Referencia al usuario propietario
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String,
    val color: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
