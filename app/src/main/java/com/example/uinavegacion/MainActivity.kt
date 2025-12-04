package com.example.uinavegacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.uinavegacion.ui.theme.AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.uinavegacion.data.local.database.AppDatabase
import com.example.uinavegacion.data.remote.RemoteDataSource
import com.example.uinavegacion.data.remote.RetrofitClient
import com.example.uinavegacion.data.repository.ServiceRequestRepository
import com.example.uinavegacion.data.repository.UserRepository
import com.example.uinavegacion.data.repository.ServiceRepository
import com.example.uinavegacion.data.repository.VehicleRepository
import com.example.uinavegacion.data.repository.AddressRepository
import com.example.uinavegacion.data.repository.MechanicRepository
import com.example.uinavegacion.data.repository.ImageRepository
import com.example.uinavegacion.navigation.AppNavGraph
import com.example.uinavegacion.ui.viewmodel.AuthViewModel
import com.example.uinavegacion.ui.viewmodel.AuthViewModelFactory
import com.example.uinavegacion.ui.viewmodel.ServiceViewModel
import com.example.uinavegacion.ui.viewmodel.ServiceViewModelFactory
import com.example.uinavegacion.ui.viewmodel.ThemeViewModel
import com.example.uinavegacion.ui.viewmodel.RoleViewModel
import com.example.uinavegacion.ui.viewmodel.RequestFormViewModel
import kotlinx.coroutines.delay
/**
 * MainActivity - Actividad principal de la aplicación
 * 
 * Punto clave: Esta es la actividad principal de la aplicación
 * - Extiende ComponentActivity
 * - setContent{} define toda la UI con Jetpack Compose
 * - AppRoot() maneja toda la lógica de la aplicación
 * 
 * Flujo: MainActivity -> AppRoot -> NavGraph -> Pantallas
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Cambiar al tema normal ANTES de setContent para evitar ActionBar del sistema
        setTheme(com.example.uinavegacion.R.style.Theme_UINavegacion)
        
        // Instalar el splash screen ANTES de setContent
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Mantener el splash screen visible hasta que la app esté lista
        var keepSplashOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        
        setContent {
            AppRoot(
                onSplashScreenReady = {
                    // Cuando la app esté lista, ocultar el splash screen
                    keepSplashOnScreen = false
                }
            )
        }
    }
}

/**
 * AppRoot - Raíz de la aplicación
 * 
 * Inicializa todas las dependencias necesarias:
 * - Base de datos local (Room)
 * - Fuente de datos remota (Retrofit)
 * - Repositorios
 * - ViewModels
 * - Navegación
 */
@Composable
fun AppRoot(
    onSplashScreenReady: () -> Unit = {}
) {
    // Crear contexto de las dependencias
    val context = LocalContext.current.applicationContext

    // Instancia BD local (cacheada con remember para evitar recreación)
    val db = remember { AppDatabase.getInstance(context) }
    
    // Configuración de Retrofit para consumir microservicios (lazy initialization)
    val remoteDataSource = remember {
        RemoteDataSource(
            userApiService = RetrofitClient.userApiService,
            serviceRequestApiService = RetrofitClient.serviceRequestApiService,
            vehicleApiService = RetrofitClient.vehicleApiService,
            imageApiService = RetrofitClient.imageApiService
        )
    }
    
    // Repositorios - Migrados a Retrofit (usuarios, solicitudes y vehículos)
    val userRepository = remember { UserRepository(remoteDataSource) }
    val serviceRequestRepository = remember { ServiceRequestRepository(remoteDataSource) }
    val vehicleRepository = remember { VehicleRepository(remoteDataSource) }
    
    // Repositorios locales (mantienen Room para datos que no están en microservicios)
    val serviceRepository = remember { ServiceRepository(db.serviceRequestDao()) } // Mantener por compatibilidad
    val addressRepository = remember { AddressRepository(db.addressDao()) }
    val mechanicRepository = remember { MechanicRepository(db.mechanicDao()) }
    val imageRepository = remember { ImageRepository(db.imageDao(), context) }
    
    // ViewModels
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )
    val serviceViewModel: ServiceViewModel = viewModel(
        factory = ServiceViewModelFactory(serviceRepository)
    )
    val themeViewModel: ThemeViewModel = viewModel()
    val roleViewModel: RoleViewModel = viewModel()
    val requestFormViewModel: RequestFormViewModel = viewModel()

    val navController = rememberNavController() // Controlador de navegación
    
    // Cambiar el tema de la actividad después de que el splash termine
    LaunchedEffect(Unit) {
        // Esperar un momento para que la UI se cargue
        delay(500)
        // Notificar que la app está lista
        onSplashScreenReady()
    }
    
    AppTheme { // Tema personalizado con colores naranja y azul
        Surface(color = MaterialTheme.colorScheme.background) { // Fondo general
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                serviceViewModel = serviceViewModel,
                themeViewModel = themeViewModel,
                roleViewModel = roleViewModel,
                db = db,
                requestFormViewModel = requestFormViewModel,
                serviceRequestRepository = serviceRequestRepository,
                imageRepository = imageRepository,
                remoteDataSource = remoteDataSource
            ) // Carga el NavHost + Scaffold + Drawer
        }
    }
}
