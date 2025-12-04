package com.example.uinavegacion.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Utilidades para manejo de imágenes
 */
object ImageUtils {
    
    /**
     * Tamaño máximo de imagen en píxeles (para compresión)
     * Reducido a 800px para asegurar archivos más pequeños
     */
    private const val MAX_IMAGE_SIZE = 800 // 800x800 píxeles máximo
    
    /**
     * Calidad de compresión JPEG (0-100)
     * Reducida a 75 para archivos más pequeños
     */
    private const val JPEG_QUALITY = 75
    
    /**
     * Tamaño máximo de archivo en bytes (1MB)
     * Reducido para evitar timeouts
     */
    private const val MAX_FILE_SIZE_BYTES = 1024 * 1024 // 1MB
    
    /**
     * Convierte un URI de imagen a base64 con compresión automática
     * @param context Contexto de la aplicación
     * @param imageUri URI de la imagen
     * @return String base64 de la imagen comprimida, o null si hay error
     */
    fun uriToBase64(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                null
            } else {
                // Leer la imagen original con opciones de muestreo para reducir memoria
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()
                
                // Calcular el factor de muestreo para reducir el tamaño
                val scaleFactor = calculateInSampleSize(options, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
                
                // Decodificar la imagen con el factor de muestreo
                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = scaleFactor
                }
                val inputStream2 = context.contentResolver.openInputStream(imageUri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream2, null, decodeOptions)
                inputStream2?.close()
                
                if (originalBitmap == null) {
                    return null
                }
                
                // Comprimir la imagen a tamaño final
                val compressedBitmap = compressBitmap(originalBitmap)
                originalBitmap.recycle()
                
                // Convertir a ByteArray con compresión progresiva
                var byteArray = compressToByteArray(compressedBitmap, JPEG_QUALITY)
                compressedBitmap.recycle()
                
                // Si aún es muy grande, comprimir más agresivamente
                if (byteArray.size > MAX_FILE_SIZE_BYTES) {
                    val inputStream3 = context.contentResolver.openInputStream(imageUri)
                    val bitmap3 = BitmapFactory.decodeStream(inputStream3, null, decodeOptions)
                    inputStream3?.close()
                    
                    if (bitmap3 != null) {
                        val result = compressMoreAggressively(bitmap3)
                        bitmap3.recycle()
                        return result
                    }
                }
                
                Base64.encodeToString(byteArray, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Calcula el factor de muestreo para reducir el tamaño de la imagen antes de decodificarla
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    /**
     * Comprime un bitmap a ByteArray con compresión progresiva si es necesario
     */
    private fun compressToByteArray(bitmap: Bitmap, quality: Int): ByteArray {
        var currentQuality = quality
        val outputStream = ByteArrayOutputStream()
        
        // Intentar comprimir con la calidad inicial
        bitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, outputStream)
        var byteArray = outputStream.toByteArray()
        outputStream.reset()
        
        // Si es muy grande, reducir calidad progresivamente
        while (byteArray.size > MAX_FILE_SIZE_BYTES && currentQuality > 50) {
            currentQuality -= 10
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, currentQuality, outputStream)
            byteArray = outputStream.toByteArray()
        }
        
        outputStream.close()
        return byteArray
    }
    
    /**
     * Comprime un Bitmap si excede el tamaño máximo
     */
    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        // Si la imagen es más pequeña que el máximo, retornar sin cambios
        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return bitmap
        }
        
        // Calcular nuevo tamaño manteniendo aspect ratio
        val scale = if (width > height) {
            MAX_IMAGE_SIZE.toFloat() / width
        } else {
            MAX_IMAGE_SIZE.toFloat() / height
        }
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Compresión más agresiva para imágenes muy grandes
     */
    private fun compressMoreAggressively(bitmap: Bitmap): String? {
        return try {
            // Reducir a 512x512 máximo
            val smallerSize = 512
            val width = bitmap.width
            val height = bitmap.height
            
            val scale = if (width > height) {
                smallerSize.toFloat() / width
            } else {
                smallerSize.toFloat() / height
            }
            
            val newWidth = (width * scale).toInt().coerceAtLeast(1)
            val newHeight = (height * scale).toInt().coerceAtLeast(1)
            
            val scaledBitmap = Bitmap.createScaledBitmap(
                bitmap,
                newWidth,
                newHeight,
                true
            )
            
            // Comprimir con calidad baja (60) y verificar tamaño
            var quality = 60
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            var byteArray = outputStream.toByteArray()
            
            // Si aún es muy grande, reducir más la calidad
            while (byteArray.size > MAX_FILE_SIZE_BYTES && quality > 40) {
                quality -= 10
                outputStream.reset()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                byteArray = outputStream.toByteArray()
            }
            
            scaledBitmap.recycle()
            outputStream.close()
            
            Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Convierte un ByteArray a base64
     * @param byteArray Array de bytes de la imagen
     * @return String base64 de la imagen
     */
    fun byteArrayToBase64(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
    
    /**
     * Obtiene el MIME type de un URI
     * @param context Contexto de la aplicación
     * @param imageUri URI de la imagen
     * @return MIME type o "image/jpeg" por defecto
     */
    fun getMimeType(context: Context, imageUri: Uri): String {
        return context.contentResolver.getType(imageUri) ?: "image/jpeg"
    }
    
    /**
     * Obtiene el nombre del archivo de un URI
     * @param imageUri URI de la imagen
     * @return Nombre del archivo o "image.jpg" por defecto
     */
    fun getFileName(imageUri: Uri): String {
        return imageUri.lastPathSegment ?: "image.jpg"
    }
}

