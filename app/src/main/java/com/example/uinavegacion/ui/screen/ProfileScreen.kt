package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.ui.viewmodel.ServiceViewModel
import com.example.uinavegacion.data.local.service.ServiceRequest

@Composable
fun ProfileScreen(
    serviceViewModel: ServiceViewModel? = null,
    userName: String? = null,
    isLoggedIn: Boolean = false,
    onGoSettings: () -> Unit = {},
    onGoHelp: () -> Unit = {},
    onToggleDarkMode: () -> Unit = {},
    onLogout: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    val requestsState = serviceViewModel?.requests?.collectAsState(initial = emptyList())
    val bg = MaterialTheme.colorScheme.surfaceVariant

    LaunchedEffect(Unit) {
        serviceViewModel?.loadAll()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Header del perfil
                ProfileHeader(
                    userName = userName,
                    isLoggedIn = isLoggedIn
                )
            }

            if (isLoggedIn) {
                // Todas las opciones en una sola sección sin títulos
                item {
                    ProfileSection(
                        title = "",
                        items = listOf(
                            ProfileItem(
                                title = "Configuraciones",
                                subtitle = "Ver solicitudes, vehículos y direcciones",
                                icon = Icons.Filled.Settings,
                                onClick = onGoSettings
                            ),
                            ProfileItem(
                                title = "Ayuda",
                                subtitle = "Centro de ayuda y soporte",
                                icon = Icons.Filled.Help,
                                onClick = onGoHelp
                            ),
                            ProfileItem(
                                title = "Modo Oscuro",
                                subtitle = if (isDarkMode) "Activado" else "Desactivado",
                                icon = Icons.Filled.DarkMode,
                                onClick = onToggleDarkMode,
                                trailingContent = {
                                    Switch(
                                        checked = isDarkMode,
                                        onCheckedChange = { onToggleDarkMode() }
                                    )
                                }
                            ),
                            ProfileItem(
                                title = "Cerrar Sesión",
                                subtitle = "Salir de tu cuenta",
                                icon = Icons.Filled.Logout,
                                onClick = onLogout,
                                textColor = Color(0xFFE53E3E)
                            )
                        )
                    )
                }

            } else {
                // Usuario no logueado
                item {
                    NotLoggedInProfile(onGoLogin = { /* TODO: Navegar a login */ })
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userName: String?,
    isLoggedIn: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Card(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Usuario",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
        Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isLoggedIn && userName != null) userName else "Usuario",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = if (isLoggedIn) "Mi Perfil" else "Inicia sesión para ver tu perfil",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    items: List<ProfileItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            items.forEach { item ->
                ProfileItemRow(item = item)
                if (item != items.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileItemRow(item: ProfileItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = item.textColor ?: MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = item.textColor ?: MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = item.textColor?.copy(alpha = 0.7f) ?: MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        item.trailingContent?.invoke()
        
        if (item.trailingContent == null) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Ir",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RequestHistoryItem(request: ServiceRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Assignment,
                contentDescription = "Solicitud",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = request.type,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = request.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (request.urgent) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE53E3E)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "URGENTE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotLoggedInProfile(onGoLogin: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Bloqueado",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
        Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Inicia sesión para acceder a tu perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
        Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Necesitas estar logueado para ver tu información personal y configuraciones",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onGoLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Login,
                    contentDescription = "Login",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Sesión")
            }
        }
    }
}

data class ProfileItem(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
    val textColor: Color? = null,
    val trailingContent: (@Composable () -> Unit)? = null
)
