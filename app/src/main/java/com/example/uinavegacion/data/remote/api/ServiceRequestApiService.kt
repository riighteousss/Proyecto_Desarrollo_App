package com.example.uinavegacion.data.remote.api

import com.example.uinavegacion.data.remote.dto.ServiceRequestDTO
import com.example.uinavegacion.data.remote.dto.ServiceRequestRequestDTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz Retrofit para el microservicio de solicitudes
 * Define todos los endpoints disponibles en el servicio de solicitudes
 */
interface ServiceRequestApiService {
    
    /**
     * Obtener todas las solicitudes
     */
    @GET("api/requests")
    suspend fun getAllRequests(): Response<List<ServiceRequestDTO>>
    
    /**
     * Obtener solicitud por ID
     */
    @GET("api/requests/{id}")
    suspend fun getRequestById(@Path("id") id: Long): Response<ServiceRequestDTO>
    
    /**
     * Obtener solicitudes por usuario
     */
    @GET("api/requests/user/{userId}")
    suspend fun getRequestsByUserId(@Path("userId") userId: Long): Response<List<ServiceRequestDTO>>
    
    /**
     * Obtener solicitudes por mecánico
     */
    @GET("api/requests/mechanic/{mechanicName}")
    suspend fun getRequestsByMechanicName(@Path("mechanicName") mechanicName: String): Response<List<ServiceRequestDTO>>
    
    /**
     * Obtener solicitudes por estado
     */
    @GET("api/requests/status/{status}")
    suspend fun getRequestsByStatus(@Path("status") status: String): Response<List<ServiceRequestDTO>>
    
    /**
     * Crear nueva solicitud
     */
    @POST("api/requests")
    suspend fun createRequest(@Body request: ServiceRequestRequestDTO): Response<ServiceRequestDTO>
    
    /**
     * Actualizar solicitud completa
     */
    @PUT("api/requests/{id}")
    suspend fun updateRequest(
        @Path("id") id: Long,
        @Body request: ServiceRequestRequestDTO
    ): Response<ServiceRequestDTO>
    
    /**
     * Actualizar estado de solicitud
     */
    @PUT("api/requests/{id}/status")
    suspend fun updateRequestStatus(
        @Path("id") id: Long,
        @Query("status") status: String
    ): Response<ServiceRequestDTO>
    
    /**
     * Asignar mecánico a solicitud
     */
    @PUT("api/requests/{id}/assign")
    suspend fun assignMechanic(
        @Path("id") id: Long,
        @Query("mechanicName") mechanicName: String
    ): Response<ServiceRequestDTO>
    
    /**
     * Eliminar solicitud
     */
    @DELETE("api/requests/{id}")
    suspend fun deleteRequest(@Path("id") id: Long): Response<Void>
}

