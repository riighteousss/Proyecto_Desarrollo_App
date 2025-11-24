package com.example.uinavegacion.ui.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.R
import com.example.uinavegacion.data.remote.dto.ServiceRequestDTO
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * RequestHistoryScreen - Pantalla de historial de solicitudes
 * 
 * Muestra el historial completo de solicitudes del usuario:
 * - Lista todas las solicitudes ordenadas por fecha
 * - Muestra estado, tipo de servicio, descripción
 * - Permite filtrar por estado (Pendiente, En Proceso, Completado)
 * - Incluye estadísticas del usuario
 * 
 * Elementos principales:
 * - Header con título y filtros
 * - Lista de solicitudes con cards
 * - Estadísticas resumidas
 * - Botones de acción (ver detalles, cancelar)
 * 
 * Flujo:
 * 1. Usuario accede desde Configuraciones
 * 2. Se cargan todas sus solicitudes
 * 3. Puede filtrar por estado
 * 4. Ve detalles de cada solicitud
 */
@Composable
fun RequestHistoryScreen(
    userId: Long,
    serviceRequestRepository: com.example.uinavegacion.data.repository.ServiceRequestRepository,
    onGoBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Strings de recursos para los filtros
    val historyAll = stringResource(R.string.history_all)
    val historyPending = stringResource(R.string.history_pending)
    val historyInProgress = stringResource(R.string.history_in_progress)
    val historyCompleted = stringResource(R.string.history_completed)
    val historyCancelled = stringResource(R.string.history_cancelled)
    
    // Estado del filtro seleccionado
    var selectedFilter by remember { mutableStateOf(historyAll) }
    val filterOptions = listOf(
        historyAll,
        historyPending,
        historyInProgress,
        historyCompleted,
        historyCancelled
    )
    
    // Estados para las solicitudes cargadas del microservicio
    var requests by remember { mutableStateOf<List<com.example.uinavegacion.data.remote.dto.ServiceRequestDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Estado para controlar qué solicitud se está editando
    var selectedRequestForDetails by remember { mutableStateOf<com.example.uinavegacion.data.remote.dto.ServiceRequestDTO?>(null) }
    
    // Cargar solicitudes cuando cambia el usuario o el filtro
    LaunchedEffect(userId, selectedFilter) {
        isLoading = true
        errorMessage = null
        try {
            val result = if (selectedFilter == historyAll) {
                serviceRequestRepository.getRequestsByUserId(userId)
            } else {
                // Mapear el filtro al estado correspondiente
                val status = when (selectedFilter) {
                    historyPending -> "Pendiente"
                    historyInProgress -> "En Proceso"
                    historyCompleted -> "Completado"
                    historyCancelled -> "Cancelado"
                    else -> ""
                }
                serviceRequestRepository.getRequestsByStatus(status)
            }
            
            result.onSuccess { requestList ->
                // Filtrar por userId si se usa getRequestsByStatus (porque puede devolver todas)
                val filteredList = if (selectedFilter == historyAll) {
                    requestList
                } else {
                    requestList.filter { it.userId == userId }
                }
                requests = filteredList
                isLoading = false
            }.onFailure { exception ->
                errorMessage = exception.message ?: "Error al cargar solicitudes"
                isLoading = false
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Error al cargar solicitudes"
            isLoading = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header de la pantalla con título y botón de regreso
            Card(
            modifier = Modifier.fillMaxWidth(),
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
                        text = "📋 ${stringResource(R.string.history_title)}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.history_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
        
        // Sección de filtros para ordenar las solicitudes por estado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.history_filter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Chips seleccionables para filtrar por estado
                LazyColumn(
                    modifier = Modifier.height(120.dp)
                ) {
                    items(filterOptions.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { filter ->
                                FilterChip(
                                    onClick = { selectedFilter = filter },
                                    label = { Text(filter) },
                                    selected = selectedFilter == filter,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        
        // Indicador de carga mientras se obtienen las solicitudes
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        // Mensaje de error si falla la carga de solicitudes
        else if (errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        // Lista de solicitudes o pantalla vacía
        else if (requests.isEmpty()) {
            // Pantalla que se muestra cuando no hay solicitudes
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedFilter == historyAll) 
                            stringResource(R.string.history_empty) 
                        else 
                            stringResource(R.string.history_empty_filtered, selectedFilter),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.history_empty_message),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Lista de solicitudes del usuario
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(requests) { request ->
                    RequestHistoryCard(
                        request = request,
                        onViewDetails = { 
                            // Abrir diálogo de edición para esta solicitud
                            selectedRequestForDetails = request
                        },
                        onCancelRequest = { 
                            // Cancelar esta solicitud cambiando su estado
                            scope.launch {
                                try {
                                    val result = serviceRequestRepository.updateRequestStatus(request.id, "Cancelado")
                                    result.onSuccess {
                                        snackbarHostState.showSnackbar("Solicitud cancelada correctamente")
                                        // Recargar la lista de solicitudes
                                        val reloadResult = serviceRequestRepository.getRequestsByUserId(userId)
                                        reloadResult.onSuccess { requestList ->
                                            requests = requestList
                                        }
                                    }.onFailure {
                                        snackbarHostState.showSnackbar("Error al cancelar la solicitud: ${it.message}")
                                    }
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error al cancelar la solicitud")
                                }
                            }
                        },
                        serviceRequestRepository = serviceRequestRepository
                    )
                }
            }
        }
        }
        
        // Snackbar para mostrar mensajes informativos al usuario
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // Diálogo de edición que se muestra al hacer clic en "Editar"
        selectedRequestForDetails?.let { request ->
            RequestDetailsDialog(
                request = request,
                onDismiss = { selectedRequestForDetails = null },
                onSave = { updatedRequest ->
                    // Recargar la lista de solicitudes después de guardar cambios
                    scope.launch {
                        isLoading = true
                        val reloadResult = serviceRequestRepository.getRequestsByUserId(userId)
                        reloadResult.onSuccess { requestList ->
                            requests = requestList
                            isLoading = false
                            snackbarHostState.showSnackbar("Solicitud actualizada correctamente")
                        }.onFailure {
                            isLoading = false
                            snackbarHostState.showSnackbar("Error al recargar solicitudes: ${it.message}")
                        }
                    }
                },
                serviceRequestRepository = serviceRequestRepository
            )
        }
    }
}

/**
 * RequestHistoryCard - Card que muestra una solicitud individual en el historial
 * 
 * Muestra la información básica de la solicitud:
 * - Estado con color indicativo
 * - Fecha de creación
 * - Tipo de servicio y vehículo
 * - Descripción
 * - Imágenes adjuntas (si existen)
 * - Mecánico asignado y costo (si aplica)
 * - Botones para editar o cancelar
 */
@Composable
fun RequestHistoryCard(
    request: ServiceRequestDTO,
    onViewDetails: () -> Unit,
    onCancelRequest: () -> Unit,
    serviceRequestRepository: com.example.uinavegacion.data.repository.ServiceRequestRepository? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Estado y fecha de la solicitud
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge de estado con color según el estado actual
                val statusColor = when (request.status.lowercase()) {
                    "pendiente" -> Color(0xFFFF9800)
                    "en proceso" -> Color(0xFF2196F3)
                    "completado" -> Color(0xFF4CAF50)
                    "cancelado" -> Color(0xFFF44336)
                    else -> MaterialTheme.colorScheme.primary
                }
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = request.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                
                // Fecha
                Text(
                    text = formatDate(request.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Información principal: Tipo de servicio
            Text(
                text = "🔧 ${request.serviceType}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Información del vehículo relacionado
            if (request.vehicleInfo.isNotEmpty()) {
                Text(
                    text = "🚗 ${request.vehicleInfo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Descripción del problema reportado
            if (!request.description.isNullOrBlank()) {
                Text(
                    text = request.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Galería de imágenes adjuntas a la solicitud
            if (!request.images.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "📷 Fotos del problema:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Lista horizontal de miniaturas de las imágenes
                val imageList = (request.images ?: "").split(",").filter { it.isNotBlank() }
                if (imageList.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(imageList) { imagePath ->
                            val uri = tryParseUri(imagePath.trim())
                            if (uri != null) {
                                // Mostrar imagen si se puede parsear el URI
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Foto del problema",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Placeholder si la imagen no se puede cargar
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Image,
                                        contentDescription = "Imagen no disponible",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Información adicional: Mecánico y costo
            if (!request.mechanicAssigned.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "👨‍🔧 Mecánico asignado: ${request.mechanicAssigned}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (!request.estimatedCost.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💰 Costo estimado: ${request.estimatedCost}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botones de acción: Editar y Cancelar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón: Abrir diálogo de edición
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar")
                }
                
                // Botón: Cancelar solicitud (solo visible si está pendiente o en proceso)
                if (request.status == stringResource(R.string.history_pending) || 
                    request.status == stringResource(R.string.history_in_progress)) {
                    OutlinedButton(
                        onClick = onCancelRequest,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFF44336)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.history_cancel))
                    }
                }
            }
        }
    }
}

/**
 * RequestDetailsDialog - Diálogo editable para modificar una solicitud existente
 * 
 * Permite editar los siguientes campos de la solicitud:
 * - Tipo de servicio (dropdown)
 * - Vehículo (dropdown)
 * - Descripción (texto)
 * - Ubicación (texto)
 * - Notas (texto)
 * 
 * Campos de solo lectura:
 * - Estado (muestra el estado actual)
 * - Fecha de creación
 * - Imágenes (solo visualización)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsDialog(
    request: ServiceRequestDTO,
    onDismiss: () -> Unit,
    onSave: (ServiceRequestDTO) -> Unit,
    serviceRequestRepository: com.example.uinavegacion.data.repository.ServiceRequestRepository
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Estados editables del formulario
    var serviceType by remember { mutableStateOf(request.serviceType) }
    var vehicleInfo by remember { mutableStateOf(request.vehicleInfo) }
    var description by remember { mutableStateOf(request.description ?: "") }
    var location by remember { mutableStateOf(request.location ?: "") }
    var notes by remember { mutableStateOf(request.notes ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    
    // Opciones disponibles para los dropdowns
    val serviceTypes = listOf("Mantenimiento", "Reparación", "Diagnóstico", "Limpieza", "Inspección", "Otros")
    val vehicleTypes = listOf("Automóvil", "Motocicleta", "Camión", "Bus", "Otro")
    
    // Parsear las imágenes de la solicitud para mostrarlas
    val imageList = remember(request.images) {
        (request.images ?: "").split(",").filter { it.isNotBlank() }
    }
    
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "Editar Solicitud",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sección: Estado de la solicitud (solo lectura)
                val statusColor = when (request.status.lowercase()) {
                    "pendiente" -> Color(0xFFFF9800)
                    "en proceso" -> Color(0xFF2196F3)
                    "completado" -> Color(0xFF4CAF50)
                    "cancelado" -> Color(0xFFF44336)
                    else -> MaterialTheme.colorScheme.primary
                }
                
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Estado: ${request.status}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                
                // Fecha (solo lectura)
                Text(
                    text = "Fecha: ${formatDate(request.createdAt)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Divider()
                
                // Sección: Tipo de servicio (editable con dropdown)
                Text(
                    text = "Tipo de Servicio",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                var expandedService by remember { mutableStateOf(false) }
                // Dropdown para seleccionar tipo de servicio
                ExposedDropdownMenuBox(
                    expanded = expandedService,
                    onExpandedChange = { expandedService = !expandedService }
                ) {
                    OutlinedTextField(
                        value = serviceType,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedService) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedService,
                        onDismissRequest = { expandedService = false }
                    ) {
                        serviceTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    serviceType = type
                                    expandedService = false
                                }
                            )
                        }
                    }
                }
                
                // Sección: Vehículo (editable con dropdown)
                Text(
                    text = "Vehículo",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                var expandedVehicle by remember { mutableStateOf(false) }
                // Dropdown para seleccionar tipo de vehículo
                ExposedDropdownMenuBox(
                    expanded = expandedVehicle,
                    onExpandedChange = { expandedVehicle = !expandedVehicle }
                ) {
                    OutlinedTextField(
                        value = vehicleInfo,
                        onValueChange = { },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVehicle) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedVehicle,
                        onDismissRequest = { expandedVehicle = false }
                    ) {
                        vehicleTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    vehicleInfo = type
                                    expandedVehicle = false
                                }
                            )
                        }
                    }
                }
                
                // Sección: Descripción del problema (editable)
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    placeholder = { Text("Describe el problema...") }
                )
                
                // Sección: Ubicación del servicio (editable)
                Text(
                    text = "Ubicación",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ubicación del servicio...") }
                )
                
                // Sección: Notas adicionales (editable)
                Text(
                    text = "Notas",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    placeholder = { Text("Notas adicionales...") }
                )
                
                // Sección: Imágenes adjuntas (solo lectura)
                if (imageList.isNotEmpty()) {
                    Divider()
                    Text(
                        text = "Fotos del Problema",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(imageList) { imagePath ->
                            val uri = tryParseUri(imagePath.trim())
                            if (uri != null) {
                                Card(
                                    modifier = Modifier.size(120.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Foto del problema",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            // Botones de acción del diálogo
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón: Cancelar y cerrar el diálogo sin guardar
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                // Botón: Guardar los cambios en el microservicio
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                // Crear DTO con los datos actualizados
                                val updateRequest = com.example.uinavegacion.data.remote.dto.ServiceRequestRequestDTO(
                                    userId = request.userId,
                                    serviceType = serviceType,
                                    vehicleInfo = vehicleInfo,
                                    description = description.ifEmpty { null },
                                    images = request.images ?: "",
                                    location = location,
                                    notes = notes
                                )
                                // Llamar al repositorio para actualizar en el microservicio
                                val result = serviceRequestRepository.updateRequest(request.id, updateRequest)
                                result.onSuccess {
                                    // Actualizar la lista primero
                                    onSave(it)
                                    // Esperar un momento para que se actualice la lista
                                    kotlinx.coroutines.delay(300)
                                    // Cerrar el diálogo después de actualizar
                                    onDismiss()
                                    // El snackbar se mostrará desde la pantalla principal
                                }.onFailure {
                                    // Mostrar error sin cerrar el diálogo
                                    snackbarHostState.showSnackbar("Error al actualizar: ${it.message ?: "Error desconocido"}")
                                }
                            } catch (e: Exception) {
                                // Mostrar error sin cerrar el diálogo
                                snackbarHostState.showSnackbar("Error: ${e.message ?: "Error desconocido"}")
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        // Mostrar indicador de carga mientras se guarda
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            }
        }
    )
    
    // Snackbar para mostrar errores dentro del diálogo
    LaunchedEffect(Unit) {
        snackbarHostState.currentSnackbarData
    }
}

/**
 * Formatea un timestamp a una cadena de texto legible
 * Formato: dd/MM/yyyy HH:mm
 */
private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}

/**
 * Intenta parsear una cadena como URI de forma segura
 * Retorna null si no se puede parsear
 */
private fun tryParseUri(path: String): android.net.Uri? {
    return try {
        android.net.Uri.parse(path)
    } catch (e: Exception) {
        null
    }
}
