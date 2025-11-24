package com.example.uinavegacion.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.ui.viewmodel.ProfileViewModel
import com.example.uinavegacion.ui.components.SimpleMapView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.uinavegacion.R
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * HOMESCREEN - PANTALLA PRINCIPAL DE LA APLICACIÓN
 * 
 * PUNTO CLAVE: Esta es la PANTALLA PRINCIPAL de la aplicación
 * - Muestra servicios disponibles (Emergencia, Mantenimiento, etc.)
 * - Estadísticas de la app (mecánicos, servicios, calificación)
 * - Botones de login/registro si no está logueado
 * - Botón de perfil si está logueado
 * 
 * ELEMENTOS PRINCIPALES:
 * - Header con información del usuario y foto de perfil
 * - Barra de búsqueda
 * - Sección de mecánicos cercanos
 * - Botones de acción principales
 * - Sección de servicios rápidos
 * 
 * La pantalla se adapta según el estado de autenticación del usuario.
 */
@Composable
fun HomeScreen(
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoRequests: () -> Unit = {},
    userName: String? = null,
    isLoggedIn: Boolean = false,
    onGoSettings: () -> Unit = {},
    onGoEmergency: () -> Unit = {},
    onGoRequestService: () -> Unit = {},
    onGoFavorites: () -> Unit = {},
    onGoAppointments: () -> Unit = {},
    onGoMap: () -> Unit = {}
) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    val userProfile by profileViewModel.userProfile.collectAsState()
    
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(context)
    }
    var searchQuery by remember { mutableStateOf("") }
    val bg = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header con banner oscuro y avatar
            HeaderBanner(
                userName = userName, 
                isLoggedIn = isLoggedIn,
                profileImageUri = userProfile.profileImageUri,
                onGoLogin = onGoLogin,
                onGoProfile = onGoSettings
            )
            
            // Barra de búsqueda fija
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { /* Implementar búsqueda */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Contenido principal deslizable
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mapa pequeño
                item {
                    Column {
                        Text(
                            text = "📍 Ubicación",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SimpleMapView(
                            onMapClick = onGoMap
                        )
                    }
                }
                
                // Botones de acción en grid 2x2
                item {
                    ActionButtonsGrid(
                        onGoRequests = onGoRequests,
                        onGoSettings = onGoSettings,
                        onGoEmergency = onGoEmergency,
                        onGoRequestService = onGoRequestService,
                        onGoFavorites = onGoFavorites,
                        onGoAppointments = onGoAppointments,
                        isLoggedIn = isLoggedIn
                    )
                }
                
                
                // Sección de estadísticas
                item {
                    StatisticsSection()
                }
                
                // Espacio adicional al final
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // Barra de navegación inferior fija
            BottomNavigationBar(
                onGoRequests = onGoRequests,
                onGoSettings = onGoSettings
            )
        }
    }
}

@Composable
private fun HeaderBanner(
    userName: String? = null, 
    isLoggedIn: Boolean = false,
    profileImageUri: String? = null,
    onGoLogin: () -> Unit = {},
    onGoProfile: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo de Fixsy
            Text(
                text = "🔧",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(end = 12.dp)
            )
            // Avatar circular clickeable
            Card(
                modifier = Modifier
                    .size(50.dp)
                    .clickable { 
                        if (isLoggedIn) onGoProfile() else onGoLogin() 
                    },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoggedIn && profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = if (isLoggedIn) "Perfil" else "Iniciar sesión",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = if (isLoggedIn && userName != null) "¡Hola $userName!" else "¡Hola!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                
                Text(
                    text = if (isLoggedIn) "Encuentra el mecánico perfecto" else "Inicia sesión para acceder a todos los servicios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun NearbyMechanicsSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mecánicos Cercanos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { /* Ver todos */ }) {
                Text("Ver todos")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Mapa mejorado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Simulación del mapa con mejor diseño
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFE3F2FD),
                                    Color(0xFFBBDEFB)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Ubicación",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = "Mapa de Ubicaciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2)
                        )
                    Text(
                            text = "Mecánicos cercanos marcados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Lista de mecánicos cercanos vacía por defecto
        val mechanics = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
        
        if (mechanics.value.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOff,
                        contentDescription = "Sin mecánicos",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No hay mecánicos cercanos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Intenta ampliar el rango de búsqueda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            mechanics.value.forEach { (name, status) ->
                MechanicItem(name = name, status = status)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun MechanicItem(
    name: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Ubicación",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "4.8",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun ActionButtonsGrid(
    onGoRequests: () -> Unit = {},
    onGoSettings: () -> Unit = {},
    onGoEmergency: () -> Unit = {},
    onGoRequestService: () -> Unit = {},
    onGoFavorites: () -> Unit = {},
    onGoAppointments: () -> Unit = {},
    isLoggedIn: Boolean = false
) {
    Column {
        Text(
            text = "Servicios Rápidos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fila superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    title = "Emergencia",
                    subtitle = "Atención 24/7",
                    icon = Icons.Filled.LocalShipping,
                    backgroundColor = Color(0xFFE53E3E),
                    onClick = { onGoEmergency() },
                    modifier = Modifier.weight(1f),
                    requiresAuth = false // Las emergencias no requieren autenticación
                )
                ActionButton(
                    title = "Solicitar",
                    subtitle = "Servicio",
                    icon = Icons.Filled.Person,
                    backgroundColor = Color(0xFF1A1A1A),
                    onClick = { onGoRequestService() },
                    modifier = Modifier.weight(1f),
                    requiresAuth = true,
                    isLoggedIn = isLoggedIn
                )
            }
            
            // Fila inferior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    title = "Favoritos",
                    subtitle = "Guardados",
                    icon = Icons.Filled.Star,
                    backgroundColor = Color(0xFFFFD700),
                    onClick = { onGoFavorites() },
                    modifier = Modifier.weight(1f),
                    requiresAuth = true,
                    isLoggedIn = isLoggedIn
                )
                ActionButton(
                    title = "Agenda",
                    subtitle = "Citas",
                    icon = Icons.Filled.CalendarToday,
                    backgroundColor = Color(0xFF4CAF50),
                    onClick = { onGoAppointments() },
                    modifier = Modifier.weight(1f),
                    requiresAuth = true,
                    isLoggedIn = isLoggedIn
                )
            }
            
        }
    }
}

@Composable
private fun ActionButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    requiresAuth: Boolean = false,
    isLoggedIn: Boolean = false
) {
    val isDisabled = requiresAuth && !isLoggedIn
    val displayColor = if (isDisabled) backgroundColor.copy(alpha = 0.5f) else backgroundColor
    
    Card(
        modifier = modifier
            .height(90.dp)
            .animateContentSize() // Animación de contenido
            .clickable { 
                if (!isDisabled) {
                    onClick()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = displayColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isDisabled) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Bloqueado",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isDisabled) "Inicia sesión" else title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isDisabled) "Requerido" else subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { 
                    Text(
                        "Buscar mecánicos...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    ) 
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodySmall,
                singleLine = true
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onGoRequests: () -> Unit = {},
    onGoSettings: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Principal (Home) - seleccionado
            BottomNavItem(
                icon = Icons.Filled.Home,
                label = "Principal",
                isSelected = true,
                onClick = { /* Ya en home */ }
            )
            
            // Solicitudes
            BottomNavItem(
                icon = Icons.Filled.Assignment,
                label = "Solicitudes",
                isSelected = false,
                onClick = onGoRequests
            )
            
            // Configuraciones
            BottomNavItem(
                icon = Icons.Filled.Settings,
                label = "Configuraciones",
                isSelected = false,
                onClick = onGoSettings
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Card(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 4.dp else 0.dp
            )
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}


@Composable
private fun StatisticsSection() {
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
                text = "Estadísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    number = "150+",
                    label = "Mecánicos",
                    iconRes = R.mipmap.ic_launcher
                )
                StatItem(
                    number = "500+",
                    label = "Servicios",
                    iconRes = R.drawable.ic_launcher_background
                )
                StatItem(
                    number = "4.8★",
                    label = "Calificación",
                    iconRes = R.mipmap.ic_launcher
                )
            }
        }
    }
}


@Composable
private fun StatItem(
    number: String,
    label: String,
    iconRes: Int? = null
) {
    // Animación de entrada
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "alpha"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .alpha(alpha) // Aplicar animación de transparencia
            .animateContentSize() // Animación de contenido
    ) {
        if (iconRes != null) {
            Icon(
                Icons.Filled.Person,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        Text(
            text = number,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}