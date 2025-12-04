package com.example.uinavegacion.data.local.image

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con imágenes almacenadas como BLOB
 */
@Dao
interface ImageDao {
    
    /**
     * Insertar una nueva imagen
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity): Long
    
    /**
     * Insertar múltiples imágenes
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>): List<Long>
    
    /**
     * Obtener todas las imágenes de una solicitud
     */
    @Query("SELECT * FROM request_images WHERE request_id = :requestId ORDER BY created_at ASC")
    fun getImagesByRequestId(requestId: Long): Flow<List<ImageEntity>>
    
    /**
     * Obtener todas las imágenes de una solicitud (suspend)
     */
    @Query("SELECT * FROM request_images WHERE request_id = :requestId ORDER BY created_at ASC")
    suspend fun getImagesByRequestIdSuspend(requestId: Long): List<ImageEntity>
    
    /**
     * Obtener una imagen por ID
     */
    @Query("SELECT * FROM request_images WHERE id = :imageId")
    suspend fun getImageById(imageId: Long): ImageEntity?
    
    /**
     * Eliminar una imagen
     */
    @Delete
    suspend fun deleteImage(image: ImageEntity)
    
    /**
     * Eliminar todas las imágenes de una solicitud
     */
    @Query("DELETE FROM request_images WHERE request_id = :requestId")
    suspend fun deleteImagesByRequestId(requestId: Long)
    
    /**
     * Contar imágenes de una solicitud
     */
    @Query("SELECT COUNT(*) FROM request_images WHERE request_id = :requestId")
    suspend fun countImagesByRequestId(requestId: Long): Int
}

