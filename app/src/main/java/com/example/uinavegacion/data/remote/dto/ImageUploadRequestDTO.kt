package com.example.uinavegacion.data.remote.dto

/**
 * DTO para enviar una imagen al microservicio de imágenes
 */
data class ImageUploadRequestDTO(
    val userId: Long,
    val requestId: Long? = null, // Opcional: ID de la solicitud asociada
    val base64Data: String, // Imagen codificada en base64
    val fileName: String? = null, // Nombre del archivo (opcional)
    val mimeType: String? = null // Tipo MIME (opcional, por defecto image/jpeg)
)

