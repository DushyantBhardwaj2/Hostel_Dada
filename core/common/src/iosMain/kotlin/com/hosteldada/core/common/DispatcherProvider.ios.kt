package com.hosteldada.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * iOS implementation of DispatcherProvider
 * Note: iOS doesn't have Dispatchers.IO, using Default instead
 */
actual class DispatcherProviderImpl actual constructor() : DispatcherProvider {
    actual override val main: CoroutineDispatcher = Dispatchers.Main
    actual override val io: CoroutineDispatcher = Dispatchers.Default  // No IO on iOS
    actual override val default: CoroutineDispatcher = Dispatchers.Default
}
