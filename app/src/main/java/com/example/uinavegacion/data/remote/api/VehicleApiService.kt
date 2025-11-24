package com.example.uinavegacion.data.remote.api

import com.example.uinavegacion.data.remote.dto.VehicleDTO
import com.example.uinavegacion.data.remote.dto.VehicleRequestDTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz Retrofit para el microservicio de vehículos
 * Define todos los endpoints disponibles en el servicio de vehículos
 */
interface VehicleApiService {
    
    /**
     * Obtener todos los vehículos de un usuario
     */
    @GET("api/vehicles/user/{userId}")
    suspend fun getVehiclesByUserId(@Path("userId") userId: Long): Response<List<VehicleDTO>>
    
    /**
     * Obtener vehículo por ID
     */
    @GET("api/vehicles/{id}")
    suspend fun getVehicleById(@Path("id") id: Long): Response<VehicleDTO>
    
    /**
     * Obtener vehículo por defecto de un usuario
     */
    @GET("api/vehicles/user/{userId}/default")
    suspend fun getDefaultVehicle(@Path("userId") userId: Long): Response<VehicleDTO>
    
    /**
     * Crear nuevo vehículo
     */
    @POST("api/vehicles")
    suspend fun createVehicle(@Body vehicleRequest: VehicleRequestDTO): Response<VehicleDTO>
    
    /**
     * Actualizar vehículo
     */
    @PUT("api/vehicles/{id}")
    suspend fun updateVehicle(
        @Path("id") id: Long,
        @Body vehicleRequest: VehicleRequestDTO
    ): Response<VehicleDTO>
    
    /**
     * Eliminar vehículo
     */
    @DELETE("api/vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: Long): Response<Void>
    
    /**
     * Establecer vehículo como predeterminado
     */
    @PUT("api/vehicles/{id}/set-default")
    suspend fun setAsDefault(
        @Path("id") id: Long,
        @Query("userId") userId: Long
    ): Response<VehicleDTO>
    
    /**
     * Obtener cantidad de vehículos de un usuario
     */
    @GET("api/vehicles/user/{userId}/count")
    suspend fun getVehicleCount(@Path("userId") userId: Long): Response<Int>
}

