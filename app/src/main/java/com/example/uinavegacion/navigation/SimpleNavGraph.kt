package com.example.uinavegacion.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uinavegacion.ui.screen.SimpleHomeScreen
import com.example.uinavegacion.ui.screen.SimpleLoginScreen
import com.example.uinavegacion.ui.screen.SimpleRegisterScreen

/**
 * SIMPLE NAVIGATION GRAPH - VERSIÓN SIMPLIFICADA
 * 
 * 🎯 PUNTO CLAVE: Aquí está toda la NAVEGACIÓN de la aplicación
 * - NavHost es el contenedor de todas las pantallas
 * - startDestination = "home" define cuál pantalla se muestra primero
 * - composable("home") define cada pantalla y su ruta
 * - navController.navigate() cambia entre pantallas
 * 
 * 📱 PANTALLAS DISPONIBLES:
 * - "home" → SimpleHomeScreen (pantalla principal)
 * - "login" → SimpleLoginScreen (iniciar sesión)
 * - "register" → SimpleRegisterScreen (registrarse)
 * 
 * 🔄 FLUJO DE NAVEGACIÓN:
 * Home → Login → Home (después de login exitoso)
 * Home → Register → Home (después de registro exitoso)
 */
@Composable
fun SimpleNavGraph(
    navController: NavHostController = rememberNavController()
) {
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                SimpleHomeScreen(
                    onGoLogin = { navController.navigate("login") },
                    onGoRegister = { navController.navigate("register") },
                    onGoProfile = { /* TODO: Implementar perfil */ },
                    onGoSettings = { /* TODO: Implementar configuración */ }
                )
            }
            
            composable("login") {
                SimpleLoginScreen(
                    onLoginSuccess = { navController.navigate("home") },
                    onGoRegister = { navController.navigate("register") }
                )
            }
            
            composable("register") {
                SimpleRegisterScreen(
                    onRegisterSuccess = { navController.navigate("home") },
                    onGoLogin = { navController.navigate("login") }
                )
            }
        }
    }
}
