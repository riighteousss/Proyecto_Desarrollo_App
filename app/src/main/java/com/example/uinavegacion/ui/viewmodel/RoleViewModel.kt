package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoleViewModel : ViewModel() {
    
    // Estado del rol actual
    private val _currentRole = MutableStateFlow<UserRole?>(null)
    val currentRole: StateFlow<UserRole?> = _currentRole.asStateFlow()
    
    // Estado de si se ha seleccionado un rol
    private val _hasSelectedRole = MutableStateFlow(false)
    val hasSelectedRole: StateFlow<Boolean> = _hasSelectedRole.asStateFlow()
    
    // Estado de si es la primera vez que se abre la app
    private val _isFirstTime = MutableStateFlow(true)
    val isFirstTime: StateFlow<Boolean> = _isFirstTime.asStateFlow()
    
    init {
        // En una app real, aquí cargarías el rol desde SharedPreferences o base de datos
        loadSavedRole()
    }
    
    fun selectRole(role: UserRole) {
        viewModelScope.launch {
            _currentRole.value = role
            _hasSelectedRole.value = true
            _isFirstTime.value = false
            
            // En una app real, aquí guardarías el rol en SharedPreferences
            saveRole(role)
        }
    }
    
    fun changeRole(role: UserRole) {
        viewModelScope.launch {
            _currentRole.value = role
            saveRole(role)
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _currentRole.value = null
            _hasSelectedRole.value = false
            _isFirstTime.value = true
            clearSavedRole()
        }
    }
    
    private fun loadSavedRole() {
        // En una app real, cargarías desde SharedPreferences
        // Por ahora, simulamos que no hay rol guardado
        viewModelScope.launch {
            // Simular carga de datos
            // _currentRole.value = loadFromPreferences()
        }
    }
    
    private fun saveRole(role: UserRole) {
        // En una app real, guardarías en SharedPreferences
        // saveToPreferences(role)
    }
    
    private fun clearSavedRole() {
        // En una app real, limpiarías SharedPreferences
        // clearPreferences()
    }
}

enum class UserRole {
    CLIENT,     // Cliente - necesita servicios
    MECHANIC,   // Mecánico - ofrece servicios
    ADMIN       // Administrador - gestiona la plataforma
}

