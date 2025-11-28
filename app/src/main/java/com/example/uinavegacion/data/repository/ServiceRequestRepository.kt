package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.remote.RemoteDataSource
import com.example.uinavegacion.data.remote.dto.ServiceRequestDTO
import com.example.uinavegacion.data.remote.dto.ServiceRequestRequestDTO

/**
 * Repositorio de solicitudes que consume el microservicio REST
 * 
 * Todas las operaciones se realizan contra el microservicio de solicitudes
 */
class ServiceRequestRepository(
    private val remoteDataSource: RemoteDataSource
) {
    /**
     * Obtener todas las solicitudes de un usuario
     */
    suspend fun getRequestsByUserId(userId: Long): Result<List<ServiceRequestDTO>> {
        return remoteDataSource.getRequestsByUserId(userId)
    }
    
    /**
     * Crear nueva solicitud
     */
    suspend fun createRequest(request: ServiceRequestRequestDTO): Result<ServiceRequestDTO> {
        return remoteDataSource.createRequest(request)
    }
    
    /**
     * Actualizar solicitud completa
     */
    suspend fun updateRequest(id: Long, request: ServiceRequestRequestDTO): Result<ServiceRequestDTO> {
        return remoteDataSource.updateRequest(id, request)
    }
    
    /**
     * Actualizar estado de solicitud
     */
    suspend fun updateRequestStatus(id: Long, status: String): Result<ServiceRequestDTO> {
        return remoteDataSource.updateRequestStatus(id, status)
    }
    
    /**
     * Asignar mecánico a solicitud
     */
    suspend fun assignMechanic(id: Long, mechanicName: String): Result<ServiceRequestDTO> {
        return remoteDataSource.assignMechanic(id, mechanicName)
    }
    
    /**
     * Obtener solicitudes por estado
     */
    suspend fun getRequestsByStatus(status: String): Result<List<ServiceRequestDTO>> {
        return remoteDataSource.getRequestsByStatus(status)
    }
}

