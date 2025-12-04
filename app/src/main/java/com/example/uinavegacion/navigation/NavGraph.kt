/**
 * NAVGRAPH - NAVEGACIÓN PRINCIPAL
 * 
 * PUNTO CLAVE: Aquí está toda la NAVEGACIÓN de la aplicación
 * - NavHost es el contenedor de todas las pantallas
 * - startDestination define cuál pantalla se muestra primero
 * - composable() define cada pantalla y su ruta
 * - navController.navigate() cambia entre pantallas
 * 
 * PANTALLAS DISPONIBLES:
 * - HomeScreen (pantalla principal)
 * - LoginScreen (iniciar sesión)
 * - RegisterScreen (registrarse)
 * - ProfileScreen (perfil de usuario)
 * - Y muchas más...
 * 
 * FLUJO DE NAVEGACIÓN:
 * Home → Login → Home (después de login exitoso)
 * Home → Register → Home (después de registro exitoso)
 */
package com.example.uinavegacion.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.uinavegacion.ui.components.AppTopBar
import com.example.uinavegacion.data.local.storage.UserPreferences
import com.example.uinavegacion.ui.screen.*
import com.example.uinavegacion.ui.viewmodel.*
import kotlinx.coroutines.launch

@Composable // Gráfico de navegación + Drawer + Scaffold
fun AppNavGraph(navController: NavHostController,
                authViewModel: AuthViewModel,
                serviceViewModel: ServiceViewModel,
                themeViewModel: ThemeViewModel,
                roleViewModel: RoleViewModel,
                db: com.example.uinavegacion.data.local.database.AppDatabase,
                requestFormViewModel: RequestFormViewModel,
                serviceRequestRepository: com.example.uinavegacion.data.repository.ServiceRequestRepository,
                imageRepository: com.example.uinavegacion.data.repository.ImageRepository,
                remoteDataSource: com.example.uinavegacion.data.remote.RemoteDataSource) { // Recibe el controlador

    val scope = rememberCoroutineScope() // Necesario para corrutinas
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    
    // Inicializar RoleViewModel con contexto para DataStore
    LaunchedEffect(Unit) {
        roleViewModel.initialize(context)
    }
    
    // Función helper para logout que limpia DataStore y restablece RoleSelection
    val performLogout: () -> Unit = {
        scope.launch {
            userPrefs.clearSession()
        }
        authViewModel.logout()
        roleViewModel.logout() // Restablecer isFirstTime para mostrar RoleSelection
    }
    
    // Estados observables
    val isDarkModeState = themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val isLoggedInState = authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentRoleState = roleViewModel.currentRole.collectAsStateWithLifecycle()
    val isFirstTimeState = roleViewModel.isFirstTime.collectAsStateWithLifecycle()
    val userRoleState = userPrefs.userRole.collectAsStateWithLifecycle(initialValue = "CLIENT")
    val userNameState = userPrefs.userName.collectAsStateWithLifecycle(initialValue = "")
    
    val isDarkMode = isDarkModeState.value
    val isLoggedIn = isLoggedInState.value
    val currentRole = currentRoleState.value
    val isFirstTime = isFirstTimeState.value
    val loggedInUserRole = userRoleState.value
    val loggedInUserName = userNameState.value
    val currentUserIdState = userPrefs.userId.collectAsStateWithLifecycle(initialValue = -1L)
    val currentUserId = currentUserIdState.value

    // Obtener la ruta actual para ocultar topBar en ciertas pantallas
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Lista de rutas donde NO debe mostrarse la TopBar
    val routesWithoutTopBar = setOf(
        Route.RoleSelection.path,
        Route.Login.path,
        Route.Register.path,
        Route.Splash.path
    )
    
    // Solo mostrar TopBar si la ruta actual existe y NO está en la lista de rutas sin TopBar
    // Si currentRoute es null, no mostrar TopBar (por seguridad)
    val shouldShowTopBar = currentRoute != null && currentRoute !in routesWithoutTopBar

    // Helpers de navegación (reutilizamos en topbar/drawer/botones)
    val goHome: () -> Unit    = { navController.navigate(Route.Home.path) }    // Ir a Home
    val goLogin: () -> Unit   = { navController.navigate(Route.Login.path) }   // Ir a Login
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } // Ir a Registro
    val goRequests: () -> Unit = { navController.navigate(Route.RequestHistory.path) } // Ir a Historial de Solicitudes
    val goSettings: () -> Unit = { navController.navigate(Route.Settings.path) } // Ir a Configuraciones
    val goVehicles: () -> Unit = { navController.navigate(Route.MyVehicles.path) } // Ir a Mis Vehículos
    val goAddresses: () -> Unit = { navController.navigate(Route.MyAddresses.path) } // Ir a Mis Direcciones
    val goHelp: () -> Unit = { navController.navigate(Route.Help.path) } // Ir a Ayuda

    Scaffold( // Estructura base de pantalla
            topBar = { // Barra superior con íconos/menú (solo si debe mostrarse)
                // Solo mostrar TopBar si shouldShowTopBar es true
                if (shouldShowTopBar) {
                    AppTopBar(
                        onHome = goHome,     // Botón Home
                        onUserAction = { 
                            if (isLoggedIn) {
                                navController.navigate(Route.EditProfile.path)
                            } else {
                                navController.navigate(Route.Login.path)
                            }
                        },
                        isLoggedIn = isLoggedIn
                    )
                }
                // Si shouldShowTopBar es false, no renderizar nada (TopBar oculta)
            }
        ) { innerPadding -> // Padding que evita solapar contenido
            NavHost( // Contenedor de destinos navegables
                navController = navController, // Controlador
                startDestination = when {
                    // ⚠️ PRIORIDAD 1: Si el usuario está logueado, ir directamente a su pantalla principal
                    isLoggedIn && currentRole == UserRole.MECHANIC -> Route.MechanicHome.path
                    isLoggedIn && currentRole == UserRole.ADMIN -> Route.AdminHome.path
                    isLoggedIn -> Route.Home.path
                    // ⚠️ PRIORIDAD 2: Si NO está logueado, SIEMPRE mostrar RoleSelection primero
                    // (El usuario puede cambiar de rol o continuar con el rol guardado)
                    else -> Route.RoleSelection.path
                }, // Inicio basado en rol y estado de login
                modifier = Modifier.padding(
                    if (shouldShowTopBar) innerPadding else PaddingValues(0.dp)
                ) // Respeta topBar solo si está visible
            ) {
                composable(Route.Home.path) { // Destino Home
                    HomeScreen(
                        onGoLogin = goLogin, // Botón para ir a Login
                        onGoRegister = goRegister, // Botón para ir a Registro
                        onGoRequests = goRequests, // Botón para ir a Solicitudes
                        userName = if (isLoggedIn && loggedInUserName.isNotEmpty()) loggedInUserName else null, // Nombre del usuario logueado desde UserPreferences
                        isLoggedIn = isLoggedIn, // Estado de login
                        onGoSettings = goSettings, // Botón para ir a Configuraciones
                        onGoEmergency = { navController.navigate(Route.EmergencyService.path) },
                        onGoRequestService = { navController.navigate(Route.RequestService.path) },
                        onGoFavorites = { navController.navigate(Route.Favorites.path) },
                        onGoAppointments = { navController.navigate(Route.Appointments.path) },
                        onGoMap = { navController.navigate(Route.Map.path) }
                    )
                }
                composable(Route.AssistanceChoice.path) {
                    AssistanceChoiceScreen(
                        onSchedule = { navController.navigate(Route.Schedule.path) },
                        onEmergency = { navController.navigate(Route.Emergency.path) }
                    )
                }
                composable(Route.Schedule.path) {
                    ScheduleScreen(onConfirmed = { navController.navigate(Route.Mechanics.path) }, serviceViewModel = serviceViewModel)
                }
                composable(Route.Emergency.path) {
                    // Redirigir a EmergencyService para evitar duplicación
                    EmergencyScreen(
                        onGoBack = { navController.popBackStack() },
                        onRequestService = { type, description, location ->
                            scope.launch {
                                if (currentUserId > 0) {
                                    val emergencyRequest = com.example.uinavegacion.data.remote.dto.ServiceRequestRequestDTO(
                                        userId = currentUserId,
                                        serviceType = "Emergencia: $type",
                                        vehicleInfo = "Emergencia",
                                        description = description,
                                        images = "",
                                        location = location.ifEmpty { "Ubicación no especificada" },
                                        notes = "Solicitud de emergencia - Prioridad alta"
                                    )
                                    serviceRequestRepository.createRequest(emergencyRequest)
                                }
                            }
                            navController.navigate(Route.Mechanics.path)
                        }
                    )
                }
                composable(Route.Mechanics.path) {
                    MechanicsListScreen(onRequest = { mech ->
                        // crear una solicitud simplificada usando el serviceViewModel
                        val now = System.currentTimeMillis()
                        val req = com.example.uinavegacion.data.local.service.ServiceRequest(
                            type = "solicitud_mecanico",
                            description = "Solicitud para ${mech.name}",
                            address = "Dirección de ejemplo",
                            timestamp = now
                        )
                        serviceViewModel.create(req) { id -> /* opcional: mostrar confirmación */ }
                    })
                }
                composable(Route.Profile.path) {
                    ProfileScreen(
                        serviceViewModel = serviceViewModel,
                        userName = authViewModel.getCurrentUserName(),
                        isLoggedIn = isLoggedIn,
                        onGoSettings = goSettings,
                        onGoHelp = goHelp,
                        onToggleDarkMode = { themeViewModel.toggleDarkMode() },
                        onEditProfile = { navController.navigate(Route.EditProfile.path) },
                        onLogout = { 
                            performLogout()
                            // Después de logout, volver a RoleSelection (pantalla inicial)
                            navController.navigate(Route.RoleSelection.path) {
                                popUpTo(Route.RoleSelection.path) { inclusive = true }
                            }
                        },
                        isDarkMode = isDarkMode
                    )
                }
                composable(Route.Requests.path) { // Destino Solicitudes
                    RequestsScreen(
                        isLoggedIn = isLoggedIn,
                        onGoLogin = goLogin,
                        serviceViewModel = serviceViewModel
                    )
                }
                composable(Route.Login.path) { // Destino Login
                    //1 modificamos el acceso a la pagina
                    // Usamos la versión con ViewModel (LoginScreenVm) para formularios/validación en tiempo real
                    LoginScreenVm(
                        vm = authViewModel,
                        roleViewModel = roleViewModel,
                        onLoginOkNavigateHome = {
                            // Navegar según el rol seleccionado
                            when (currentRole) {
                                UserRole.MECHANIC -> navController.navigate(Route.MechanicHome.path) {
                                    popUpTo(Route.Login.path) { inclusive = true }
                                }
                                UserRole.ADMIN -> navController.navigate(Route.AdminHome.path) {
                                    popUpTo(Route.Login.path) { inclusive = true }
                                }
                                else -> goHome() // Cliente va a Home
                            }
                        },
                        onGoRegister = goRegister, // Enlace para ir a la pantalla de Registro
                        onGoForgotPassword = { navController.navigate(Route.ForgotPassword.path) } // Enlace para recuperar contraseña
                    )
                }
                composable(Route.Register.path) { // Destino Registro
                    //2 modificamos el acceso a la pagina
                    // Usamos la versión con ViewModel (RegisterScreenVm) para formularios/validación en tiempo real
                    RegisterScreenVm(
                        vm= authViewModel,
                        onRegisteredNavigateLogin = goLogin,       // Si el VM marca success=true, volvemos a Login
                        onGoLogin = goLogin                        // Botón alternativo para ir a Login
                    )
                }
                composable(Route.Settings.path) { // Destino Configuraciones
                    SettingsScreen(
                        onGoRequests = goRequests,
                        onGoVehicles = goVehicles,
                        onGoAddresses = goAddresses,
                        onGoHelp = goHelp,
                        onToggleDarkMode = { themeViewModel.toggleDarkMode() },
                        onLogout = { 
                            performLogout()
                            // Después de logout, volver a RoleSelection (pantalla inicial)
                            navController.navigate(Route.RoleSelection.path) {
                                popUpTo(Route.RoleSelection.path) { inclusive = true }
                            }
                        },
                        onChangeRole = { navController.navigate(Route.RoleSelection.path) },
                        onGoAdmin = { navController.navigate(Route.AdminAuth.path) },
                        isDarkMode = isDarkMode,
                        currentRole = currentRole?.name
                    )
                }
                composable(Route.MyVehicles.path) { // Destino Mis Vehículos
                    MyVehiclesScreen(
                        isLoggedIn = isLoggedIn,
                        onGoLogin = goLogin,
                        onAddVehicle = { /* TODO: Implementar agregar vehículo */ },
                        onEditVehicle = { /* TODO: Implementar editar vehículo */ },
                        onDeleteVehicle = { /* TODO: Implementar eliminar vehículo */ }
                    )
                }
                composable(Route.MyAddresses.path) { // Destino Mis Direcciones
                    MyAddressesScreen(
                        isLoggedIn = isLoggedIn,
                        onGoLogin = goLogin,
                        onAddAddress = { /* TODO: Implementar agregar dirección */ },
                        onEditAddress = { /* TODO: Implementar editar dirección */ },
                        onDeleteAddress = { /* TODO: Implementar eliminar dirección */ },
                        onSetDefault = { /* TODO: Implementar establecer predeterminada */ }
                    )
                }
                composable(Route.Help.path) { // Destino Ayuda
                    HelpScreen(
                        onContactSupport = { /* TODO: Implementar contacto soporte */ },
                        onSendFeedback = { /* TODO: Implementar enviar comentarios */ }
                    )
                }
                
                // Pantalla de servicios de emergencia
                composable(Route.EmergencyService.path) { // Destino Emergencia desde HomeScreen
                    EmergencyScreen(
                        onGoBack = { navController.popBackStack() },
                        onRequestService = { type, description, location ->
                            // Guardar solicitud de emergencia en el microservicio
                            scope.launch {
                                if (currentUserId > 0) {
                                    val emergencyRequest = com.example.uinavegacion.data.remote.dto.ServiceRequestRequestDTO(
                                        userId = currentUserId,
                                        serviceType = "Emergencia: $type",
                                        vehicleInfo = "Emergencia",
                                        description = description,
                                        images = "",
                                        location = location.ifEmpty { "Ubicación no especificada" },
                                        notes = "Solicitud de emergencia - Prioridad alta"
                                    )
                                    serviceRequestRepository.createRequest(emergencyRequest)
                                }
                            }
                            navController.popBackStack()
                        }
                    )
                }
                
                composable(Route.RequestService.path) { // Destino Solicitar Servicio
                    RequestServiceScreen(
                        onGoBack = { navController.popBackStack() },
                        onRequestService = { service, vehicle, description, images ->
                            // Guardar solicitud en el microservicio con persistencia obligatoria de imágenes
                            val currentUserId = authViewModel.getCurrentUserId() ?: 1L
                            scope.launch {
                                try {
                                    // 1. Crear la solicitud primero
                                    val requestDTO = com.example.uinavegacion.data.remote.dto.ServiceRequestRequestDTO(
                                        userId = currentUserId,
                                        serviceType = service,
                                        vehicleInfo = vehicle,
                                        description = description,
                                        images = "", // Se actualizará después de subir las imágenes
                                        location = "",
                                        notes = ""
                                    )
                                    val createResult = serviceRequestRepository.createRequest(requestDTO)
                                    
                                    createResult.onSuccess { createdRequest ->
                                        val requestId = createdRequest.id
                                        
                                        // 2. Guardar imágenes en BD local
                                        val localImageIds = mutableListOf<Long>()
                                        for (imageUri in images) {
                                            val saveResult = imageRepository.saveImageFromUri(requestId, imageUri)
                                            saveResult.onSuccess { localImageId ->
                                                localImageIds.add(localImageId)
                                            }
                                        }
                                        
                                        // 3. Enviar todas las imágenes al microservicio de imágenes
                                        val uploadedImageIds = mutableListOf<Long>()
                                        for (imageUri in images) {
                                            val uri = android.net.Uri.parse(imageUri)
                                            val base64Data = com.example.uinavegacion.util.ImageUtils.uriToBase64(context, uri)
                                            
                                            if (base64Data != null) {
                                                val uploadResult = remoteDataSource.uploadImage(
                                                    userId = currentUserId,
                                                    requestId = requestId,
                                                    base64Data = base64Data,
                                                    fileName = com.example.uinavegacion.util.ImageUtils.getFileName(uri),
                                                    mimeType = com.example.uinavegacion.util.ImageUtils.getMimeType(context, uri)
                                                )
                                                uploadResult.onSuccess { imageId ->
                                                    uploadedImageIds.add(imageId)
                                                }
                                            }
                                        }
                                        
                                        // 4. Actualizar la solicitud con los IDs de las imágenes subidas
                                        if (uploadedImageIds.isNotEmpty()) {
                                            val updatedRequestDTO = com.example.uinavegacion.data.remote.dto.ServiceRequestRequestDTO(
                                                userId = currentUserId,
                                                serviceType = service,
                                                vehicleInfo = vehicle,
                                                description = description,
                                                images = uploadedImageIds.joinToString(","),
                                                location = "",
                                                notes = ""
                                            )
                                            serviceRequestRepository.updateRequest(requestId, updatedRequestDTO)
                                        }
                                    }.onFailure {
                                        // Error al crear solicitud - se puede mostrar un mensaje al usuario
                                    }
                                } catch (e: Exception) {
                                    // Manejar error
                                }
                            }
                            navController.popBackStack()
                        },
                        onGoCamera = { navController.navigate(Route.Camera.path) },
                        onSaveToHistory = { _, _, _, _ -> 
                            // Esta función ya no se usa, la lógica está en onRequestService
                        },
                        requestFormViewModel = requestFormViewModel
                    )
                }
                
                composable(Route.Favorites.path) { // Destino Favoritos
                    FavoritesScreen(
                        onGoBack = { navController.popBackStack() },
                        onContactMechanic = { mechanicId ->
                            // TODO: Implementar contacto con mecánico
                        }
                    )
                }
                
                composable(Route.Appointments.path) { // Destino Citas
                    AppointmentsScreen(
                        onGoBack = { navController.popBackStack() },
                        onBookAppointment = {
                            // TODO: Implementar reserva de cita
                        },
                        isLoggedIn = isLoggedIn
                    )
                }
                
                // Pantalla de selección de roles
                composable(Route.RoleSelection.path) { // Destino Selección de Roles
                    RoleSelectionScreen(
                        onSelectRole = { role ->
                            roleViewModel.selectRole(role)
                            
                            // Verificar si el usuario ya está logueado y el rol coincide
                            val roleString = when (role) {
                                UserRole.CLIENT -> "CLIENT"
                                UserRole.MECHANIC -> "MECHANIC"
                                UserRole.ADMIN -> "ADMIN"
                            }
                            
                            if (isLoggedIn && loggedInUserRole == roleString) {
                                // Si ya está logueado con el mismo rol, ir directamente a la pantalla correspondiente
                                when (role) {
                                    UserRole.MECHANIC -> navController.navigate(Route.MechanicHome.path) {
                                        popUpTo(Route.RoleSelection.path) { inclusive = true }
                                    }
                                    UserRole.ADMIN -> navController.navigate(Route.AdminHome.path) {
                                        popUpTo(Route.RoleSelection.path) { inclusive = true }
                                    }
                                    else -> navController.navigate(Route.Home.path) {
                                        popUpTo(Route.RoleSelection.path) { inclusive = true }
                                    }
                                }
                            } else {
                                // Si no está logueado o el rol es diferente, limpiar sesión e ir a Login
                                scope.launch {
                                    userPrefs.clearSession()
                                    authViewModel.logout()
                                }
                                navController.navigate(Route.Login.path) {
                                    popUpTo(Route.RoleSelection.path) { inclusive = true }
                                }
                            }
                        }
                    )
                }
                
                // Pantalla Home para Mecánicos
                composable(Route.MechanicHome.path) { // Destino Home Mecánico
                    val currentUserId = authViewModel.getCurrentUserId() ?: 0L
                    MechanicHomeScreen(
                        requestHistoryDao = db.requestHistoryDao(),
                        mechanicId = currentUserId,
                        onGoProfile = { /* TODO: Implementar perfil mecánico */ },
                        onGoRequests = { /* TODO: Implementar solicitudes mecánico */ },
                        onGoSchedule = { /* TODO: Implementar agenda mecánico */ },
                        onGoEarnings = { /* TODO: Implementar ganancias */ },
                        onGoSettings = { navController.navigate(Route.Settings.path) },
                        onLogout = {
                            performLogout()
                            roleViewModel.logout()
                            navController.navigate(Route.RoleSelection.path) {
                                popUpTo(Route.RoleSelection.path) { inclusive = true }
                            }
                        }
                    )
                }
                
                // Pantalla de autenticación de Admin
                composable(Route.AdminAuth.path) { // Destino Auth Admin
                    AdminAuthScreen(
                        onGoBack = { navController.popBackStack() },
                        onAuthSuccess = {
                            roleViewModel.changeRole(UserRole.ADMIN)
                            navController.navigate(Route.AdminHome.path) {
                                popUpTo(Route.AdminAuth.path) { inclusive = true }
                            }
                        }
                    )
                }
                
                // Pantalla Home para Administradores
                composable(Route.AdminHome.path) { // Destino Home Admin
                    AdminHomeScreen(
                        onGoUsers = { /* TODO: Implementar gestión de usuarios */ },
                        onGoMechanics = { /* TODO: Implementar gestión de mecánicos */ },
                        onGoReports = { /* TODO: Implementar reportes */ },
                        onGoSettings = { navController.navigate(Route.Settings.path) },
                        onGoAnalytics = { /* TODO: Implementar analíticas */ },
                        onLogout = {
                            performLogout()
                            roleViewModel.logout()
                            navController.navigate(Route.RoleSelection.path) {
                                popUpTo(Route.RoleSelection.path) { inclusive = true }
                            }
                        }
                    )
                }
                
                // Pantalla de edición de perfil
                composable(Route.EditProfile.path) { // Destino Editar Perfil
                    // Obtener el email del usuario logueado desde AuthViewModel
                    // Prioridad: 1) Usuario en memoria, 2) Email de UserPreferences
                    val currentUser = authViewModel.getCurrentUser()
                    val currentUserEmail = currentUser?.email ?: ""
                    val userEmailFromPrefs = userPrefs.userEmail.collectAsStateWithLifecycle(initialValue = "").value
                    val userEmail = if (currentUserEmail.isNotEmpty()) currentUserEmail else userEmailFromPrefs
                    
                    EditProfileScreen(
                        userEmail = userEmail, // Pasar el email del usuario logueado para cargar desde el microservicio
                        onGoBack = { 
                            // ⚠️ Navegar de forma segura al Home para evitar pantallas blancas
                            navController.navigate(Route.Home.path) {
                                // Limpiar la pila de navegación hasta Home (incluyendo EditProfile)
                                popUpTo(Route.Home.path) { inclusive = false }
                                // Evitar múltiples instancias de Home
                                launchSingleTop = true
                            }
                        },
                        onSaveProfile = { name, email, phone, imageUri ->
                            // ⚠️ Navegar de forma segura al Home después de guardar
                            navController.navigate(Route.Home.path) {
                                // Limpiar la pila de navegación hasta Home (incluyendo EditProfile)
                                popUpTo(Route.Home.path) { inclusive = false }
                                // Evitar múltiples instancias de Home
                                launchSingleTop = true
                            }
                        }
                    )
                }
                
                composable(Route.Map.path) { // Destino Mapa
                    MapScreen(
                        onGoBack = { navController.popBackStack() }
                    )
                }
                
                composable(Route.Camera.path) { // Destino Cámara
                    CameraScreen(
                        onBack = { navController.popBackStack() },
                        onPhotoTaken = { photoUri ->
                            // TODO: Guardar la foto y regresar a la pantalla anterior
                            navController.popBackStack()
                        },
                        requestFormViewModel = requestFormViewModel
                    )
                }
                
                composable(Route.RequestHistory.path) { // Destino Historial de Solicitudes
                    val currentUserId = authViewModel.getCurrentUserId() ?: 1L
                    RequestHistoryScreen(
                        userId = currentUserId,
                        serviceRequestRepository = serviceRequestRepository,
                        remoteDataSource = remoteDataSource,
                        onGoBack = { navController.popBackStack() }
                    )
                }
                
                composable(Route.ForgotPassword.path) { // Destino Recuperar Contraseña
                    ForgotPasswordScreen(
                        onGoBack = { navController.popBackStack() },
                        onPasswordReset = {
                            navController.navigate(Route.Login.path) {
                                popUpTo(Route.ForgotPassword.path) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
}