package com.example.uinavegacion.data.local.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ServiceRequestDao {
    @Insert
    suspend fun insert(request: ServiceRequest): Long

    @Query("SELECT * FROM service_requests ORDER BY timestamp DESC")
    suspend fun getAll(): List<ServiceRequest>

    @Query("DELETE FROM service_requests")
    suspend fun clearAll()
}
