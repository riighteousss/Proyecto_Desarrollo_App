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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.R
import com.example.uinavegacion.data.local.request.RequestHistoryEntity
import com.example.uinavegacion.data.local.request.RequestHistoryDao
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.net.Uri

/**
 * REQUESTHISTORYSCREEN - PANTALLA DE HISTORIAL DE SOLICITUDES
 * 
 * 🎯 PUNTO CLAVE: Esta pantalla muestra el historial completo de solicitudes del usuario
 * - Lista todas las solicitudes ordenadas por fecha
 * - Muestra estado, tipo de servicio, descripción
 * - Permite filtrar por estado (Pendiente, En Proceso, Completado)
 * - Incluye estadísticas del usuario
 * 
 * 📱 ELEMENTOS PRINCIPALES:
 * - Header con título y filtros
 * - Lista de solicitudes con cards
 * - Estadísticas resumidas
 * - Botones de acción (ver detalles, cancelar)
 * 
 * 🔄 FLUJO:
 * 1. Usuario accede desde Configuraciones
 * 2. Se cargan todas sus solicitudes
 * 3. Puede filtrar por estado
 * 4. Ve detalles de cada solicitud
 */
@Composable
fun RequestHistoryScreen(
    userId: Long,
    requestHistoryDao: RequestHistoryDao,
    onGoBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Estados para filtros y datos
    var selectedFilter by remember { mutableStateOf(stringResource(R.string.history_all)) }
    val filterOptions = listOf(
        stringResource(R.string.history_all),
        stringResource(R.string.history_pending),
        stringResource(R.string.history_in_progress),
        stringResource(R.string.history_completed),
        stringResource(R.string.history_cancelled)
    )
    
    // Obtener solicitudes según el filtro
    val requests by if (selectedFilter == stringResource(R.string.history_all)) {
        requestHistoryDao.getAllRequestsByUser(userId).collectAsStateWithLifecycle(initialValue = emptyList())
    } else {
        requestHistoryDao.getRequestsByStatus(userId, selectedFilter).collectAsStateWithLifecycle(initialValue = emptyList())
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con título y botón de regreso
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
        
        // Filtros
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
                
                // Chips de filtro
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
        
        // Lista de solicitudes
        if (requests.isEmpty()) {
            // Pantalla vacía
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
                        text = if (selectedFilter == stringResource(R.string.history_all)) 
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
                            scope.launch {
                                snackbarHostState.showSnackbar("Mostrando detalles de la solicitud")
                            }
                        },
                        onCancelRequest = { 
                            scope.launch {
                                try {
                                    requestHistoryDao.updateRequestStatus(request.id, stringResource(R.string.history_cancelled))
                                    snackbarHostState.showSnackbar("Solicitud cancelada correctamente")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error al cancelar la solicitud")
                                }
                            }
                        }
                    )
                }
            }
        }
        
        // Snackbar para mensajes informativos
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Card para mostrar cada solicitud en el historial
 */
@Composable
fun RequestHistoryCard(
    request: RequestHistoryEntity,
    onViewDetails: () -> Unit,
    onCancelRequest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con estado y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Estado con color
                val statusColor = when (request.status) {
                    stringResource(R.string.history_pending) -> Color(0xFFFF9800)
                    stringResource(R.string.history_in_progress) -> Color(0xFF2196F3)
                    stringResource(R.string.history_completed) -> Color(0xFF4CAF50)
                    stringResource(R.string.history_cancelled) -> Color(0xFFF44336)
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
            
            // Tipo de servicio
            Text(
                text = "🔧 ${request.serviceType}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Información del vehículo
            if (request.vehicleInfo.isNotEmpty()) {
                Text(
                    text = "🚗 ${request.vehicleInfo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            // Descripción
            Text(
                text = request.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Mostrar imágenes si existen
            if (request.images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "📷 Fotos del problema:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Parsear y mostrar imágenes
                val imageList = request.images.split(",").filter { it.isNotBlank() }
                if (imageList.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(imageList) { imagePath ->
                            try {
                                val uri = Uri.parse(imagePath.trim())
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Foto del problema",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } catch (e: Exception) {
                                // Si no se puede parsear como URI, mostrar placeholder
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
            
            // Información adicional
            if (request.mechanicAssigned.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "👨‍🔧 ${stringResource(R.string.history_mechanic, request.mechanicAssigned)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (request.estimatedCost.isNotEmpty()) {
                Text(
                    text = "💰 ${stringResource(R.string.history_cost, request.estimatedCost)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.history_view_details))
                }
                
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
 * Función para formatear fecha
 */
private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}
