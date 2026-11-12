# Contador Mus (Android)

Marcador de mus para el móvil, en horizontal. Funciona sin internet.

## Qué hace
- Tocar en cualquier parte de tu lado suma 1; botones +2, +3, +5 y Órdago (dos toques).
- Juego a 40 (o 30) tantos y partida al primero que llegue a 4 juegos (se cambia en ajustes).
- Deshacer sin límite, guardado automático, vibración y pantalla siempre encendida.
- **Solo horizontal.** Para usarla en vertical hay que activarlo en Ajustes → "Permitir jugar en vertical".
- Botón atrás: cierra ventanas abiertas; para salir hay que pulsarlo dos veces.

## Estructura
```
app/src/main/assets/index.html   ← toda la app (pantalla, lógica, estilos)
app/src/main/assets/fonts/       ← tipografía Inter (licencia OFL)
app/src/main/java/.../MainActivity.kt ← envoltorio Android: horizontal, pantalla encendida,
                                        pantalla completa, vibración, botón atrás
```
Para cambiar el aspecto o las reglas, se edita solo `index.html`.

## Conseguir el APK

### Opción A – GitHub (sin instalar nada en el ordenador)
1. Crea un repositorio en GitHub y sube esta carpeta tal cual.
2. Se compila solo: pestaña **Actions** → "Compilar APK" → la última ejecución → **Artifacts** → `ContadorMus-apk`.
3. Descomprime el zip, pasa `app-release.apk` al móvil y ábrelo (Android pedirá permitir instalar apps de esa fuente).

### Opción B – Android Studio
1. Android Studio → **Open** → elige esta carpeta. Espera a que termine de sincronizar Gradle.
2. Con el móvil conectado por USB (depuración USB activada) pulsa ▶ Run.
   O bien **Build → Build App Bundle(s) / APK(s) → Build APK(s)** y copia el APK al móvil.

Requisitos: Android 8.0 o superior.
