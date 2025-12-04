package com.example.uinavegacion.data.local.address

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.uinavegacion.data.local.user.UserEntity

/**
 * Entidad de direcciones con Foreign Key a usuarios
 * Normalizada con relación explícita a la tabla users
 */
@Entity(
    tableName = "addresses",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Si se elimina el usuario, se eliminan sus direcciones
        )
    ],
    indices = [Index("userId")] // Índice para mejorar rendimiento de consultas
)
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long, // Referencia al usuario propietario (FK)
    val name: String, // Nombre descriptivo (ej: "Casa", "Trabajo")
    val address: String, // Dirección completa
    val city: String,
    val region: String,
    val postalCode: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
