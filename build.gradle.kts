plugins {
    // Kotlin Multiplatform
    kotlin("multiplatform") version "1.9.21" apply false
    kotlin("plugin.serialization") version "1.9.21" apply false
    kotlin("android") version "1.9.21" apply false
    
    // Android
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    
    // Compose
    id("org.jetbrains.compose") version "1.5.11" apply false
    
    // SQLDelight
    id("app.cash.sqldelight") version "2.0.1" apply false
    
    // Google Services (Firebase)
    id("com.google.gms.google-services") version "4.4.0" apply false
}

allprojects {
    group = "com.hosteldada"
    version = "1.0.0"
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}
