package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun AssistanceChoiceScreen(
    onSchedule: () -> Unit,
    onEmergency: () -> Unit
) {
    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("¿Qué tipo de asistencia necesitas?", style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier
                .weight(1f)
                .clickable { onSchedule() }, elevation = CardDefaults.cardElevation()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(android.R.drawable.ic_menu_my_calendar), contentDescription = null)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Agendar una cita", style = MaterialTheme.typography.titleMedium)
                    Text("Programa tu servicio", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Card(modifier = Modifier
                .weight(1f)
                .clickable { onEmergency() }, elevation = CardDefaults.cardElevation()) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(android.R.drawable.ic_dialog_alert), contentDescription = null)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Emergencia", style = MaterialTheme.typography.titleMedium)
                    Text("Atención inmediata", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
