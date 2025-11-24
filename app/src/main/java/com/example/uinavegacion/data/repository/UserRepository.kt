package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.remote.RemoteDataSource

/**
 * Repositorio de usuarios que consume el microservicio REST
 * 
 * Migrado de Room (SQLite local) a Retrofit (API REST)
 * Todas las operaciones ahora se realizan contra el microservicio de usuarios
 */
class UserRepository(
    private val remoteDataSource: RemoteDataSource
) {
    /**
     * Login: Autentica usuario contra el microservicio
     */
    suspend fun login(email: String, password: String): Result<UserEntity> {
        return remoteDataSource.login(email, password)
    }

    /**
     * Registro: Crea nuevo usuario en el microservicio
     */
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: String = "CLIENT"
    ): Result<Long> {
        return remoteDataSource.register(name, email, phone, password, role)
    }
    
    /**
     * Obtener usuario por email
     */
    suspend fun getUserByEmail(email: String): Result<UserEntity> {
        return remoteDataSource.getUserByEmail(email)
    }
    
    /**
     * Obtener usuario por ID
     */
    suspend fun getUserById(id: Long): Result<UserEntity> {
        return remoteDataSource.getUserById(id)
    }
    
    /**
     * Actualizar usuario
     */
    suspend fun updateUser(
        userId: Long,
        name: String,
        email: String,
        phone: String,
        password: String? = null
    ): Result<UserEntity> {
        return remoteDataSource.updateUser(userId, name, email, phone, password)
    }
}