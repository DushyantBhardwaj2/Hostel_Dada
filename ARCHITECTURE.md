# Hostel Dada - Architecture Documentation

## Overview

This document describes the architecture of the Hostel Dada KMP application, designed following **Clean Architecture** principles with **MVVM** presentation pattern.

---

## 1. Architectural Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Screens    │  │  ViewModels  │  │   UiState    │          │
│  │  (Compose)   │  │  (StateFlow) │  │  (Immutable) │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                            │                                     │
│                            ▼                                     │
├─────────────────────────────────────────────────────────────────┤
│                         DOMAIN LAYER                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Use Cases  │  │  Repository  │  │   Entities   │          │
│  │   (Single    │  │  Interfaces  │  │   (Models)   │          │
│  │   Responsi-  │  │              │  │              │          │
│  │   bility)    │  │              │  │              │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                            │                                     │
│                            ▼                                     │
├─────────────────────────────────────────────────────────────────┤
│                          DATA LAYER                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  Repository  │  │ Data Sources │  │    Mappers   │          │
│  │   Impls      │  │  (Firebase,  │  │   (DTO ↔    │          │
│  │              │  │   Local)     │  │    Entity)   │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Module Structure

### 2.1 Core Modules

```
core/
├── common/          # Shared utilities
│   ├── Result.kt    # Sealed class for operation outcomes
│   ├── Extensions.kt
│   └── DispatcherProvider.kt
│
├── domain/          # Business logic (NO dependencies on frameworks)
│   ├── model/       # Domain entities
│   ├── repository/  # Repository interfaces
│   ├── algorithm/   # DSA implementations
│   └── usecase/     # Use case base class
│
└── data/            # Data access layer
    ├── repository/  # Repository implementations
    └── source/
        ├── remote/  # Firebase, API
        └── local/   # SQLDelight, cache
```

### 2.2 Feature Modules

```
feature/
├── auth/
│   ├── domain/      # Auth-specific use cases
│   └── presentation/
│       ├── LoginViewModel.kt
│       ├── AuthUiState.kt
│       └── LoginScreen.kt
│
├── snackcart/
│   ├── domain/
│   └── presentation/
│       ├── SnackCartViewModel.kt
│       ├── SnackCartAdminViewModel.kt
│       └── screens/
│
└── roomie/
    ├── domain/
    └── presentation/
        ├── SurveyViewModel.kt
        ├── MatchingViewModel.kt
        └── screens/
```

---

## 3. MVVM Implementation

### 3.1 State Management

```kotlin
// Immutable UI State
data class SnackCartUiState(
    val isLoading: Boolean = false,
    val snacks: List<Snack> = emptyList(),
    val cart: Cart = Cart(),
    val error: String? = null
)

// User Intents (Actions)
sealed interface SnackCartIntent {
    data class SearchSnacks(val query: String) : SnackCartIntent
    data class AddToCart(val snack: Snack) : SnackCartIntent
    object PlaceOrder : SnackCartIntent
}
```

### 3.2 ViewModel Pattern

```kotlin
class SnackCartViewModel(
    private val searchSnacks: SearchSnacksUseCase,
    private val addToCart: AddToCartUseCase,
    private val dispatcher: DispatcherProvider
) {
    private val _uiState = MutableStateFlow(SnackCartUiState())
    val uiState: StateFlow<SnackCartUiState> = _uiState.asStateFlow()
    
    fun onIntent(intent: SnackCartIntent) {
        when (intent) {
            is SnackCartIntent.SearchSnacks -> search(intent.query)
            is SnackCartIntent.AddToCart -> addItem(intent.snack)
            is SnackCartIntent.PlaceOrder -> placeOrder()
        }
    }
}
```

### 3.3 Unidirectional Data Flow

```
┌──────────┐         ┌──────────────┐         ┌──────────┐
│  Screen  │ ──────► │  ViewModel   │ ──────► │ Use Case │
│          │  Intent │              │  Call   │          │
└──────────┘         └──────────────┘         └──────────┘
     ▲                      │                       │
     │                      │                       │
     │    State            State                   Result
     │                      │                       │
     └──────────────────────┴───────────────────────┘
```

---

## 4. Dependency Injection

### 4.1 Koin Modules

```kotlin
// Core Module
val coreModule = module {
    single<DispatcherProvider> { DispatcherProviderImpl() }
}

// Data Module
val dataModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<SnackRepository> { SnackRepositoryImpl(get(), get()) }
}

// Use Case Module
val useCaseModule = module {
    factory { SearchSnacksUseCase(get()) }
    factory { AddToCartUseCase(get()) }
}

// ViewModel Module
val viewModelModule = module {
    factory { SnackCartViewModel(get(), get(), get()) }
}
```

### 4.2 Injection Graph

```
Application
    │
    ├── Koin Container
    │       │
    │       ├── Singletons
    │       │   ├── Repositories
    │       │   └── DataSources
    │       │
    │       └── Factories
    │           ├── Use Cases
    │           └── ViewModels
    │
    └── Platform Modules
        ├── Android: Context, Activity
        └── iOS: NSUserDefaults
```

---

## 5. Data Flow

### 5.1 Search Operation Flow

```
User Input: "Chips"
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│ SearchSnacksUseCase                                     │
│   - Validates input                                     │
│   - Calls repository                                    │
└─────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│ SnackRepositoryImpl                                     │
│   - Uses Trie for O(k) prefix search                    │
│   - Falls back to local cache if offline               │
└─────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│ SnackSearchTrie                                         │
│   - Traverses trie nodes                               │
│   - Collects all snacks with matching prefix           │
└─────────────────────────────────────────────────────────┘
       │
       ▼
Result<List<Snack>>
```

### 5.2 Compatibility Calculation Flow

```
Request: Match for Student A
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│ GetTopMatchesUseCase                                    │
│   - Validates student has survey                        │
│   - Requests compatibility scores                       │
└─────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│ CompatibilityRepositoryImpl                             │
│   - Initializes graph if needed                         │
│   - Gets edges for student                              │
└─────────────────────────────────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────────────────────────┐
│ CompatibilityGraph                                      │
│   - Calculates weighted scores                          │
│   - Sorts by compatibility                              │
│   - Returns top N matches                               │
└─────────────────────────────────────────────────────────┘
       │
       ▼
Result<List<CompatibilityScore>>
```

---

## 6. Error Handling

### 6.1 Result Sealed Class

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Extension functions
fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data

fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}
```

### 6.2 Error Propagation

```
Data Source Error
       │
       ▼
┌─────────────────────┐
│ Repository catches  │
│ and wraps in Result │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│ Use Case receives   │
│ Result.Error        │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│ ViewModel updates   │
│ UiState with error  │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│ Screen displays     │
│ error message       │
└─────────────────────┘
```

---

## 7. Testing Strategy

### 7.1 Test Pyramid

```
          ┌─────────┐
         /   E2E    \
        /   Tests    \
       ───────────────
      /  Integration  \
     /     Tests       \
    ─────────────────────
   /      Unit Tests     \
  /   (Use Cases, VMs)    \
 ───────────────────────────
```

### 7.2 Testable Design

```kotlin
// Use Case is easily testable with mock repository
class SearchSnacksUseCaseTest {
    private val mockRepository = mockk<SnackRepository>()
    private val useCase = SearchSnacksUseCase(mockRepository)
    
    @Test
    fun `search returns filtered results`() = runTest {
        // Given
        coEvery { mockRepository.searchSnacks("ch") } returns 
            Result.Success(listOf(testSnack))
        
        // When
        val result = useCase("ch")
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(1, (result as Result.Success).data.size)
    }
}
```

---

## 8. Platform Specifics

### 8.1 Expect/Actual Pattern

```kotlin
// commonMain
expect class DispatcherProviderImpl() : DispatcherProvider {
    override val main: CoroutineDispatcher
    override val io: CoroutineDispatcher
}

// androidMain
actual class DispatcherProviderImpl actual constructor() : DispatcherProvider {
    actual override val main = Dispatchers.Main
    actual override val io = Dispatchers.IO
}

// iosMain
actual class DispatcherProviderImpl actual constructor() : DispatcherProvider {
    actual override val main = Dispatchers.Main
    actual override val io = Dispatchers.Default  // iOS doesn't have Dispatchers.IO
}
```

---

## 9. Scalability Considerations

### 9.1 Adding New Features

1. Create feature module under `feature/`
2. Define domain entities and repository interfaces
3. Implement use cases
4. Create ViewModel and UiState
5. Register in Koin modules
6. Create Compose screens

### 9.2 Database Migration

When switching from Firebase to another backend:
1. Only modify Data layer
2. Create new DataSource implementations
3. Repository implementations remain same
4. Domain and Presentation layers unchanged

---

## 10. Performance Optimizations

### 10.1 Implemented

- **Trie** for O(k) search instead of O(n*k)
- **Lazy loading** of compatibility scores
- **In-memory caching** with StateFlow
- **Pagination** support in use cases

### 10.2 Planned

- SQLDelight for offline persistence
- Image caching with Coil/Kamel
- Background sync with WorkManager (Android)

---

## References

- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Guide to app architecture - Android](https://developer.android.com/topic/architecture)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Koin Documentation](https://insert-koin.io/docs/quickstart/kotlin)
