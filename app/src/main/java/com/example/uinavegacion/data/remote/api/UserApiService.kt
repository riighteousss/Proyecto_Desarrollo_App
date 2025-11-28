package com.example.uinavegacion.data.remote.api

import com.example.uinavegacion.data.remote.dto.LoginRequestDTO
import com.example.uinavegacion.data.remote.dto.UserDTO
import com.example.uinavegacion.data.remote.dto.UserRequestDTO
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz Retrofit para el microservicio de usuarios
 * Define todos los endpoints disponibles en el servicio de usuarios
 */
interface UserApiService {
    
    /**
     * Obtener todos los usuarios
     */
    @GET("api/users")
    suspend fun getAllUsers(): Response<List<UserDTO>>
    
    /**
     * Obtener usuario por ID
     */
    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserDTO>
    
    /**
     * Obtener usuario por email
     */
    @GET("api/users/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<UserDTO>
    
    /**
     * Crear nuevo usuario (registro)
     */
    @POST("api/users")
    suspend fun createUser(@Body userRequest: UserRequestDTO): Response<UserDTO>
    
    /**
     * Actualizar usuario
     */
    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body userRequest: UserRequestDTO
    ): Response<UserDTO>
    
    /**
     * Eliminar usuario
     */
    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Void>
    
    /**
     * Login - Verificar credenciales
     * Nota: Como el microservicio no tiene endpoint de login específico,
     * usamos getUserByEmail y validamos la contraseña localmente
     */
    @POST("api/users/login")
    suspend fun login(@Body loginRequest: LoginRequestDTO): Response<UserDTO>
}

