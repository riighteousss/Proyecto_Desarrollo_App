# 📚 GUÍA DE ESTUDIO - APLICACIÓN FIXSY

## 🎯 **RESPUESTAS CLAVE PARA EL PROFESOR**

### **PREGUNTA 1: "¿Cómo funciona tu aplicación?"**

**RESPUESTA:**
"Mi aplicación Fixsy es una app de servicios mecánicos que funciona así:

1. **Al abrir**: Se muestra la pantalla principal (HomeScreen)
2. **Si no está logueado**: Ve botones de Login/Registro
3. **Si está logueado**: Ve su perfil y puede acceder a servicios
4. **Servicios disponibles**: Emergencia, Mantenimiento, Revisión, Consultas
5. **Navegación**: Entre pantallas usando Navigation Compose
6. **Base de datos**: Room Database para guardar datos localmente"

### **PREGUNTA 2: "¿Dónde está la lógica de autenticación?"**

**RESPUESTA:**
"En el archivo `AuthViewModel.kt`:
- **Líneas 68-90**: Clase AuthViewModel con estados
- **Líneas 95-120**: Función onLoginEmailChange() - maneja email
- **Líneas 125-150**: Función onLoginPasswordChange() - maneja contraseña
- **Líneas 155-180**: Función onLoginSubmit() - ejecuta login
- **Líneas 185-220**: Función onRegisterSubmit() - ejecuta registro
- **Líneas 72-80**: Lista de usuarios de prueba en memoria"

### **PREGUNTA 3: "¿Dónde está la navegación?"**

**RESPUESTA:**
"En el archivo `NavGraph.kt`:
- **Líneas 50-60**: NavHost con rutas definidas
- **Líneas 70-80**: Ruta 'home' → HomeScreen
- **Líneas 85-95**: Ruta 'login' → LoginScreen  
- **Líneas 100-110**: Ruta 'register' → RegisterScreen
- **Líneas 115-125**: Ruta 'profile' → ProfileScreen
- **Líneas 75, 90, 105**: Navegación entre pantallas con navController.navigate()"

### **PREGUNTA 4: "¿Dónde está la pantalla principal?"**

**RESPUESTA:**
"En el archivo `HomeScreen.kt`:
- **Líneas 52-60**: Función principal HomeScreen
- **Líneas 80-120**: Header con logo y bienvenida
- **Líneas 130-150**: Barra de búsqueda
- **Líneas 160-200**: Sección de mecánicos cercanos
- **Líneas 210-250**: Botones de acción (Emergencia, Mantenimiento)
- **Líneas 260-300**: Servicios rápidos
- **Líneas 310-350**: Estadísticas (Mecánicos, Servicios, Calificación)"

### **PREGUNTA 5: "¿Dónde está la base de datos?"**

**RESPUESTA:**
"En el archivo `AppDatabase.kt`:
- **Líneas 33-37**: Definición de la base de datos con entidades
- **Líneas 40-44**: DAOs para acceder a los datos
- **Líneas 46-60**: Patrón Singleton para la base de datos
- **Líneas 65-85**: Data seeding (datos iniciales)
- **Entidades**: UserEntity, ServiceRequest, MechanicEntity, VehicleEntity, AddressEntity"

## 🔧 **EJERCICIOS DE MODIFICACIÓN**

### **EJERCICIO 1: "Agregar un nuevo servicio"**

**UBICACIÓN:** `HomeScreen.kt` líneas 210-250

**CÓDIGO A AGREGAR:**
```kotlin
// Después de la línea 240, agregar:
ActionButton(
    icon = Icons.Filled.Star,
    label = "Nuevo Servicio",
    onClick = { /* TODO: Implementar nuevo servicio */ }
)
```

### **EJERCICIO 2: "Cambiar el logo"**

**UBICACIÓN:** `HomeScreen.kt` línea 90

**CÓDIGO A CAMBIAR:**
```kotlin
// Cambiar esta línea:
Text(text = "🔧", style = MaterialTheme.typography.headlineLarge)

// Por esta:
Text(text = "🚗", style = MaterialTheme.typography.headlineLarge)
```

### **EJERCICIO 3: "Agregar validación de email"**

**UBICACIÓN:** `AuthViewModel.kt` líneas 155-180

**CÓDIGO A AGREGAR:**
```kotlin
// Después de la línea 170, agregar:
if (!email.contains("@")) {
    _login.update { it.copy(emailError = "Email debe contener @") }
    return@launch
}
```

### **EJERCICIO 4: "Cambiar usuarios de prueba"**

**UBICACIÓN:** `AuthViewModel.kt` líneas 72-80

**CÓDIGO A CAMBIAR:**
```kotlin
// Cambiar la lista de usuarios:
private val USERS = mutableListOf(
    DemoUser(name = "Admin", email = "admin@fixsy.cl", phone = "12345678", pass = "Admin123!"),
    DemoUser(name = "Mecánico", email = "mecanico@fixsy.cl", phone = "87654321", pass = "Mecanico123!"),
    DemoUser(name = "Cliente", email = "cliente@fixsy.cl", phone = "11223344", pass = "Cliente123!")
)
```

## 🏗️ **ARQUITECTURA DE LA APLICACIÓN**

### **MVVM (Model-View-ViewModel):**
- **Model**: Datos (Room Database + Entities)
- **View**: Pantallas (HomeScreen, LoginScreen, etc.)
- **ViewModel**: Lógica (AuthViewModel, ServiceViewModel, etc.)

### **Jetpack Compose:**
- **@Composable**: Función que crea UI
- **remember**: Mantiene estado entre recomposiciones
- **StateFlow**: Datos reactivos que actualizan la UI automáticamente

### **Navegación:**
- **NavHost**: Contenedor de pantallas
- **composable()**: Define cada pantalla
- **navController.navigate()**: Cambia entre pantallas

### **Base de Datos:**
- **Room Database**: Base de datos local
- **Entities**: Tablas (UserEntity, ServiceRequest, etc.)
- **DAOs**: Acceso a datos (UserDao, ServiceRequestDao, etc.)
- **Repositories**: Lógica de acceso a datos

## 📱 **FLUJO DE LA APLICACIÓN**

1. **MainActivity** → Inicia la aplicación
2. **AppRoot** → Configura dependencias y ViewModels
3. **NavGraph** → Maneja la navegación
4. **HomeScreen** → Pantalla principal
5. **AuthViewModel** → Maneja autenticación
6. **AppDatabase** → Almacena datos localmente

## 🎓 **CONCEPTOS CLAVE**

### **StateFlow:**
- Datos reactivos que se actualizan automáticamente
- `collectAsState()` observa cambios en el estado
- `update{}` modifica el estado de forma segura

### **Corrutinas:**
- `viewModelScope.launch{}` para operaciones asíncronas
- `delay()` para simular operaciones de red
- `suspend` para funciones que pueden pausarse

### **Navigation Compose:**
- `NavHost` contiene todas las pantallas
- `composable()` define cada ruta
- `navController.navigate()` cambia de pantalla

### **Room Database:**
- `@Entity` define tablas
- `@Dao` define operaciones de base de datos
- `@Database` configura la base de datos

## 🚀 **COMANDOS ÚTILES**

```bash
# Compilar la app
.\gradlew.bat assembleDebug

# Instalar en dispositivo
.\gradlew.bat installDebug

# Limpiar proyecto
.\gradlew.bat clean

# Ver logs
.\gradlew.bat --info
```

## 📋 **CHECKLIST PARA EL PROFESOR**

- ✅ **Arquitectura MVVM** implementada
- ✅ **Jetpack Compose** para UI
- ✅ **Navigation Compose** para navegación
- ✅ **Room Database** para datos locales
- ✅ **StateFlow** para datos reactivos
- ✅ **Corrutinas** para operaciones asíncronas
- ✅ **Material Design** para UI moderna
- ✅ **Validación** de formularios
- ✅ **Manejo de errores** implementado
- ✅ **Código bien documentado** con comentarios

**¡Ahora estás preparado para explicar tu aplicación como un experto!** 🎯
