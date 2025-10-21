package com.example.uinavegacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.uinavegacion.data.local.database.AppDatabase
import com.example.uinavegacion.data.repository.UserRepository
import com.example.uinavegacion.navigation.AppNavGraph
import com.example.uinavegacion.ui.viewmodel.AuthViewModel
import com.example.uinavegacion.ui.viewmodel.AuthViewModelFactory
import com.example.uinavegacion.data.repository.ServiceRepository
import com.example.uinavegacion.ui.viewmodel.ServiceViewModel
import com.example.uinavegacion.ui.viewmodel.ServiceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

/*
* En Compose, Surface es un contenedor visual que viene de Material 3.Crea un bloque
* que puedes personalizar con color, forma, sombra (elevación).
* Sirve para aplicar un fondo (color, borde, elevación, forma) siguiendo las guías de diseño
* de Material.
* Piensa en él como una “lona base” sobre la cual vas a pintar tu UI.
* Si cambias el tema a dark mode, colorScheme.background
* cambia automáticamente y el Surface pinta la pantalla con el nuevo color.
*/
@Composable
fun AppRoot() { // Raíz de la app para separar responsabilidades
    // Crear contexto de las dependencias
    val context = LocalContext.current.applicationContext

    // Instancia BD y dependencias
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val userRepository = UserRepository(userDao)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    // Servicio de solicitudes
    val serviceDao = db.serviceRequestDao()
    val serviceRepository = ServiceRepository(serviceDao)
    val serviceViewModel: ServiceViewModel = viewModel(
        factory = ServiceViewModelFactory(serviceRepository)
    )

    val navController = rememberNavController() // Controlador de navegación
    MaterialTheme { // Provee colores/tipografías Material 3
        Surface(color = MaterialTheme.colorScheme.background) { // Fondo general
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                serviceViewModel = serviceViewModel
            ) // Carga el NavHost + Scaffold + Drawer
        }
    }
}
