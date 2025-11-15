package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Mechanic(val id: Int, val name: String, val rating: Double, val distanceKm: Double)

@Composable
fun MechanicsListScreen(mechanics: List<Mechanic> = listOf(
    Mechanic(1, "Juan - FixIt", 4.5, 1.2),
    Mechanic(2, "Ana - RapidRepair", 4.8, 2.1),
    Mechanic(3, "Carlos - MotoHelp", 4.2, 0.8)
), onRequest: (Mechanic) -> Unit = {}) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Mecánicos cercanos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        mechanics.forEach { mech ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(mech.name, style = MaterialTheme.typography.titleMedium)
                    Text("${mech.rating} ★ - ${mech.distanceKm} km", style = MaterialTheme.typography.bodySmall)
                }
                Button(onClick = { onRequest(mech) }) { Text("Solicitar") }
            }
            Divider()
        }
    }
}
