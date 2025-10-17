package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity

//orquesta reglas de negocio para el login/ registro sobre el DAO en común
class UserRepository(
    private val userDao: UserDao //inyectando el DAO
){
    //login (email y pass en la tabla user)
    suspend fun login(email: String, password: String): Result<UserEntity>{
        val user = userDao.getByEmail(email)
        return if( user != null && user.password == password){
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales Inválidas"))
        }
    }

    //registro: valide correo duplicado
    suspend fun register(name:String, email: String, phone: String, password: String): Result<Long>{
        val exists = userDao.getByEmail(email) != null
        if(exists){
            return Result.failure(IllegalArgumentException("El correo ya está registrado"))
        }
        else{
            val id = userDao.insert(
                UserEntity(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password
                )
            )
            return Result.success(id)
        }
    }
}