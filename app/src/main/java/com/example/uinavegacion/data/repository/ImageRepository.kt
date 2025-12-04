package com.example.uinavegacion.data.repository

import android.content.Context
import android.net.Uri
import com.example.uinavegacion.data.local.image.ImageDao
import com.example.uinavegacion.data.local.image.ImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Repositorio para manejar imágenes almacenadas como BLOB en la base de datos
 */
class ImageRepository(
    private val imageDao: ImageDao,
    private val context: Context
) {
    
    /**
     * Guarda una imagen desde un URI a la base de datos como BLOB
     * @param requestId ID de la solicitud asociada
     * @param imageUri URI de la imagen
     * @param fileName Nombre del archivo (opcional)
     * @return ID de la imagen guardada
     */
    suspend fun saveImageFromUri(
        requestId: Long,
        imageUri: String,
        fileName: String = ""
    ): Result<Long> {
        return try {
            withContext(Dispatchers.IO) {
                val uri = Uri.parse(imageUri)
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                
                if (inputStream == null) {
                    Result.failure(Exception("No se pudo abrir el archivo de imagen"))
                } else {
                    val byteArray = inputStream.use { stream ->
                        val buffer = ByteArrayOutputStream()
                        stream.copyTo(buffer)
                        buffer.toByteArray()
                    }
                    
                    // Determinar MIME type
                    val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                    
                    val imageEntity = ImageEntity(
                        requestId = requestId,
                        imageData = byteArray,
                        mimeType = mimeType,
                        fileName = fileName.ifEmpty { uri.lastPathSegment ?: "image.jpg" }
                    )
                    
                    val imageId = imageDao.insertImage(imageEntity)
                    Result.success(imageId)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Guarda múltiples imágenes desde URIs
     */
    suspend fun saveImagesFromUris(
        requestId: Long,
        imageUris: List<String>
    ): Result<List<Long>> {
        return try {
            val imageIds = mutableListOf<Long>()
            for (uri in imageUris) {
                val result = saveImageFromUri(requestId, uri)
                result.onSuccess { imageIds.add(it) }
                    .onFailure { return Result.failure(it) }
            }
            Result.success(imageIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene todas las imágenes de una solicitud
     */
    suspend fun getImagesByRequestId(requestId: Long): Result<List<ImageEntity>> {
        return try {
            val images = imageDao.getImagesByRequestIdSuspend(requestId)
            Result.success(images)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene una imagen por ID
     */
    suspend fun getImageById(imageId: Long): Result<ImageEntity> {
        return try {
            val image = imageDao.getImageById(imageId)
            if (image != null) {
                Result.success(image)
            } else {
                Result.failure(Exception("Imagen no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Elimina una imagen
     */
    suspend fun deleteImage(imageId: Long): Result<Unit> {
        return try {
            val image = imageDao.getImageById(imageId)
            if (image != null) {
                imageDao.deleteImage(image)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Imagen no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Elimina todas las imágenes de una solicitud
     */
    suspend fun deleteImagesByRequestId(requestId: Long): Result<Unit> {
        return try {
            imageDao.deleteImagesByRequestId(requestId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Convierte ImageEntity a URI temporal para mostrar en la UI
     * Nota: En producción, sería mejor guardar en almacenamiento interno y retornar File URI
     */
    suspend fun imageEntityToUri(imageEntity: ImageEntity): Result<android.net.Uri> {
        return try {
            withContext(Dispatchers.IO) {
                // Guardar temporalmente en cache y retornar URI
                val file = java.io.File(context.cacheDir, "temp_image_${imageEntity.id}.${getFileExtension(imageEntity.mimeType)}")
                file.writeBytes(imageEntity.imageData)
                Result.success(android.net.Uri.fromFile(file))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getFileExtension(mimeType: String): String {
        return when {
            mimeType.contains("jpeg") || mimeType.contains("jpg") -> "jpg"
            mimeType.contains("png") -> "png"
            mimeType.contains("gif") -> "gif"
            mimeType.contains("webp") -> "webp"
            else -> "jpg"
        }
    }
}

