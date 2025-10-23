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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun HelpScreen(
    onContactSupport: () -> Unit = {},
    onSendFeedback: () -> Unit = {}
) {
    var showContactDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
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
                    text = "Centro de Ayuda",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Preguntas frecuentes
            item {
                HelpSection(
                    title = "Preguntas Frecuentes",
                    items = listOf(
                        HelpItem(
                            question = "¿Cómo solicito un servicio?",
                            answer = "Ve a la pantalla principal y selecciona 'Solicitar Servicio'. Completa el formulario con los detalles de tu problema y espera a que un mecánico acepte tu solicitud."
                        ),
                        HelpItem(
                            question = "¿Cuánto tiempo tarda en llegar un mecánico?",
                            answer = "El tiempo de respuesta depende de la ubicación y disponibilidad. En promedio, un mecánico puede llegar entre 15-30 minutos."
                        ),
                        HelpItem(
                            question = "¿Cómo puedo cancelar una solicitud?",
                            answer = "Ve a 'Mis Solicitudes' y selecciona la solicitud que deseas cancelar. Toca el botón 'Cancelar' si aún no ha sido aceptada."
                        ),
                        HelpItem(
                            question = "¿Qué tipos de servicios ofrecen?",
                            answer = "Ofrecemos servicios de emergencia, mantenimiento programado, reparaciones generales, cambio de aceite, revisión de frenos y más."
                        )
                    )
                )
            }

            // Contacto y soporte
            item {
                HelpSection(
                    title = "Contacto y Soporte",
                    items = listOf(
                        HelpItem(
                            question = "Soporte Técnico",
                            answer = "Si tienes problemas con la aplicación, contacta a nuestro equipo de soporte.",
                            action = "Contactar",
                            onAction = { showContactDialog = true }
                        ),
                        HelpItem(
                            question = "Enviar Comentarios",
                            answer = "Tu opinión es importante para nosotros. Ayúdanos a mejorar la aplicación.",
                            action = "Enviar",
                            onAction = { showFeedbackDialog = true }
                        )
                    )
                )
            }

            // Información de contacto
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Información de Contacto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ContactInfoItem(
                            icon = Icons.Filled.Phone,
                            label = "Teléfono",
                            value = "+56 9 1234 5678"
                        )
                        
                        ContactInfoItem(
                            icon = Icons.Filled.Email,
                            label = "Email",
                            value = "soporte@mecanicosapp.cl"
                        )
                        
                        ContactInfoItem(
                            icon = Icons.Filled.Schedule,
                            label = "Horario de Atención",
                            value = "Lunes a Viernes: 8:00 - 18:00"
                        )
                    }
                }
            }
        }
    }
    
    // Diálogos
    if (showContactDialog) {
        ContactSupportDialog(
            onDismiss = { showContactDialog = false }
        )
    }
    
    if (showFeedbackDialog) {
        FeedbackDialog(
            onDismiss = { showFeedbackDialog = false }
        )
    }
}

@Composable
private fun HelpSection(
    title: String,
    items: List<HelpItem>
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
                HelpItemRow(item = item)
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
private fun HelpItemRow(item: HelpItem) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.question,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (item.action != null) {
                Button(
                    onClick = { item.onAction?.invoke() },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(item.action)
                }
            } else {
                IconButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "Contraer" else "Expandir"
                    )
                }
            }
        }
        
        if (isExpanded && item.answer.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ContactInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

data class HelpItem(
    val question: String,
    val answer: String = "",
    val action: String? = null,
    val onAction: (() -> Unit)? = null
)

@Composable
private fun ContactSupportDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Contactar Soporte")
        },
        text = {
            Column {
                Text("¿Cómo te gustaría contactarnos?")
                Spacer(modifier = Modifier.height(16.dp))
                
                ContactOption(
                    icon = Icons.Filled.Phone,
                    title = "Llamar",
                    subtitle = "+56 9 1234 5678",
                    onClick = { /* TODO: Implementar llamada */ }
                )
                
                ContactOption(
                    icon = Icons.Filled.Email,
                    title = "Email",
                    subtitle = "soporte@mecanicosapp.cl",
                    onClick = { /* TODO: Implementar email */ }
                )
                
                ContactOption(
                    icon = Icons.Filled.Chat,
                    title = "Chat en vivo",
                    subtitle = "Disponible 24/7",
                    onClick = { /* TODO: Implementar chat */ }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun FeedbackDialog(
    onDismiss: () -> Unit
) {
    var feedbackText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Enviar Comentarios")
        },
        text = {
            Column {
                Text("¿Cómo calificarías nuestra aplicación?")
                Spacer(modifier = Modifier.height(8.dp))
                
                // Rating stars
                Row {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = index + 1 }
                        ) {
                            Icon(
                                imageVector = if (index < rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "Estrella ${index + 1}",
                                tint = if (index < rating) Color(0xFFFFD700) else Color.Gray
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Comentarios adicionales:")
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    placeholder = { Text("Escribe tus comentarios aquí...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // TODO: Implementar envío de feedback
                    onDismiss()
                },
                enabled = feedbackText.isNotBlank() || rating > 0
            ) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ContactOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
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
            modifier = Modifier.size(16.dp)
        )
    }
}

