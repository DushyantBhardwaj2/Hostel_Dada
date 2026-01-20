package com.hosteldada.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Android implementation of DispatcherProvider
 */
actual class DispatcherProviderImpl actual constructor() : DispatcherProvider {
    actual override val main: CoroutineDispatcher = Dispatchers.Main
    actual override val io: CoroutineDispatcher = Dispatchers.IO
    actual override val default: CoroutineDispatcher = Dispatchers.Default
}
