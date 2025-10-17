package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.uinavegacion.ui.viewmodel.ServiceViewModel
import com.example.uinavegacion.data.local.service.ServiceRequest
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults

@Composable
fun EmergencyScreen(onSent: () -> Unit, serviceViewModel: ServiceViewModel? = null) {
    val address = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val needTow = remember { mutableStateOf(false) }
    val addressError = remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Emergencia", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = description.value, onValueChange = { description.value = it }, label = { Text("¿Qué pasó?") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = address.value, onValueChange = { address.value = it }, label = { Text("Dirección actual") }, modifier = Modifier.fillMaxWidth(), isError = addressError.value != null)
    if (addressError.value != null) Text(addressError.value ?: "", color = Color(0xFFB00020), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Necesito grúa")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(checked = needTow.value, onCheckedChange = { needTow.value = it })
        }
        Spacer(modifier = Modifier.height(16.dp))
        val isValid = address.value.isNotBlank()
        if (!isValid) addressError.value = "La dirección es requerida" else addressError.value = null
        Button(onClick = {
            val req = ServiceRequest(
                type = "emergency",
                description = description.value.ifEmpty { null },
                address = address.value,
                timestamp = System.currentTimeMillis(),
                urgent = true,
                needsTow = needTow.value
            )
            if (serviceViewModel != null) {
                serviceViewModel.create(req) { onSent() }
            } else {
                onSent()
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Enviar solicitud urgente")
        }
    }
}
