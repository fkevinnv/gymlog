# GymLog

App Android nativa en Java para gestionar rutinas de gimnasio y registrar entrenamientos.
Funciona 100% sin conexión: los datos se guardan en SQLite con Room.

## Funcionalidad

- Crear rutinas, días de entrenamiento y ejercicios (con series objetivo y descanso).
- Entrenamiento en vivo: se registra peso y repeticiones de cada serie y arranca un
  temporizador de descanso (CountDownTimer) con anillo de progreso, +15 s y saltar.
- Muestra lo que se hizo la última vez en cada ejercicio y rellena peso y repeticiones.
- Resumen final: series, volumen total y duración.

## Arquitectura

Capas: `data` (Room + repositorio) -> `ui/GymViewModel` -> Activities con ViewBinding.

    com.gymlog.app
    |-- data
    |   |-- entidad     Rutina, DiaEntrenamiento, Ejercicio, Serie
    |   |-- relacion    RutinaConDias, DiaConEjercicios, ResumenSesion
    |   |-- dao         RutinaDao, DiaDao, EjercicioDao, SerieDao
    |   |-- AppDatabase
    |   `-- GymRepositorio
    |-- ui
    |   |-- GymViewModel
    |   |-- principal      MainActivity, RutinaAdapter
    |   |-- rutina         RutinaActivity, DiaAdapter
    |   `-- entrenamiento  EntrenamientoActivity, SerieAdapter, FormatoPeso
    `-- util               Animaciones, Dialogos

Relaciones: Rutina 1-N DiaEntrenamiento 1-N Ejercicio 1-N Serie (borrado en cascada).

## Requisitos

- JDK 17
- Android SDK con la plataforma 34 (API 34)
- Gradle 8.7 (lo descarga el wrapper) y Android Gradle Plugin 8.5.2

## Cómo abrirlo

Con Android Studio: File > Open, elegir la carpeta GymLog y esperar a la sincronización de Gradle.

Desde terminal (sin Android Studio):

    export ANDROID_HOME=$HOME/Android/Sdk          # donde esté el SDK
    echo "sdk.dir=$ANDROID_HOME" > local.properties
    gradle wrapper --gradle-version 8.7            # genera gradlew la primera vez
    ./gradlew assembleDebug
    adb install -r app/build/outputs/apk/debug/app-debug.apk
