# Integración Retrofit - Consumo de Microservicios

## 📋 Resumen

Esta aplicación ha sido migrada de **Room (SQLite local)** a **Retrofit (APIs REST)** para consumir los microservicios de Spring Boot.

## 🏗️ Estructura Implementada

### 1. DTOs (Data Transfer Objects)
- `UserDTO.kt` - DTOs para usuarios (request/response)
- `ServiceRequestDTO.kt` - DTOs para solicitudes (request/response)

### 2. Interfaces de API (Retrofit)
- `UserApiService.kt` - Endpoints del microservicio de usuarios
- `ServiceRequestApiService.kt` - Endpoints del microservicio de solicitudes

### 3. Cliente Retrofit
- `RetrofitClient.kt` - Configuración de Retrofit con:
  - URLs base de los microservicios
  - Interceptor de logging
  - Timeouts configurados
  - Conversor Gson

### 4. Fuente de Datos Remota
- `RemoteDataSource.kt` - Encapsula las llamadas a la API y maneja errores

### 5. Repositorios Actualizados
- `UserRepository.kt` - Ahora usa `RemoteDataSource` en lugar de `UserDao`
- `ServiceRequestRepository.kt` - Nuevo repositorio para solicitudes

## 🔧 Configuración

### URLs Base

En `RetrofitClient.kt`, las URLs están configuradas para:

**Emulador Android:**
```kotlin
private const val BASE_URL_USUARIOS = "http://10.0.2.2:8081/"
private const val BASE_URL_SOLICITUDES = "http://10.0.2.2:8082/"
```

**Dispositivo Físico:**
Necesitas cambiar a la IP de tu PC:
```kotlin
private const val BASE_URL_USUARIOS = "http://192.168.1.X:8081/"
private const val BASE_URL_SOLICITUDES = "http://192.168.1.X:8082/"
```

### Permisos de Internet

Asegúrate de tener en `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 📱 Uso en ViewModels

Los ViewModels no necesitan cambios, siguen usando los repositorios igual que antes:

```kotlin
class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {
    // El código sigue igual, pero ahora consume la API
    suspend fun login(email: String, password: String) {
        repository.login(email, password)
    }
}
```

## 🔄 Migración Completa

### Datos Migrados a Microservicios:
- ✅ **Usuarios** - Microservicio `usuarios` (puerto 8081)
- ✅ **Solicitudes** - Microservicio `gestionsolicitudes` (puerto 8082)

### Datos que Siguen en Room (Local):
- ⚠️ **Vehículos** - No hay microservicio aún
- ⚠️ **Direcciones** - No hay microservicio aún
- ⚠️ **Mecánicos** - No hay microservicio aún

## 🚀 Próximos Pasos

1. **Ejecutar los microservicios** en Spring Boot
2. **Verificar conectividad** desde el emulador/dispositivo
3. **Probar login y registro** contra la API
4. **Migrar vehículos y direcciones** si se crean microservicios para ellos

## ⚠️ Notas Importantes

- **Login**: Actualmente obtiene el usuario por email. Si necesitas validar contraseña en el servidor, agrega un endpoint de login en el backend.
- **Errores**: Todos los errores se manejan con `Result<T>` para mantener compatibilidad.
- **Logging**: El interceptor de logging muestra todas las requests/responses en Logcat (útil para debug).

