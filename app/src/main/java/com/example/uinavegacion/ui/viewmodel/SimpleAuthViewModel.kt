package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * SIMPLE AUTH VIEWMODEL - VERSIÓN SIMPLIFICADA
 * 
 * 🎯 PUNTO CLAVE: Aquí está toda la LÓGICA DE AUTENTICACIÓN
 * - Maneja login, registro y logout de usuarios
 * - StateFlow para datos reactivos (se actualiza automáticamente la UI)
 * - viewModelScope para operaciones asíncronas
 * - Datos en memoria (lista de usuarios de prueba)
 * 
 * 📊 ESTADOS PRINCIPALES:
 * - isLoggedIn: Boolean → Si el usuario está logueado
 * - currentUser: String? → Email del usuario actual
 * - errorMessage: String? → Mensajes de error
 * 
 * 🔧 FUNCIONES PRINCIPALES:
 * - login(email, password) → Iniciar sesión
 * - register(name, email, phone, password) → Registrarse
 * - logout() → Cerrar sesión
 * 
 * 💾 DATOS: Los usuarios se guardan en una lista en memoria
 * (En una app real se usaría una base de datos)
 */
class SimpleAuthViewModel : ViewModel() {
    
    // Estados simples
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Usuarios de prueba (simplificado)
    private val users = listOf(
        "usuario@fixsy.cl" to "Fixsy123!",
        "test@test.com" to "123456"
    )
    
    /**
     * Función simple de login
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            
            // Validación simple
            if (email.isEmpty() || password.isEmpty()) {
                _errorMessage.value = "Por favor completa todos los campos"
                return@launch
            }
            
            // Buscar usuario
            val user = users.find { it.first == email && it.second == password }
            
            if (user != null) {
                _isLoggedIn.value = true
                _currentUser.value = email
            } else {
                _errorMessage.value = "Email o contraseña incorrectos"
            }
        }
    }
    
    /**
     * Función simple de registro
     */
    fun register(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            
            // Validación simple
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                _errorMessage.value = "Por favor completa todos los campos"
                return@launch
            }
            
            if (!email.contains("@")) {
                _errorMessage.value = "Email inválido"
                return@launch
            }
            
            if (password.length < 6) {
                _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
                return@launch
            }
            
            // Simular registro exitoso
            _isLoggedIn.value = true
            _currentUser.value = email
        }
    }
    
    /**
     * Función simple de logout
     */
    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        _errorMessage.value = null
    }
    
    /**
     * Limpiar errores
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
