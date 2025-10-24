package com.example.uinavegacion.ui.viewmodel

import androidx.lifecycle.ViewModel                       // Base de ViewModel
import androidx.lifecycle.viewModelScope                  // Scope de corrutinas ligado al VM
import com.example.uinavegacion.data.repository.UserRepository
import kotlinx.coroutines.delay                            // Simulamos tareas async (IO/red)
import kotlinx.coroutines.flow.MutableStateFlow            // Estado observable mutable
import kotlinx.coroutines.flow.StateFlow                   // Exposición inmutable
import kotlinx.coroutines.flow.update                      // Helper para actualizar flows
import kotlinx.coroutines.launch                            // Lanzar corrutinas
import com.example.uinavegacion.domain.validation.*             // Importamos las funciones de validación

/**
 * AUTHVIEWMODEL - MANEJO DE AUTENTICACIÓN
 * 
 * 🎯 PUNTO CLAVE: Aquí está toda la LÓGICA DE AUTENTICACIÓN
 * - Maneja login, registro y logout de usuarios
 * - StateFlow para datos reactivos (se actualiza automáticamente la UI)
 * - viewModelScope para operaciones asíncronas
 * - Datos en memoria (lista de usuarios de prueba)
 * 
 * 📊 ESTADOS PRINCIPALES:
 * - login: LoginUiState → Estado del login
 * - register: RegisterUiState → Estado del registro
 * - isLoggedIn: Boolean → Si el usuario está logueado
 * 
 * 🔧 FUNCIONES PRINCIPALES:
 * - onLoginEmailChange() → Maneja cambios en email
 * - onLoginPasswordChange() → Maneja cambios en contraseña
 * - onLoginSubmit() → Ejecuta el login
 * - onRegisterSubmit() → Ejecuta el registro
 * 
 * 💾 DATOS: Los usuarios se guardan en una lista en memoria
 * (En una app real se usaría una base de datos)
 */

// ----------------- ESTADOS DE UI (observable con StateFlow) -----------------

data class LoginUiState(                                   // Estado de la pantalla Login
    val email: String = "",                                // Campo email
    val pass: String = "",                                 // Campo contraseña (texto)
    val emailError: String? = null,                        // Error de email
    val passError: String? = null,                         // (Opcional) error de pass en login
    val isSubmitting: Boolean = false,                     // Flag de carga
    val canSubmit: Boolean = false,                        // Habilitar botón
    val success: Boolean = false,                          // Resultado OK
    val errorMsg: String? = null                           // Error global (credenciales inválidas)
)

data class RegisterUiState(                                // Estado de la pantalla Registro (<= 5 campos)
    val name: String = "",                                 // 1) Nombre
    val email: String = "",                                // 2) Email
    val phone: String = "",                                // 3) Teléfono
    val pass: String = "",                                 // 4) Contraseña
    val confirm: String = "",                              // 5) Confirmación

    val nameError: String? = null,                         // Errores por campo
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,                     // Flag de carga
    val canSubmit: Boolean = false,                        // Habilitar botón
    val success: Boolean = false,                          // Resultado OK
    val errorMsg: String? = null                           // Error global (ej: duplicado)
)

// ----------------- COLECCIÓN EN MEMORIA (solo para la demo) -----------------

// Modelo mínimo de usuario para la colección
private data class DemoUser(                               // Datos que vamos a guardar en la colección
    val name: String,                                      // Nombre
    val email: String,                                     // Email (lo usamos como “id”)
    val phone: String,                                     // Teléfono
    val pass: String                                       // Contraseña en texto (solo demo; no producción)
)

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {                         // ViewModel que maneja Login/Registro

    // Colección **estática** en memoria compartida entre instancias del VM (sin storage persistente)
    companion object {
        // Lista mutable de usuarios para la demo (se pierde al cerrar la app)
        private val USERS = mutableListOf(
            // Usuario por defecto para probar login:
            DemoUser(name = "Usuario Fixsy", email = "usuario@fixsy.cl", phone = "12345678", pass = "Fixsy123!"),
            // Usuario de prueba simple:
            DemoUser(name = "Usuario Prueba", email = "test@test.com", phone = "+56 9 1234 5678", pass = "123456")
        )
    }

    // Flujos de estado para observar desde la UI
    private val _login = MutableStateFlow(LoginUiState())   // Estado interno (Login)
    val login: StateFlow<LoginUiState> = _login             // Exposición inmutable

    private val _register = MutableStateFlow(RegisterUiState()) // Estado interno (Registro)
    val register: StateFlow<RegisterUiState> = _register        // Exposición inmutable

    // Estado de autenticación global
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // ----------------- LOGIN: handlers y envío -----------------

    /**
     * Maneja cambios en el campo de email durante el login
     * Valida el formato del email y actualiza el estado
     */
    fun onLoginEmailChange(value: String) {                 // Handler cuando cambia el email
        _login.update { it.copy(email = value, emailError = validateEmail(value)) } // Guardamos + validamos
        recomputeLoginCanSubmit()                           // Recalculamos habilitado
    }

    /**
     * Maneja cambios en el campo de contraseña durante el login
     * No valida la contraseña en login (solo en registro)
     */
    fun onLoginPassChange(value: String) {                  // Handler cuando cambia la contraseña
        _login.update { it.copy(pass = value) }             // Guardamos (sin validar fuerza aquí)
        recomputeLoginCanSubmit()                           // Recalculamos habilitado
    }

    private fun recomputeLoginCanSubmit() {                 // Regla para habilitar botón "Entrar"
        val s = _login.value                                // Tomamos el estado actual
        val can = s.emailError == null &&                   // Email válido
                s.email.isNotBlank() &&                   // Email no vacío
                s.pass.isNotBlank()                       // Password no vacía
        _login.update { it.copy(canSubmit = can) }          // Actualizamos el flag
    }

    /**
     * Procesa el intento de login del usuario
     * Busca el usuario en la colección en memoria y valida las credenciales
     * Actualiza el estado de autenticación global
     */
    fun submitLogin() {                                     // Acción de login (simulación async)
        val s = _login.value                                // Snapshot del estado
        if (!s.canSubmit || s.isSubmitting) return          // Si no se puede o ya está cargando, salimos
        viewModelScope.launch {                             // Lanzamos corrutina
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) } // Seteamos loading
            delay(500)                                      // Simulamos tiempo de verificación

            // Buscamos en la **colección en memoria** un usuario con ese email
            val user = USERS.firstOrNull { it.email.equals(s.email, ignoreCase = true) }

            // Debug: imprimir información
            println("DEBUG - Intentando login con email: ${s.email}")
            println("DEBUG - Usuarios registrados: ${USERS.map { it.email }}")
            println("DEBUG - Usuario encontrado: ${user?.email}")
            println("DEBUG - Contraseña ingresada: ${s.pass}")
            println("DEBUG - Contraseña del usuario: ${user?.pass}")

            // ¿Coincide email + contraseña?
            val ok = user != null && user.pass == s.pass

            _login.update {                                 // Actualizamos con el resultado
                it.copy(
                    isSubmitting = false,                   // Fin carga
                    success = ok,                           // true si credenciales correctas
                    errorMsg = if (!ok) {
                        if (user == null) "Email no encontrado" else "Contraseña incorrecta"
                    } else null // Mensaje si falla
                )
            }
            
            // Actualizar estado de login global
            _isLoggedIn.value = ok
        }
    }

    fun clearLoginResult() {                                // Limpia banderas tras navegar
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO: handlers y envío -----------------

    fun onNameChange(value: String) {                       // Handler del nombre
        val filtered = value.filter { it.isLetter() || it.isWhitespace() } // Filtramos números/símbolos (solo letras/espacios)
        _register.update {                                  // Guardamos + validamos
            it.copy(name = filtered, nameError = validateNameLettersOnly(filtered))
        }
        recomputeRegisterCanSubmit()                        // Recalculamos habilitado
    }

    fun onRegisterEmailChange(value: String) {              // Handler del email
        _register.update { it.copy(email = value, emailError = validateEmail(value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {                      // Handler del teléfono
        val digitsOnly = value.filter { it.isDigit() }      // Dejamos solo dígitos
        _register.update {                                  // Guardamos + validamos
            it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {               // Handler de la contraseña
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) } // Validamos seguridad
        // Revalidamos confirmación con la nueva contraseña
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {                    // Handler de confirmación
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {              // Habilitar "Registrar" si todo OK
        val s = _register.value                              // Tomamos el estado actual
        val noErrors = listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null } // Sin errores
        val filled = s.name.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank() // Todo lleno
        _register.update { it.copy(canSubmit = noErrors && filled) } // Actualizamos flag
    }

    fun submitRegister() {                                  // Acción de registro (simulación async)
        val s = _register.value                              // Snapshot del estado
        if (!s.canSubmit || s.isSubmitting) return          // Evitamos reentradas
        viewModelScope.launch {                             // Corrutina
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) } // Loading
            delay(700)                                      // Simulamos IO

            // Verificar si el email ya existe
            val existingUser = USERS.firstOrNull { it.email.equals(s.email, ignoreCase = true) }
            
            if (existingUser != null) {
                // Email ya existe
                _register.update { 
                    it.copy(
                        isSubmitting = false, 
                        success = false,
                        errorMsg = "Este email ya está registrado"
                    )
                }
            } else {
                // Crear nuevo usuario y agregarlo a la lista
                val newUser = DemoUser(
                    name = s.name,
                    email = s.email,
                    phone = s.phone,
                    pass = s.pass
                )
                USERS.add(newUser)
                
                _register.update {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                }
            }
        }
    }

    fun clearRegisterResult() {                             // Limpia banderas tras navegar
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- FUNCIONES DE AUTENTICACIÓN -----------------

    fun logout() {                                          // Cerrar sesión
        _isLoggedIn.value = false
        _login.update { LoginUiState() }                    // Limpiar estado de login
        _register.update { RegisterUiState() }              // Limpiar estado de registro
    }

    // Función de debug para ver usuarios registrados
    fun getRegisteredUsers(): List<String> {
        return USERS.map { it.email }
    }

    // Obtener el nombre del usuario actualmente logueado
    fun getCurrentUserName(): String? {
        return if (_isLoggedIn.value) {
            // Buscar el usuario logueado por email (necesitamos guardar el email del usuario logueado)
            val currentEmail = _login.value.email
            USERS.firstOrNull { it.email.equals(currentEmail, ignoreCase = true) }?.name
        } else {
            null
        }
    }
}