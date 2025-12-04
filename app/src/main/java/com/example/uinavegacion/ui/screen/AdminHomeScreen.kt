@file:Suppress("DEPRECATION")
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AdminHomeScreen(
    onGoUsers: () -> Unit = {},
    onGoMechanics: () -> Unit = {},
    onGoReports: () -> Unit = {},
    onGoSettings: () -> Unit = {},
    onGoAnalytics: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var totalUsers by remember { mutableStateOf(1250) }
    var totalMechanics by remember { mutableStateOf(89) }
    var activeServices by remember { mutableStateOf(45) }
    var totalRevenue by remember { mutableStateOf(1250000) }

    val adminActions = listOf(
        AdminAction("Usuarios", Icons.Filled.People, Color(0xFF4CAF50)) { onGoUsers() },
        AdminAction("Mecánicos", Icons.Filled.Build, Color(0xFF2196F3)) { onGoMechanics() },
        AdminAction("Reportes", Icons.Filled.Assessment, Color(0xFFFF9800)) { onGoReports() },
        AdminAction("Analíticas", Icons.Filled.Analytics, Color(0xFF9C27B0)) { onGoAnalytics() },
        AdminAction("Configuración", Icons.Filled.Settings, Color(0xFF607D8B)) { onGoSettings() }
    )

    val recentActivities = listOf(
        AdminActivity(
            id = "1",
            type = "Nuevo usuario",
            description = "María González se registró como cliente",
            time = "Hace 5 minutos",
            icon = Icons.Filled.PersonAdd
        ),
        AdminActivity(
            id = "2",
            type = "Servicio completado",
            description = "Carlos Mendoza completó un servicio de $35,000",
            time = "Hace 15 minutos",
            icon = Icons.Filled.CheckCircle
        ),
        AdminActivity(
            id = "3",
            type = "Mecánico verificado",
            description = "Ana Rodríguez fue verificada como mecánico",
            time = "Hace 1 hora",
            icon = Icons.Filled.Verified
        ),
        AdminActivity(
            id = "4",
            type = "Reporte generado",
            description = "Reporte mensual de ganancias generado",
            time = "Hace 2 horas",
            icon = Icons.Filled.Assessment
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Header administrativo
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
                Column {
                    Text(
                        text = "Panel de Administración",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Gestiona la plataforma Fixsy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Filled.AdminPanelSettings,
                    contentDescription = "Admin",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estadísticas generales
            item {
                Text(
                    text = "Estadísticas de la plataforma",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        title = "Usuarios",
                        value = totalUsers.toString(),
                        icon = Icons.Filled.People,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Mecánicos",
                        value = totalMechanics.toString(),
                        icon = Icons.Filled.Build,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        title = "Servicios Activos",
                        value = activeServices.toString(),
                        icon = Icons.Filled.Assignment,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Ingresos Totales",
                        value = "$${totalRevenue}",
                        icon = Icons.Filled.AttachMoney,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Acciones administrativas
            item {
                Text(
                    text = "Herramientas de administración",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(adminActions) { action ->
                        AdminActionCard(
                            action = action,
                            modifier = Modifier.width(140.dp)
                        )
                    }
                }
            }

            // Actividad reciente
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Actividad reciente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { /* Ver todas las actividades */ }) {
                        Text("Ver todo")
                    }
                }
            }

            items(recentActivities) { activity ->
                AdminActivityCard(activity = activity)
            }

            // Espacio al final
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun AdminStatCard(
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AdminActionCard(
    action: AdminAction,
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
private fun AdminActivityCard(
    activity: AdminActivity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = activity.icon,
                contentDescription = activity.type,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.type,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = activity.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class AdminAction(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

data class AdminActivity(
    val id: String,
    val type: String,
    val description: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)


