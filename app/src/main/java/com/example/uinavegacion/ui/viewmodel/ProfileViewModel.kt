package com.example.uinavegacion.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Datos del perfil de usuario
 * Almacena toda la información personal del usuario
 */
data class UserProfile(
    val name: String = "",                    // Nombre completo del usuario
    val email: String = "",                   // Email del usuario
    val phone: String = "",                   // Teléfono del usuario
    val profileImageUri: String? = null,      // URI de la imagen de perfil
    val password: String = ""                 // Contraseña del usuario
)

/**
 * PROFILEVIEWMODEL - MANEJO DEL PERFIL DE USUARIO
 * 
 * Este ViewModel maneja toda la información del perfil del usuario:
 * - Carga y guarda datos del perfil usando SharedPreferences
 * - Maneja la imagen de perfil
 * - Valida cambios de contraseña y email
 * - Persiste los datos entre sesiones
 */
class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    /**
     * Carga el perfil del usuario desde SharedPreferences
     * Se ejecuta al inicializar la pantalla de perfil
     */
    fun loadProfile(context: Context) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            _userProfile.value = UserProfile(
                name = prefs.getString("name", "") ?: "",
                email = prefs.getString("email", "") ?: "",
                phone = prefs.getString("phone", "") ?: "",
                profileImageUri = prefs.getString("profileImageUri", null),
                password = prefs.getString("password", "") ?: ""
            )
        }
    }
    
    fun updateProfile(name: String, email: String, phone: String, imageUri: String?, context: Context) {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("name", name)
                putString("email", email)
                putString("phone", phone)
                if (imageUri != null) putString("profileImageUri", imageUri)
                apply()
            }
            _userProfile.value = UserProfile(
                name = name,
                email = email,
                phone = phone,
                profileImageUri = imageUri,
                password = _userProfile.value.password
            )
        }
    }
    
    fun updatePassword(currentPassword: String, newPassword: String, context: Context): Boolean {
        return if (_userProfile.value.password == currentPassword) {
            viewModelScope.launch {
                val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("password", newPassword)
                    apply()
                }
                _userProfile.value = _userProfile.value.copy(password = newPassword)
            }
            true
        } else {
            false
        }
    }
    
    fun updateEmail(newEmail: String, currentPassword: String, context: Context): Boolean {
        return if (_userProfile.value.password == currentPassword) {
            viewModelScope.launch {
                val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("email", newEmail)
                    apply()
                }
                _userProfile.value = _userProfile.value.copy(email = newEmail)
            }
            true
        } else {
            false
        }
    }
}
