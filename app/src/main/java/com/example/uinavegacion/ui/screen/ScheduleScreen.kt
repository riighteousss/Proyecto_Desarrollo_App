package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.ui.viewmodel.ServiceViewModel
import com.example.uinavegacion.data.local.service.ServiceRequest
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

@Composable
fun ScheduleScreen(onConfirmed: () -> Unit, serviceViewModel: ServiceViewModel? = null) {
    val address = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val dateStr = remember { mutableStateOf("") } // formato yyyy-MM-dd
    val timeStr = remember { mutableStateOf("") } // formato HH:mm
    val dateError = remember { mutableStateOf<String?>(null) }
    val addressError = remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var ok = true
        if (address.value.isBlank()) {
            addressError.value = "La dirección es requerida"
            ok = false
        } else {
            addressError.value = null
        }

        if (dateStr.value.isBlank() || timeStr.value.isBlank()) {
            dateError.value = "Fecha y hora son requeridas"
            ok = false
        } else {
            // parsear y verificar que sea futuro
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val dt = sdf.parse("${dateStr.value} ${timeStr.value}")
                val now = Calendar.getInstance().time
                if (dt != null && dt.before(now)) {
                    dateError.value = "La fecha/hora debe ser en el futuro"
                    ok = false
                } else {
                    dateError.value = null
                }
            } catch (e: Exception) {
                dateError.value = "Formato inválido"
                ok = false
            }
        }

        return ok
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Agendar cita", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = address.value, onValueChange = { address.value = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), isError = addressError.value != null)
        if (addressError.value != null) Text(addressError.value ?: "", color = Color(0xFFB00020), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = description.value, onValueChange = { description.value = it }, label = { Text("Descripción (opcional)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        // Campos simples para fecha/hora (puedes reemplazar por pickers nativos más adelante)
        OutlinedTextField(value = dateStr.value, onValueChange = { dateStr.value = it }, label = { Text("Fecha (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = timeStr.value, onValueChange = { timeStr.value = it }, label = { Text("Hora (HH:mm)") }, modifier = Modifier.fillMaxWidth())
        if (dateError.value != null) Text(dateError.value ?: "", color = Color(0xFFB00020), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))
        val isValid = validate()
        Button(onClick = {
            // crear request y delegar al viewmodel si está disponible
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val dt = sdf.parse("${dateStr.value} ${timeStr.value}")
            val timestamp = dt?.time ?: System.currentTimeMillis()
            val req = ServiceRequest(
                type = "schedule",
                description = description.value.ifEmpty { null },
                address = address.value,
                timestamp = timestamp
            )
            if (serviceViewModel != null) {
                serviceViewModel.create(req) { onConfirmed() }
            } else {
                onConfirmed()
            }
        }, enabled = isValid, modifier = Modifier.fillMaxWidth()) {
            Text("Confirmar cita")
        }
    }
}
