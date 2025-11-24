package com.example.uinavegacion.ui.components

import androidx.compose.material.icons.Icons // Conjunto de íconos Material
import androidx.compose.material.icons.filled.Home // Ícono Home
import androidx.compose.material.icons.filled.AccountCircle // Ícono Login
import androidx.compose.material.icons.filled.Person // Ícono Registro
import androidx.compose.material3.CenterAlignedTopAppBar // TopAppBar centrada
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon // Para mostrar íconos
import androidx.compose.material3.IconButton // Botones con ícono
import androidx.compose.material3.MaterialTheme // Tema Material
import androidx.compose.material3.Text // Texto
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable // Composable reutilizable: barra superior
fun AppTopBar(
    onHome: () -> Unit,       // Navega a Home
    onUserAction: () -> Unit, // Acción del usuario (login/perfil)
    isLoggedIn: Boolean = false // Estado de autenticación
) {
    CenterAlignedTopAppBar( // Barra alineada al centro
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = { // Slot del título
            Text(
                text = "Fixsy", // Título visible
                style = MaterialTheme.typography.titleLarge, // Estilo grande
                maxLines = 1,              // asegura una sola línea
                overflow = TextOverflow.Ellipsis // agrega "..." si no cabe
            )
        },
        actions = { // Acciones a la derecha (íconos)
            IconButton(onClick = onHome) { // Ir a Home
                Icon(Icons.Filled.Home, contentDescription = "Home") // Ícono Home
            }
            // Solo un icono de usuario que cambia según el estado
            IconButton(onClick = onUserAction) { // Acción del usuario
                Icon(
                    imageVector = if (isLoggedIn) Icons.Filled.Person else Icons.Filled.AccountCircle,
                    contentDescription = if (isLoggedIn) "Perfil" else "Iniciar sesión"
                )
            }
        }
    )
}