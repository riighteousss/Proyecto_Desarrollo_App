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
import androidx.compose.material3.Divider
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.data.local.storage.UserPreferences
import com.example.uinavegacion.data.repository.UserRepository
import com.example.uinavegacion.data.remote.RemoteDataSource
import com.example.uinavegacion.data.remote.RetrofitClient
import com.example.uinavegacion.domain.validation.*
import com.example.uinavegacion.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    userEmail: String = "", // Email del usuario logueado (se pasa desde NavGraph)
    onGoBack: () -> Unit,
    onSaveProfile: (String, String, String, String?) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val profileViewModel: ProfileViewModel = viewModel()
    
    // Obtener datos del usuario desde UserPreferences (como respaldo)
    val userIdFlow = userPrefs.userId.collectAsStateWithLifecycle(initialValue = -1L)
    val userNameFlow = userPrefs.userName.collectAsStateWithLifecycle(initialValue = "")
    val userEmailFlow = userPrefs.userEmail.collectAsStateWithLifecycle(initialValue = "")
    val userPhoneFlow = userPrefs.userPhone.collectAsStateWithLifecycle(initialValue = "")
    
    val userId = userIdFlow.value
    val initialName = userNameFlow.value
    // Usar el email pasado como parámetro, o el de UserPreferences como respaldo
    val initialEmail = if (userEmail.isNotEmpty()) userEmail else userEmailFlow.value
    val initialPhone = userPhoneFlow.value
    
    // Estados de los campos
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf(initialPhone) }
    var profileImageUri by remember { mutableStateOf<String?>(null) }
    var showPasswordSection by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    // Estados para edición individual de campos
    var editingName by remember { mutableStateOf(false) }
    var editingEmail by remember { mutableStateOf(false) }
    var editingPhone by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var tempEmail by remember { mutableStateOf("") }
    var tempPhone by remember { mutableStateOf("") }
    
    // Estados de validación
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    // Estados de mensajes
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    // Repository para actualizar en el microservicio
    val userRepository = remember {
        UserRepository(RemoteDataSource(
            userApiService = RetrofitClient.userApiService,
            serviceRequestApiService = RetrofitClient.serviceRequestApiService,
            vehicleApiService = RetrofitClient.vehicleApiService
        ))
    }
    
    // Cargar imagen de perfil desde ProfileViewModel
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(context)
    }
    
    // Observar cambios en el perfil para cargar la imagen
    val userProfile by profileViewModel.userProfile.collectAsState()
    LaunchedEffect(userProfile.profileImageUri) {
        if (userProfile.profileImageUri != null && profileImageUri == null) {
            profileImageUri = userProfile.profileImageUri
        }
    }
    
    // Cargar datos del usuario desde el microservicio
    // Se ejecuta cuando se abre la pantalla para cargar los datos del usuario que inició sesión
    // Prioridad: 1) Email pasado como parámetro, 2) Email de UserPreferences, 3) userId de UserPreferences
    LaunchedEffect(initialEmail) {
        isLoading = true
        errorMessage = ""
        
        try {
            var loadedUser: com.example.uinavegacion.data.local.user.UserEntity? = null
            
            // Estrategia de carga: siempre intentar por email primero (es más confiable)
            // 1. Intentar cargar por email (prioridad: email pasado como parámetro > email de UserPreferences)
            if (initialEmail.isNotEmpty()) {
                val emailResult = userRepository.getUserByEmail(initialEmail)
                if (emailResult.isSuccess) {
                    loadedUser = emailResult.getOrNull()
                    // Guardar el userId encontrado para futuras operaciones
                    loadedUser?.let { user ->
                        userPrefs.setUserId(user.id)
                    }
                }
            }
            
            // 2. Si no se cargó por email, intentar por userId como respaldo
            if (loadedUser == null && userId > 0) {
                val result = userRepository.getUserById(userId)
                if (result.isSuccess) {
                    loadedUser = result.getOrNull()
                }
            }
            
            // 3. Si se cargó el usuario, actualizar los campos
            if (loadedUser != null) {
                val user = loadedUser!!
                // Usar los datos del microservicio (son la fuente de verdad)
                name = user.name
                email = user.email
                phone = user.phone
                
                // Actualizar UserPreferences con los datos del servidor para mantener sincronización
                userPrefs.setUserId(user.id)
                userPrefs.setUserName(user.name)
                userPrefs.setUserEmail(user.email)
                userPrefs.setUserPhone(user.phone)
                
                isLoading = false
            } else {
                // Si no se pudo cargar, usar datos locales como respaldo
                if (initialName.isNotEmpty() || initialEmail.isNotEmpty() || initialPhone.isNotEmpty()) {
                    // Hay datos locales, usarlos
                    if (name.isEmpty() && initialName.isNotEmpty()) name = initialName
                    if (email.isEmpty() && initialEmail.isNotEmpty()) email = initialEmail
                    if (phone.isEmpty() && initialPhone.isNotEmpty()) phone = initialPhone
                    errorMessage = "No se pudo conectar al servidor. Mostrando datos locales."
                } else {
                    // No hay datos ni locales ni del servidor
                    errorMessage = "No se encontró información del usuario. Por favor, inicia sesión nuevamente."
                }
                isLoading = false
            }
        } catch (e: Exception) {
            // Error de conexión - usar datos locales si están disponibles
            if (initialName.isNotEmpty() || initialEmail.isNotEmpty() || initialPhone.isNotEmpty()) {
                if (name.isEmpty() && initialName.isNotEmpty()) name = initialName
                if (email.isEmpty() && initialEmail.isNotEmpty()) email = initialEmail
                if (phone.isEmpty() && initialPhone.isNotEmpty()) phone = initialPhone
                errorMessage = "Error de conexión: ${e.message ?: "Error desconocido"}. Usando datos locales."
            } else {
                errorMessage = "Error de conexión: ${e.message ?: "Error desconocido"}. No hay datos locales disponibles."
            }
            isLoading = false
        }
    }

    // Launcher para seleccionar imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileImageUri = it.toString()
            // Guardar la imagen inmediatamente en ProfileViewModel
            profileViewModel.updateProfile(name, email, phone, it.toString(), context)
        }
    }
    
    // Función helper para obtener el userId correcto
    suspend fun getValidUserId(): Long {
        if (userId > 0) return userId
        // Si no hay userId, intentar obtenerlo del email
        if (email.isNotEmpty()) {
            val userResult = userRepository.getUserByEmail(email)
            return userResult.getOrNull()?.let { user ->
                // Guardar el userId encontrado para futuras operaciones
                userPrefs.setUserId(user.id)
                user.id
            } ?: -1L
        }
        return -1L
    }
    
    // Función para guardar cambios
    fun saveChanges() {
        errorMessage = ""
        successMessage = ""
        
        // Validar campos
        nameError = validateNameLettersOnly(name)
        emailError = validateEmail(email)
        phoneError = if (phone.isBlank()) null else validatePhoneDigitsOnly(phone)
        
        if (nameError != null || emailError != null || phoneError != null) {
            errorMessage = "Por favor, corrige los errores en el formulario"
            return
        }
        
        // Validar contraseña si se está cambiando
        if (showPasswordSection) {
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                passwordError = "Completa todos los campos de contraseña"
                errorMessage = passwordError ?: ""
                return
            }
            if (newPassword != confirmPassword) {
                passwordError = "Las contraseñas no coinciden"
                errorMessage = passwordError ?: ""
                return
            }
            val passwordValidation = validateStrongPassword(newPassword)
            if (passwordValidation != null) {
                passwordError = passwordValidation
                errorMessage = passwordError ?: ""
                return
            }
        }
        
        scope.launch {
            isLoading = true
            try {
                // Si se está cambiando la contraseña, validar la contraseña actual primero
                if (showPasswordSection && currentPassword.isNotEmpty()) {
                    // Intentar login con la contraseña actual para validarla
                    val loginResult = userRepository.login(email, currentPassword)
                    if (loginResult.isFailure) {
                        errorMessage = "La contraseña actual es incorrecta"
                        isLoading = false
                        return@launch
                    }
                }
                
                // Obtener userId válido (intentar obtenerlo del email si no está disponible)
                val currentUserId = getValidUserId()
                
                if (currentUserId > 0) {
                    val result = userRepository.updateUser(
                        userId = currentUserId,
                        name = name,
                        email = email,
                        phone = phone,
                        password = if (showPasswordSection && newPassword.isNotEmpty() && currentPassword.isNotEmpty()) newPassword else null
                    )
                    
                    result.onSuccess {
                        // Actualizar UserPreferences con los nuevos datos
                        userPrefs.setUserName(name)
                        userPrefs.setUserEmail(email)
                        userPrefs.setUserPhone(phone)
                        
                        // Actualizar ProfileViewModel con la imagen de perfil
                        profileViewModel.updateProfile(name, email, phone, profileImageUri, context)
                        
                        successMessage = "Perfil actualizado correctamente"
                        
                        // Limpiar campos de contraseña
                        if (showPasswordSection) {
                            showPasswordSection = false
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        }
                        
                        // Llamar al callback
                        onSaveProfile(name, email, phone, profileImageUri)
                    }.onFailure {
                        errorMessage = "Error al actualizar: ${it.message ?: "Error desconocido"}"
                    }
                } else {
                    errorMessage = "Usuario no encontrado. Por favor, inicia sesión nuevamente."
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de foto de perfil (mantener como está)
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

            // Sección de información personal (solo muestra información, con botones de editar)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Información Personal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Nombre - solo muestra, con botón de editar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = "Nombre",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Nombre",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (editingName) {
                            OutlinedTextField(
                                            value = tempName,
                                            onValueChange = { 
                                                tempName = it
                                                nameError = null
                                            },
                                label = { Text("Nuevo nombre") },
                                modifier = Modifier.fillMaxWidth(),
                                            isError = nameError != null,
                                            supportingText = nameError?.let { { Text(it) } },
                                            singleLine = true
                                        )
                                    } else {
                                        Text(
                                            text = name.ifEmpty { "No especificado" },
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                            if (!editingName) {
                                IconButton(onClick = { 
                                    editingName = true
                                    tempName = name
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Editar nombre", tint = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                Row {
                                    IconButton(onClick = { 
                                        val validation = validateNameLettersOnly(tempName)
                                        if (validation == null) {
                                            name = tempName
                                            editingName = false
                                            nameError = null
                                            // Guardar automáticamente
                                            scope.launch {
                                                val currentUserId = getValidUserId()
                                                if (currentUserId > 0) {
                                                    userRepository.updateUser(currentUserId, name, email, phone, null)
                                                        .onSuccess {
                                                            userPrefs.setUserName(name)
                                                            profileViewModel.updateProfile(name, email, phone, profileImageUri, context)
                                                            successMessage = "Nombre actualizado"
                                                        }.onFailure {
                                                            errorMessage = "Error al actualizar nombre: ${it.message}"
                                                        }
                                                } else {
                                                    errorMessage = "Usuario no encontrado. Por favor, inicia sesión nuevamente."
                                                }
                                            }
                                        } else {
                                            nameError = validation
                                        }
                                    }) {
                                        Icon(Icons.Filled.Check, contentDescription = "Guardar", tint = Color(0xFF4CAF50))
                                    }
                                    IconButton(onClick = { 
                                        editingName = false
                                        tempName = name
                                        nameError = null
                                    }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Cancelar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                        
                        HorizontalDivider()
                        
                        // Email - solo muestra, con botón de editar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Filled.Email,
                                    contentDescription = "Email",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Email",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (editingEmail) {
                            OutlinedTextField(
                                            value = tempEmail,
                                            onValueChange = { 
                                                tempEmail = it
                                                emailError = null
                                            },
                                            label = { Text("Nuevo email") },
                                modifier = Modifier.fillMaxWidth(),
                                            isError = emailError != null,
                                            supportingText = emailError?.let { { Text(it) } },
                                            singleLine = true
                                        )
                                    } else {
                                        Text(
                                            text = email.ifEmpty { "No especificado" },
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                            if (!editingEmail) {
                                IconButton(onClick = { 
                                    editingEmail = true
                                    tempEmail = email
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Editar email", tint = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                Row {
                                    IconButton(onClick = { 
                                        val validation = validateEmail(tempEmail)
                                        if (validation == null) {
                                            email = tempEmail
                                            editingEmail = false
                                            emailError = null
                                            // Guardar automáticamente
                                            scope.launch {
                                                val currentUserId = getValidUserId()
                                                if (currentUserId > 0) {
                                                    userRepository.updateUser(currentUserId, name, email, phone, null)
                                                        .onSuccess {
                                                            userPrefs.setUserEmail(email)
                                                            profileViewModel.updateProfile(name, email, phone, profileImageUri, context)
                                                            successMessage = "Email actualizado"
                                                        }.onFailure {
                                                            errorMessage = "Error al actualizar email: ${it.message}"
                                                        }
                                                } else {
                                                    errorMessage = "Usuario no encontrado. Por favor, inicia sesión nuevamente."
                                                }
                                            }
                                        } else {
                                            emailError = validation
                                        }
                                    }) {
                                        Icon(Icons.Filled.Check, contentDescription = "Guardar", tint = Color(0xFF4CAF50))
                        }
                                    IconButton(onClick = { 
                                        editingEmail = false
                                        tempEmail = email
                                        emailError = null
                                    }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Cancelar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                        
                        HorizontalDivider()
                        
                        // Teléfono - solo muestra, con botón de editar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Filled.Phone,
                                    contentDescription = "Teléfono",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Teléfono",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (editingPhone) {
                                        OutlinedTextField(
                                            value = tempPhone,
                                            onValueChange = { 
                                                tempPhone = it
                                                phoneError = null
                                            },
                                            label = { Text("Nuevo teléfono") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = phoneError != null,
                                            supportingText = phoneError?.let { { Text(it) } },
                                            singleLine = true
                                        )
                                    } else {
                                        Text(
                                            text = phone.ifEmpty { "No especificado" },
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                            if (!editingPhone) {
                                IconButton(onClick = { 
                                    editingPhone = true
                                    tempPhone = phone
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Editar teléfono", tint = MaterialTheme.colorScheme.primary)
                                }
                            } else {
                                Row {
                                    IconButton(onClick = { 
                                        val validation = if (tempPhone.isBlank()) null else validatePhoneDigitsOnly(tempPhone)
                                        if (validation == null) {
                                            phone = tempPhone
                                            editingPhone = false
                                            phoneError = null
                                            // Guardar automáticamente
                                            scope.launch {
                                                val currentUserId = getValidUserId()
                                                if (currentUserId > 0) {
                                                    userRepository.updateUser(currentUserId, name, email, phone, null)
                                                        .onSuccess {
                                                            userPrefs.setUserPhone(phone)
                                                            profileViewModel.updateProfile(name, email, phone, profileImageUri, context)
                                                            successMessage = "Teléfono actualizado"
                                                        }.onFailure {
                                                            errorMessage = "Error al actualizar teléfono: ${it.message}"
                                                        }
                                                } else {
                                                    errorMessage = "Usuario no encontrado. Por favor, inicia sesión nuevamente."
                                                }
                                            }
                                        } else {
                                            phoneError = validation
                                        }
                                    }) {
                                        Icon(Icons.Filled.Check, contentDescription = "Guardar", tint = Color(0xFF4CAF50))
                            }
                                    IconButton(onClick = { 
                                        editingPhone = false
                                        tempPhone = phone
                                        phoneError = null
                                    }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Cancelar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Sección de cambio de contraseña (simplificada)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                onCheckedChange = { 
                                    showPasswordSection = it
                                    if (!it) {
                                        currentPassword = ""
                                        newPassword = ""
                                        confirmPassword = ""
                                        passwordError = null
                                    }
                                }
                            )
                        }
                        
                        if (showPasswordSection) {
                            // Contraseña actual (requerida)
                            OutlinedTextField(
                                value = currentPassword,
                                onValueChange = { 
                                    currentPassword = it
                                    passwordError = null
                                },
                                label = { Text("Contraseña actual") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = "Contraseña actual")
                                },
                                visualTransformation = PasswordVisualTransformation(),
                                isError = passwordError != null,
                                supportingText = if (passwordError != null && passwordError!!.contains("actual")) { 
                                    { Text(passwordError!!) } 
                                } else null
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Nueva contraseña
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { 
                                    newPassword = it
                                    passwordError = null
                                },
                                label = { Text("Nueva contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = "Nueva contraseña")
                                },
                                visualTransformation = PasswordVisualTransformation(),
                                isError = passwordError != null,
                                supportingText = passwordError?.let { { Text(it) } }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Confirmar nueva contraseña
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { 
                                    confirmPassword = it
                                    passwordError = null
                                },
                                label = { Text("Confirmar nueva contraseña") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = "Confirmar contraseña")
                                },
                                visualTransformation = PasswordVisualTransformation(),
                                isError = passwordError != null
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onGoBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = { saveChanges() },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardando...")
                        } else {
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
}
