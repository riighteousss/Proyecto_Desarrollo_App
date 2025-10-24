package com.example.uinavegacion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp

/**
 * SIMPLEMAPVIEW - MAPA SIMPLE SIN GOOGLE MAPS
 * 
 * Muestra una representación visual del mapa sin depender de Google Maps API.
 * Ideal para desarrollo y pruebas.
 */
@Composable
fun SimpleMapView(
    onMapClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onMapClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Fondo del mapa simulado
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color(0xFFE8F5E8) // Verde claro para simular mapa
                    )
            )
            
            // Elementos del mapa simulado
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de ubicación
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "🗺️ Santiago, Chile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Toca para ver mapa completo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            // Indicador de que es clickeable
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    )
                ) {
                    Icon(
                        Icons.Filled.TouchApp,
                        contentDescription = "Toca",
                        modifier = Modifier.padding(4.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
