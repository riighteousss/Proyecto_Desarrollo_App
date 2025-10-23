package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onGoRequests: () -> Unit = {},
    onGoVehicles: () -> Unit = {},
    onGoAddresses: () -> Unit = {},
    onGoHelp: () -> Unit = {},
    onToggleDarkMode: () -> Unit = {},
    onLogout: () -> Unit = {},
    onChangeRole: () -> Unit = {},
    onGoAdmin: () -> Unit = {},
    isDarkMode: Boolean = false,
    currentRole: String? = null
) {
    var isConfigExpanded by remember { mutableStateOf(true) }
    var isRoleExpanded by remember { mutableStateOf(true) }
    val bg = MaterialTheme.colorScheme.surfaceVariant

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
                Text(
                    text = "Configuraciones",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Sección de rol y cuenta
            item {
                RoleSettingsSection(
                    currentRole = currentRole,
                    onChangeRole = onChangeRole,
                    onGoAdmin = onGoAdmin,
                    isExpanded = isRoleExpanded,
                    onToggleExpanded = { isRoleExpanded = !isRoleExpanded }
                )
            }
            
            // Sección de configuraciones generales
            item {
                GeneralSettingsSection(
                    isConfigExpanded = isConfigExpanded,
                    onToggleConfig = { isConfigExpanded = !isConfigExpanded },
                    onGoRequests = onGoRequests,
                    onGoVehicles = onGoVehicles,
                    onGoAddresses = onGoAddresses,
                    onGoHelp = onGoHelp,
                    onToggleDarkMode = onToggleDarkMode,
                    onLogout = onLogout,
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    items: List<SettingsItem>
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
                SettingsItemRow(item = item)
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
private fun SettingsItemRow(item: SettingsItem) {
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
private fun AllSettingsSection(
    isConfigExpanded: Boolean,
    onToggleConfig: () -> Unit,
    onGoRequests: () -> Unit,
    onGoVehicles: () -> Unit,
    onGoAddresses: () -> Unit,
    onGoHelp: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onLogout: () -> Unit,
    isDarkMode: Boolean
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
            // Botón principal de Configuraciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configuraciones",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Configuraciones",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Historial, vehículos y direcciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onToggleConfig) {
                    Icon(
                        imageVector = if (isConfigExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isConfigExpanded) "Contraer" else "Expandir"
                    )
                }
            }
            
            // Opciones expandibles de Configuraciones
            if (isConfigExpanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                
                Column {
                    ConfigOptionItem(
                        title = "Historial de Solicitudes",
                        subtitle = "Gestiona tus solicitudes de servicio",
                        icon = Icons.Filled.Assignment,
                        onClick = onGoRequests
                    )
                    
                    ConfigOptionItem(
                        title = "Mis Vehículos",
                        subtitle = "Administra tus vehículos registrados",
                        icon = Icons.Filled.DirectionsCar,
                        onClick = onGoVehicles
                    )
                    
                    ConfigOptionItem(
                        title = "Mis Direcciones",
                        subtitle = "Gestiona tus direcciones guardadas",
                        icon = Icons.Filled.LocationOn,
                        onClick = onGoAddresses
                    )
                }
            }
            
            // Separador entre secciones
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            // Otras opciones
            SettingsOptionItem(
                title = "Ayuda",
                subtitle = "Centro de ayuda y soporte",
                icon = Icons.Filled.Help,
                onClick = onGoHelp
            )
            
            SettingsOptionItem(
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
            )
            
            SettingsOptionItem(
                title = "Cerrar Sesión",
                subtitle = "Salir de tu cuenta",
                icon = Icons.Filled.Logout,
                onClick = onLogout,
                textColor = Color(0xFFE53E3E)
            )
        }
    }
}

@Composable
private fun SettingsOptionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    textColor: Color? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor ?: MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = textColor ?: MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (trailingContent != null) {
            trailingContent()
        } else {
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
private fun ExpandableConfigSection(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onGoRequests: () -> Unit,
    onGoVehicles: () -> Unit,
    onGoAddresses: () -> Unit
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
            // Botón principal de Configuraciones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configuraciones",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Configuraciones",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Historial, vehículos y direcciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "Contraer" else "Expandir"
                    )
                }
            }
            
            // Opciones expandibles
            if (isExpanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                
                Column {
                    ConfigOptionItem(
                        title = "Historial de Solicitudes",
                        subtitle = "Gestiona tus solicitudes de servicio",
                        icon = Icons.Filled.Assignment,
                        onClick = onGoRequests
                    )
                    
                    ConfigOptionItem(
                        title = "Mis Vehículos",
                        subtitle = "Administra tus vehículos registrados",
                        icon = Icons.Filled.DirectionsCar,
                        onClick = onGoVehicles
                    )
                    
                    ConfigOptionItem(
                        title = "Mis Direcciones",
                        subtitle = "Gestiona tus direcciones guardadas",
                        icon = Icons.Filled.LocationOn,
                        onClick = onGoAddresses
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigOptionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = "Ir",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun RoleSettingsSection(
    currentRole: String?,
    onChangeRole: () -> Unit,
    onGoAdmin: () -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Rol de usuario",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Tipo de Cuenta",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Cambiar tipo de usuario",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Mostrar rol actual
                if (currentRole != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when (currentRole) {
                                "CLIENT" -> "Cliente"
                                "MECHANIC" -> "Mecánico"
                                "ADMIN" -> "Admin"
                                else -> "Cliente"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                IconButton(onClick = onToggleExpanded) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "Contraer" else "Expandir"
                    )
                }
            }
            
            // Opciones de rol (solo si está expandido)
            if (isExpanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                
                Column {
                    ConfigOptionItem(
                        title = "Cambiar tipo de cuenta",
                        subtitle = "Seleccionar entre Cliente o Mecánico",
                        icon = Icons.Filled.SwapHoriz,
                        onClick = onChangeRole
                    )
                    
                    // Solo mostrar acceso a Admin si el usuario actual es Admin
                    if (currentRole == "ADMIN") {
                        ConfigOptionItem(
                            title = "Panel de Administración",
                            subtitle = "Acceder a herramientas de administración",
                            icon = Icons.Filled.AdminPanelSettings,
                            onClick = onGoAdmin
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GeneralSettingsSection(
    isConfigExpanded: Boolean,
    onToggleConfig: () -> Unit,
    onGoRequests: () -> Unit,
    onGoVehicles: () -> Unit,
    onGoAddresses: () -> Unit,
    onGoHelp: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onLogout: () -> Unit,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configuraciones",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Configuraciones",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Historial, vehículos y direcciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onToggleConfig) {
                    Icon(
                        imageVector = if (isConfigExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isConfigExpanded) "Contraer" else "Expandir"
                    )
                }
            }
            
            // Opciones expandibles
            if (isConfigExpanded) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                
                Column {
                    ConfigOptionItem(
                        title = "Historial de Solicitudes",
                        subtitle = "Gestiona tus solicitudes de servicio",
                        icon = Icons.Filled.Assignment,
                        onClick = onGoRequests
                    )
                    
                    ConfigOptionItem(
                        title = "Mis Vehículos",
                        subtitle = "Administra tus vehículos registrados",
                        icon = Icons.Filled.DirectionsCar,
                        onClick = onGoVehicles
                    )
                    
                    ConfigOptionItem(
                        title = "Mis Direcciones",
                        subtitle = "Gestiona tus direcciones guardadas",
                        icon = Icons.Filled.LocationOn,
                        onClick = onGoAddresses
                    )
                    
                    ConfigOptionItem(
                        title = "Ayuda y Soporte",
                        subtitle = "Centro de ayuda y contacto",
                        icon = Icons.Filled.Help,
                        onClick = onGoHelp
                    )
                    
                    ConfigOptionItem(
                        title = "Modo Oscuro",
                        subtitle = if (isDarkMode) "Desactivar tema oscuro" else "Activar tema oscuro",
                        icon = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        onClick = onToggleDarkMode
                    )
                    
                    ConfigOptionItem(
                        title = "Cerrar Sesión",
                        subtitle = "Salir de tu cuenta",
                        icon = Icons.Filled.Logout,
                        onClick = onLogout
                    )
                }
            }
        }
    }
}

data class SettingsItem(
    val title: String,
    val subtitle: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
    val textColor: Color? = null,
    val trailingContent: (@Composable () -> Unit)? = null
)
