package com.example.uinavegacion.data.local.request

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * REQUESTHISTORYDAO - DAO PARA HISTORIAL DE SOLICITUDES
 * 
 * PUNTO CLAVE: Aquí están todas las operaciones de base de datos para el historial
 * - CRUD completo (Create, Read, Update, Delete)
 * - Consultas por usuario, estado, fecha
 * - Operaciones reactivas con Flow
 * 
 * OPERACIONES PRINCIPALES:
 * - insertRequest() → Guardar nueva solicitud
 * - getAllRequestsByUser() → Obtener historial del usuario
 * - updateRequestStatus() → Actualizar estado de solicitud
 * - deleteRequest() → Eliminar solicitud
 * - getRequestById() → Obtener solicitud específica
 */
@Dao
interface RequestHistoryDao {
    
    // Insertar nueva solicitud
    @Insert
    suspend fun insertRequest(request: RequestHistoryEntity): Long
    
    // Obtener todas las solicitudes de un usuario (reactivo)
    @Query("SELECT * FROM request_history WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllRequestsByUser(userId: Long): Flow<List<RequestHistoryEntity>>
    
    // Obtener solicitudes por estado
    @Query("SELECT * FROM request_history WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    fun getRequestsByStatus(userId: Long, status: String): Flow<List<RequestHistoryEntity>>
    
    // Obtener solicitud por ID
    @Query("SELECT * FROM request_history WHERE id = :requestId")
    suspend fun getRequestById(requestId: Long): RequestHistoryEntity?
    
    // Actualizar estado de solicitud
    @Query("UPDATE request_history SET status = :newStatus WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: Long, newStatus: String)
    
    // Actualizar solicitud completa
    @Update
    suspend fun updateRequest(request: RequestHistoryEntity)
    
    // Eliminar solicitud
    @Delete
    suspend fun deleteRequest(request: RequestHistoryEntity)
    
    // Obtener solicitudes recientes (últimas 10)
    @Query("SELECT * FROM request_history WHERE userId = :userId ORDER BY createdAt DESC LIMIT 10")
    fun getRecentRequests(userId: Long): Flow<List<RequestHistoryEntity>>
    
    // Contar solicitudes por estado
    @Query("SELECT COUNT(*) FROM request_history WHERE userId = :userId AND status = :status")
    suspend fun countRequestsByStatus(userId: Long, status: String): Int
    
    // Obtener estadísticas del usuario
    @Query("SELECT status, COUNT(*) as count FROM request_history WHERE userId = :userId GROUP BY status")
    suspend fun getUserStats(userId: Long): List<RequestStats>
}

/**
 * Clase de datos para estadísticas de solicitudes
 */
data class RequestStats(
    val status: String,
    val count: Int
)
