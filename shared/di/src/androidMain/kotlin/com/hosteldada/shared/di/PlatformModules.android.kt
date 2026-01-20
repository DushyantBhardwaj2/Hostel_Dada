package com.hosteldada.shared.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android platform-specific DI modules
 */
actual fun platformModules(): List<Module> = listOf(
    androidPlatformModule
)

val androidPlatformModule = module {
    // Android-specific dependencies
    // Context, Activity, etc.
}
