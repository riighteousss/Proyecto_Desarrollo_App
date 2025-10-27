/**
 * NAVGRAPH - NAVEGACIÓN PRINCIPAL
 * 
 * 🎯 PUNTO CLAVE: Aquí está toda la NAVEGACIÓN de la aplicación
 * - NavHost es el contenedor de todas las pantallas
 * - startDestination define cuál pantalla se muestra primero
 * - composable() define cada pantalla y su ruta
 * - navController.navigate() cambia entre pantallas
 * 
 * 📱 PANTALLAS DISPONIBLES:
 * - HomeScreen (pantalla principal)
 * - LoginScreen (iniciar sesión)
 * - RegisterScreen (registrarse)
 * - ProfileScreen (perfil de usuario)
 * - Y muchas más...
 * 
 * 🔄 FLUJO DE NAVEGACIÓN:
 * Home → Login → Home (después de login exitoso)
 * Home → Register → Home (después de registro exitoso)
 */
package com.example.uinavegacion.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uinavegacion.data.local.database.AppDatabase
import com.example.uinavegacion.ui.components.AppDrawer
import com.example.uinavegacion.ui.components.AppTopBar
import com.example.uinavegacion.ui.components.defaultDrawerItems
import com.example.uinavegacion.ui.screen.*
import com.example.uinavegacion.ui.viewmodel.*
import kotlinx.coroutines.launch

@Composable // Gráfico de navegación + Drawer + Scaffold
fun AppNavGraph(navController: NavHostController,
                authViewModel: AuthViewModel,
                serviceViewModel: ServiceViewModel,
                themeViewModel: ThemeViewModel,
                vehicleViewModel: VehicleViewModel,
                addressViewModel: AddressViewModel,
                mechanicViewModel: MechanicViewModel,
                roleViewModel: RoleViewModel,
                db: com.example.uinavegacion.data.local.database.AppDatabase,
                requestFormViewModel: RequestFormViewModel) { // Recibe el controlador

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Estado del drawer
    val scope = rememberCoroutineScope() // Necesario para abrir/cerrar drawer
    
    // Estados observables
    val isDarkModeState = themeViewModel.isDarkMode.collectAsStateWithLifecycle()
    val isLoggedInState = authViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentRoleState = roleViewModel.currentRole.collectAsStateWithLifecycle()
    val hasSelectedRoleState = roleViewModel.hasSelectedRole.collectAsStateWithLifecycle()
    val isFirstTimeState = roleViewModel.isFirstTime.collectAsStateWithLifecycle()
    
    val isDarkMode = isDarkModeState.value
    val isLoggedIn = isLoggedInState.value
    val currentRole = currentRoleState.value
    val hasSelectedRole = hasSelectedRoleState.value
    val isFirstTime = isFirstTimeState.value

    // Helpers de navegación (reutilizamos en topbar/drawer/botones)
    val goHome: () -> Unit    = { navController.navigate(Route.Home.path) }    // Ir a Home
    val goLogin: () -> Unit   = { navController.navigate(Route.Login.path) }   // Ir a Login
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) } // Ir a Registro
    val goRequests: () -> Unit = { navController.navigate(Route.RequestHistory.path) } // Ir a Historial de Solicitudes
    val goSettings: () -> Unit = { navController.navigate(Route.Settings.path) } // Ir a Configuraciones
    val goVehicles: () -> Unit = { navController.navigate(Route.MyVehicles.path) } // Ir a Mis Vehículos
    val goAddresses: () -> Unit = { navController.navigate(Route.MyAddresses.path) } // Ir a Mis Direcciones
    val goHelp: () -> Unit = { navController.navigate(Route.Help.path) } // Ir a Ayuda

    ModalNavigationDrawer( // Capa superior con drawer lateral
        drawerState = drawerState, // Estado del drawer
        drawerContent = { // Contenido del drawer (menú)
            AppDrawer( // Nuestro componente Drawer
                currentRoute = null, // Puedes pasar navController.currentBackStackEntry?.destination?.route
                items = defaultDrawerItems( // Lista estándar
                    onHome = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goHome() // Navega a Home
                    },
                    onLogin = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goLogin() // Navega a Login
                    },
                    onRegister = {
                        scope.launch { drawerState.close() } // Cierra drawer
                        goRegister() // Navega a Registro
                    }
                )
            )
        }
    ) {
        Scaffold( // Estructura base de pantalla
            topBar = { // Barra superior con íconos/menú
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } }, // Abre drawer
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
        ) { innerPadding -> // Padding que evita solapar contenido
            NavHost( // Contenedor de destinos navegables
                navController = navController, // Controlador
                startDestination = when {
                    isFirstTime -> Route.RoleSelection.path
                    currentRole == UserRole.MECHANIC -> Route.MechanicHome.path
                    currentRole == UserRole.ADMIN -> Route.AdminHome.path
                    else -> Route.Home.path
                }, // Inicio basado en rol
                modifier = Modifier.padding(innerPadding) // Respeta topBar
            ) {
                composable(Route.Home.path) { // Destino Home
                    HomeScreen(
                        onGoLogin = goLogin,     // Botón para ir a Login
                        onGoRegister = goRegister, // Botón para ir a Registro
                        onGoRequests = goRequests, // Botón para ir a Solicitudes
                        userName = authViewModel.getCurrentUserName(), // Nombre del usuario logueado
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
                    EmergencyScreen(
                        onGoBack = { navController.popBackStack() },
                        onRequestService = { type, description, location ->
                            // TODO: Implementar lógica de solicitud de emergencia
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
                            authViewModel.logout()
                            navController.navigate(Route.Home.path) {
                                popUpTo(Route.Home.path) { inclusive = true }
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
                        vm=authViewModel,
                        onLoginOkNavigateHome = goHome,            // Si el VM marca success=true, navegamos a Home
                        onGoRegister = goRegister                  // Enlace para ir a la pantalla de Registro
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
                            authViewModel.logout()
                            navController.navigate(Route.Home.path) {
                                popUpTo(Route.Home.path) { inclusive = true }
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
                
                // Nuevas pantallas de servicios rápidos
                composable(Route.EmergencyService.path) { // Destino Emergencia
                    EmergencyScreen(
                        onGoBack = { navController.popBackStack() },
                        onRequestService = { type, description, location ->
                            // TODO: Implementar lógica de solicitud de emergencia
                            navController.popBackStack()
                        }
                    )
                }
                
                composable(Route.RequestService.path) { // Destino Solicitar Servicio
                    RequestServiceScreen(
                        onGoBack = { navController.popBackStack() },
                        onRequestService = { service, vehicle, description, images ->
                            // TODO: Implementar lógica de solicitud de servicio
                            navController.popBackStack()
                        },
                        onGoCamera = { navController.navigate(Route.Camera.path) },
                        onSaveToHistory = { service, vehicle, description, images ->
                            // Guardar en el historial de solicitudes
                            val requestHistory = com.example.uinavegacion.data.local.request.RequestHistoryEntity(
                                userId = 1L, // TODO: Obtener ID del usuario logueado
                                serviceType = service,
                                vehicleInfo = vehicle,
                                description = description,
                                status = "Pendiente",
                                images = images.joinToString(",")
                            )
                            // TODO: Implementar guardado en base de datos
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
                            when (role) {
                                UserRole.CLIENT -> navController.navigate(Route.Home.path) {
                                    popUpTo(Route.RoleSelection.path) { inclusive = true }
                                }
                                UserRole.MECHANIC -> navController.navigate(Route.MechanicHome.path) {
                                    popUpTo(Route.RoleSelection.path) { inclusive = true }
                                }
                                UserRole.ADMIN -> navController.navigate(Route.AdminHome.path) {
                                    popUpTo(Route.RoleSelection.path) { inclusive = true }
                                }
                            }
                        }
                    )
                }
                
                // Pantalla Home para Mecánicos
                composable(Route.MechanicHome.path) { // Destino Home Mecánico
                    MechanicHomeScreen(
                        onGoProfile = { /* TODO: Implementar perfil mecánico */ },
                        onGoRequests = { /* TODO: Implementar solicitudes mecánico */ },
                        onGoSchedule = { /* TODO: Implementar agenda mecánico */ },
                        onGoEarnings = { /* TODO: Implementar ganancias */ },
                        onGoSettings = { navController.navigate(Route.Settings.path) },
                        onLogout = {
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
                            roleViewModel.logout()
                            navController.navigate(Route.RoleSelection.path) {
                                popUpTo(Route.RoleSelection.path) { inclusive = true }
                            }
                        }
                    )
                }
                
                // Pantalla de edición de perfil
                composable(Route.EditProfile.path) { // Destino Editar Perfil
                    EditProfileScreen(
                        onGoBack = { navController.popBackStack() },
                        onSaveProfile = { name, email, phone, imageUri ->
                            // TODO: Implementar guardado de perfil
                            navController.popBackStack()
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
                    RequestHistoryScreen(
                        userId = 1L, // TODO: Obtener ID del usuario logueado
                        requestHistoryDao = db.requestHistoryDao(),
                        onGoBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}