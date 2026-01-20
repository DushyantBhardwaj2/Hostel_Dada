package com.hosteldada.android

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hosteldada.android.data.firebase.*
import com.hosteldada.android.presentation.viewmodel.*

/**
 * Koin Dependency Injection Modules for Android App
 * 
 * Demonstrates Clean Architecture with proper dependency inversion:
 * - Presentation depends on Domain
 * - Data implements Domain interfaces
 * - Firebase is abstracted behind interfaces
 */

// App-level module for Android-specific dependencies
val appModule = module {
    // Android Dispatcher Provider
    single { AndroidDispatcherProvider() }
}

// Firebase SDK instances module
val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
}

// Repository implementations with Firebase
val repositoryModule = module {
    // Auth Repository
    single<AuthRepository> { 
        FirebaseAuthRepositoryImpl(
            firebaseAuth = get(),
            dispatchers = get()
        ) 
    }
    
    // User Repository
    single<UserRepository> { 
        FirebaseUserRepositoryImpl(
            firestore = get(),
            realtimeDb = get(),
            dispatchers = get()
        ) 
    }
    
    // SnackCart Repository
    single<SnackCartRepository> { 
        FirebaseSnackCartRepositoryImpl(
            firestore = get(),
            realtimeDb = get(),
            dispatchers = get()
        ) 
    }
    
    // Roomie Repository
    single<RoomieRepository> { 
        FirebaseRoomieRepositoryImpl(
            firestore = get(),
            realtimeDb = get(),
            dispatchers = get()
        ) 
    }
}

// Use Case module
val useCaseModule = module {
    // Auth Use Cases
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { GoogleSignInUseCase(get()) }
    
    // SnackCart Use Cases
    factory { GetSnacksUseCase(get()) }
    factory { SearchSnacksUseCase(get()) }
    factory { AddToCartUseCase(get()) }
    factory { PlaceOrderUseCase(get()) }
    factory { GetOrdersUseCase(get()) }
    
    // Roomie Use Cases
    factory { GetRoomieProfileUseCase(get()) }
    factory { SaveRoomieProfileUseCase(get()) }
    factory { FindMatchesUseCase(get()) }
    factory { GetCompatibilityScoreUseCase(get()) }
    factory { SendMatchRequestUseCase(get()) }
}

// ViewModel module
val viewModelModule = module {
    viewModel { 
        LoginViewModel(
            loginUseCase = get(),
            googleSignInUseCase = get(),
            getCurrentUserUseCase = get()
        ) 
    }
    
    viewModel { 
        RegisterViewModel(
            registerUseCase = get()
        ) 
    }
    
    viewModel { 
        SnackCartViewModel(
            getSnacksUseCase = get(),
            searchSnacksUseCase = get(),
            addToCartUseCase = get(),
            placeOrderUseCase = get(),
            getOrdersUseCase = get()
        ) 
    }
    
    viewModel { 
        RoomieViewModel(
            getProfileUseCase = get(),
            saveProfileUseCase = get(),
            findMatchesUseCase = get(),
            getCompatibilityUseCase = get(),
            sendRequestUseCase = get()
        ) 
    }
    
    viewModel { 
        DashboardViewModel(
            getCurrentUserUseCase = get(),
            logoutUseCase = get()
        ) 
    }
}

// Interface stubs - These reference the actual implementations from shared modules
// They're declared here for Koin to resolve properly

interface AuthRepository
interface UserRepository
interface SnackCartRepository  
interface RoomieRepository

// Use Case stubs
class LoginUseCase(repo: AuthRepository)
class RegisterUseCase(repo: AuthRepository)
class LogoutUseCase(repo: AuthRepository)
class GetCurrentUserUseCase(repo: AuthRepository)
class GoogleSignInUseCase(repo: AuthRepository)
class GetSnacksUseCase(repo: SnackCartRepository)
class SearchSnacksUseCase(repo: SnackCartRepository)
class AddToCartUseCase(repo: SnackCartRepository)
class PlaceOrderUseCase(repo: SnackCartRepository)
class GetOrdersUseCase(repo: SnackCartRepository)
class GetRoomieProfileUseCase(repo: RoomieRepository)
class SaveRoomieProfileUseCase(repo: RoomieRepository)
class FindMatchesUseCase(repo: RoomieRepository)
class GetCompatibilityScoreUseCase(repo: RoomieRepository)
class SendMatchRequestUseCase(repo: RoomieRepository)

// Dispatcher provider for Android
class AndroidDispatcherProvider : DispatcherProvider {
    override val main = kotlinx.coroutines.Dispatchers.Main
    override val io = kotlinx.coroutines.Dispatchers.IO
    override val default = kotlinx.coroutines.Dispatchers.Default
}

interface DispatcherProvider {
    val main: kotlinx.coroutines.CoroutineDispatcher
    val io: kotlinx.coroutines.CoroutineDispatcher
    val default: kotlinx.coroutines.CoroutineDispatcher
}
