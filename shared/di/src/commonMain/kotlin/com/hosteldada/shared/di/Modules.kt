package com.hosteldada.shared.di

import com.hosteldada.core.common.DispatcherProvider
import com.hosteldada.core.common.DispatcherProviderImpl
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * ============================================
 * DEPENDENCY INJECTION MODULES
 * ============================================
 * 
 * Koin DI configuration for Clean Architecture.
 */

// ==========================================
// CORE MODULE
// ==========================================

val coreModule = module {
    single<DispatcherProvider> { DispatcherProviderImpl() }
}

// ==========================================
// DATA LAYER MODULES
// ==========================================

val dataSourceModule = module {
    // Firebase data sources
    // single<AuthFirebaseDataSource> { AuthFirebaseDataSourceImpl() }
    // single<SnackFirebaseDataSource> { SnackFirebaseDataSourceImpl() }
    
    // Local data sources
    // single<SnackLocalDataSource> { SnackLocalDataSourceImpl() }
}

val repositoryModule = module {
    // Auth
    // single<AuthRepository> { AuthRepositoryImpl(get()) }
    
    // SnackCart
    // single<SnackRepository> { SnackRepositoryImpl(get(), get()) }
    // single<CartRepository> { CartRepositoryImpl(get()) }
    // single<OrderRepository> { OrderRepositoryImpl(get()) }
    
    // Roomie
    // single<SurveyRepository> { SurveyRepositoryImpl(get()) }
    // single<CompatibilityRepository> { CompatibilityRepositoryImpl(get(), get()) }
}

// ==========================================
// DOMAIN LAYER MODULES (Use Cases)
// ==========================================

val authUseCaseModule = module {
    // factory { SignInWithEmailUseCase(get()) }
    // factory { SignUpWithEmailUseCase(get()) }
    // factory { SignOutUseCase(get()) }
    // factory { ObserveCurrentUserUseCase(get()) }
}

val snackCartUseCaseModule = module {
    // factory { GetAllSnacksUseCase(get()) }
    // factory { SearchSnacksUseCase(get()) }
    // factory { AddToCartUseCase(get()) }
    // factory { PlaceOrderUseCase(get(), get()) }
}

val roomieUseCaseModule = module {
    // factory { SubmitSurveyUseCase(get()) }
    // factory { GetTopMatchesUseCase(get()) }
    // factory { GenerateAllCompatibilitiesUseCase(get()) }
}

// ==========================================
// PRESENTATION LAYER MODULES (ViewModels)
// ==========================================

val viewModelModule = module {
    // factory { LoginViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    // factory { SnackCartViewModel(...) }
    // factory { RoomieViewModel(...) }
}

// ==========================================
// COMBINED MODULES
// ==========================================

val commonModules: List<Module> = listOf(
    coreModule,
    dataSourceModule,
    repositoryModule,
    authUseCaseModule,
    snackCartUseCaseModule,
    roomieUseCaseModule,
    viewModelModule
)

/**
 * Platform-specific modules (expect/actual pattern)
 */
expect fun platformModules(): List<Module>

/**
 * All modules combined
 */
fun allModules(): List<Module> = commonModules + platformModules()

/**
 * Initialize Koin DI
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(allModules())
}
