package com.example.uinavegacion.data.local.vehicle

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles WHERE userId = :userId ORDER BY isDefault DESC, createdAt DESC")
    fun getVehiclesByUser(userId: Long): Flow<List<VehicleEntity>>
    
    @Query("SELECT * FROM vehicles WHERE userId = :userId AND isDefault = 1 LIMIT 1")
    suspend fun getDefaultVehicle(userId: Long): VehicleEntity?
    
    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    suspend fun getVehicleById(vehicleId: Long): VehicleEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: VehicleEntity): Long
    
    @Update
    suspend fun update(vehicle: VehicleEntity)
    
    @Delete
    suspend fun delete(vehicle: VehicleEntity)
    
    @Query("DELETE FROM vehicles WHERE id = :vehicleId")
    suspend fun deleteById(vehicleId: Long)
    
    @Query("UPDATE vehicles SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultForUser(userId: Long)
    
    @Query("UPDATE vehicles SET isDefault = 1 WHERE id = :vehicleId")
    suspend fun setAsDefault(vehicleId: Long)
    
    @Query("SELECT COUNT(*) FROM vehicles WHERE userId = :userId")
    suspend fun countByUser(userId: Long): Int
}
