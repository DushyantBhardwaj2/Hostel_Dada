package com.hosteldada.shared.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS platform-specific DI modules
 */
actual fun platformModules(): List<Module> = listOf(
    iosPlatformModule
)

val iosPlatformModule = module {
    // iOS-specific dependencies
}
