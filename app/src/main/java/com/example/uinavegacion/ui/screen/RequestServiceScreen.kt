package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.R
import com.example.uinavegacion.ui.viewmodel.RequestFormViewModel
import kotlinx.coroutines.launch

@Composable
fun RequestServiceScreen(
    onGoBack: () -> Unit,
    onRequestService: (String, String, String, List<String>) -> Unit = { _, _, _, _ -> },
    onGoCamera: () -> Unit = {},
    onSaveToHistory: (String, String, String, List<String>) -> Unit = { _, _, _, _ -> },
    requestFormViewModel: RequestFormViewModel
) {
    // Estados del ViewModel (persistentes entre navegaciones)
    val selectedService by requestFormViewModel.selectedService.collectAsStateWithLifecycle()
    val selectedVehicle by requestFormViewModel.selectedVehicle.collectAsStateWithLifecycle()
    val description by requestFormViewModel.description.collectAsStateWithLifecycle()
    val selectedImages by requestFormViewModel.selectedImages.collectAsStateWithLifecycle()
    val isFormValid by requestFormViewModel.isFormValid.collectAsStateWithLifecycle()
    
    var showSuccessDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val serviceTypes = listOf(
        "Mantenimiento" to Icons.Filled.Build,
        "Reparación" to Icons.Filled.Build,
        "Diagnóstico" to Icons.Filled.Search,
        "Limpieza" to Icons.Filled.CleaningServices,
        "Inspección" to Icons.Filled.Visibility,
        "Otros" to Icons.Filled.More
    )

    val vehicleTypes = listOf(
        "Automóvil",
        "Motocicleta", 
        "Camión",
        "Bus",
        "Otro"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onGoBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Column {
                    Text(
                        text = "🔧 ${stringResource(R.string.request_service_title)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.request_service_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.request_service_type),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(serviceTypes.size) { index ->
                        val (service, icon) = serviceTypes[index]
                        ServiceTypeChip(
                            title = service,
                            icon = icon,
                            isSelected = selectedService == service,
                            onClick = { requestFormViewModel.updateService(service) }
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.request_vehicle_type),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vehicleTypes.size) { index ->
                        val vehicle = vehicleTypes[index]
                        FilterChip(
                            onClick = { requestFormViewModel.updateVehicle(vehicle) },
                            label = { Text(vehicle) },
                            selected = selectedVehicle == vehicle
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.request_description),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { requestFormViewModel.updateDescription(it) },
                    placeholder = { Text(stringResource(R.string.request_description_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }

            item {
                Text(
                    text = stringResource(R.string.request_photos),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón para tomar foto
                    OutlinedButton(
                        onClick = { 
                            onGoCamera()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Cámara",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.request_camera))
                    }
                    
                    // Botón para galería
                    OutlinedButton(
                        onClick = { 
                            scope.launch {
                                snackbarHostState.showSnackbar("Funcionalidad de galería próximamente")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = "Galería",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.request_gallery))
                    }
                }
            }

            // Mostrar imágenes seleccionadas
            if (selectedImages.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.request_images_selected, selectedImages.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Text(
                    text = "Ubicación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = "Mi ubicación actual",
                        onValueChange = { },
                        modifier = Modifier.weight(1f),
                        readOnly = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { 
                            // TODO: Implementar obtención de ubicación GPS
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "GPS",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (isFormValid) {
                            // Guardar en el historial
                            onSaveToHistory(selectedService, selectedVehicle, description, selectedImages)
                            // Procesar solicitud
                            onRequestService(selectedService, selectedVehicle, description, selectedImages)
                            // Limpiar formulario después de enviar
                            requestFormViewModel.clearForm()
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.request_submit), fontWeight = FontWeight.Bold)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "ℹ️ ${stringResource(R.string.request_info_title)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.request_info_details),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        // Snackbar para mensajes informativos
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Dialog de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("✅ ${stringResource(R.string.request_success_title)}") },
            text = { 
                Text(stringResource(R.string.request_success_message)) 
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccessDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar(stringResource(R.string.message_request_created))
                        }
                        onGoBack()
                    }
                ) {
                    Text(stringResource(R.string.request_success_ok))
                }
            }
        )
    }
}

@Composable
private fun ServiceTypeChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                           else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
