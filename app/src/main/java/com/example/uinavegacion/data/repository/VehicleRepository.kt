package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.vehicle.VehicleEntity
import com.example.uinavegacion.data.remote.RemoteDataSource
import com.example.uinavegacion.data.remote.dto.VehicleDTO
import com.example.uinavegacion.data.remote.dto.VehicleRequestDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Repositorio de vehículos que consume el microservicio REST
 * 
 * Todas las operaciones se realizan contra el microservicio de vehículos
 * Mantiene compatibilidad con Flow para los ViewModels existentes
 */
class VehicleRepository(
    private val remoteDataSource: RemoteDataSource
) {
    // Cache local para mantener Flow reactivo
    private val _vehiclesCache = MutableStateFlow<Map<Long, List<VehicleEntity>>>(emptyMap())
    private val vehiclesCache: StateFlow<Map<Long, List<VehicleEntity>>> = _vehiclesCache.asStateFlow()
    
    /**
     * Obtener todos los vehículos de un usuario (Flow para compatibilidad)
     */
    fun getVehiclesByUser(userId: Long): Flow<List<VehicleEntity>> {
        // Crear un StateFlow que se actualiza cuando se cargan los datos
        val flow = MutableStateFlow<List<VehicleEntity>>(emptyList())
        
        // Cargar datos de la API y actualizar el flow
        CoroutineScope(Dispatchers.IO).launch {
            val result = remoteDataSource.getVehiclesByUserId(userId)
            result.onSuccess { vehicleDTOs ->
                val vehicles = vehicleDTOs.map { dtoToEntity(it) }
                flow.value = vehicles
                // Actualizar cache
                _vehiclesCache.value = _vehiclesCache.value.toMutableMap().apply {
                    put(userId, vehicles)
                }
            }
        }
        
        return flow
    }
    
    /**
     * Cargar vehículos desde la API y actualizar el cache
     */
    suspend fun loadVehiclesByUser(userId: Long): Result<List<VehicleEntity>> {
        return remoteDataSource.getVehiclesByUserId(userId).map { vehicleDTOs: List<VehicleDTO> ->
            val vehicles = vehicleDTOs.map { dto: VehicleDTO -> dtoToEntity(dto) }
            // Actualizar cache
            _vehiclesCache.value = _vehiclesCache.value.toMutableMap().apply {
                put(userId, vehicles)
            }
            vehicles
        }
    }
    
    /**
     * Obtener vehículo predeterminado
     */
    suspend fun getDefaultVehicle(userId: Long): VehicleEntity? {
        return remoteDataSource.getDefaultVehicle(userId)
            .getOrNull()
            ?.let { dtoToEntity(it) }
    }
    
    /**
     * Obtener vehículo por ID
     */
    suspend fun getVehicleById(vehicleId: Long): VehicleEntity? {
        return remoteDataSource.getVehicleById(vehicleId)
            .getOrNull()
            ?.let { dtoToEntity(it) }
    }
    
    /**
     * Crear nuevo vehículo
     */
    suspend fun insertVehicle(vehicle: VehicleEntity): Long {
        val request = VehicleRequestDTO(
            userId = vehicle.userId,
            brand = vehicle.brand,
            model = vehicle.model,
            year = vehicle.year,
            plate = vehicle.plate,
            color = vehicle.color,
            isDefault = vehicle.isDefault
        )
        
        return remoteDataSource.createVehicle(request)
            .map { dtoToEntity(it).id }
            .getOrElse { 0L }
    }
    
    /**
     * Convierte VehicleDTO a VehicleEntity
     */
    private fun dtoToEntity(dto: VehicleDTO): VehicleEntity {
        return VehicleEntity(
            id = dto.id,
            userId = dto.userId,
            brand = dto.brand,
            model = dto.model,
            year = dto.year,
            plate = dto.plate,
            color = dto.color,
            isDefault = dto.isDefault ?: false,
            createdAt = dto.createdAt
        )
    }
    
    /**
     * Actualizar vehículo
     */
    suspend fun updateVehicle(vehicle: VehicleEntity) {
        val request = VehicleRequestDTO(
            userId = vehicle.userId,
            brand = vehicle.brand,
            model = vehicle.model,
            year = vehicle.year,
            plate = vehicle.plate,
            color = vehicle.color,
            isDefault = vehicle.isDefault
        )
        
        remoteDataSource.updateVehicle(vehicle.id, request)
    }
    
    /**
     * Eliminar vehículo
     */
    suspend fun deleteVehicle(vehicleId: Long) {
        remoteDataSource.deleteVehicle(vehicleId)
    }
    
    /**
     * Establecer vehículo como predeterminado
     */
    suspend fun setAsDefaultVehicle(userId: Long, vehicleId: Long) {
        remoteDataSource.setAsDefaultVehicle(vehicleId, userId)
    }
    
    /**
     * Obtener cantidad de vehículos
     */
    suspend fun getVehicleCount(userId: Long): Int {
        return remoteDataSource.getVehicleCount(userId)
            .getOrElse { 0 }
    }
}
