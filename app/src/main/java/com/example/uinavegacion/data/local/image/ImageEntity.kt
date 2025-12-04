package com.example.uinavegacion.data.local.image

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.example.uinavegacion.data.local.request.RequestHistoryEntity

/**
 * Entidad para almacenar imágenes como BLOB en la base de datos
 * Normalizada con Foreign Key a request_history
 */
@Entity(
    tableName = "request_images",
    foreignKeys = [
        ForeignKey(
            entity = RequestHistoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["request_id"],
            onDelete = ForeignKey.CASCADE // Si se elimina la solicitud, se eliminan sus imágenes
        )
    ],
    indices = [Index("request_id")] // Índice para mejorar rendimiento de consultas
)
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    @ColumnInfo(name = "request_id")
    val requestId: Long, // Referencia a la solicitud (FK)
    
    @ColumnInfo(name = "image_data", typeAffinity = ColumnInfo.BLOB)
    val imageData: ByteArray, // Imagen como BLOB
    
    @ColumnInfo(name = "mime_type")
    val mimeType: String = "image/jpeg", // Tipo MIME de la imagen
    
    @ColumnInfo(name = "file_name")
    val fileName: String = "", // Nombre original del archivo
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis() // Timestamp de creación
) {
    // Override equals y hashCode para comparar correctamente ByteArray
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageEntity

        if (id != other.id) return false
        if (requestId != other.requestId) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (mimeType != other.mimeType) return false
        if (fileName != other.fileName) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + requestId.hashCode()
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}

