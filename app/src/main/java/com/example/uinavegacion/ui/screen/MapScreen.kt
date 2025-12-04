@file:Suppress("DEPRECATION")
package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * MAPSCREEN - PANTALLA DE MAPA SIMPLE CON UBICACIÓN
 * 
 * Muestra un mapa de Google Maps con ubicación por defecto.
 * Simple y funcional para estudiar.
 */
@Composable
fun MapScreen(
    onGoBack: () -> Unit
) {
    // Ubicación por defecto (Santiago, Chile)
    val santiagoLocation = LatLng(-33.4489, -70.6693)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(santiagoLocation, 15f)
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar
        @OptIn(ExperimentalMaterial3Api::class)
        TopAppBar(
            title = { Text("Mi Ubicación") },
            navigationIcon = {
                IconButton(onClick = onGoBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { 
                    // Centrar en Santiago
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(santiagoLocation, 15f)
                }) {
                    Icon(Icons.Filled.MyLocation, contentDescription = "Mi ubicación")
                }
            }
        )
        
        // Mapa
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    compassEnabled = true,
                    myLocationButtonEnabled = false
                )
            ) {
                // Marcador principal en Santiago
                Marker(
                    state = MarkerState(position = santiagoLocation),
                    title = "Santiago, Chile",
                    snippet = "Tu ubicación actual"
                )
                
                // Algunos marcadores de ejemplo
                Marker(
                    state = MarkerState(position = LatLng(-33.4372, -70.6506)),
                    title = "Plaza de Armas",
                    snippet = "Centro histórico"
                )
                
                Marker(
                    state = MarkerState(position = LatLng(-33.4170, -70.6067)),
                    title = "Parque Forestal",
                    snippet = "Área verde"
                )
            }
            
            // Información del mapa
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "🗺️ Mapa de Ubicación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Centrado en Santiago, Chile",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Usa los controles para navegar",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

