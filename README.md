# Hostel Dada - Kotlin Multiplatform 🏠

## College Placement Project Portfolio

## About This Project

Hostel Dada is a **Kotlin Multiplatform Project (KMP)** designed to demonstrate a modern, scalable, and maintainable approach to cross-platform app development. The project targets Android, iOS, and Desktop from a single codebase, leveraging shared business logic, data handling, and UI components where possible.

### What is Kotlin Multiplatform (KMP)?
KMP allows you to write common code (business logic, data, algorithms, etc.) once and use it across multiple platforms (Android, iOS, Desktop, Web). Platform-specific code is only written where necessary (e.g., UI, platform APIs).

**Key Benefits:**
- **Code Reuse:** Share up to 80%+ of code across platforms
- **Consistency:** Single source of truth for business logic and models
- **Maintainability:** Fewer bugs, easier updates
- **Modern Tooling:** Compose Multiplatform, Coroutines, Koin, SQLDelight, Ktor

---

A **Kotlin Multiplatform** hostel management application demonstrating **Clean Architecture**, **MVVM**, and **DSA implementations** for efficient data handling.

> **Note**: This project is a migration from the Next.js web application in the parent folder to a cross-platform mobile/desktop solution using modern Kotlin technologies.

---

## 🎯 Project Highlights (For Interviewers)

### 1. Architecture & Design Patterns
- **Clean Architecture** - 3-layer separation (Domain → Data → Presentation)
- **MVVM Pattern** - ViewModel + StateFlow + Intent/Event pattern
- **Repository Pattern** - Abstraction over data sources
- **Dependency Injection** - Koin for loose coupling

### 2. Data Structures & Algorithms
- **Trie** - O(k) prefix search for snack items
- **Graph** - Weighted adjacency list for roommate compatibility
- **Priority Queue (Max-Heap)** - Maintenance request prioritization

### 3. Cross-Platform Development
- **Kotlin Multiplatform** - Single codebase for Android, iOS, Desktop
- **Compose Multiplatform** - Shared UI components
- **Expect/Actual** - Platform-specific implementations

---

## 📁 Project Structure

```
HostelDada-KMP/
├── buildSrc/                    # Build configuration & dependencies
│   └── Dependencies.kt
│
├── core/                        # Core modules (shared logic)
│   ├── common/                  # Result wrapper, extensions, dispatchers
│   │   └── src/commonMain/
│   │       └── Result.kt        # Sealed class for operation results
│   │       └── Extensions.kt    # Kotlin extension functions
│   │
│   ├── domain/                  # Business logic layer
│   │   └── src/commonMain/
│   │       ├── model/
│   │       │   ├── CompleteEntities.kt  # All domain models
│   │       │   └── Entities.kt
│   │       ├── repository/
│   │       │   └── CompleteRepositories.kt  # Repository interfaces
│   │       ├── algorithm/
│   │       │   └── DSAImplementations.kt  # ⭐ Trie, Graph, PriorityQueue
│   │       └── usecase/
│   │           └── UseCaseBase.kt
│   │
│   └── data/                    # Data layer
│       └── src/commonMain/
│           ├── repository/      # Repository implementations
│           │   ├── SnackCartRepositoryImpl.kt
│           │   └── RoomieRepositoryImpl.kt
│           └── source/
│               ├── remote/firebase/  # Firebase data sources
│               └── local/            # Local caching (SQLDelight)
│
├── feature/                     # Feature modules
│   ├── auth/                    # Authentication feature
│   │   └── src/commonMain/
│   │       ├── domain/
│   │       │   └── AuthUseCases.kt
│   │       └── presentation/
│   │           ├── LoginViewModel.kt
│   │           ├── AuthUiState.kt
│   │           └── LoginScreen.kt
│   │
│   ├── snackcart/               # SnackCart feature
│   │   └── src/commonMain/
│   │       ├── domain/
│   │       │   └── SnackCartUseCases.kt
│   │       └── presentation/
│   │           ├── SnackCartViewModel.kt
│   │           ├── SnackCartAdminViewModel.kt
│   │           └── SnackCartUiState.kt
│   │
│   └── roomie/                  # Roommate Matcher feature
│       └── src/commonMain/
│           ├── domain/
│           │   └── CompleteRoomieUseCases.kt
│           └── presentation/
│               ├── RoomieViewModels.kt
│               └── RoomieUiState.kt
│
├── shared/                      # Shared KMP module
│   ├── di/                      # Dependency Injection
│   │   └── src/
│   │       ├── commonMain/
│   │       │   └── CompleteDiModules.kt
│   │       ├── androidMain/
│   │       │   └── PlatformModules.android.kt
│   │       └── iosMain/
│   │           └── PlatformModules.ios.kt
│   └── ui/                      # Shared UI components
│
├── androidApp/                  # Android application
│   └── src/main/
│       ├── HostelDadaApp.kt
│       ├── MainActivity.kt
│       └── navigation/
│           └── NavHost.kt
│
├── iosApp/                      # iOS application (Xcode)
│
└── desktopApp/                  # Desktop application (JVM)
```

---

## 🧮 DSA Implementations

### 1. Trie (Prefix Tree) - Snack Search
```kotlin
// O(k) search where k is query length
class SnackSearchTrie {
    fun insert(snack: Snack)  // O(k)
    fun search(prefix: String): List<Snack>  // O(k + m)
    fun clear()  // O(1)
}
```

**Use Case**: Instant search suggestions as user types snack names.

### 2. Weighted Graph - Compatibility Matching
```kotlin
// Adjacency list with weighted edges
class CompatibilityGraph {
    fun addStudent(id: String, survey: RoommateSurvey)
    fun calculateEdge(id1: String, id2: String): CompatibilityScore?
    fun getTopMatches(studentId: String, limit: Int): List<CompatibilityScore>
}
```

**Scoring Algorithm**:
- Lifestyle: 20% weight
- Study Habits: 20% weight
- Cleanliness: 20% weight
- Social Preferences: 15% weight
- Sleep Schedule: 15% weight
- Personality: 10% weight

### 3. Priority Queue (Max-Heap) - Maintenance Requests
```kotlin
// Max-heap based on priority
class MaintenancePriorityQueue {
    fun insert(request: MaintenanceRequest)  // O(log n)
    fun extractMax(): MaintenanceRequest?  // O(log n)
    fun peek(): MaintenanceRequest?  // O(1)
}
```

---

## 🏗️ Clean Architecture

```
┌─────────────────────────────────────────────────┐
│                 PRESENTATION                     │
│   ViewModels, UiState, Screens, Intents         │
│                    ↑ ↓                          │
├─────────────────────────────────────────────────┤
│                   DOMAIN                         │
│   Use Cases, Repository Interfaces, Entities    │
│                    ↑ ↓                          │
├─────────────────────────────────────────────────┤
│                    DATA                          │
│   Repository Impl, DataSources, Mappers         │
└─────────────────────────────────────────────────┘
```

### Dependency Rule
- Inner layers don't know about outer layers
- All dependencies point inward
- Interfaces in Domain, implementations in Data

---

## 📱 Features

### 1. Authentication
- Email/Password login
- Google Sign-In
- Domain validation (@nsut.ac.in)
- Session management

### 2. SnackCart
- Browse snacks with categories
- **Trie-based** instant search
- Cart management
- Order placement & tracking
- Admin dashboard with statistics

### 3. Roomie Matcher
- Comprehensive survey (10+ questions)
- **Graph-based** compatibility algorithm
- Top matches visualization
- Admin room assignment
- Auto-assign based on compatibility

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 1.9.21 |
| UI | Compose Multiplatform 1.5.11 |
| DI | Koin 3.5.3 |
| Async | Coroutines + Flow |
| Network | Ktor 2.3.7 |
| Database | Firebase RTDB + SQLDelight 2.0.1 |
| Serialization | Kotlinx Serialization 1.6.2 |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog+ or IntelliJ IDEA
- JDK 17+
- Xcode 15+ (for iOS)

---

## 🏃‍♂️ How the Project Works

### 1. Code Structure
- **Shared Modules:** All core business logic, data models, repositories, and algorithms are in `core/`, `feature/`, and `shared/` modules. These are used by all platforms.
- **Platform Modules:**
    - `androidApp/` — Android-specific UI and entry point
    - `iosApp/` — iOS-specific UI and entry point
    - `desktopApp/` — Desktop (JVM) UI and entry point (if present)

### 2. Build & Run
- **Android:**
    1. Open the project in Android Studio
    2. Select the `androidApp` configuration
    3. Run on emulator or device
- **iOS:**
    1. Open `iosApp/iosApp.xcodeproj` in Xcode
    2. Select a simulator or device
    3. Build & run
- **Desktop:**
    1. Run `./gradlew :desktopApp:run` from terminal

### 3. Workflow
1. **Business logic** (algorithms, repositories, use cases) is written once in shared modules
2. **UI** is implemented using Compose Multiplatform (Android/Desktop) and SwiftUI (iOS)
3. **Dependency Injection** is handled by Koin, with platform-specific modules for each target
4. **Data** is fetched from Firebase or local storage (SQLDelight), abstracted behind repositories
5. **Features** (Auth, SnackCart, Roomie) are implemented as independent modules for modularity

---

### Build Commands
```bash
# Android
./gradlew :androidApp:assembleDebug

# Desktop
./gradlew :desktopApp:run

# iOS (from Xcode)
# Open iosApp/iosApp.xcodeproj
```

---

## 📊 Complexity Analysis

| Operation | Data Structure | Time | Space |
|-----------|---------------|------|-------|
| Search Snack | Trie | O(k) | O(n*k) |
| Find Matches | Graph | O(n) | O(n²) |
| Get Next Request | Max-Heap | O(log n) | O(n) |
| Add to Cart | HashMap | O(1) | O(n) |

---

## 🎓 Learning Outcomes

1. **Multiplatform Development** - Code sharing across platforms
2. **Reactive Programming** - StateFlow, SharedFlow
3. **Dependency Injection** - Loose coupling, testability
4. **Design Patterns** - Repository, Factory, Observer
5. **Algorithm Design** - Efficient data structures for real problems

---

## 📄 Documentation

- [Architecture Guide](ARCHITECTURE.md)
- [Functional Specification](FUNCTIONAL_SPECIFICATION.md)
- [DSA Implementations](core/domain/src/commonMain/kotlin/com/hosteldada/core/domain/algorithm/DSAImplementations.kt)

---

## 👨‍💻 Author

**Dushy** - College Placement Project

---

## 🌐 Repository

GitHub: [https://github.com/DushyantBhardwaj2/Hostel_Dada](https://github.com/DushyantBhardwaj2/Hostel_Dada)

---

## 📜 License

This project is for educational/portfolio purposes.
