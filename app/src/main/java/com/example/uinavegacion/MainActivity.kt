package com.example.uinavegacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.uinavegacion.ui.theme.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.uinavegacion.data.local.database.AppDatabase
import com.example.uinavegacion.data.repository.UserRepository
import com.example.uinavegacion.data.repository.ServiceRepository
import com.example.uinavegacion.data.repository.VehicleRepository
import com.example.uinavegacion.data.repository.AddressRepository
import com.example.uinavegacion.data.repository.MechanicRepository
import com.example.uinavegacion.navigation.AppNavGraph
import com.example.uinavegacion.ui.viewmodel.AuthViewModel
import com.example.uinavegacion.ui.viewmodel.AuthViewModelFactory
import com.example.uinavegacion.ui.viewmodel.ServiceViewModel
import com.example.uinavegacion.ui.viewmodel.ServiceViewModelFactory
import com.example.uinavegacion.ui.viewmodel.ThemeViewModel
import com.example.uinavegacion.ui.viewmodel.VehicleViewModel
import com.example.uinavegacion.ui.viewmodel.AddressViewModel
import com.example.uinavegacion.ui.viewmodel.MechanicViewModel
import com.example.uinavegacion.ui.viewmodel.RoleViewModel
import com.example.uinavegacion.ui.viewmodel.RequestFormViewModel

/**
 * MAIN ACTIVITY - ACTIVIDAD PRINCIPAL
 * 
 * 🎯 PUNTO CLAVE: Esta es la ACTIVIDAD PRINCIPAL de la aplicación
 * - Extiende ComponentActivity (nueva forma de crear actividades)
 * - setContent{} es donde se define toda la UI con Jetpack Compose
 * - AppRoot() maneja toda la lógica de la aplicación
 * 
 * 📱 FLUJO: MainActivity → AppRoot → NavGraph → Pantallas
 */
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
    
    // Repositorios
    val userRepository = UserRepository(db.userDao())
    val serviceRepository = ServiceRepository(db.serviceRequestDao())
    val vehicleRepository = VehicleRepository(db.vehicleDao())
    val addressRepository = AddressRepository(db.addressDao())
    val mechanicRepository = MechanicRepository(db.mechanicDao())
    
    // ViewModels
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )
    val serviceViewModel: ServiceViewModel = viewModel(
        factory = ServiceViewModelFactory(serviceRepository)
    )
    val vehicleViewModel: VehicleViewModel = viewModel { VehicleViewModel(vehicleRepository) }
    val addressViewModel: AddressViewModel = viewModel { AddressViewModel(addressRepository) }
    val mechanicViewModel: MechanicViewModel = viewModel { MechanicViewModel(mechanicRepository) }
    val themeViewModel: ThemeViewModel = viewModel()
    val roleViewModel: RoleViewModel = viewModel()
    val requestFormViewModel: RequestFormViewModel = viewModel()

    val navController = rememberNavController() // Controlador de navegación
    AppTheme { // Tema personalizado con colores naranja y azul
        Surface(color = MaterialTheme.colorScheme.background) { // Fondo general
            AppNavGraph(
                navController = navController, 
                authViewModel = authViewModel, 
                serviceViewModel = serviceViewModel,
                themeViewModel = themeViewModel,
                vehicleViewModel = vehicleViewModel,
                addressViewModel = addressViewModel,
                mechanicViewModel = mechanicViewModel,
                roleViewModel = roleViewModel,
                db = db,
                requestFormViewModel = requestFormViewModel
            ) // Carga el NavHost + Scaffold + Drawer
        }
    }
}
