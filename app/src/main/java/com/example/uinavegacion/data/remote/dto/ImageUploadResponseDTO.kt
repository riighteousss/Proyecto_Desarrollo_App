package com.example.uinavegacion.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para recibir respuesta del microservicio de imágenes
 * Compatible con ImageDTO del backend (campos coinciden exactamente)
 */
data class ImageUploadResponseDTO(
    val id: Long,
    @SerializedName("userId")
    val userId: Long? = null,
    @SerializedName("fileName")
    val fileName: String? = null,
    @SerializedName("originalName")
    val originalName: String? = null,
    @SerializedName("contentType")
    val contentType: String? = null,
    @SerializedName("fileSize")
    val fileSize: Long? = null,
    @SerializedName("entityType")
    val entityType: String? = null, // "USER", "VEHICLE", "SERVICE_REQUEST"
    @SerializedName("entityId")
    val entityId: Long? = null,
    @SerializedName("downloadUrl")
    val downloadUrl: String? = null,
    @SerializedName("base64Data")
    val base64Data: String? = null, // Datos de la imagen en base64 (opcional)
    @SerializedName("createdAt")
    val createdAt: String? = null // LocalDateTime del backend como String (Gson lo maneja)
) {
    /**
     * Obtiene requestId desde entityId si es una imagen de solicitud
     */
    val requestId: Long? 
        get() = if (entityType == "SERVICE_REQUEST") entityId else null
    
    /**
     * Obtiene mimeType desde contentType (alias para compatibilidad)
     */
    val mimeType: String? 
        get() = contentType
    
    /**
     * Convierte createdAt a timestamp Long (si viene como String de LocalDateTime)
     */
    val createdAtTimestamp: Long?
        get() = createdAt?.let {
            try {
                // Si viene como LocalDateTime desde el backend, Gson puede convertirlo
                // Por ahora retornamos null y se maneja en el código que lo use
                null
            } catch (e: Exception) {
                null
            }
        }
}

