package com.example.uinavegacion.data.local.service

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_requests")
data class ServiceRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long? = null,
    val type: String,
    val description: String?,
    val address: String,
    val timestamp: Long,
    val urgent: Boolean = false,
    val needsTow: Boolean = false
)
