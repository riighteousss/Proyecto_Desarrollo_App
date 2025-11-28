package com.example.uinavegacion.navigation

// Clase sellada para rutas: evita "strings mágicos" y facilita refactors
sealed class Route(val path: String) { // Cada objeto representa una pantalla
    data object Home     : Route("home")     // Ruta Home
    data object Login    : Route("login")    // Ruta Login
    data object Register : Route("register") // Ruta Registro
    data object Requests : Route("requests") // Ruta Solicitudes
    data object AssistanceChoice : Route("assistance_choice")
    data object Schedule         : Route("schedule")
    data object Emergency        : Route("emergency")
    data object Mechanics        : Route("mechanics")
    data object Profile          : Route("profile")
    data object Settings         : Route("settings")
    data object MyVehicles       : Route("my_vehicles")
    data object MyAddresses      : Route("my_addresses")
    data object Help             : Route("help")
    data object EmergencyService : Route("emergency_service")
    data object RequestService   : Route("request_service")
    data object Favorites        : Route("favorites")
    data object Appointments     : Route("appointments")
    data object RoleSelection    : Route("role_selection")
    data object MechanicHome     : Route("mechanic_home")
    data object AdminHome        : Route("admin_home")
    data object AdminAuth        : Route("admin_auth")
    data object EditProfile      : Route("edit_profile")
    data object Map              : Route("map")
    data object Camera          : Route("camera")
    data object RequestHistory  : Route("request_history")
    data object Splash          : Route("splash")
}

/**
 * Routes - Definición de rutas de navegación
 * 
 * Evita "strings mágicos" centralizando todas las rutas en una clase sellada.
 * Si necesitas cambiar una ruta, solo modificas este archivo.
 */