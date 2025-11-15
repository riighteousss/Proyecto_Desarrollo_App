package com.example.uinavegacion.ui.theme

import androidx.compose.ui.graphics.Color

// 🎨 Paleta principal Fixsy
val OrangePrimary = Color(0xFFFF9800)     // Naranja brillante
val OrangeSecondary = Color(0xFFFFB74D)   // Naranja claro
val BlueAccent = Color(0xFF2196F3)        // Azul vibrante
val BlueDark = Color(0xFF1976D2)          // Azul oscuro
val ErrorRed = Color(0xFFEF5350)          // Rojo de error

// ☀️ Modo claro
val BackgroundLight = Color(0xFFFDFDFD)
val TextOnPrimaryLight = Color.White
val TextOnSecondaryLight = Color.White
val TextOnBackgroundLight = Color.Black
val TextOnSurfaceLight = Color.Black

// 🌙 Modo oscuro
val BackgroundDark = Color(0xFF121212)
val TextOnPrimaryDark = Color.White
val TextOnSecondaryDark = Color.White
val TextOnBackgroundDark = Color(0xFFECECEC)
val TextOnSurfaceDark = Color(0xFFECECEC)

// 🎯 Colores de estado y acciones
val SuccessGreen = Color(0xFF4CAF50)      // Verde éxito
val WarningOrange = Color(0xFFFF9800)     // Naranja advertencia
val InfoBlue = Color(0xFF2196F3)          // Azul información
val DangerRed = Color(0xFFF44336)          // Rojo peligro
val PurpleAccent = Color(0xFF9C27B0)      // Morado acento
val GrayNeutral = Color(0xFF9E9E9E)       // Gris neutro
val BlueGray = Color(0xFF607D8B)          // Azul gris

// 🏠 Colores específicos por pantalla/componente
// Pantalla de emergencia
val EmergencyRed = Color(0xFFE53E3E)      // Rojo emergencia

// Pantalla de perfil
val ProfileDanger = Color(0xFFE53E3E)     // Rojo peligro perfil

// Pantalla de configuración
val SettingsDanger = Color(0xFFE53E3E)    // Rojo configuración

// Pantalla de ayuda
val StarGold = Color(0xFFFFD700)          // Dorado estrella
val StarGray = Color.Gray                 // Gris estrella

// Pantalla de citas
val ErrorText = Color(0xFFB00020)         // Rojo texto error

// Componentes de mapa
val MapGreen = Color(0xFFE8F5E8)          // Verde mapa

// 🎨 Colores de transparencia
val WhiteTransparent10 = Color.White.copy(alpha = 0.1f)
val WhiteTransparent30 = Color.White.copy(alpha = 0.3f)
val WhiteTransparent90 = Color.White.copy(alpha = 0.9f)

// 📊 Colores de estado de solicitudes
val RequestCompleted = Color(0xFF4CAF50)  // Completado
val RequestInProgress = Color(0xFFFF9800) // En proceso
val RequestPending = Color(0xFF2196F3)    // Pendiente
val RequestCancelled = Color(0xFFF44336)  // Cancelado
val RequestConfirmed = Color(0xFF4CAF50)   // Confirmado

// 🔧 Colores de acciones rápidas mecánico
val ActionRequests = Color(0xFF4CAF50)    // Solicitudes
val ActionSchedule = Color(0xFF2196F3)    // Agenda
val ActionEarnings = Color(0xFFFF9800)    // Ganancias
val ActionProfile = Color(0xFF9C27B0)     // Perfil

// 👨‍💼 Colores de acciones admin
val AdminUsers = Color(0xFF4CAF50)        // Usuarios
val AdminMechanics = Color(0xFF2196F3)    // Mecánicos
val AdminReports = Color(0xFFFF9800)      // Reportes
val AdminAnalytics = Color(0xFF9C27B0)   // Analíticas
val AdminSettings = Color(0xFF607D8B)     // Configuración

// 🎭 Colores de roles
val RoleUser = Color(0xFF4CAF50)          // Usuario
val RoleMechanic = Color(0xFF2196F3)      // Mecánico

// Legacy colors (mantener compatibilidad)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)