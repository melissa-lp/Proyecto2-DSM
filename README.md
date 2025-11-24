# 📱 EVENTOS APP

Aplicación móvil Android para la gestión integral de eventos, desarrollada con Jetpack Compose y Firebase.

## 👥 INTEGRANTES

| Nombre | Carnet |
|--------|--------|
| Jairo Dennis Ramos Jiménez | RJ172021 |
| Melissa Vanina López Peña | LP223029 |
| César Enrique Regalado Villalta | RV210723 |
| Denis Josué Vásquez Rodríguez | VR222731 |
| Emilia Eunice Meléndez Barreiro | MB211545 |
| Victor Amilcar Elías Peña | EP171613 |

## 🔗 ENLACES

- **Trello del Proyecto**: [https://trello.com/invite/b/691cb1f4acf4fde041bf2cf0/ATTI4cfe312bfa7bd7274019bf4af41d87563B96EB35/proyecto-2-dsm](https://trello.com/invite/b/691cb1f4acf4fde041bf2cf0/ATTI4cfe312bfa7bd7274019bf4af41d87563B96EB35/proyecto-2-dsm)
- **Firebase Console**: [https://console.firebase.google.com/](https://console.firebase.google.com/)
- **Documentación Jetpack Compose**: [https://developer.android.com/jetpack/compose](https://developer.android.com/jetpack/compose)


## 📋 DESCRIPCIÓN

**Eventos App** es una aplicación Android nativa que permite a los usuarios crear, descubrir y participar en eventos de manera eficiente. La aplicación integra funcionalidades completas para la planificación y desarrollo de un evento, desde su creación hasta la valoración posterior mediante comentarios y calificaciones.

## ✨ CARACTERÍSTICAS PRINCIPALES

### 🔐 Autenticación
- Registro e inicio de sesión con correo electrónico y contraseña
- Inicio de sesión con Google (Google Sign-In)
- Gestión segura de sesiones

### 📅 Gestión de Eventos
- **Crear eventos**: Título, descripción, fecha, hora, ubicación, categoría y capacidad máxima
- **Ver eventos disponibles**: Lista completa con búsqueda y filtros
- **Editar eventos propios**: Modificar detalles de eventos creados
- **Eliminar eventos**: Con confirmación de seguridad
- **Detalles completos**: Información detallada de cada evento

### 👥 Participación
- **Confirmar asistencia**: Registro para asistir a eventos
- **Cancelar asistencia**: Opción de cancelar participación
- **Mis eventos**: Vista de eventos confirmados
- **Eventos creados**: Gestión de eventos organizados

### 💬 Interacción Social
- **Comentarios**: Dejar opiniones sobre eventos finalizados
- **Calificaciones**: Sistema de valoración con estrellas (1-5)

### 📊 Historial
- Historial de eventos pasados
- Vista de eventos finalizados con participación
- Información de asistentes por evento

### 🎨 Interfaz
- Diseño moderno siguiendo Material Design 3
- Navegación intuitiva con Bottom Navigation Bar
- Estados de carga, error y vacío bien definidos
- Responsive y adaptable

## 🛠️ TECNOLOGÍAS UTILIZADAS

### Frontend
- **Kotlin**: Lenguaje de programación principal
- **Jetpack Compose**: Framework de UI declarativo
- **Material Design 3**: Sistema de diseño
- **Compose Navigation**: Gestión de navegación

### Backend
- **Firebase Authentication**: Gestión de usuarios
- **Cloud Firestore**: Base de datos en tiempo real
- **Firebase Google Sign-In**: Autenticación social

### Arquitectura
- **MVVM (Model-View-ViewModel)**: Patrón arquitectónico
- **StateFlow**: Gestión reactiva de estados
- **Coroutines**: Programación asíncrona
- **Repository Pattern**: Abstracción de fuentes de datos

### Herramientas
- **Android Studio**: IDE de desarrollo
- **Gradle**: Sistema de construcción
- **Git**: Control de versiones

## 📦 REQUISITOS DEL SISTEMA

- **Android SDK**: Mínimo API 24 (Android 7.0)
- **Android Studio**: Última versión estable
- **Kotlin**: 1.9+
- **Gradle**: 8.0+

## 🚀 INSTALACIÓN Y CONFIGURACIÓN

### 1. Clonar el repositorio
```bash
git clone https://github.com/melissa-lp/Proyecto2-DSM
cd eventos-app
```

### 2. Configurar Firebase

1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Agregar una aplicación Android con el package name: `com.example.proyecto2`
3. Descargar el archivo `google-services.json`
4. Colocar `google-services.json` en la carpeta `app/`

### 3. Habilitar servicios de Firebase

En Firebase Console:
- **Authentication**: Habilitar Email/Password y Google Sign-In
- **Firestore Database**: Crear base de datos en modo producción
- **Configurar reglas de seguridad** (ver sección de Firestore Rules)

### 4. Configurar Google Sign-In

1. En Firebase Console → Authentication → Sign-in method → Google
2. Habilitar y agregar email de soporte
3. Obtener el SHA-1 de tu keystore:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```
4. Agregar SHA-1 en Firebase Console → Configuración del proyecto → Tus apps

### 5. Compilar y ejecutar
```bash
# En Android Studio
File → Sync Project with Gradle Files
Build → Clean Project
Build → Rebuild Project
Run → Run 'app'
```

## 🔥 FIRESTORE RULES

Configurar las siguientes reglas de seguridad en Firestore:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Eventos: Lectura pública, escritura autenticada
    match /events/{eventId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && 
                               request.auth.uid == resource.data.organizerId;
    }
  }
}
```

## 📂 ESTRUCTURA DEL PROYECTO
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/proyecto2/
│   │   │   ├── data/
│   │   │   │   ├── Event.kt
│   │   │   │   ├── Comment.kt
│   │   │   │   ├── EventRepository.kt
│   │   │   │   ├── AuthRepository.kt
│   │   │   │   ├── GoogleSignInHelper.kt
│   │   │   │   └── Constants.kt
│   │   │   ├── ui/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── EventListScreen.kt
│   │   │   │   │   ├── EventDetailScreen.kt
│   │   │   │   │   ├── CreateEventScreen.kt
│   │   │   │   │   ├── EditEventScreen.kt
│   │   │   │   │   ├── MyCreatedEventsScreen.kt
│   │   │   │   │   └── HistoryScreen.kt
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt
│   │   │   │       ├── Theme.kt
│   │   │   │       └── Type.kt
│   │   │   ├── viewmodel/
│   │   │   │   ├── AuthViewModel.kt
│   │   │   │   └── EventViewModel.kt
│   │   │   └── MainActivity.kt
│   │   └── res/
│   │       ├── values/
│   │       └── drawable/
│   └── google-services.json
└── build.gradle.kts
```

## 🎯 CASOS DE USO

### Usuario No Registrado
1. Ver pantalla de login
2. Registrarse con email/password o Google
3. Iniciar sesión

### Usuario Registrado
1. Ver lista de eventos disponibles
2. Ver detalles de un evento
3. Confirmar asistencia a un evento
4. Ver sus eventos confirmados
5. Cancelar asistencia

### Organizador
1. Crear un nuevo evento
2. Ver sus eventos creados
3. Editar evento
4. Eliminar evento
5. Ver estadísticas de asistentes

### Participante de Evento Finalizado
1. Ver eventos pasados en el historial
2. Dejar comentarios
3. Calificar el evento
4. Ver comentarios de otros usuarios

## 📄 LICENCIA

Este proyecto está bajo la Licencia Creative Commons Atribución-NoComercial 4.0 Internacional (CC BY-NC 4.0).

**Términos:**
- ✅ Compartir: Copiar y redistribuir el material
- ✅ Adaptar: Remezclar, transformar y construir sobre el material
- ❌ Uso comercial: No se permite el uso comercial
- ✅ Atribución: Se debe dar crédito apropiado

© 2025 Equipo de Desarrollo - Universidad Don Bosco
