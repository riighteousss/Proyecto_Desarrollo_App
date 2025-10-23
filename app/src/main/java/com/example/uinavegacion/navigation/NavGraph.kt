package com.example.uinavegacion.navigation
import androidx.compose.foundation.layout.padding // Para aplicar innerPadding
import androidx.compose.material3.Scaffold // Estructura base con slots
import androidx.compose.runtime.Composable // Marcador composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier // Modificador
import androidx.navigation.NavHostController // Controlador de navegación
import androidx.navigation.compose.NavHost // Contenedor de destinos
import androidx.navigation.compose.composable // Declarar cada destino
import kotlinx.coroutines.launch // Para abrir/cerrar drawer con corrutinas

import androidx.compose.material3.ModalNavigationDrawer // Drawer lateral modal
import androidx.compose.material3.rememberDrawerState // Estado del drawer
import androidx.compose.material3.DrawerValue // Valores (Opened/Closed)
import androidx.compose.runtime.rememberCoroutineScope // Alcance de corrutina


import com.example.uinavegacion.ui.components.AppTopBar // Barra superior
import com.example.uinavegacion.ui.components.AppDrawer // Drawer composable
import com.example.uinavegacion.ui.components.defaultDrawerItems // Ítems por defecto
import com.example.uinavegacion.ui.screen.HomeScreen // Pantalla Home
import com.example.uinavegacion.ui.screen.AssistanceChoiceScreen
import com.example.uinavegacion.ui.screen.ScheduleScreen
import com.example.uinavegacion.ui.screen.EmergencyScreen
import com.example.uinavegacion.ui.screen.MechanicsListScreen
import com.example.uinavegacion.ui.screen.ProfileScreen
import com.example.uinavegacion.ui.screen.RequestsScreen // Pantalla Solicitudes
import com.example.uinavegacion.ui.screen.LoginScreenVm // Pantalla Login
import com.example.uinavegacion.ui.screen.RegisterScreenVm // Pantalla Registro
import com.example.uinavegacion.ui.screen.SettingsScreen // Pantalla Configuraciones
import com.example.uinavegacion.ui.screen.MyVehiclesScreen // Pantalla Mis Vehículos
import com.example.uinavegacion.ui.screen.MyAddressesScreen // Pantalla Mis Direcciones
import com.example.uinavegacion.ui.screen.HelpScreen // Pantalla Ayuda
import com.example.uinavegacion.ui.screen.EmergencyScreen // Pantalla Emergencia
import com.example.uinavegacion.ui.screen.RequestServiceScreen // Pantalla Solicitar Servicio
import com.example.uinavegacion.ui.screen.FavoritesScreen // Pantalla Favoritos
import com.example.uinavegacion.ui.screen.AppointmentsScreen // Pantalla Citas
import com.example.uinavegacion.ui.screen.RoleSelectionScreen // Pantalla Selección de Roles
import com.example.uinavegacion.ui.screen.MechanicHomeScreen // Pantalla Home para Mecánicos
import com.example.uinavegacion.ui.screen.AdminHomeScreen // Pantalla Home para Administradores
import com.example.uinavegacion.ui.screen.AdminAuthScreen // Pantalla Autenticación de Admin
import com.example.uinavegacion.ui.viewmodel.AuthViewModel
import com.example.uinavegacion.ui.viewmodel.ServiceViewModel
import com.example.uinavegacion.ui.viewmodel.ThemeViewModel
import com.example.uinavegacion.ui.viewmodel.VehicleViewModel
import com.example.uinavegacion.ui.viewmodel.AddressViewModel
import com.example.uinavegacion.ui.viewmodel.RoleViewModel
import com.example.uinavegacion.ui.viewmodel.UserRole

@Composable // Gráfico de navegación + Drawer + Scaffold
fun AppNavGraph(navController: NavHostController,
                authViewModel: AuthViewModel,
                serviceViewModel: ServiceViewModel,
                themeViewModel: ThemeViewModel,
                vehicleViewModel: VehicleViewModel,
                addressViewModel: AddressViewModel,
                roleViewModel: RoleViewModel) { // Recibe el controlador

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
    val goRequests: () -> Unit = { navController.navigate(Route.Requests.path) } // Ir a Solicitudes
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
                    onLogin = goLogin,   // Botón Login
                    onRegister = goRegister // Botón Registro
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
                        onGoAppointments = { navController.navigate(Route.Appointments.path) }
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
                        vehicleViewModel = vehicleViewModel,
                        isLoggedIn = isLoggedIn,
                        onGoLogin = goLogin,
                        onAddVehicle = { /* TODO: Implementar agregar vehículo */ },
                        onEditVehicle = { /* TODO: Implementar editar vehículo */ },
                        onDeleteVehicle = { /* TODO: Implementar eliminar vehículo */ }
                    )
                }
                composable(Route.MyAddresses.path) { // Destino Mis Direcciones
                    MyAddressesScreen(
                        addressViewModel = addressViewModel,
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
                        }
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
                        }
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
            }
        }
    }
}