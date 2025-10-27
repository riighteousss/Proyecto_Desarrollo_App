package com.example.uinavegacion.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * PREFERENCESHELPER - Almacenamiento Local
 * 
 * Guarda datos simples localmente (como si fuera localStorage de web)
 * - SharedPreferences: Para datos simples (strings, números, booleanos)
 * - SQLite: Para datos complejos (usuarios, solicitudes, etc.)
 */
class PreferencesHelper(context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "FixsyPreferences"
        
        // Keys para los valores guardados
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_SELECTED_ROLE = "selected_role"
    }
    
    // ---------- SESIÓN DE USUARIO ----------
    
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
    
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    
    fun saveUserEmail(email: String?) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }
    
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    
    fun saveUserName(name: String?) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }
    
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)
    
    fun saveUserId(id: Long) {
        prefs.edit().putLong(KEY_USER_ID, id).apply()
    }
    
    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1L)
    
    fun saveSelectedRole(role: String?) {
        prefs.edit().putString(KEY_SELECTED_ROLE, role).apply()
    }
    
    fun getSelectedRole(): String? = prefs.getString(KEY_SELECTED_ROLE, null)
    
    // Limpiar todos los datos de sesión
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

