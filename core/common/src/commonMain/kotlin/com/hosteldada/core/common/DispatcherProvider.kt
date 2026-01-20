package com.hosteldada.core.common

import kotlinx.coroutines.CoroutineDispatcher

/**
 * ============================================
 * DISPATCHER PROVIDER
 * ============================================
 * 
 * Interface for providing coroutine dispatchers.
 * Allows for easy testing and platform-specific implementations.
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

/**
 * Expect declaration for platform-specific implementation
 */
expect class DispatcherProviderImpl() : DispatcherProvider {
    override val main: CoroutineDispatcher
    override val io: CoroutineDispatcher
    override val default: CoroutineDispatcher
}
