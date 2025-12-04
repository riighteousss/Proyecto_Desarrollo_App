package com.example.uinavegacion.data.local.service

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.uinavegacion.data.local.user.UserEntity

/**
 * Entidad de solicitudes de servicio con Foreign Key a usuarios
 * Normalizada con relación explícita a la tabla users
 */
@Entity(
    tableName = "service_requests",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.SET_NULL // Si se elimina el usuario, userId se pone en null
        )
    ],
    indices = [Index("userId")] // Índice para mejorar rendimiento de consultas
)
data class ServiceRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long? = null, // Referencia al usuario (FK, nullable)
    val type: String,
    val description: String?,
    val address: String,
    val timestamp: Long,
    val urgent: Boolean = false,
    val needsTow: Boolean = false
)
