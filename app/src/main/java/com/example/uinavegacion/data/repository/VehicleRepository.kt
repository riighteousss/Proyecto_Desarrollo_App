package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.vehicle.VehicleDao
import com.example.uinavegacion.data.local.vehicle.VehicleEntity
import kotlinx.coroutines.flow.Flow

class VehicleRepository(private val vehicleDao: VehicleDao) {
    
    fun getVehiclesByUser(userId: Long): Flow<List<VehicleEntity>> = 
        vehicleDao.getVehiclesByUser(userId)
    
    suspend fun getDefaultVehicle(userId: Long): VehicleEntity? = 
        vehicleDao.getDefaultVehicle(userId)
    
    suspend fun getVehicleById(vehicleId: Long): VehicleEntity? = 
        vehicleDao.getVehicleById(vehicleId)
    
    suspend fun insertVehicle(vehicle: VehicleEntity): Long = 
        vehicleDao.insert(vehicle)
    
    suspend fun updateVehicle(vehicle: VehicleEntity) = 
        vehicleDao.update(vehicle)
    
    suspend fun deleteVehicle(vehicleId: Long) = 
        vehicleDao.deleteById(vehicleId)
    
    suspend fun setAsDefaultVehicle(userId: Long, vehicleId: Long) {
        vehicleDao.clearDefaultForUser(userId)
        vehicleDao.setAsDefault(vehicleId)
    }
    
    suspend fun getVehicleCount(userId: Long): Int = 
        vehicleDao.countByUser(userId)
}
