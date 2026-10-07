# HormiApp

Aplicación nativa Android para el control de **finanzas personales**, enfocada en detectar y reducir los **gastos hormiga** (compras pequeñas y frecuentes que pasan desapercibidas). Funciona sin internet: todos los datos se guardan en el dispositivo.

Proyecto de la **Entrega 3 – APP Nativa** del curso de Aplicaciones Móviles (Android Studio + Kotlin + Jetpack Compose).

## Equipo

| Integrante | Rol |
|---|---|
| Sebastián Cruz | Diseño: boceto y wireframe en Figma |
| Sebastián Muñoz | Desarrollo Android |
| Juan David Londoño | Desarrollo Android |

## Funcionalidades

- **Registro e inicio de sesión local** con nombre, PIN y pregunta de seguridad; recuperación de PIN.
- **Onboarding** con ingreso mensual.
- **Inicio:** saldo, resumen de ingresos y gastos recientes.
- **Gastos:** listado agrupado por categoría y detalle de cada gasto (con eliminación).
- **Registrar gasto:** formulario con categoría, fecha y clasificación automática como gasto hormiga según un umbral configurable.
- **Ingresos:** registro y consulta de ingresos.
- **Análisis:** total de gastos hormiga, comparación semanal, categorías principales y proyección.
- **Metas de ahorro:** crear y seguir metas.
- **Perfil, Configuración** (umbral de gasto hormiga) y **Créditos**.

## Pantallas y navegación

Flujo de entrada: `Splash → Login / Registro → Onboarding → Inicio`.
Barra inferior: **Inicio · Gastos · Análisis · Perfil**.
Desde estas: Registrar gasto, Detalle de gasto, Ingresos, Metas, Configuración y Créditos.
Las rutas están definidas en `navigation/Screen.kt` y `navigation/HormiAppNavigation.kt`.

## Arquitectura

MVVM por capas, con inyección de dependencias:

```
ui (Compose Screen + ViewModel)
        │
domain/usecase
        │
data/repository ──► data/local (Room: DAO + entidades)
                └─► data/preferences (DataStore)
```

```
app/src/main/java/com/hormi/hormiapp/
├── data/
│   ├── local/          # Room: HormiAppDatabase, dao/, entity/
│   ├── preferences/    # DataStore (usuario, PIN, umbral)
│   └── repository/     # TransactionRepository, GoalRepository
├── di/                 # Módulo Hilt
├── domain/usecase/     # Casos de uso
├── navigation/         # Rutas y NavHost
└── ui/                 # Pantallas por funcionalidad, componentes y tema
```

## Tecnologías

- Kotlin y Jetpack Compose (Material 3)
- Navigation Compose
- Room (transacciones y metas) y DataStore Preferences (datos de usuario)
- Hilt (inyección de dependencias) y KSP
- Gradle con catálogo de versiones (`gradle/libs.versions.toml`)

Configuración: `minSdk 26`, `targetSdk 37`, `compileSdk 37`.

## Diseño

> Completar con los enlaces/archivos reales.

- Wireframe en papel: _pendiente de subir a `docs/` (fotos)_
- Wireframe digital en Figma: _pendiente de agregar el enlace_

## Cómo compilar y ejecutar

Requisitos: Android Studio reciente (compatible con AGP 9.3) y JDK 17 o superior.

1. Clonar el repositorio y abrirlo en Android Studio.
2. Sincronizar Gradle y ejecutar en un emulador o dispositivo físico (Android 8.0 / API 26 o superior).

Desde terminal:

```bash
./gradlew assembleDebug
```

## Publicación

> Completar a medida que avance.

- [ ] Probado en dispositivo físico
- [ ] Keystore de firma creado (guardado fuera del repositorio)
- [ ] `.aab` generado (`./gradlew bundleRelease`)
- [ ] Ficha de Google Play (nombre, descripción, capturas, ícono)
- [ ] Política de privacidad
- [ ] Aplicación publicada: _enlace pendiente_

## Flujo de trabajo del equipo

- Rama principal: `main`.
- Cada integrante trabaja en su propia rama y la integra mediante pull request.
- Mensajes de commit con prefijos: `feat:`, `fix:`, `ui:`, `style:`, `build:`, `chore:`.
