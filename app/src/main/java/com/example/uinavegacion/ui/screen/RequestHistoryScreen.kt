package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.data.local.request.RequestHistoryEntity
import java.text.SimpleDateFormat
import java.util.*

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
    requestHistoryDao: com.example.uinavegacion.data.local.request.RequestHistoryDao,
    onGoBack: () -> Unit
) {
    // Estados para filtros y datos
    var selectedFilter by remember { mutableStateOf("Todas") }
    val filterOptions = listOf("Todas", "Pendiente", "En Proceso", "Completado", "Cancelado")
    
    // Obtener solicitudes según el filtro
    val requests by if (selectedFilter == "Todas") {
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
                        text = "📋 Historial de Solicitudes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Revisa todas tus solicitudes de servicio",
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
                    text = "Filtrar por estado:",
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
                        text = if (selectedFilter == "Todas") "No tienes solicitudes" else "No hay solicitudes $selectedFilter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cuando hagas una solicitud de servicio, aparecerá aquí.",
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
                        onViewDetails = { /* TODO: Implementar vista de detalles */ },
                        onCancelRequest = { /* TODO: Implementar cancelación */ }
                    )
                }
            }
        }
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
                    "Pendiente" -> Color(0xFFFF9800)
                    "En Proceso" -> Color(0xFF2196F3)
                    "Completado" -> Color(0xFF4CAF50)
                    "Cancelado" -> Color(0xFFF44336)
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
            
            // Información adicional
            if (request.mechanicAssigned.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "👨‍🔧 Mecánico: ${request.mechanicAssigned}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (request.estimatedCost.isNotEmpty()) {
                Text(
                    text = "💰 Costo estimado: ${request.estimatedCost}",
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
                    Text("Ver Detalles")
                }
                
                if (request.status == "Pendiente" || request.status == "En Proceso") {
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
                        Text("Cancelar")
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
