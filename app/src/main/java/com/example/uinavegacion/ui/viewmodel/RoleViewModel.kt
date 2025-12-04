package com.example.uinavegacion.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore para el rol del usuario
private val Context.roleDataStore by preferencesDataStore(name = "role_prefs")

class RoleViewModel : ViewModel() {
    
    // Key para DataStore
    private val roleKey = stringPreferencesKey("selected_role")
    
    // Estado del rol actual
    private val _currentRole = MutableStateFlow<UserRole?>(null)
    val currentRole: StateFlow<UserRole?> = _currentRole.asStateFlow()
    
    // Estado de si se ha seleccionado un rol
    private val _hasSelectedRole = MutableStateFlow(false)
    val hasSelectedRole: StateFlow<Boolean> = _hasSelectedRole.asStateFlow()
    
    // Estado de si es la primera vez que se abre la app
    private val _isFirstTime = MutableStateFlow(true)
    val isFirstTime: StateFlow<Boolean> = _isFirstTime.asStateFlow()
    
    // Contexto para DataStore (se inicializa cuando se llama desde la UI)
    private var appContext: Context? = null
    
    /**
     * Inicializa el contexto para DataStore
     * Debe llamarse desde la UI antes de usar otras funciones
     */
    fun initialize(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
            loadSavedRole()
        }
    }
    
    fun selectRole(role: UserRole) {
        viewModelScope.launch {
            _currentRole.value = role
            _hasSelectedRole.value = true
            _isFirstTime.value = false
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
        viewModelScope.launch {
            appContext?.let { context ->
                val savedRole = context.roleDataStore.data
                    .map { prefs -> prefs[roleKey] }
                    .first()
                
                if (savedRole != null) {
                    _currentRole.value = UserRole.valueOf(savedRole)
                    _hasSelectedRole.value = true
                    _isFirstTime.value = false
                }
            }
        }
    }
    
    private fun saveRole(role: UserRole) {
        viewModelScope.launch {
            appContext?.let { context ->
                context.roleDataStore.edit { prefs ->
                    prefs[roleKey] = role.name
                }
            }
        }
    }
    
    private fun clearSavedRole() {
        viewModelScope.launch {
            appContext?.let { context ->
                context.roleDataStore.edit { prefs ->
                    prefs.remove(roleKey)
                }
            }
        }
    }
}

enum class UserRole {
    CLIENT,     // Cliente - necesita servicios
    MECHANIC,   // Mecánico - ofrece servicios
    ADMIN       // Administrador - gestiona la plataforma
}

