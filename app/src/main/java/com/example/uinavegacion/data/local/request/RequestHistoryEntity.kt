package com.example.uinavegacion.data.local.request

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * REQUESTHISTORYENTITY - ENTIDAD PARA HISTORIAL DE SOLICITUDES
 * 
 * 🎯 PUNTO CLAVE: Esta entidad guarda el historial de todas las solicitudes de servicio
 * - Almacena información completa de cada solicitud
 * - Incluye fecha, estado, descripción y detalles del servicio
 * - Permite consultar el historial del usuario
 * 
 * 📊 CAMPOS PRINCIPALES:
 * - id: Identificador único
 * - userId: ID del usuario que hizo la solicitud
 * - serviceType: Tipo de servicio (Emergencia, Mantenimiento, etc.)
 * - vehicleInfo: Información del vehículo
 * - description: Descripción del problema
 * - status: Estado actual (Pendiente, En Proceso, Completado)
 * - createdAt: Fecha de creación
 * - images: Lista de imágenes adjuntas
 */
@Entity(tableName = "request_history")
data class RequestHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val serviceType: String, // "Emergencia", "Mantenimiento", "Revisión", etc.
    val vehicleInfo: String, // Información del vehículo seleccionado
    val description: String, // Descripción del problema
    val status: String = "Pendiente", // "Pendiente", "En Proceso", "Completado", "Cancelado"
    val createdAt: Long = System.currentTimeMillis(), // Timestamp de creación
    val images: String = "", // JSON string con rutas de imágenes
    val mechanicAssigned: String = "", // Nombre del mecánico asignado (si aplica)
    val estimatedCost: String = "", // Costo estimado
    val location: String = "", // Ubicación del servicio
    val notes: String = "" // Notas adicionales
)
