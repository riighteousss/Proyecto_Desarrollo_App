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

@Composable
fun MyAddressesScreen(
    isLoggedIn: Boolean = false,
    onGoLogin: () -> Unit = {},
    onAddAddress: () -> Unit = {},
    onEditAddress: (Long) -> Unit = {},
    onDeleteAddress: (Long) -> Unit = {},
    onSetDefault: (Long) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Direcciones",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (isLoggedIn) {
                FloatingActionButton(
                    onClick = onAddAddress,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar dirección")
                }
            }
        }
        
        if (!isLoggedIn) {
            // Mensaje para usuarios no logueados
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
                        Icons.Filled.Person,
                        contentDescription = "Usuario",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Inicia sesión para gestionar tus direcciones",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onGoLogin) {
                        Text("Iniciar Sesión")
                    }
                }
            }
        } else {
            // Lista de direcciones (simulada)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏠 Casa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Text("Predeterminada", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                    Text(
                        text = "Av. Principal 123, Santiago",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Región Metropolitana",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onEditAddress(1L) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Editar")
                        }
                        OutlinedButton(
                            onClick = { onDeleteAddress(1L) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏢 Trabajo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = { onSetDefault(2L) },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Establecer como predeterminada", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text(
                        text = "Calle Secundaria 456, Providencia",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Región Metropolitana",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onEditAddress(2L) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Editar")
                        }
                        OutlinedButton(
                            onClick = { onDeleteAddress(2L) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}