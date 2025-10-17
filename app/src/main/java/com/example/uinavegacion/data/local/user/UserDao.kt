package com.example.uinavegacion.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao{
    //insert un usuario - Abort si hay errores (pk duplicada)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    //buscar usuario por email (si no existe devuelve null)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    //contar el total de registros en la tabla users
    @Query("SELECT count(*) FROM users")
    suspend fun count(): Int

    //lista de usuarios ordenada ascendente por su id
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>

}