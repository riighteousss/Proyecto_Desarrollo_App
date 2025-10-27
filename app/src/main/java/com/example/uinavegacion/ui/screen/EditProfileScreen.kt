package com.example.uinavegacion.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.ui.viewmodel.ProfileViewModel
import com.example.uinavegacion.domain.validation.*

@Composable
fun EditProfileScreen(
    onGoBack: () -> Unit,
    onSaveProfile: (String, String, String, String?) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    val userProfile by profileViewModel.userProfile.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<String?>(null) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswordSection by remember { mutableStateOf(false) }
    var showEmailSection by remember { mutableStateOf(false) }
    var showNameSection by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    
    // Estados de validación
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    
    // Cargar datos del perfil
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(context)
    }
    
    // Actualizar campos cuando se cargan los datos
    LaunchedEffect(userProfile) {
        if (userProfile.name.isNotEmpty()) {
            name = userProfile.name
            email = userProfile.email
            phone = userProfile.phone
            profileImageUri = userProfile.profileImageUri
        }
    }

    // Launcher para seleccionar imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileImageUri = it.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onGoBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Column {
                    Text(
                        text = "Editar Perfil",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Actualiza tu información personal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de foto de perfil
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Foto de Perfil",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Avatar con opción de cambiar
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileImageUri != null) {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = "Usuario",
                                    modifier = Modifier.size(60.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            // Icono de cámara en la esquina
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CameraAlt,
                                    contentDescription = "Cambiar foto",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Toca para cambiar la foto",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Sección de información personal
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información Personal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Nombre - se puede cambiar sin contraseña
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nombre: ${userProfile.name}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { showNameSection = !showNameSection },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar nombre")
                            }
                        }
                        
                        if (showNameSection) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nuevo nombre") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Filled.Person, contentDescription = "Nombre")
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Email - requiere contraseña
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Email: ${userProfile.email}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { showEmailSection = !showEmailSection },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar email")
                            }
                        }
                        
                        if (showEmailSection) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                label = { Text("Contraseña actual") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = "Contraseña")
                                },
                                visualTransformation = PasswordVisualTransformation()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Nuevo email") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Filled.Email, contentDescription = "Email")
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Teléfono - se puede cambiar sin contraseña
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Teléfono") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Filled.Phone, contentDescription = "Teléfono")
                            }
                        )
                    }
                }
            }

            // Sección de cambio de contraseña
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Cambiar Contraseña",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Switch(
                                checked = showPasswordSection,
                                onCheckedChange = { showPasswordSection = it }
                            )
                        }
                        
                        if (showPasswordSection) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OutlinedTextField(
                                value = currentPassword,
                                onValueChange = { currentPassword = it },
                                label = { Text("Contraseña actual") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = "Contraseña actual")
                                },
                                visualTransformation = PasswordVisualTransformation()
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Nueva contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = "Nueva contraseña")
                                },
                                visualTransformation = PasswordVisualTransformation()
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirmar nueva contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = "Confirmar contraseña")
                                },
                                visualTransformation = PasswordVisualTransformation()
                            )
                        }
                    }
                }
            }

            // Mensajes de error/éxito
            if (errorMessage.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            
            if (successMessage.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = successMessage,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Botones de acción
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onGoBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = {
                            errorMessage = ""
                            successMessage = ""
                            
                            // Guardar nombre (sin contraseña)
                            if (name != userProfile.name) {
                                profileViewModel.updateProfile(name, userProfile.email, userProfile.phone, profileImageUri, context)
                                successMessage = "Nombre actualizado correctamente"
                            }
                            
                            // Guardar teléfono (sin contraseña)
                            if (phone != userProfile.phone) {
                                profileViewModel.updateProfile(userProfile.name, userProfile.email, phone, profileImageUri, context)
                                successMessage = "Teléfono actualizado correctamente"
                            }
                            
                            // Guardar email (con contraseña)
                            if (showEmailSection && email != userProfile.email && currentPassword.isNotEmpty()) {
                                if (profileViewModel.updateEmail(email, currentPassword, context)) {
                                    successMessage = "Email actualizado correctamente"
                                    showEmailSection = false
                                    currentPassword = ""
                                } else {
                                    errorMessage = "Contraseña incorrecta"
                                }
                            }
                            
                            // Guardar contraseña
                            if (showPasswordSection && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                                if (newPassword == confirmPassword) {
                                    if (profileViewModel.updatePassword(currentPassword, newPassword, context)) {
                                        successMessage = "Contraseña actualizada correctamente"
                                        showPasswordSection = false
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                    } else {
                                        errorMessage = "Contraseña actual incorrecta"
                                    }
                                } else {
                                    errorMessage = "Las contraseñas no coinciden"
                                }
                            }
                            
                            // Guardar imagen
                            if (profileImageUri != userProfile.profileImageUri) {
                                profileViewModel.updateProfile(userProfile.name, userProfile.email, userProfile.phone, profileImageUri, context)
                                successMessage = "Foto de perfil actualizada correctamente"
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Guardar",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}
