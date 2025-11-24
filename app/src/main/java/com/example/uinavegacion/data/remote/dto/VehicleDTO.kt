package com.example.uinavegacion.data.remote.dto

/**
 * DTO para recibir datos de vehículo desde el microservicio
 */
data class VehicleDTO(
    val id: Long,
    val userId: Long,
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String,
    val color: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * DTO para enviar datos de vehículo al microservicio (crear/actualizar)
 */
data class VehicleRequestDTO(
    val userId: Long,
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String,
    val color: String,
    val isDefault: Boolean = false
)

