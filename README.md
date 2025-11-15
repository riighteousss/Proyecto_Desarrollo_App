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
- Login con validaciones completas
- Registro de nuevos usuarios
- Gestión de perfil de usuario
- Autenticación de administradores
- Sistema de roles (Cliente, Mecánico, Administrador)
- Gestión de sesión con DataStore Preferences

### Solicitud de Servicios
- Formulario completo para solicitar servicios
- Validaciones en tiempo real
- Integración con cámara nativa para fotos del problema
- Selección de tipo de servicio y vehículo
- Descripción detallada del problema
- Historial completo de solicitudes con fotografías asociadas
- Estados de solicitud (Pendiente, En Proceso, Completado, Cancelado)

### Recursos Nativos del Dispositivo
- **Cámara**: Captura de fotos del problema usando CameraX
- **Almacenamiento Local**: Base de datos Room para persistencia
- **Permisos**: Gestión segura de permisos de cámara y almacenamiento

### Gestión de Datos
- Historial completo de solicitudes
- Persistencia local con Room Database
- Gestión de vehículos y direcciones
- Estados de solicitudes
- Sistema de notificaciones con Snackbars y Toasts

### Navegación y UI
- Navegación fluida entre pantallas
- Material Design 3
- Interfaz responsive y accesible
- Animaciones y transiciones suaves
- Textos en español usando strings.xml
- Mensajes informativos para todas las acciones del usuario

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

## Arquitectura del Proyecto

### Patrón MVVM Implementado

```
app/src/main/java/com/example/uinavegacion/
├── data/                    # Capa de Datos
│   ├── local/               # Base de datos local
│   │   ├── database/        # Room Database
│   │   ├── user/           # Entidades de usuario
│   │   ├── service/        # Entidades de servicios
│   │   ├── request/        # Historial de solicitudes
│   │   └── storage/        # DataStore Preferences
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
- **Room Database** - Persistencia local
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

### Pasos para Ejecutar

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/riighteousss/Proyecto_Desarrollo_App.git
   cd Proyecto_Desarrollo_App
   ```

2. **Abrir en Android Studio**
   - Abrir Android Studio
   - Seleccionar "Open an existing project"
   - Navegar a la carpeta del proyecto

3. **Configurar el proyecto**
   - Sincronizar dependencias con Gradle
   - Verificar que todas las dependencias se descarguen correctamente

4. **Ejecutar la aplicación**
   - Conectar dispositivo Android o iniciar emulador
   - Hacer clic en "Run" o presionar Shift+F10

## Pantallas de la Aplicación

### Pantallas de Autenticación
- **LoginScreen**: Inicio de sesión con validaciones y mensajes de éxito
- **RegisterScreen**: Registro de nuevos usuarios con validaciones en tiempo real
- **AdminAuthScreen**: Autenticación para administradores

### Pantallas Principales
- **HomeScreen**: Pantalla principal con servicios rápidos
- **ProfileScreen**: Gestión del perfil de usuario
- **SettingsScreen**: Configuraciones de la aplicación

### Pantallas de Servicios
- **RequestServiceScreen**: Solicitud de servicios con formularios completos
- **CameraScreen**: Captura de fotos del problema
- **RequestHistoryScreen**: Historial de solicitudes realizadas con fotografías

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
- Eliminación de registros
- Consultas con filtros por estado

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
