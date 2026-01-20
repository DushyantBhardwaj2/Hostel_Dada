/**
 * ============================================
 * DEPENDENCY VERSIONS & CONSTANTS
 * ============================================
 * 
 * Centralized dependency management for the entire project.
 */
object Versions {
    // Kotlin
    const val kotlin = "1.9.21"
    const val coroutines = "1.7.3"
    const val serialization = "1.6.2"
    const val datetime = "0.5.0"
    
    // Compose
    const val compose = "1.5.11"
    const val composeCompiler = "1.5.6"
    
    // Android
    const val androidGradle = "8.2.0"
    const val androidxCore = "1.12.0"
    const val androidxLifecycle = "2.6.2"
    const val androidxActivity = "1.8.2"
    
    // DI
    const val koin = "3.5.3"
    const val koinCompose = "3.5.3"
    
    // Network
    const val ktor = "2.3.7"
    
    // Database
    const val sqldelight = "2.0.1"
    
    // Firebase
    const val firebaseKotlin = "1.11.1"
    
    // Testing
    const val junit = "4.13.2"
    const val mockk = "1.13.8"
    const val turbine = "1.0.0"
}

object Deps {
    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val serializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.datetime}"
    }
    
    object Android {
        const val coreKtx = "androidx.core:core-ktx:${Versions.androidxCore}"
        const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidxLifecycle}"
        const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidxLifecycle}"
        const val activityCompose = "androidx.activity:activity-compose:${Versions.androidxActivity}"
    }
    
    object Compose {
        const val runtime = "org.jetbrains.compose.runtime:runtime:${Versions.compose}"
        const val foundation = "org.jetbrains.compose.foundation:foundation:${Versions.compose}"
        const val material3 = "org.jetbrains.compose.material3:material3:${Versions.compose}"
        const val ui = "org.jetbrains.compose.ui:ui:${Versions.compose}"
    }
    
    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koinCompose}"
    }
    
    object Ktor {
        const val core = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val cio = "io.ktor:ktor-client-cio:${Versions.ktor}"
        const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:${Versions.ktor}"
        const val serializationJson = "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}"
        const val logging = "io.ktor:ktor-client-logging:${Versions.ktor}"
        const val android = "io.ktor:ktor-client-android:${Versions.ktor}"
        const val darwin = "io.ktor:ktor-client-darwin:${Versions.ktor}"
    }
    
    object SQLDelight {
        const val runtime = "app.cash.sqldelight:runtime:${Versions.sqldelight}"
        const val coroutines = "app.cash.sqldelight:coroutines-extensions:${Versions.sqldelight}"
        const val androidDriver = "app.cash.sqldelight:android-driver:${Versions.sqldelight}"
        const val nativeDriver = "app.cash.sqldelight:native-driver:${Versions.sqldelight}"
        const val jvmDriver = "app.cash.sqldelight:sqlite-driver:${Versions.sqldelight}"
    }
    
    object Firebase {
        const val auth = "dev.gitlive:firebase-auth:${Versions.firebaseKotlin}"
        const val database = "dev.gitlive:firebase-database:${Versions.firebaseKotlin}"
        const val firestore = "dev.gitlive:firebase-firestore:${Versions.firebaseKotlin}"
    }
    
    object Testing {
        const val junit = "junit:junit:${Versions.junit}"
        const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
        const val mockk = "io.mockk:mockk:${Versions.mockk}"
        const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
    }
}
