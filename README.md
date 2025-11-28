# Fixsy - Aplicación de Servicios Mecánicos

## Descripción del Proyecto

Fixsy es una aplicación móvil desarrollada en Android que permite a los usuarios solicitar servicios mecánicos de manera rápida y eficiente. La aplicación integra funcionalidades de cámara, geolocalización, y gestión de datos locales para ofrecer una experiencia completa.

**Nombre del Paquete:** `com.example.uinavegacion`  
**Nombre de la App:** Fixsy  
**Arquitectura:** MVVM + Room Database

## Integrantes del Equipo

- **Matías** - Desarrollador Principal y Organización de Código
- **Santiago** - Desarrollador Colaborador (Base de datos, mejoras y finalización)

## Funcionalidades Implementadas

### Autenticación y Usuario
- **Pantalla de selección de rol** (Cliente/Mecánico) al iniciar la app
- Login con validaciones completas
- **Validación específica para emails de mecánico** (deben ser @mecanicofixsy.cl)
- Registro de nuevos usuarios
- **Gestión de perfil de usuario mejorada** (edición individual de campos)
- **Cambio de contraseña con validación de contraseña actual**
- **Imagen de perfil sincronizada** (se guarda y muestra en HomeScreen después de seleccionarla)
- Autenticación de administradores
- Sistema de roles (Cliente, Mecánico, Administrador)
- Gestión de sesión con DataStore Preferences
- **Redirección automática según rol y estado de sesión**

### Solicitud de Servicios
- Formulario completo para solicitar servicios
- Validaciones en tiempo real
- Integración con cámara nativa para fotos del problema
- **Selección de imágenes desde galería del dispositivo**
- **Preview de imágenes seleccionadas (cámara y galería)**
- Selección de tipo de servicio y vehículo
- Descripción detallada del problema
- Historial completo de solicitudes con fotografías asociadas
- **Edición de solicitudes existentes** (tipo de servicio, vehículo, descripción, ubicación, notas)
- Estados de solicitud (Pendiente, En Proceso, Completado, Cancelado)
- **Cancelación de solicitudes pendientes o en proceso**

### Recursos Nativos del Dispositivo
- **Cámara**: Captura de fotos del problema usando CameraX
- **Galería**: Selección de imágenes desde la galería del dispositivo
- **Preview de Imágenes**: Visualización inmediata de imágenes seleccionadas (cámara o galería)
- **Almacenamiento Local**: Base de datos Room para persistencia
- **Permisos**: Gestión segura de permisos de cámara y almacenamiento

### Gestión de Datos
- Historial completo de solicitudes
- Persistencia local con Room Database
- Gestión de vehículos y direcciones
- Estados de solicitudes
- Sistema de notificaciones con Snackbars y Toasts

### Navegación y UI
- **Splash Screen con tema separado** (Android 12+ Splash Screen API)
- Navegación fluida entre pantallas
- Material Design 3
- Interfaz responsive y accesible
- Animaciones y transiciones suaves
- Textos en español usando strings.xml
- Mensajes informativos para todas las acciones del usuario
- **TopBar simplificado** (sin menú hamburguesa ni menú de opciones)
- **Eliminación del drawer de navegación lateral**

### Funcionalidad para Mecánicos
- Pantalla principal para mecánicos con resumen del día
- Visualización de solicitudes pendientes y completadas
- Acciones para aceptar o rechazar solicitudes
- Cálculo de ganancias del día
- Gestión de disponibilidad

### Contribuciones de Santiago
- Implementación completa de base de datos SQLite con Room
- Optimización de estructura de carpeta (uinavegacion → fixsy)
- Eliminación de código redundante y mejora de la base de datos
- Corrección de errores de compilación en NavGraph
- Integración completa de UserDao con UserRepository
- Sistema de CRUD funcional para todas las entidades
- Mejoras en la inicialización de AppDatabase
- Corrección de referencias rotas en el código

## Integración con Microservicios (API REST)

La aplicación consume microservicios desarrollados en Spring Boot mediante **Retrofit**:

### Microservicios Integrados

#### 1. Microservicio de Usuarios (Puerto 8081)
- **Base URL**: `http://10.0.2.2:8081/` (emulador) o `http://192.168.1.X:8081/` (dispositivo físico)
- **Endpoints**:
  - `POST /api/users/login` - Iniciar sesión
  - `POST /api/users` - Registrar nuevo usuario
  - `GET /api/users/{id}` - Obtener usuario por ID
  - `GET /api/users/email/{email}` - Obtener usuario por email
  - **`PUT /api/users/{id}` - Actualizar usuario** (permite actualización parcial, contraseña opcional)
  - `DELETE /api/users/{id}` - Eliminar usuario

#### 2. Microservicio de Solicitudes (Puerto 8082)
- **Base URL**: `http://10.0.2.2:8082/` (emulador) o `http://192.168.1.X:8082/` (dispositivo físico)
- **Endpoints**:
  - `GET /api/requests` - Obtener todas las solicitudes
  - `GET /api/requests/{id}` - Obtener solicitud por ID
  - `GET /api/requests/user/{userId}` - Obtener solicitudes por usuario
  - `GET /api/requests/mechanic/{mechanicName}` - Obtener solicitudes por mecánico
  - `GET /api/requests/status/{status}` - Obtener solicitudes por estado
  - `POST /api/requests` - Crear nueva solicitud
  - **`PUT /api/requests/{id}` - Actualizar solicitud completa** (tipo, vehículo, descripción, ubicación, notas)
  - `PUT /api/requests/{id}/status` - Actualizar estado de solicitud
  - `PUT /api/requests/{id}/assign` - Asignar mecánico a solicitud
  - `DELETE /api/requests/{id}` - Eliminar solicitud

### Configuración de Retrofit

La integración se realiza mediante:
- **Retrofit 2.9.0** - Cliente HTTP para APIs REST
- **Gson Converter** - Conversión JSON
- **OkHttp** - Cliente HTTP con logging
- **RemoteDataSource** - Capa de abstracción para llamadas API
- **Repositorios** - `UserRepository` y `ServiceRequestRepository` consumen los microservicios

### Documentación de Microservicios

Los microservicios incluyen **Swagger UI** para documentación:
- Usuarios: `http://localhost:8081/swagger-ui.html`
- Solicitudes: `http://localhost:8082/swagger-ui.html`

## Arquitectura del Proyecto

### Patrón MVVM Implementado

```
app/src/main/java/com/example/uinavegacion/
├── data/                    # Capa de Datos
│   ├── local/               # Base de datos local (Room)
│   │   ├── database/        # Room Database
│   │   ├── user/           # Entidades de usuario
│   │   ├── service/        # Entidades de servicios
│   │   ├── request/        # Historial de solicitudes
│   │   └── storage/        # DataStore Preferences
│   ├── remote/              # Integración con microservicios
│   │   ├── api/            # Interfaces Retrofit
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── RetrofitClient.kt
│   │   └── RemoteDataSource.kt
│   └── repository/         # Repositorios
├── ui/                     # Capa de Presentación
│   ├── screen/            # Pantallas de la aplicación
│   ├── components/         # Componentes reutilizables
│   └── viewmodel/         # ViewModels
├── navigation/            # Navegación
└── domain/                # Lógica de negocio
    └── validation/        # Validadores
```

### Tecnologías Utilizadas

- **Android Studio** - IDE de desarrollo
- **Jetpack Compose** - UI moderna y declarativa
- **Room Database** - Persistencia local (para vehículos, direcciones, mecánicos)
- **Retrofit** - Consumo de APIs REST (microservicios)
- **Gson** - Conversión JSON
- **OkHttp** - Cliente HTTP con logging
- **CameraX** - Cámara nativa
- **Navigation Compose** - Navegación entre pantallas
- **Material Design 3** - Sistema de diseño
- **StateFlow** - Gestión de estado reactiva
- **Kotlin Coroutines** - Programación asíncrona
- **DataStore Preferences** - Gestión de preferencias del usuario
- **JUnit, MockK, Robolectric** - Testing unitario

## Instalación y Ejecución

### Requisitos Previos
- Android Studio (versión más reciente)
- SDK de Android 34+
- Dispositivo Android o Emulador
- **Microservicios Spring Boot ejecutándose** (usuarios en puerto 8081, solicitudes en puerto 8082)
- MySQL 8.0+ configurado
- **MySQL configurado sin contraseña para root** (o actualizar `application.properties` en microservicios)

### Pasos para Ejecutar

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/riighteousss/Proyecto_Desarrollo_App.git
   cd Proyecto_Desarrollo_App
   ```

2. **Ejecutar los microservicios**
   ```bash
   # Microservicio de usuarios
   cd microservicios/usuarios
   mvn spring-boot:run
   
   # Microservicio de solicitudes (en otra terminal)
   cd microservicios/gestionsolicitudes
   mvn spring-boot:run
   ```

3. **Abrir en Android Studio**
   - Abrir Android Studio
   - Seleccionar "Open an existing project"
   - Navegar a la carpeta del proyecto

4. **Configurar el proyecto**
   - Sincronizar dependencias con Gradle
   - Verificar que todas las dependencias se descarguen correctamente
   - **Verificar URLs en `RetrofitClient.kt`**:
     - Emulador: `http://10.0.2.2:8081/` y `http://10.0.2.2:8082/`
     - Dispositivo físico: Cambiar a la IP de tu PC

5. **Ejecutar la aplicación**
   - Conectar dispositivo Android o iniciar emulador
   - Hacer clic en "Run" o presionar Shift+F10

## Pantallas de la Aplicación

### Pantallas de Autenticación
- **SplashScreen**: Pantalla de inicio con tema separado (Android 12+)
- **RoleSelectionScreen**: Selección de rol (Cliente/Mecánico) al iniciar la app
- **LoginScreen**: Inicio de sesión con validaciones y mensajes de éxito
- **RegisterScreen**: Registro de nuevos usuarios con validaciones en tiempo real
- **AdminAuthScreen**: Autenticación para administradores

### Pantallas Principales
- **HomeScreen**: Pantalla principal con servicios rápidos, saludo personalizado y **imagen de perfil sincronizada**
- **ProfileScreen**: Gestión del perfil de usuario
- **EditProfileScreen**: Edición mejorada del perfil (campos individuales editables, cambio de contraseña con validación, **selección y guardado de imagen de perfil**)
- **SettingsScreen**: Configuraciones de la aplicación

### Pantallas de Servicios
- **RequestServiceScreen**: Solicitud de servicios con formularios completos, selección de imágenes (cámara y galería), y preview de imágenes
- **CameraScreen**: Captura de fotos del problema
- **RequestHistoryScreen**: Historial de solicitudes realizadas con fotografías, filtros por estado, y **edición de solicitudes existentes**

### Pantallas de Gestión
- **MyVehiclesScreen**: Gestión de vehículos del usuario
- **MyAddressesScreen**: Gestión de direcciones
- **AppointmentsScreen**: Gestión de citas

### Pantallas Especializadas
- **MechanicHomeScreen**: Pantalla principal para usuarios mecánicos
- **AdminHomeScreen**: Pantalla principal para administradores

## Base de Datos

### Entidades Implementadas
- **UserEntity**: Información de usuarios con sistema de roles
- **ServiceRequest**: Solicitudes de servicios
- **RequestHistoryEntity**: Historial de solicitudes con imágenes asociadas
- **VehicleEntity**: Vehículos del usuario
- **AddressEntity**: Direcciones del usuario
- **MechanicEntity**: Información de mecánicos

### Operaciones de Base de Datos
- Inserción de nuevos registros
- Consulta de datos por usuario y estado
- Actualización de información
- **Actualización parcial de usuarios** (sin requerir contraseña)
- Eliminación de registros
- Consultas con filtros por estado
- **Persistencia de solicitudes en microservicio** (MySQL)

## Funcionalidades Técnicas

### Validaciones Implementadas
- Validación de formularios en tiempo real
- Mensajes de error claros y específicos en español
- Retroalimentación visual inmediata
- Lógica centralizada en ViewModels
- Tests unitarios para todas las validaciones

### Gestión de Estado
- StateFlow para flujos de datos reactivos
- ViewModels para lógica de negocio
- Separación clara entre UI y lógica
- Persistencia de estado entre navegaciones
- DataStore para gestión de sesión
- **ProfileViewModel para sincronización de imagen de perfil** entre EditProfileScreen y HomeScreen

### Recursos Nativos
- **Cámara**: Integración completa con CameraX
- **Permisos**: Gestión segura de permisos del sistema
- **Almacenamiento**: Persistencia local con Room
- **Navegación**: Sistema de navegación robusto

### Sistema de Mensajes
- Snackbars para notificaciones de acciones
- Toasts para mensajes informativos
- Mensajes de éxito y error contextuales
- Todos los mensajes en español usando strings.xml

## Criterios de Evaluación Cumplidos

### IE 2.1.1 - Interfaz Visual Coherente (15%)
- Interfaz estructurada y jerárquica
- Navegación fluida entre vistas
- Principios de usabilidad aplicados
- Coherencia lingüística en español

### IE 2.1.2 - Formularios con Validaciones (15%)
- Formularios completos con validaciones
- Retroalimentación visual clara
- Íconos y mensajes apropiados
- Validaciones en tiempo real

### IE 2.2.1 - Lógica Centralizada (10%)
- Lógica desacoplada de la UI
- ViewModels para gestión de estado
- Respuesta coherente a cambios

### IE 2.2.2 - Animaciones (10%)
- Transiciones suaves
- Animaciones funcionales
- Retroalimentación visual
- AnimatedVisibility en mensajes de error

### IE 2.3.1 - Estructura Modular (15%)
- Arquitectura MVVM implementada
- Separación clara de responsabilidades
- Persistencia local integrada

### IE 2.3.2 - Herramientas Colaborativas (20%)
- Repositorio GitHub configurado
- Commits distribuidos y documentados
- Planificación en Trello

### IE 2.4.1 - Recursos Nativos (15%)
- Cámara nativa implementada
- Almacenamiento local funcional
- Integración coherente en la UI

## Nota Importante

**Contribuciones de Santiago**: Santiago ha realizado contribuciones significativas al proyecto, incluyendo la implementación completa de la base de datos SQLite, optimización de la arquitectura, y corrección de errores críticos. Debido a inconvenientes técnicos con su GitHub, sus avances han sido integrados por el desarrollador principal para preservar su trabajo y permitir la finalización del proyecto.

## Funcionalidades Recientes Implementadas

### Versión Actual
- **Splash Screen** con tema separado usando Android 12+ Splash Screen API
- **Selección de rol** (Cliente/Mecánico) con redirección a login
- **Validación de emails de mecánico** (deben ser @mecanicofixsy.cl)
- **Selección de imágenes desde galería** además de cámara
- **Preview de imágenes** seleccionadas antes de enviar solicitud
- **Visualización de imágenes** en el historial de solicitudes
- **Edición de solicitudes** (tipo, vehículo, descripción, ubicación, notas)
- **Edición mejorada de perfil** con campos individuales editables
- **Cambio de contraseña** con validación de contraseña actual
- **Imagen de perfil sincronizada** (se guarda automáticamente y aparece en HomeScreen)
- **Integración completa con microservicios** para solicitudes
- **Actualización de solicitudes** en el microservicio
- **Actualización parcial de usuarios** (sin requerir contraseña para otros campos)
- **UI simplificada** (sin drawer, TopBar simplificado)
- **Código organizado** con comentarios descriptivos

## Próximas Mejoras

- Implementar notificaciones push
- Agregar geolocalización GPS completa
- Integrar pagos en línea
- Sistema de calificaciones para mecánicos
- Chat en tiempo real entre cliente y mecánico
- Sistema de reportes y analíticas para administradores

## Contacto

Para consultas sobre el proyecto, contactar a través del repositorio de GitHub.

---

**Desarrollado para la asignatura DSY1105 - Desarrollo de Aplicaciones Móviles**
