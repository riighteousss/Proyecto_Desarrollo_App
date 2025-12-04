@file:Suppress("DEPRECATION")
package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun EmergencyScreen(
    onGoBack: () -> Unit,
    onRequestService: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var selectedEmergency by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val emergencyTypes = listOf(
        "Avería en carretera" to Icons.Filled.Warning,
        "Accidente" to Icons.Filled.Error,
        "Batería descargada" to Icons.Filled.BatteryAlert,
        "Pinchazo" to Icons.Filled.TireRepair,
        "Fallo motor" to Icons.Filled.Build,
        "Otro" to Icons.Filled.Help
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE53E3E)
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
                        text = "🚨 EMERGENCIA 24/7",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Servicio de emergencia disponible",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Tipo de Emergencia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(emergencyTypes.size) { index ->
                val (type, icon) = emergencyTypes[index]
                EmergencyTypeCard(
                    title = type,
                    icon = icon,
                    isSelected = selectedEmergency == type,
                    onClick = { selectedEmergency = type }
                )
            }

            item {
                Text(
                    text = "Descripción del problema",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe brevemente el problema...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }

            item {
                Text(
                    text = "Ubicación actual",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("Dirección o ubicación...") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { 
                        // TODO: Implementar obtención de ubicación GPS
                        location = "Ubicación obtenida por GPS"
                    }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "GPS",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        if (selectedEmergency.isNotEmpty() && description.isNotEmpty()) {
                            onRequestService(selectedEmergency, description, location)
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53E3E)
                    ),
                    enabled = selectedEmergency.isNotEmpty() && description.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Emergency,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SOLICITAR EMERGENCIA", fontWeight = FontWeight.Bold)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "ℹ️ Información importante",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Tiempo de respuesta estimado: 15-30 minutos\n" +
                                    "• Servicio disponible 24/7\n" +
                                    "• Costo adicional por emergencia\n" +
                                    "• Mantén el teléfono cerca para confirmación",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // Dialog de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("✅ Solicitud enviada") },
            text = { 
                Text("Tu solicitud de emergencia ha sido enviada. Un mecánico se pondrá en contacto contigo en breve.") 
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccessDialog = false
                        onGoBack()
                    }
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
private fun EmergencyTypeCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                           else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurface
            )
            if (isSelected) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

