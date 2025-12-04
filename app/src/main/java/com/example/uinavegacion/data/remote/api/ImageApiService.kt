package com.example.uinavegacion.data.remote.api

import com.example.uinavegacion.data.remote.dto.ImageUploadRequestDTO
import com.example.uinavegacion.data.remote.dto.ImageUploadResponseDTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz Retrofit para el microservicio de imágenes
 * Define todos los endpoints disponibles en el servicio de imágenes
 */
interface ImageApiService {
    
    /**
     * Subir una imagen al microservicio (Base64)
     * @param request DTO con userId, requestId (opcional) y base64Data
     * @return Respuesta con el ID de la imagen guardada
     */
    @POST("api/images/base64")
    suspend fun uploadImage(@Body request: ImageUploadRequestDTO): Response<ImageUploadResponseDTO>
    
    /**
     * Obtener imagen por ID
     * @param id ID de la imagen
     * @return Respuesta con los datos de la imagen
     */
    @GET("api/images/{id}")
    suspend fun getImageById(@Path("id") id: Long): Response<ImageUploadResponseDTO>
    
    /**
     * Obtener todas las imágenes de una solicitud (usando entityType/entityId)
     * @param entityType Tipo de entidad (SERVICE_REQUEST, USER, etc.)
     * @param entityId ID de la entidad
     * @return Lista de imágenes asociadas a la entidad
     */
    @GET("api/images/entity/{entityType}/{entityId}")
    suspend fun getImagesByEntity(
        @Path("entityType") entityType: String,
        @Path("entityId") entityId: Long
    ): Response<List<ImageUploadResponseDTO>>
    
    /**
     * Obtener todas las imágenes de un usuario
     * @param userId ID del usuario
     * @return Lista de imágenes del usuario
     */
    @GET("api/images/user/{userId}")
    suspend fun getImagesByUserId(@Path("userId") userId: Long): Response<List<ImageUploadResponseDTO>>
    
    /**
     * Eliminar imagen por ID
     * @param id ID de la imagen
     * @return Respuesta vacía si se eliminó correctamente
     */
    @DELETE("api/images/{id}")
    suspend fun deleteImage(@Path("id") id: Long): Response<Void>
}

