package com.example.uinavegacion.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.SkipNext

@Composable
fun PantallaOnboardingScreen(
    ventanas: List<VentanaOnboarding>,
    onFinalizado: () -> Unit
) {
    var indice by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val ventanaActual = ventanas[indice]

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Imagenes con animación
            ImagenesAnimadas(imagenes = ventanaActual.imagenes)

            // Título
            Text(
                text = ventanaActual.titulo,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Descripción
            Text(
                text = ventanaActual.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
            )

            // Puntos indicadores
            Indicadores(total = ventanas.size, actual = indice)

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de navegación
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onFinalizado() }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Saltar"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Saltar")
                }

                Button(
                    onClick = {
                        if (indice < ventanas.lastIndex) {
                            scope.launch { indice++ }
                        } else {
                            onFinalizado()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Siguiente"
                    )
                }
            }
        }
    }

}

