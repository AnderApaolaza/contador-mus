plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ander.contadormus"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ander.contadormus"
        minSdk = 26          // Android 8.0 o superior
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Para uso personal se firma con la clave de depuración: se instala sin más pasos
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
// Sin dependencias: solo SDK de Android + Kotlin estándar
