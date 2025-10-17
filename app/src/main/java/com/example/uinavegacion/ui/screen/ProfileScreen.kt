package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.ui.viewmodel.ServiceViewModel
import com.example.uinavegacion.data.local.service.ServiceRequest
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(serviceViewModel: ServiceViewModel? = null) {
    val requestsState = serviceViewModel?.requests?.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        serviceViewModel?.loadAll()
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Mi perfil", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Nombre: —", style = MaterialTheme.typography.bodyLarge)
        Text("Correo: —", style = MaterialTheme.typography.bodyLarge)
        Text("Teléfono: —", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* TODO: editar */ }, modifier = Modifier.fillMaxWidth()) { Text("Editar información") }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Historial de solicitudes", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        val items = requestsState?.value ?: emptyList<ServiceRequest>()
        items.forEach { r ->
            Text("- [${r.type}] ${r.address} (${if (r.urgent) "Urgente" else "Normal"})", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
