package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.service.ServiceRequest
import com.example.uinavegacion.data.local.service.ServiceRequestDao

class ServiceRepository(private val dao: ServiceRequestDao) {
    suspend fun insert(request: ServiceRequest): Long = dao.insert(request)
    suspend fun getAll(): List<ServiceRequest> = dao.getAll()
    suspend fun clearAll() = dao.clearAll()
}
