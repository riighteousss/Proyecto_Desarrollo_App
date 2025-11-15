package com.example.uinavegacion.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * USERPREFERENCES - SISTEMA DE PERSISTENCIA CON DATASTORE
 * 
 * 🎯 PUNTO CLAVE: Sistema mejorado de persistencia usando DataStore
 * - Reemplaza SharedPreferences con una solución más moderna y type-safe
 * - Maneja el estado de sesión del usuario
 * - Almacena información del usuario logueado
 * 
 * 📊 FUNCIONALIDADES:
 * - Guardar estado de login
 * - Guardar ID del usuario actual
 * - Guardar email del usuario
 * - Guardar nombre del usuario
 * - Guardar rol del usuario
 * 
 * 🔧 VENTAJAS SOBRE SHAREDPREFERENCES:
 * - Type-safe (tipado seguro)
 * - Asíncrono por defecto
 * - Mejor manejo de errores
 * - Flujos reactivos con Flow
 */
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    // Keys para los valores guardados
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    private val userIdKey = longPreferencesKey("user_id")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val userNameKey = stringPreferencesKey("user_name")
    private val userRoleKey = stringPreferencesKey("user_role")

    // ---------- SESIÓN DE USUARIO ----------
    
    /**
     * Guarda el estado de login del usuario
     */
    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }

    /**
     * Obtiene el estado de login como Flow reactivo
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[isLoggedInKey] ?: false }

    /**
     * Guarda el ID del usuario actual
     */
    suspend fun setUserId(id: Long) {
        context.dataStore.edit { prefs ->
            prefs[userIdKey] = id
        }
    }

    /**
     * Obtiene el ID del usuario actual como Flow reactivo
     */
    val userId: Flow<Long> = context.dataStore.data
        .map { prefs -> prefs[userIdKey] ?: -1L }

    /**
     * Guarda el email del usuario actual
     */
    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[userEmailKey] = email
        }
    }

    /**
     * Obtiene el email del usuario actual como Flow reactivo
     */
    val userEmail: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[userEmailKey] ?: "" }

    /**
     * Guarda el nombre del usuario actual
     */
    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[userNameKey] = name
        }
    }

    /**
     * Obtiene el nombre del usuario actual como Flow reactivo
     */
    val userName: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[userNameKey] ?: "" }

    /**
     * Guarda el rol del usuario actual
     */
    suspend fun setUserRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[userRoleKey] = role
        }
    }

    /**
     * Obtiene el rol del usuario actual como Flow reactivo
     */
    val userRole: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[userRoleKey] ?: "CLIENT" }

    /**
     * Guarda toda la información del usuario de una vez
     */
    suspend fun saveUserSession(userId: Long, email: String, name: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = true
            prefs[userIdKey] = userId
            prefs[userEmailKey] = email
            prefs[userNameKey] = name
            prefs[userRoleKey] = role
        }
    }

    /**
     * Limpia toda la información de sesión
     */
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = false
            prefs.remove(userIdKey)
            prefs.remove(userEmailKey)
            prefs.remove(userNameKey)
            prefs.remove(userRoleKey)
        }
    }
}

