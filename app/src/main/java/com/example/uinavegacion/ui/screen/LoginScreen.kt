package com.example.uinavegacion.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background                 // Fondo
import androidx.compose.foundation.layout.*                   // Box/Column/Row/Spacer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*                           // Material 3
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*                             // remember y Composable
import androidx.compose.ui.Alignment                          // Alineaciones
import androidx.compose.ui.Modifier                           // Modificador
import androidx.compose.ui.text.input.*                       // KeyboardOptions/Types/Transformations
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp                            // DPs
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Observa StateFlow con lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel         // Obtiene ViewModel
import com.example.uinavegacion.ui.viewmodel.AuthViewModel         // Nuestro ViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.example.uinavegacion.R
import com.example.uinavegacion.data.local.storage.UserPreferences
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

//1 Lo primero que creamos en el archivo
@Composable                                                  // Pantalla Login conectada al VM
fun LoginScreenVm(
    vm: AuthViewModel,
    roleViewModel: com.example.uinavegacion.ui.viewmodel.RoleViewModel,
    onLoginOkNavigateHome: () -> Unit,                       // Navega a Home cuando el login es exitoso
    onGoRegister: () -> Unit,                                // Navega a Registro
    onGoForgotPassword: () -> Unit = {}                      // Navega a Recuperar Contraseña
) {
    //val vm: AuthViewModel = viewModel()                      // Crea/obtiene VM
    val state by vm.login.collectAsStateWithLifecycle()      // Observa el StateFlow en tiempo real
    val currentRole by roleViewModel.currentRole.collectAsStateWithLifecycle() // Obtener rol actual
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    // Estado para mostrar Dialog de éxito
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Guardar sesión cuando el login es exitoso
    LaunchedEffect(state.success) {
        if (state.success) {
            // Mostrar Dialog de éxito
            showSuccessDialog = true
            
            // Guardar sesión usando DataStore
            val email = state.email
            
            if (email.isNotEmpty()) {
                val result = vm.getUserByEmail(email)
                if (result.isSuccess) {
                    val userFromServer = result.getOrNull()
                    if (userFromServer != null) {
                        userPrefs.saveUserSession(
                            userId = userFromServer.id,
                            email = userFromServer.email,
                            name = userFromServer.name,
                            role = userFromServer.role,
                            phone = userFromServer.phone
                        )
                        kotlinx.coroutines.delay(500)
                    }
                } else {
                    val user = vm.getCurrentUser()
                    if (user != null) {
                        userPrefs.saveUserSession(
                            userId = user.id,
                            email = user.email,
                            name = user.name,
                            role = user.role,
                            phone = user.phone
                        )
                        kotlinx.coroutines.delay(500)
                    }
                }
            } else {
                val user = vm.getCurrentUser()
                if (user != null) {
                    userPrefs.saveUserSession(
                        userId = user.id,
                        email = user.email,
                        name = user.name,
                        role = user.role,
                        phone = user.phone
                    )
                    kotlinx.coroutines.delay(500)
                }
            }
            
            // ⚠️ CRÍTICO: Delay de 2.5 segundos para que el usuario lea el mensaje
            kotlinx.coroutines.delay(2500)
            
            // Ocultar dialog y navegar
            showSuccessDialog = false
            vm.clearLoginResult()
            kotlinx.coroutines.delay(300) // Pequeño delay para cerrar el dialog suavemente
            onLoginOkNavigateHome()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(                                             // Delegamos a UI presentacional
            email = state.email,                                 // Valor de email
            pass = state.pass,                                   // Valor de password
            emailError = state.emailError,                       // Error de email
            passError = state.passError,                         // (Opcional) error de pass en login
            canSubmit = state.canSubmit,                         // Habilitar botón
            isSubmitting = state.isSubmitting,                   // Loading
            errorMsg = state.errorMsg,                           // Error global
            onEmailChange = { vm.onLoginEmailChange(it, currentRole) }, // Handler email con validación de rol
            onPassChange = vm::onLoginPassChange,                // Handler pass
            onSubmit = vm::submitLogin,                          // Acción enviar
            onGoRegister = onGoRegister,                         // Ir a Registro
            onGoForgotPassword = onGoForgotPassword,            // Ir a Recuperar Contraseña
            isMechanic = currentRole == com.example.uinavegacion.ui.viewmodel.UserRole.MECHANIC // Indicar si es mecánico
        )
        
        // Snackbar para mensajes informativos
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // ⚠️ CRÍTICO: Dialog de éxito visible para el usuario
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { /* No permitir cerrar manualmente */ },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Éxito",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = {
                    Text(
                        text = "¡Inicio de sesión exitoso!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Text(
                        text = "Bienvenido de nuevo. Serás redirigido en un momento...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    // No mostrar botón, el dialog se cierra automáticamente
                }
            )
        }
    }
}


//2 modificamos la funcion principal haciendo private y agregando variable y elementos dle fiormulario
@Composable // Pantalla Login (solo navegación, sin formularios)
private fun LoginScreen(
    //3 Modificamos estos parametros
    email: String,                                           // Campo email
    pass: String,                                            // Campo contraseña
    emailError: String?,                                     // Error de email
    passError: String?,                                      // Error de password (opcional)
    canSubmit: Boolean,                                      // Habilitar botón
    isSubmitting: Boolean,                                   // Flag loading
    errorMsg: String?,                                       // Error global (credenciales)
    onEmailChange: (String) -> Unit,                         // Handler cambio email
    onPassChange: (String) -> Unit,                          // Handler cambio password
    onSubmit: () -> Unit,                                    // Acción enviar
    onGoRegister: () -> Unit,                                // Acción ir a registro
    onGoForgotPassword: () -> Unit = {},                     // Acción ir a recuperar contraseña
    isMechanic: Boolean = false                              // Indica si es login de mecánico
) {
    val bg = MaterialTheme.colorScheme.secondaryContainer // Fondo distinto para contraste
    //4 Agregamos la siguiente linea
    var showPass by remember { mutableStateOf(false) }        // Estado local para mostrar/ocultar contraseña

    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo
            .background(bg) // Fondo
            .padding(16.dp), // Margen
        contentAlignment = Alignment.Center // Centro
    ) {
        Column(
            //5 Anexamos el modificador
            modifier = Modifier.fillMaxWidth(),              // Ancho completo
            horizontalAlignment = Alignment.CenterHorizontally // Centrado horizontal
        ) {
            // Logo de Fixsy
            Text(
                text = "🔧",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(Modifier.height(16.dp)) // Separación
            
            Text(
                text = stringResource(R.string.login_title),
                style = MaterialTheme.typography.headlineSmall // Título
            )
            Spacer(Modifier.height(12.dp)) // Separación

            Text(
                text = stringResource(R.string.login_welcome),
                textAlign = TextAlign.Center // Alineación centrada
            )
            
            // Mensaje informativo para mecánicos
            if (isMechanic) {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "⚠️ Los correos de mecánico deben ser @mecanicofixsy.cl",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(Modifier.height(20.dp)) // Separación

            //5 Borramos los elementos anteriores y comenzamos a agregar los elementos dle formulario
// ---------- EMAIL ----------
            OutlinedTextField(
                value = email,                               // Valor actual
                onValueChange = onEmailChange,               // Notifica VM (valida email)
                label = { Text(stringResource(R.string.login_email)) },                   // Etiqueta
                singleLine = true,                           // Una línea
                isError = emailError != null,                // Marca error si corresponde
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email        // Teclado de email
                ),
                modifier = Modifier.fillMaxWidth()           // Ancho completo
            )
            // Animación para mostrar/ocultar error de email
            AnimatedVisibility(
                visible = emailError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Text(
                    text = emailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(8.dp))                    // Espacio

            // ---------- PASSWORD (oculta por defecto) ----------
            OutlinedTextField(
                value = pass,                                // Valor actual
                onValueChange = onPassChange,                // Notifica VM
                label = { Text(stringResource(R.string.login_password)) },              // Etiqueta
                singleLine = true,                           // Una línea
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(), // Toggle mostrar/ocultar
                trailingIcon = {                             // Ícono para alternar visibilidad
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) stringResource(R.string.login_hide_password) else stringResource(R.string.login_show_password)
                        )
                    }
                },
                isError = passError != null,                 // (Opcional) marcar error
                modifier = Modifier.fillMaxWidth()           // Ancho completo
            )
            if (passError != null) {                         // (Opcional) mostrar error
                Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))                   // Espacio

            // ---------- BOTÓN ENTRAR ----------
            Button(
                onClick = onSubmit,                          // Envía login
                enabled = canSubmit && !isSubmitting,        // Solo si válido y no cargando
                modifier = Modifier.fillMaxWidth()           // Ancho completo
            ) {
                if (isSubmitting) {                          // UI de carga
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.login_validating))
                } else {
                    Text(stringResource(R.string.login_button))
                }
            }

            if (errorMsg != null) {                          // Error global (credenciales)
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))                   // Espacio

            // ---------- BOTÓN IR A REGISTRO ----------
            OutlinedButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.login_create_account))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // ---------- ENLACE RECUPERAR CONTRASEÑA ----------
            TextButton(
                onClick = onGoForgotPassword,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Olvidaste tu contraseña?")
            }
            //fin modificacion de formulario
        }
    }
}
