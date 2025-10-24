package com.example.uinavegacion.ui.screen

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.ui.viewmodel.RequestFormViewModel
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * CAMERASCREEN - PANTALLA DE CÁMARA
 * 
 * 🎯 PUNTO CLAVE: Esta pantalla permite tomar fotos del problema del vehículo
 * - Solicita permisos de cámara automáticamente
 * - Abre la cámara del dispositivo para tomar fotos
 * - Muestra preview de la foto tomada
 * - Permite aceptar o rechazar la foto
 * 
 * 📱 FUNCIONALIDADES:
 * - Permisos de cámara automáticos
 * - Captura de fotos con cámara nativa
 * - Preview de imagen capturada
 * - Botones de aceptar/rechazar
 * 
 * 🔄 FLUJO:
 * 1. Usuario toca "Tomar Foto"
 * 2. Se solicita permiso de cámara
 * 3. Se abre la cámara del dispositivo
 * 4. Usuario toma la foto
 * 5. Se muestra preview
 * 6. Usuario acepta o rechaza
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onBack: () -> Unit,
    onPhotoTaken: (Uri) -> Unit,
    requestFormViewModel: RequestFormViewModel? = null
) {
    val context = LocalContext.current
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isImageCaptured by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Estado del permiso de cámara
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        isLoading = false
        if (success) {
            isImageCaptured = true
            errorMessage = null
        } else {
            errorMessage = "Error al capturar la foto. Intenta nuevamente."
        }
    }
    
    // Función para crear archivo temporal con mejor manejo de errores
    fun createImageFile(context: Context): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.getExternalFilesDir("Pictures")
            if (storageDir != null && storageDir.exists()) {
                File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            } else {
                // Fallback a directorio interno si no existe el externo
                val fallbackDir = File(context.filesDir, "Pictures").apply { 
                    if (!exists()) mkdirs() 
                }
                File.createTempFile("JPEG_${timeStamp}_", ".jpg", fallbackDir)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    // Función para tomar foto con mejor manejo de errores
    fun takePicture() {
        isLoading = true
        errorMessage = null
        
        val photoFile = createImageFile(context)
        if (photoFile != null) {
            try {
                val photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    photoFile
                )
                capturedImageUri = photoUri
                cameraLauncher.launch(photoUri)
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Error al preparar la cámara: ${e.message}"
            }
        } else {
            isLoading = false
            errorMessage = "Error al crear archivo de imagen. Verifica el almacenamiento."
        }
    }
    
    // Solicitar permisos automáticamente
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header con botón de regreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tomar Foto del Problema",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Mostrar mensaje de error si existe
        errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { errorMessage = null }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Contenido principal
        if (!cameraPermissionState.status.isGranted) {
            // Pantalla de permisos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Permiso de Cámara Requerido",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Necesitamos acceso a la cámara para tomar fotos del problema de tu vehículo.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() }
                    ) {
                        Text("Solicitar Permiso")
                    }
                }
            }
        } else if (!isImageCaptured) {
            // Pantalla para tomar foto
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "¿Listo para tomar la foto?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Toca el botón para abrir la cámara y capturar una foto del problema de tu vehículo.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { takePicture() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isLoading) "Preparando..." else "Tomar Foto")
                    }
                }
            }
        } else {
            // Pantalla de preview de la foto
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Preview de la Foto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Preview de la imagen
                    capturedImageUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Foto del problema",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                isImageCaptured = false
                                capturedImageUri = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rechazar")
                        }
                        
                        Button(
                            onClick = {
                                capturedImageUri?.let { uri ->
                                    // Guardar en el ViewModel si está disponible
                                    requestFormViewModel?.addImage(uri.toString())
                                    onPhotoTaken(uri)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Aceptar")
                        }
                    }
                }
            }
        }
    }
}
