package com.example.uinavegacion.data.local.vehicle

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.uinavegacion.data.local.user.UserEntity

/**
 * Entidad de vehículos con Foreign Key a usuarios
 * Normalizada con relación explícita a la tabla users
 */
@Entity(
    tableName = "vehicles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Si se elimina el usuario, se eliminan sus vehículos
        )
    ],
    indices = [Index("userId")] // Índice para mejorar rendimiento de consultas
)
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long, // Referencia al usuario propietario (FK)
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String,
    val color: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
