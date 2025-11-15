package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.uinavegacion.R
import com.example.uinavegacion.data.local.request.RequestHistoryDao
import com.example.uinavegacion.data.local.request.RequestHistoryEntity
import kotlinx.coroutines.launch

@Composable
fun MechanicHomeScreen(
    requestHistoryDao: RequestHistoryDao? = null,
    mechanicId: Long? = null,
    onGoProfile: () -> Unit = {},
    onGoRequests: () -> Unit = {},
    onGoSchedule: () -> Unit = {},
    onGoEarnings: () -> Unit = {},
    onGoSettings: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var isOnline by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Obtener solicitudes pendientes si hay DAO disponible
    val pendingStatus = stringResource(R.string.history_pending)
    val completedStatus = stringResource(R.string.history_completed)
    
    val pendingRequestsList by if (requestHistoryDao != null && mechanicId != null) {
        requestHistoryDao.getRequestsByStatus(mechanicId, pendingStatus)
            .collectAsStateWithLifecycle(initialValue = emptyList())
    } else {
        remember { mutableStateOf(emptyList<RequestHistoryEntity>()) }
    }
    
    val completedRequestsList by if (requestHistoryDao != null && mechanicId != null) {
        requestHistoryDao.getRequestsByStatus(mechanicId, completedStatus)
            .collectAsStateWithLifecycle(initialValue = emptyList())
    } else {
        remember { mutableStateOf(emptyList<RequestHistoryEntity>()) }
    }
    
    var todayEarnings by remember { mutableStateOf(0) }
    var pendingRequests by remember { mutableStateOf(pendingRequestsList.size) }
    var completedJobs by remember { mutableStateOf(completedRequestsList.size) }
    
    // Actualizar contadores cuando cambian las listas
    LaunchedEffect(pendingRequestsList.size, completedRequestsList.size) {
        pendingRequests = pendingRequestsList.size
        completedJobs = completedRequestsList.size
    }

    val quickActions = listOf(
        QuickAction(stringResource(R.string.mechanic_requests), Icons.Filled.Assignment, Color(0xFF4CAF50)) { onGoRequests() },
        QuickAction(stringResource(R.string.mechanic_schedule), Icons.Filled.Schedule, Color(0xFF2196F3)) { onGoSchedule() },
        QuickAction(stringResource(R.string.mechanic_earnings_action), Icons.Filled.AttachMoney, Color(0xFFFF9800)) { onGoEarnings() },
        QuickAction(stringResource(R.string.mechanic_profile), Icons.Filled.Person, Color(0xFF9C27B0)) { onGoProfile() }
    )

    // Usar solicitudes reales de la base de datos
    val recentRequests = pendingRequestsList.take(3).map { request ->
        ServiceRequest(
            id = request.id.toString(),
            clientName = "Cliente #${request.userId}",
            service = request.serviceType,
            time = formatTime(request.createdAt),
            status = request.status,
            price = 0 // Se puede calcular desde estimatedCost si está disponible
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Header con estado online/offline
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isOnline) Color(0xFF4CAF50) else Color(0xFFF44336)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.mechanic_welcome),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isOnline) stringResource(R.string.mechanic_available) else stringResource(R.string.mechanic_unavailable),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Switch(
                    checked = isOnline,
                    onCheckedChange = { isOnline = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.White.copy(alpha = 0.3f),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estadísticas del día
            item {
                Text(
                    text = stringResource(R.string.mechanic_summary),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = stringResource(R.string.mechanic_earnings),
                        value = "$$todayEarnings",
                        icon = Icons.Filled.AttachMoney,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = stringResource(R.string.mechanic_pending),
                        value = pendingRequests.toString(),
                        icon = Icons.Filled.Assignment,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = stringResource(R.string.mechanic_completed),
                        value = completedJobs.toString(),
                        icon = Icons.Filled.CheckCircle,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Acciones rápidas
            item {
                Text(
                    text = stringResource(R.string.mechanic_quick_actions),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(quickActions) { action ->
                        QuickActionCard(
                            action = action,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
            }

            // Solicitudes recientes
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.mechanic_recent_requests),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onGoRequests) {
                        Text(stringResource(R.string.mechanic_view_all))
                    }
                }
            }

            items(recentRequests) { request ->
                ServiceRequestCard(
                    request = request,
                    onAccept = { 
                        scope.launch {
                            if (requestHistoryDao != null) {
                                try {
                                    requestHistoryDao.updateRequestStatus(request.id.toLong(), stringResource(R.string.history_in_progress))
                                    snackbarHostState.showSnackbar("Solicitud aceptada correctamente")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error al aceptar la solicitud")
                                }
                            }
                        }
                    },
                    onReject = { 
                        scope.launch {
                            if (requestHistoryDao != null) {
                                try {
                                    requestHistoryDao.updateRequestStatus(request.id.toLong(), stringResource(R.string.history_cancelled))
                                    snackbarHostState.showSnackbar("Solicitud rechazada")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error al rechazar la solicitud")
                                }
                            }
                        }
                    }
                )
            }

            // Espacio al final
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        // Snackbar para mensajes informativos
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// Función auxiliar para formatear tiempo
private fun formatTime(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return formatter.format(date)
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { action.onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = action.color.copy(alpha = 0.1f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.title,
                        tint = action.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ServiceRequestCard(
    request: ServiceRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val statusColor = when (request.status) {
        "Pendiente" -> Color(0xFFFF9800)
        "Confirmado" -> Color(0xFF4CAF50)
        "En progreso" -> Color(0xFF2196F3)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = request.clientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = request.service,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = statusColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = request.status,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Hora",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = request.time,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Text(
                    text = "$${request.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
                if (request.status == stringResource(R.string.history_pending)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.mechanic_reject))
                        }
                        Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.mechanic_accept))
                        }
                    }
                }
        }
    }
}

data class QuickAction(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

data class ServiceRequest(
    val id: String,
    val clientName: String,
    val service: String,
    val time: String,
    val status: String,
    val price: Int
)

