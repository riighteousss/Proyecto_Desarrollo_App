@file:Suppress("DEPRECATION")
package com.example.uinavegacion.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.example.uinavegacion.R
import com.example.uinavegacion.data.repository.UserRepository
import com.example.uinavegacion.data.remote.RemoteDataSource
import com.example.uinavegacion.data.remote.RetrofitClient
import com.example.uinavegacion.domain.validation.*
import kotlinx.coroutines.launch

/**
 * Pantalla de recuperación de contraseña
 * Permite al usuario solicitar un token de recuperación y resetear su contraseña
 */
@Composable
fun ForgotPasswordScreen(
    onGoBack: () -> Unit,
    onPasswordReset: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Repository para recuperación de contraseña
    val userRepository = remember {
        UserRepository(RemoteDataSource(
            userApiService = RetrofitClient.userApiService,
            serviceRequestApiService = RetrofitClient.serviceRequestApiService,
            vehicleApiService = RetrofitClient.vehicleApiService,
            imageApiService = RetrofitClient.imageApiService
        ))
    }
    
    // Estados
    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 = Solicitar token, 2 = Resetear contraseña
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var tokenError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    
    // Función para solicitar token
    fun requestToken() {
        emailError = validateEmail(email)
        if (emailError != null) return
        
        scope.launch {
            isLoading = true
            errorMessage = ""
            successMessage = ""
            
            val result = userRepository.forgotPassword(email)
            result.onSuccess {
                successMessage = it
                snackbarHostState.showSnackbar(it)
                step = 2 // Avanzar al siguiente paso
            }.onFailure {
                errorMessage = it.message ?: "Error al solicitar recuperación de contraseña"
                snackbarHostState.showSnackbar(errorMessage)
            }
            isLoading = false
        }
    }
    
    // Función para resetear contraseña
    fun resetPassword() {
        tokenError = if (token.isBlank()) "El token es obligatorio" else null
        passwordError = validateStrongPassword(newPassword)
        if (passwordError == null) {
            passwordError = validateConfirm(newPassword, confirmPassword)
        }
        
        if (tokenError != null || passwordError != null) return
        
        scope.launch {
            isLoading = true
            errorMessage = ""
            successMessage = ""
            
            val result = userRepository.resetPassword(email, token, newPassword)
            result.onSuccess {
                successMessage = it
                snackbarHostState.showSnackbar(it)
                // Esperar un momento y navegar al login
                kotlinx.coroutines.delay(2000)
                onPasswordReset()
            }.onFailure {
                errorMessage = it.message ?: "Error al resetear contraseña"
                snackbarHostState.showSnackbar(errorMessage)
            }
            isLoading = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onGoBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (step == 1) "Recuperar Contraseña" else "Restablecer Contraseña",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (step == 1) {
                        // Paso 1: Solicitar token
                        Text(
                            text = "Ingresa tu email y te enviaremos un token de recuperación",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = null
                            },
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(Icons.Filled.Email, contentDescription = "Email")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = emailError != null,
                            singleLine = true
                        )
                        
                        AnimatedVisibility(
                            visible = emailError != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = emailError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = { requestToken() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading && email.isNotBlank()
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enviando...")
                            } else {
                                Text("Enviar Token")
                            }
                        }
                    } else {
                        // Paso 2: Resetear contraseña
                        Text(
                            text = "Ingresa el token recibido y tu nueva contraseña",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = email,
                            onValueChange = {},
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(Icons.Filled.Email, contentDescription = "Email")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = token,
                            onValueChange = {
                                token = it
                                tokenError = null
                            },
                            label = { Text("Token de recuperación") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = tokenError != null,
                            singleLine = true
                        )
                        
                        AnimatedVisibility(
                            visible = tokenError != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = tokenError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                passwordError = null
                            },
                            label = { Text("Nueva contraseña") },
                            leadingIcon = {
                                Icon(Icons.Filled.Lock, contentDescription = "Contraseña")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            isError = passwordError != null,
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                passwordError = null
                            },
                            label = { Text("Confirmar contraseña") },
                            leadingIcon = {
                                Icon(Icons.Filled.Lock, contentDescription = "Confirmar")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            isError = passwordError != null,
                            singleLine = true
                        )
                        
                        AnimatedVisibility(
                            visible = passwordError != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = passwordError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    step = 1
                                    token = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                    passwordError = null
                                    tokenError = null
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Volver")
                            }
                            
                            Button(
                                onClick = { resetPassword() },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading && token.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank()
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Restableciendo...")
                                } else {
                                    Text("Restablecer")
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Snackbar para mensajes
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


