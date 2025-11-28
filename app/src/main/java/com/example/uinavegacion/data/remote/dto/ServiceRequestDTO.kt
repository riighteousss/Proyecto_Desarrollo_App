package com.example.uinavegacion.data.remote.dto

/**
 * DTO para recibir datos de solicitud desde el microservicio
 */
data class ServiceRequestDTO(
    val id: Long,
    val userId: Long,
    val serviceType: String,
    val vehicleInfo: String,
    val description: String?,
    val status: String,
    val createdAt: Long,
    val images: String?,
    val mechanicAssigned: String?,
    val estimatedCost: String?,
    val location: String?,
    val notes: String?
)

/**
 * DTO para enviar datos de solicitud al microservicio
 */
data class ServiceRequestRequestDTO(
    val userId: Long,
    val serviceType: String,
    val vehicleInfo: String,
    val description: String? = null,
    val images: String = "",
    val location: String = "",
    val notes: String = ""
)

