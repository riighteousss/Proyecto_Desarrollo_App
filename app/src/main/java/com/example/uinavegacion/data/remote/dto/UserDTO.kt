package com.example.uinavegacion.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para recibir datos de usuario desde el microservicio
 */
data class UserDTO(
    val id: Long,
    val email: String?,
    val name: String?,
    val phone: String?,
    val role: String?
)

/**
 * DTO para enviar datos de usuario al microservicio (registro y actualización)
 */
data class UserRequestDTO(
    val email: String,
    val password: String? = null, // Nullable para permitir actualizaciones sin cambiar contraseña
    val name: String,
    val phone: String,
    val role: String = "CLIENT"
)

/**
 * DTO para login
 */
data class LoginRequestDTO(
    val email: String,
    val password: String
)

