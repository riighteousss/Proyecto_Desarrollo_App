package com.example.uinavegacion.data.local.address

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long, // Referencia al usuario propietario
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
