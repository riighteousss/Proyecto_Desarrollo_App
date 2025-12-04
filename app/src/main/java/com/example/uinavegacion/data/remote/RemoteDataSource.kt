package com.example.uinavegacion.data.remote

import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.local.vehicle.VehicleEntity
import com.example.uinavegacion.data.remote.api.ImageApiService
import com.example.uinavegacion.data.remote.api.ServiceRequestApiService
import com.example.uinavegacion.data.remote.api.UserApiService
import com.example.uinavegacion.data.remote.api.VehicleApiService
import com.example.uinavegacion.data.remote.dto.LoginRequestDTO
import com.example.uinavegacion.data.remote.dto.ServiceRequestDTO
import com.example.uinavegacion.data.remote.dto.ImageUploadRequestDTO
import com.example.uinavegacion.data.remote.dto.ImageUploadResponseDTO
import com.example.uinavegacion.data.remote.dto.ServiceRequestRequestDTO
import com.example.uinavegacion.data.remote.dto.UserDTO
import com.example.uinavegacion.data.remote.dto.UserRequestDTO
import com.example.uinavegacion.data.remote.dto.VehicleDTO
import com.example.uinavegacion.data.remote.dto.VehicleRequestDTO

/**
 * Fuente de datos remota que encapsula las llamadas a la API
 * 
 * Esta clase actúa como intermediario entre los repositorios y Retrofit,
 * manejando errores y transformando DTOs a entidades del dominio
 */
class RemoteDataSource(
    private val userApiService: UserApiService,
    private val serviceRequestApiService: ServiceRequestApiService,
    private val vehicleApiService: VehicleApiService,
    private val imageApiService: ImageApiService
) {
    
    // ========== OPERACIONES DE USUARIO ==========
    
    /**
     * Login: Intenta usar endpoint de login, si no existe, obtiene usuario por email
     * 
     * NOTA: El microservicio actualmente no tiene endpoint de login.
     * Para una implementación completa, agrega un endpoint POST /api/users/login
     * en el backend que valide email y contraseña.
     */
    /**
     * Login: Autentica usuario contra el microservicio
     * ⚠️ SEGURIDAD: NO hay fallback - solo el endpoint de login puede autenticar
     */
    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            val loginResponse = userApiService.login(
                LoginRequestDTO(email = email, password = password)
            )
            
            if (loginResponse.isSuccessful && loginResponse.body() != null) {
                // ✅ Login exitoso - el servidor validó correctamente la contraseña
                Result.success(loginResponse.body()!!.toUserEntity())
            } else {
                // ❌ Login fallido - el servidor rechazó las credenciales
                val errorBody = loginResponse.errorBody()?.string()
                val errorMsg = when {
                    loginResponse.code() == 401 -> "Correo o contraseña incorrectos"
                    loginResponse.code() == 404 -> "Usuario no encontrado"
                    loginResponse.code() == 500 -> "Error del servidor: ${errorBody ?: "Error desconocido"}"
                    else -> "Error al iniciar sesión (Código: ${loginResponse.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor. Verifica que el microservicio esté corriendo en el puerto 8081"))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado. El servidor no respondió a tiempo."))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("No se puede encontrar el servidor. Verifica la configuración de red."))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message ?: "Error desconocido"}"))
        }
    }
    
    /**
     * Registro: Crea un nuevo usuario
     */
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        role: String = "CLIENT"
    ): Result<Long> {
        return try {
            val userRequest = UserRequestDTO(
                email = email,
                password = password,
                name = name,
                phone = phone,
                role = role
            )
            val response = userApiService.createUser(userRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.id)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = when {
                    response.code() == 400 -> {
                        // Intentar parsear el JSON de error si está disponible
                        val errorMessage = errorBody ?: "Datos inválidos. Verifica los campos del formulario"
                        "Datos inválidos: $errorMessage"
                    }
                    response.code() == 409 -> "El email ya está registrado. Usa otro correo o inicia sesión."
                    response.code() == 500 -> {
                        val errorMessage = errorBody ?: "Error del servidor. Intenta más tarde."
                        "Error del servidor: $errorMessage"
                    }
                    response.code() == 404 -> "El servicio no está disponible. Verifica que el microservicio esté corriendo."
                    else -> errorBody ?: "Error al registrar usuario (Código: ${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor.\n\nVerifica que:\n1. El microservicio de usuarios esté corriendo en el puerto 8081\n2. Laragon (MySQL) esté activo\n3. Estés usando el emulador Android (10.0.2.2) o la IP correcta si usas dispositivo físico"))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado.\n\nEl servidor no respondió a tiempo.\nVerifica que:\n1. El microservicio esté corriendo\n2. Tu conexión de red esté activa\n3. No haya firewall bloqueando la conexión"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("No se puede encontrar el servidor.\n\nVerifica que:\n1. Estés usando el emulador Android (10.0.2.2 para emulador)\n2. Si usas dispositivo físico, cambia la IP en RetrofitClient.kt a la IP de tu PC"))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message ?: "Error desconocido"}\n\nVerifica que el microservicio esté corriendo."))
        }
    }
    
    /**
     * Obtener usuario por email
     */
    suspend fun getUserByEmail(email: String): Result<UserEntity> {
        return try {
            // Retrofit codifica automáticamente los parámetros de URL
            val response = userApiService.getUserByEmail(email)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toUserEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = when {
                    response.code() == 404 -> "Usuario no encontrado con email: $email"
                    response.code() == 500 -> "Error del servidor: ${errorBody ?: "Error desconocido"}"
                    else -> "Error al obtener usuario (Código: ${response.code()}): ${errorBody ?: "Error desconocido"}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor. Verifica que el microservicio esté corriendo en el puerto 8081"))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado al obtener usuario"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("No se puede encontrar el servidor. Verifica la configuración de red"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener usuario por email: ${e.message ?: "Error desconocido"}"))
        }
    }
    
    /**
     * Obtener usuario por ID
     */
    suspend fun getUserById(id: Long): Result<UserEntity> {
        return try {
            val response = userApiService.getUserById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toUserEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = when {
                    response.code() == 404 -> "Usuario no encontrado con ID: $id"
                    response.code() == 500 -> "Error del servidor: ${errorBody ?: "Error desconocido"}"
                    else -> "Error al obtener usuario (Código: ${response.code()}): ${errorBody ?: "Error desconocido"}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor. Verifica que el microservicio esté corriendo en el puerto 8081"))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado al obtener usuario"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("No se puede encontrar el servidor. Verifica la configuración de red"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener usuario por ID: ${e.message ?: "Error desconocido"}"))
        }
    }
    
    /**
     * Solicitar recuperación de contraseña
     */
    suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val response = userApiService.forgotPassword(
                com.example.uinavegacion.data.remote.dto.ForgotPasswordRequestDTO(email = email)
            )
            if (response.isSuccessful && response.body() != null) {
                val message = response.body()!!["message"] ?: "Email de recuperación enviado"
                Result.success(message)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = when {
                    response.code() == 404 -> "Usuario no encontrado con ese email"
                    response.code() == 500 -> "Error del servidor: ${errorBody ?: "Error desconocido"}"
                    else -> errorBody ?: "Error al solicitar recuperación de contraseña (Código: ${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor. Verifica que el microservicio esté corriendo."))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message ?: "Error desconocido"}"))
        }
    }
    
    /**
     * Resetear contraseña con token
     */
    suspend fun resetPassword(email: String, token: String, newPassword: String): Result<String> {
        return try {
            val response = userApiService.resetPassword(
                com.example.uinavegacion.data.remote.dto.ResetPasswordRequestDTO(
                    email = email,
                    token = token,
                    newPassword = newPassword
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val message = response.body()!!["message"] ?: "Contraseña actualizada correctamente"
                Result.success(message)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = when {
                    response.code() == 400 -> "Token inválido o expirado"
                    response.code() == 404 -> "Usuario no encontrado"
                    response.code() == 500 -> "Error del servidor: ${errorBody ?: "Error desconocido"}"
                    else -> errorBody ?: "Error al resetear contraseña (Código: ${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor. Verifica que el microservicio esté corriendo."))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message ?: "Error desconocido"}"))
        }
    }
    
    /**
     * Actualizar usuario
     */
    suspend fun updateUser(
        id: Long,
        name: String,
        email: String,
        phone: String,
        password: String? = null,
        currentPassword: String? = null // Contraseña actual (requerida cuando se cambia la contraseña)
    ): Result<UserEntity> {
        return try {
            // Obtener el usuario actual para mantener su rol
            val currentUserResult = getUserById(id)
            val currentUser = currentUserResult.getOrNull()
            
            // Si no se proporciona contraseña nueva, enviar null (el microservicio no la actualizará)
            val userRequest = UserRequestDTO(
                email = email,
                password = password, // Enviar null si no se quiere cambiar (el microservicio lo ignorará)
                currentPassword = currentPassword, // Contraseña actual (requerida cuando se cambia la contraseña)
                name = name,
                phone = phone,
                role = currentUser?.role ?: "CLIENT" // Mantener el rol actual
            )
            val response = userApiService.updateUser(id, userRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toUserEntity())
            } else {
                val errorBody = response.errorBody()?.string()
                // Intentar parsear el mensaje de error del JSON
                val errorMessage = try {
                    if (errorBody != null && errorBody.contains("\"error\"")) {
                        // Extraer el mensaje del JSON: {"error": "mensaje"}
                        val errorMatch = Regex("\"error\"\\s*:\\s*\"([^\"]+)\"").find(errorBody)
                        errorMatch?.groupValues?.get(1) ?: errorBody
                    } else {
                        errorBody
                    }
                } catch (e: Exception) {
                    errorBody
                }
                
                val errorMsg = when {
                    response.code() == 401 -> errorMessage ?: "La contraseña actual es incorrecta"
                    response.code() == 400 -> "Datos inválidos: ${errorMessage ?: "Verifica los campos"}"
                    response.code() == 404 -> "Usuario no encontrado"
                    response.code() == 409 -> "El email ya está en uso"
                    else -> errorMessage ?: "Error al actualizar usuario (Código: ${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor. Verifica que el microservicio esté corriendo."))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
    
    // ========== OPERACIONES DE SOLICITUDES ==========
    
    /**
     * Obtener todas las solicitudes de un usuario
     */
    suspend fun getRequestsByUserId(userId: Long): Result<List<ServiceRequestDTO>> {
        return try {
            val response = serviceRequestApiService.getRequestsByUserId(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener solicitudes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Crear nueva solicitud
     */
    suspend fun createRequest(request: ServiceRequestRequestDTO): Result<ServiceRequestDTO> {
        return try {
            val response = serviceRequestApiService.createRequest(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear solicitud"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Actualizar solicitud completa
     */
    suspend fun updateRequest(id: Long, request: ServiceRequestRequestDTO): Result<ServiceRequestDTO> {
        return try {
            val response = serviceRequestApiService.updateRequest(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al actualizar solicitud"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Actualizar estado de solicitud
     */
    suspend fun updateRequestStatus(id: Long, status: String): Result<ServiceRequestDTO> {
        return try {
            val response = serviceRequestApiService.updateRequestStatus(id, status)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar estado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Asignar mecánico a solicitud
     */
    suspend fun assignMechanic(id: Long, mechanicName: String): Result<ServiceRequestDTO> {
        return try {
            val response = serviceRequestApiService.assignMechanic(id, mechanicName)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al asignar mecánico"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtener solicitudes por estado
     */
    suspend fun getRequestsByStatus(status: String): Result<List<ServiceRequestDTO>> {
        return try {
            val response = serviceRequestApiService.getRequestsByStatus(status)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener solicitudes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== OPERACIONES DE VEHÍCULOS ==========
    
    /**
     * Obtener todos los vehículos de un usuario
     */
    suspend fun getVehiclesByUserId(userId: Long): Result<List<VehicleDTO>> {
        return try {
            val response = vehicleApiService.getVehiclesByUserId(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener vehículos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtener vehículo por ID
     */
    suspend fun getVehicleById(id: Long): Result<VehicleDTO> {
        return try {
            val response = vehicleApiService.getVehicleById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Vehículo no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtener vehículo predeterminado de un usuario
     */
    suspend fun getDefaultVehicle(userId: Long): Result<VehicleDTO> {
        return try {
            val response = vehicleApiService.getDefaultVehicle(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("No se encontró vehículo predeterminado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Crear nuevo vehículo
     */
    suspend fun createVehicle(request: VehicleRequestDTO): Result<VehicleDTO> {
        return try {
            val response = vehicleApiService.createVehicle(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al crear vehículo"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Actualizar vehículo
     */
    suspend fun updateVehicle(id: Long, request: VehicleRequestDTO): Result<VehicleDTO> {
        return try {
            val response = vehicleApiService.updateVehicle(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al actualizar vehículo"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Eliminar vehículo
     */
    suspend fun deleteVehicle(id: Long): Result<Unit> {
        return try {
            val response = vehicleApiService.deleteVehicle(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar vehículo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Establecer vehículo como predeterminado
     */
    suspend fun setAsDefaultVehicle(id: Long, userId: Long): Result<VehicleDTO> {
        return try {
            val response = vehicleApiService.setAsDefault(id, userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al establecer vehículo como predeterminado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtener cantidad de vehículos de un usuario
     */
    suspend fun getVehicleCount(userId: Long): Result<Int> {
        return try {
            val response = vehicleApiService.getVehicleCount(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener cantidad de vehículos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== OPERACIONES DE IMÁGENES ==========
    
    /**
     * Subir una imagen al microservicio de imágenes
     * @param userId ID del usuario que sube la imagen
     * @param requestId ID de la solicitud asociada (opcional)
     * @param base64Data Imagen codificada en base64
     * @param fileName Nombre del archivo (opcional)
     * @param mimeType Tipo MIME (opcional)
     * @return ID de la imagen guardada en el servidor
     */
    suspend fun uploadImage(
        userId: Long,
        requestId: Long? = null,
        base64Data: String,
        fileName: String? = null,
        mimeType: String? = null
    ): Result<Long> {
        return try {
            val request = ImageUploadRequestDTO(
                userId = userId,
                requestId = requestId,
                base64Data = base64Data,
                fileName = fileName,
                mimeType = mimeType
            )
            val response = imageApiService.uploadImage(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.id)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMsg = when {
                    response.code() == 400 -> "Datos de imagen inválidos: ${errorBody ?: "Verifica el formato"}"
                    response.code() == 413 -> "Imagen demasiado grande. Reduce el tamaño de la imagen."
                    response.code() == 500 -> "Error del servidor al guardar imagen: ${errorBody ?: "Error desconocido"}"
                    else -> errorBody ?: "Error al subir imagen (Código: ${response.code()})"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: java.net.ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor de imágenes. Verifica que el microservicio esté corriendo en el puerto 8083"))
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado al subir imagen. La imagen puede ser muy grande."))
        } catch (e: Exception) {
            Result.failure(Exception("Error al subir imagen: ${e.message ?: "Error desconocido"}"))
        }
    }
    
    /**
     * Obtener imágenes de una solicitud
     * @param requestId ID de la solicitud
     * @return Lista de imágenes asociadas a la solicitud
     */
    suspend fun getImagesByRequestId(requestId: Long): Result<List<ImageUploadResponseDTO>> {
        return try {
            val response = imageApiService.getImagesByEntity("SERVICE_REQUEST", requestId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener imágenes de la solicitud"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtener imágenes de perfil de un usuario
     * @param userId ID del usuario
     * @return Lista de imágenes de perfil del usuario (entityType = "USER")
     */
    suspend fun getProfileImagesByUserId(userId: Long): Result<List<ImageUploadResponseDTO>> {
        return try {
            val response = imageApiService.getImagesByEntity("USER", userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener imágenes de perfil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtener una imagen por ID
     * @param imageId ID de la imagen
     * @return Datos de la imagen
     */
    suspend fun getImageById(imageId: Long): Result<ImageUploadResponseDTO> {
        return try {
            val response = imageApiService.getImageById(imageId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener imagen"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Eliminar imagen del servidor
     * @param imageId ID de la imagen a eliminar
     */
    suspend fun deleteImage(imageId: Long): Result<Unit> {
        return try {
            val response = imageApiService.deleteImage(imageId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar imagen"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== EXTENSIONES PARA CONVERSIÓN ==========
    
    /**
     * Convierte UserDTO a UserEntity
     */
    private fun UserDTO.toUserEntity(): UserEntity {
        return UserEntity(
            id = this.id,
            name = this.name ?: "",
            email = this.email ?: "",
            phone = this.phone ?: "",
            password = "", // La contraseña no viene del servidor por seguridad
            role = this.role ?: "CLIENT"
        )
    }
    
    /**
     * Convierte VehicleDTO a VehicleEntity
     */
    private fun VehicleDTO.toVehicleEntity(): VehicleEntity {
        return VehicleEntity(
            id = this.id,
            userId = this.userId,
            brand = this.brand,
            model = this.model,
            year = this.year,
            plate = this.plate,
            color = this.color,
            isDefault = this.isDefault ?: false,
            createdAt = this.createdAt
        )
    }
}

