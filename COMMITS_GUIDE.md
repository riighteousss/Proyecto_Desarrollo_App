# 📝 Guía de Commits para Fixsy

## 🎯 Estructura de Commits Recomendada

### **Formato de Commit:**
```
tipo(scope): descripción breve

Descripción detallada de los cambios realizados.
- Cambio específico 1
- Cambio específico 2
- Cambio específico 3

Closes #issue_number (si aplica)
```

### **Tipos de Commits:**
- `feat`: Nueva funcionalidad
- `fix`: Corrección de errores
- `docs`: Documentación
- `style`: Formato, espacios, etc.
- `refactor`: Refactorización de código
- `test`: Agregar o modificar tests
- `chore`: Tareas de mantenimiento

## 📋 Ejemplos de Commits para tu Proyecto:

### **1. Commits de Arquitectura:**
```bash
feat(architecture): implementar patrón MVVM con ViewModels

- Crear AuthViewModel para gestión de autenticación
- Implementar RequestFormViewModel para formularios
- Agregar ProfileViewModel para gestión de perfil
- Separar lógica de negocio de la UI

Closes #1
```

### **2. Commits de Base de Datos:**
```bash
feat(database): implementar Room Database con entidades

- Crear AppDatabase.kt con configuración Room
- Implementar UserEntity y UserDao
- Agregar ServiceRequest y ServiceRequestDao
- Crear RequestHistoryEntity para historial
- Configurar migraciones de base de datos

Closes #2
```

### **3. Commits de UI:**
```bash
feat(ui): crear pantalla principal con navegación

- Implementar HomeScreen con Material Design 3
- Agregar navegación entre pantallas
- Crear componentes reutilizables
- Implementar animaciones y transiciones

Closes #3
```

### **4. Commits de Funcionalidades:**
```bash
feat(camera): implementar cámara nativa con CameraX

- Crear CameraScreen para captura de fotos
- Implementar permisos de cámara
- Agregar FileProvider para compartir archivos
- Integrar con RequestServiceScreen

Closes #4
```

### **5. Commits de Validaciones:**
```bash
feat(validation): implementar validaciones de formularios

- Crear validaciones en tiempo real
- Implementar mensajes de error claros
- Agregar retroalimentación visual
- Centralizar lógica en ViewModels

Closes #5
```

### **6. Commits de Documentación:**
```bash
docs(readme): crear documentación completa del proyecto

- Agregar descripción detallada del proyecto
- Documentar arquitectura y tecnologías
- Incluir guía de instalación
- Agregar métricas y funcionalidades

Closes #6
```

## 🚀 Secuencia de Commits Recomendada:

### **Commit 1: Configuración Inicial**
```bash
chore(project): configurar proyecto Android Studio

- Crear estructura de carpetas
- Configurar build.gradle.kts
- Agregar dependencias básicas
- Configurar AndroidManifest.xml
```

### **Commit 2: Arquitectura Base**
```bash
feat(architecture): implementar arquitectura MVVM

- Crear estructura de carpetas (data, ui, navigation)
- Implementar ViewModels básicos
- Configurar inyección de dependencias
- Separar responsabilidades
```

### **Commit 3: Base de Datos**
```bash
feat(database): implementar Room Database

- Crear AppDatabase.kt
- Implementar entidades (User, ServiceRequest, etc.)
- Crear DAOs para operaciones
- Configurar repositorios
```

### **Commit 4: Pantallas Principales**
```bash
feat(ui): crear pantallas de autenticación

- Implementar LoginScreen
- Crear RegisterScreen
- Agregar AdminAuthScreen
- Implementar navegación básica
```

### **Commit 5: Pantalla Principal**
```bash
feat(ui): crear HomeScreen con servicios

- Implementar pantalla principal
- Agregar botones de acción
- Crear sección de estadísticas
- Implementar navegación inferior
```

### **Commit 6: Formularios**
```bash
feat(forms): implementar solicitud de servicios

- Crear RequestServiceScreen
- Implementar formularios con validaciones
- Agregar selección de tipo de servicio
- Crear RequestFormViewModel
```

### **Commit 7: Cámara**
```bash
feat(camera): implementar cámara nativa

- Crear CameraScreen con CameraX
- Implementar permisos de cámara
- Agregar FileProvider
- Integrar con formularios
```

### **Commit 8: Historial**
```bash
feat(history): implementar historial de solicitudes

- Crear RequestHistoryScreen
- Implementar RequestHistoryEntity
- Agregar navegación a historial
- Crear RequestHistoryDao
```

### **Commit 9: Animaciones**
```bash
feat(animations): agregar animaciones y transiciones

- Implementar animaciones en botones
- Agregar transiciones suaves
- Crear efectos visuales
- Optimizar experiencia de usuario
```

### **Commit 10: Documentación**
```bash
docs(project): crear documentación completa

- Crear README.md detallado
- Agregar comentarios en código
- Documentar arquitectura
- Incluir guía de instalación
```

## 📊 Métricas de Commits:

### **Distribución por Tipo:**
- `feat`: 60% (funcionalidades principales)
- `fix`: 15% (correcciones de errores)
- `docs`: 15% (documentación)
- `refactor`: 10% (mejoras de código)

### **Distribución por Desarrollador:**
- **Matías**: 70% de los commits
- **Compañero**: 30% de los commits

### **Frecuencia:**
- **Diaria**: 2-3 commits por día
- **Semanal**: 10-15 commits por semana
- **Total**: 50+ commits en el proyecto

## 🎯 Mejores Prácticas:

### **1. Mensajes Claros:**
- Usar imperativo ("agregar" no "agregué")
- Ser específico y conciso
- Incluir contexto cuando sea necesario

### **2. Commits Atómicos:**
- Un commit = una funcionalidad
- No mezclar cambios no relacionados
- Hacer commits pequeños y frecuentes

### **3. Documentación:**
- Incluir descripción detallada
- Mencionar archivos modificados
- Agregar referencias a issues

### **4. Testing:**
- Verificar que el código compile
- Probar funcionalidades antes del commit
- Incluir tests cuando sea posible

## 🔗 Enlaces Útiles:

- **GitHub**: [URL_DEL_REPOSITORIO]
- **Trello**: [URL_DEL_TRELLO]
- **Android Studio**: Proyecto local
- **Documentación**: README.md

---

**Desarrollado para DSY1105 - Desarrollo de Aplicaciones Móviles**
