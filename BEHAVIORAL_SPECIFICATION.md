# Hostel Dada - Behavioral Specification

## Document Information
- **Version**: 2.0 (KMP Migration)
- **Architecture**: Clean Architecture + MVVM
- **Pattern**: Unidirectional Data Flow

---

## 1. State Management Patterns

### 1.1 MVVM Architecture Flow
```
┌─────────────────────────────────────────────────────────────┐
│                         UI LAYER                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                    Compose Screen                     │    │
│  │  ┌───────────┐    ┌───────────┐    ┌───────────┐    │    │
│  │  │  Observe  │    │  Render   │    │   Send    │    │    │
│  │  │   State   │───▶│    UI     │───▶│  Intent   │    │    │
│  │  └───────────┘    └───────────┘    └─────┬─────┘    │    │
│  └──────────────────────────────────────────│──────────┘    │
│                                             │                │
│  ┌──────────────────────────────────────────▼──────────┐    │
│  │                    ViewModel                          │    │
│  │  ┌───────────┐    ┌───────────┐    ┌───────────┐    │    │
│  │  │  Handle   │    │  Execute  │    │   Emit    │    │    │
│  │  │  Intent   │───▶│  UseCase  │───▶│   State   │    │    │
│  │  └───────────┘    └───────────┘    └───────────┘    │    │
│  └──────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                           │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                      UseCase                          │    │
│  │  ┌───────────┐    ┌───────────┐    ┌───────────┐    │    │
│  │  │  Validate │    │   Call    │    │  Return   │    │    │
│  │  │   Input   │───▶│   Repo    │───▶│  Result   │    │    │
│  │  └───────────┘    └───────────┘    └───────────┘    │    │
│  └──────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        DATA LAYER                            │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                    Repository                         │    │
│  │  ┌───────────┐    ┌───────────┐    ┌───────────┐    │    │
│  │  │   Check   │    │   Fetch   │    │   Cache   │    │    │
│  │  │   Cache   │───▶│  Remote   │───▶│  Result   │    │    │
│  │  └───────────┘    └───────────┘    └───────────┘    │    │
│  └──────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 State Types
```kotlin
sealed interface UiState<T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val throwable: Throwable?) : UiState<Nothing>
}
```

---

## 2. Authentication Behavior

### 2.1 Login State Machine
```
                    ┌─────────────┐
                    │    IDLE     │
                    └──────┬──────┘
                           │ User enters credentials
                           ▼
                    ┌─────────────┐
              ┌─────│  VALIDATING │─────┐
              │     └─────────────┘     │
              │ Invalid                  │ Valid
              ▼                          ▼
       ┌─────────────┐           ┌─────────────┐
       │   ERROR     │           │  LOADING    │
       │(validation) │           │(Firebase)   │
       └─────────────┘           └──────┬──────┘
                                        │
                          ┌─────────────┼─────────────┐
                          │             │             │
                          ▼             ▼             ▼
                   ┌───────────┐ ┌───────────┐ ┌───────────┐
                   │  SUCCESS  │ │   ERROR   │ │  PROFILE  │
                   │(dashboard)│ │(Firebase) │ │  REQUIRED │
                   └───────────┘ └───────────┘ └───────────┘
```

### 2.2 Login Intent Handling
```kotlin
sealed class LoginIntent {
    data class UpdateEmail(val email: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    object TogglePasswordVisibility : LoginIntent()
    object SignIn : LoginIntent()
    object SignInWithGoogle : LoginIntent()
    object ForgotPassword : LoginIntent()
    object ClearError : LoginIntent()
}

// ViewModel handling
fun handleIntent(intent: LoginIntent) {
    when (intent) {
        is UpdateEmail -> {
            _state.update { it.copy(
                email = intent.email,
                emailError = validateEmail(intent.email)
            )}
        }
        is SignIn -> {
            if (validateForm()) {
                performSignIn()
            }
        }
        // ... other intents
    }
}
```

### 2.3 Validation Rules
```kotlin
// Email Validation
fun validateEmail(email: String): String? = when {
    email.isBlank() -> "Email is required"
    !email.contains("@") -> "Invalid email format"
    !email.endsWith("@nsut.ac.in") -> "Must use NSUT email"
    else -> null
}

// Password Validation
fun validatePassword(password: String): String? = when {
    password.isBlank() -> "Password is required"
    password.length < 6 -> "Minimum 6 characters"
    else -> null
}
```

---

## 3. SnackCart Behavior

### 3.1 Cart State Machine
```
┌────────────────────────────────────────────────────────────────┐
│                        CART LIFECYCLE                           │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│    ┌─────────┐        ┌─────────┐        ┌─────────┐          │
│    │  EMPTY  │──add──▶│ HAS_ITEMS│──clear─▶│  EMPTY  │          │
│    └─────────┘        └────┬────┘        └─────────┘          │
│                            │                                    │
│                    checkout│                                    │
│                            ▼                                    │
│                     ┌───────────┐                               │
│                     │ CHECKOUT  │                               │
│                     └─────┬─────┘                               │
│                           │                                     │
│              ┌────────────┼────────────┐                       │
│              │            │            │                        │
│              ▼            ▼            ▼                        │
│       ┌───────────┐ ┌───────────┐ ┌───────────┐               │
│       │  SUCCESS  │ │  FAILED   │ │ CANCELLED │               │
│       │(order id) │ │  (error)  │ │           │               │
│       └─────┬─────┘ └───────────┘ └───────────┘               │
│             │                                                   │
│             ▼                                                   │
│       ┌───────────┐                                            │
│       │  EMPTY    │                                            │
│       │(cart cleared)│                                         │
│       └───────────┘                                            │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

### 3.2 Snack Search Behavior (Trie)
```kotlin
// Trie Search Behavior
class SnackSearchTrie {
    private val root = TrieNode()
    
    // BEHAVIOR: Insert snack (O(k) where k = name length)
    fun insert(snack: Snack) {
        var node = root
        for (char in snack.name.lowercase()) {
            node = node.children.getOrPut(char) { TrieNode() }
        }
        node.isEndOfWord = true
        node.snacks.add(snack)
    }
    
    // BEHAVIOR: Search with prefix (O(k + m) where m = results)
    fun searchPrefix(prefix: String): List<Snack> {
        var node = root
        for (char in prefix.lowercase()) {
            node = node.children[char] ?: return emptyList()
        }
        return collectAllSnacks(node)
    }
}

// Usage in ViewModel
class SnackCartViewModel {
    private val searchTrie = SnackSearchTrie()
    
    fun onSearchQueryChange(query: String) {
        if (query.length >= 2) {
            val results = searchTrie.searchPrefix(query)
            _state.update { it.copy(
                searchResults = results,
                isSearching = true
            )}
        } else {
            _state.update { it.copy(isSearching = false) }
        }
    }
}
```

### 3.3 Order Status Transitions
```kotlin
enum class OrderStatus {
    PENDING,    // Just created
    CONFIRMED,  // Admin accepted
    PREPARING,  // Kitchen is making it
    READY,      // Ready for pickup/delivery
    DELIVERED,  // Completed
    CANCELLED   // Cancelled by user/admin
}

// Valid transitions
val validTransitions = mapOf(
    PENDING to setOf(CONFIRMED, CANCELLED),
    CONFIRMED to setOf(PREPARING, CANCELLED),
    PREPARING to setOf(READY, CANCELLED),
    READY to setOf(DELIVERED),
    DELIVERED to emptySet(),
    CANCELLED to emptySet()
)

// Transition validation
fun canTransition(from: OrderStatus, to: OrderStatus): Boolean {
    return validTransitions[from]?.contains(to) == true
}
```

### 3.4 Cart Operations
```kotlin
sealed class CartIntent {
    data class AddItem(val snack: Snack, val quantity: Int = 1) : CartIntent()
    data class UpdateQuantity(val snackId: String, val quantity: Int) : CartIntent()
    data class RemoveItem(val snackId: String) : CartIntent()
    object ClearCart : CartIntent()
    data class Checkout(
        val location: String,
        val paymentMethod: PaymentMethod,
        val notes: String
    ) : CartIntent()
}

// Business rules
fun addToCart(snack: Snack, quantity: Int): Result<Unit> {
    // Rule 1: Check availability
    if (!snack.isAvailable) {
        return Result.Error("Snack is not available")
    }
    
    // Rule 2: Check stock
    if (snack.stockQuantity < quantity) {
        return Result.Error("Insufficient stock")
    }
    
    // Rule 3: Add/update cart
    val existing = cart[snack.id]
    if (existing != null) {
        val newQty = existing.quantity + quantity
        if (newQty > snack.stockQuantity) {
            return Result.Error("Cannot add more than available stock")
        }
        cart[snack.id] = existing.copy(quantity = newQty)
    } else {
        cart[snack.id] = CartItem(snack, quantity)
    }
    
    return Result.Success(Unit)
}
```

---

## 4. Roomie Matcher Behavior

### 4.1 Survey Flow State Machine
```
┌─────────────────────────────────────────────────────────────────┐
│                      SURVEY FLOW                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐  │
│  │  START   │───▶│ LIFESTYLE│───▶│  STUDY   │───▶│ CLEANLI- │  │
│  │          │    │ (Step 1) │    │ (Step 2) │    │  NESS    │  │
│  └──────────┘    └────┬─────┘    └────┬─────┘    │ (Step 3) │  │
│                       │               │          └────┬─────┘  │
│                       │◀──back────────│◀──back───────│         │
│                                                       │         │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐       │         │
│  │ COMPLETE │◀───│ PERSONAL │◀───│  SOCIAL  │◀──────┘         │
│  │          │    │ (Step 5) │    │ (Step 4) │                  │
│  └────┬─────┘    └──────────┘    └──────────┘                  │
│       │                                                         │
│       ▼                                                         │
│  ┌──────────┐                                                   │
│  │ MATCHING │ ←── Triggers compatibility calculation            │
│  └──────────┘                                                   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 Compatibility Graph Behavior
```kotlin
class CompatibilityGraph {
    private val adjacencyList = mutableMapOf<String, MutableList<Edge>>()
    
    data class Edge(
        val targetStudentId: String,
        val score: CompatibilityScore
    )
    
    // BEHAVIOR: Add student to graph
    fun addStudent(studentId: String) {
        adjacencyList.putIfAbsent(studentId, mutableListOf())
    }
    
    // BEHAVIOR: Calculate and add edge (O(1))
    fun calculateCompatibility(
        survey1: RoommateSurvey,
        survey2: RoommateSurvey
    ): CompatibilityScore {
        val lifestyle = calculateLifestyleScore(
            survey1.lifestyle, survey2.lifestyle
        )
        val study = calculateStudyScore(
            survey1.studyHabits, survey2.studyHabits
        )
        val cleanliness = calculateCleanlinessScore(
            survey1.cleanliness, survey2.cleanliness
        )
        val social = calculateSocialScore(
            survey1.socialPreferences, survey2.socialPreferences
        )
        val sleep = calculateSleepScore(
            survey1.sleepSchedule, survey2.sleepSchedule
        )
        val personality = calculatePersonalityScore(
            survey1.personalityTraits, survey2.personalityTraits
        )
        
        // Weighted average
        val overall = (
            lifestyle * 0.20 +
            study * 0.20 +
            cleanliness * 0.20 +
            social * 0.15 +
            sleep * 0.15 +
            personality * 0.10
        ).toInt()
        
        return CompatibilityScore(
            overallScore = overall,
            lifestyleScore = lifestyle,
            studyScore = study,
            cleanlinessScore = cleanliness,
            socialScore = social,
            sleepScore = sleep,
            personalityScore = personality,
            matchReasons = generateReasons(lifestyle, study, cleanliness, social, sleep),
            warnings = generateWarnings(survey1, survey2)
        )
    }
    
    // BEHAVIOR: Get top N matches (O(n log n))
    fun getTopMatches(studentId: String, limit: Int = 10): List<Edge> {
        return adjacencyList[studentId]
            ?.sortedByDescending { it.score.overallScore }
            ?.take(limit)
            ?: emptyList()
    }
}
```

### 4.3 Score Calculation Details
```kotlin
// Lifestyle Score Calculation
fun calculateLifestyleScore(l1: LifestylePreferences, l2: LifestylePreferences): Int {
    var score = 0
    val maxScore = 100
    
    // Sleep time comparison (25 points max)
    val sleepDiff = abs(parseTimeToMinutes(l1.sleepTime) - parseTimeToMinutes(l2.sleepTime))
    score += max(0, 25 - sleepDiff / 30)  // Lose 1 point per 30 minutes difference
    
    // Wake time comparison (25 points max)
    val wakeDiff = abs(parseTimeToMinutes(l1.wakeTime) - parseTimeToMinutes(l2.wakeTime))
    score += max(0, 25 - wakeDiff / 30)
    
    // Food preference (20 points max)
    score += when {
        l1.foodPreference == l2.foodPreference -> 20
        l1.foodPreference == FoodPreference.EGGETARIAN || 
        l2.foodPreference == FoodPreference.EGGETARIAN -> 10
        else -> 0
    }
    
    // Smoking habit (15 points max)
    score += when {
        l1.smokingHabit == l2.smokingHabit -> 15
        l1.smokingHabit == SmokingHabit.OCCASIONALLY || 
        l2.smokingHabit == SmokingHabit.OCCASIONALLY -> 7
        else -> 0
    }
    
    // Drinking habit (15 points max)
    score += when {
        l1.drinkingHabit == l2.drinkingHabit -> 15
        else -> 5
    }
    
    return min(score, maxScore)
}

// Generate match reasons
fun generateReasons(
    lifestyle: Int, study: Int, cleanliness: Int, 
    social: Int, sleep: Int
): List<String> {
    val reasons = mutableListOf<String>()
    
    if (lifestyle >= 80) reasons.add("🏠 Similar lifestyle habits")
    if (study >= 80) reasons.add("📚 Compatible study preferences")
    if (cleanliness >= 80) reasons.add("🧹 Similar cleanliness standards")
    if (social >= 80) reasons.add("👥 Matching social preferences")
    if (sleep >= 80) reasons.add("😴 Compatible sleep schedules")
    
    return reasons
}

// Generate warnings
fun generateWarnings(s1: RoommateSurvey, s2: RoommateSurvey): List<String> {
    val warnings = mutableListOf<String>()
    
    // Smoking conflict
    if (s1.lifestyle.smokingHabit != s2.lifestyle.smokingHabit) {
        if (s1.lifestyle.smokingHabit == SmokingHabit.NEVER || 
            s2.lifestyle.smokingHabit == SmokingHabit.NEVER) {
            warnings.add("⚠️ Different smoking habits")
        }
    }
    
    // Food preference conflict
    if (s1.lifestyle.foodPreference == FoodPreference.VEGETARIAN &&
        s2.lifestyle.foodPreference == FoodPreference.NON_VEGETARIAN) {
        warnings.add("⚠️ Different food preferences may affect comfort")
    }
    
    // Sleep schedule conflict (> 3 hours difference)
    val sleepDiff = abs(parseTimeToMinutes(s1.sleepSchedule.bedTime) - 
                       parseTimeToMinutes(s2.sleepSchedule.bedTime))
    if (sleepDiff > 180) {
        warnings.add("⚠️ Significantly different sleep schedules")
    }
    
    return warnings
}
```

### 4.4 Auto-Assignment Algorithm
```kotlin
// Greedy assignment algorithm
class AutoAssignmentUseCase(
    private val surveyRepo: SurveyRepository,
    private val roomRepo: RoomRepository,
    private val assignmentRepo: AssignmentRepository,
    private val compatibilityRepo: CompatibilityRepository
) {
    suspend fun execute(semester: String): Result<List<RoomAssignment>> {
        // Step 1: Get all surveys for semester
        val surveys = surveyRepo.getSurveysBySemester(semester)
        
        // Step 2: Get available rooms
        val rooms = roomRepo.getAvailableRooms()
            .filter { it.capacity >= 2 }
            .toMutableList()
        
        // Step 3: Generate all compatibility pairs
        val pairs = mutableListOf<Triple<String, String, CompatibilityScore>>()
        for (i in surveys.indices) {
            for (j in i + 1 until surveys.size) {
                val score = compatibilityRepo.calculateScore(surveys[i], surveys[j])
                pairs.add(Triple(surveys[i].studentId, surveys[j].studentId, score))
            }
        }
        
        // Step 4: Sort by compatibility (descending)
        pairs.sortByDescending { it.third.overallScore }
        
        // Step 5: Greedy assignment
        val assigned = mutableSetOf<String>()
        val assignments = mutableListOf<RoomAssignment>()
        
        for ((student1, student2, score) in pairs) {
            if (student1 in assigned || student2 in assigned) continue
            if (rooms.isEmpty()) break
            
            // Find suitable room
            val room = rooms.firstOrNull { it.capacity >= 2 } ?: continue
            
            // Create assignment
            val assignment = RoomAssignment(
                id = generateId(),
                roomId = room.id,
                studentIds = listOf(student1, student2),
                compatibilityScore = score.overallScore,
                semester = semester,
                status = AssignmentStatus.PENDING_APPROVAL
            )
            
            assignments.add(assignment)
            assigned.add(student1)
            assigned.add(student2)
            
            // Update room availability
            if (room.capacity == 2) {
                rooms.remove(room)
            } else {
                // For rooms with capacity > 2, reduce available spots
                rooms[rooms.indexOf(room)] = room.copy(
                    currentOccupancy = room.currentOccupancy + 2
                )
            }
        }
        
        // Step 6: Save assignments
        assignments.forEach { assignmentRepo.createAssignment(it) }
        
        return Result.Success(assignments)
    }
}
```

---

## 5. Error Handling Behavior

### 5.1 Error Classification
```kotlin
sealed class AppError(
    val message: String,
    val isRetryable: Boolean
) {
    // Network errors - retryable
    class NetworkError(message: String) : 
        AppError(message, isRetryable = true)
    
    // Authentication errors - not retryable (user action needed)
    class AuthError(message: String) : 
        AppError(message, isRetryable = false)
    
    // Validation errors - not retryable
    class ValidationError(message: String) : 
        AppError(message, isRetryable = false)
    
    // Server errors - retryable with backoff
    class ServerError(message: String) : 
        AppError(message, isRetryable = true)
    
    // Not found - not retryable
    class NotFoundError(message: String) : 
        AppError(message, isRetryable = false)
}
```

### 5.2 Retry Strategy
```kotlin
class RetryPolicy(
    val maxRetries: Int = 3,
    val initialDelay: Long = 1000,
    val maxDelay: Long = 10000,
    val factor: Double = 2.0
) {
    suspend fun <T> execute(block: suspend () -> Result<T>): Result<T> {
        var currentDelay = initialDelay
        var lastError: Throwable? = null
        
        repeat(maxRetries) { attempt ->
            when (val result = block()) {
                is Result.Success -> return result
                is Result.Error -> {
                    lastError = result.exception
                    
                    // Check if retryable
                    if (!isRetryable(result.exception)) {
                        return result
                    }
                    
                    // Exponential backoff
                    delay(currentDelay)
                    currentDelay = min(
                        (currentDelay * factor).toLong(),
                        maxDelay
                    )
                }
            }
        }
        
        return Result.Error(
            message = "Max retries exceeded",
            exception = lastError
        )
    }
}
```

### 5.3 Error Recovery Flows
```
┌─────────────────────────────────────────────────────────────────┐
│                     ERROR RECOVERY FLOW                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌───────────┐         ┌───────────┐         ┌───────────┐     │
│  │  ERROR    │───yes──▶│  RETRY    │───yes──▶│  SUCCESS  │     │
│  │ OCCURRED  │ retry?  │  ACTION   │ success?│           │     │
│  └─────┬─────┘         └─────┬─────┘         └───────────┘     │
│        │                     │                                   │
│        │ no                  │ no (max retries)                 │
│        ▼                     ▼                                   │
│  ┌───────────┐         ┌───────────┐                           │
│  │  SHOW     │         │  SHOW     │                           │
│  │  ERROR    │         │  FALLBACK │                           │
│  │  MESSAGE  │         │  OR ERROR │                           │
│  └───────────┘         └───────────┘                           │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 6. Caching Behavior

### 6.1 LRU Cache Implementation
```kotlin
class LRUCache<K, V>(private val capacity: Int) {
    private val cache = LinkedHashMap<K, V>(capacity, 0.75f, true)
    
    // BEHAVIOR: Get with LRU update
    operator fun get(key: K): V? = cache[key]
    
    // BEHAVIOR: Put with eviction
    operator fun set(key: K, value: V) {
        if (cache.size >= capacity && !cache.containsKey(key)) {
            // Remove oldest entry
            cache.remove(cache.keys.first())
        }
        cache[key] = value
    }
    
    // BEHAVIOR: Invalidate
    fun invalidate(key: K) = cache.remove(key)
    fun invalidateAll() = cache.clear()
}
```

### 6.2 Repository Caching Strategy
```kotlin
class CachedSnackRepository(
    private val remoteDataSource: SnackRemoteDataSource,
    private val localDataSource: SnackLocalDataSource,
    private val cache: LRUCache<String, Snack>
) : SnackRepository {
    
    override suspend fun getSnack(id: String): Result<Snack> {
        // Step 1: Check in-memory cache
        cache[id]?.let { return Result.Success(it) }
        
        // Step 2: Check local database
        localDataSource.getSnack(id)?.let { snack ->
            cache[id] = snack
            return Result.Success(snack)
        }
        
        // Step 3: Fetch from remote
        return when (val result = remoteDataSource.getSnack(id)) {
            is Result.Success -> {
                // Cache in both layers
                localDataSource.saveSnack(result.data)
                cache[id] = result.data
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun refreshSnacks(): Result<List<Snack>> {
        // Force refresh from remote
        return when (val result = remoteDataSource.getAllSnacks()) {
            is Result.Success -> {
                // Clear and repopulate
                cache.invalidateAll()
                localDataSource.deleteAll()
                
                result.data.forEach { snack ->
                    cache[snack.id] = snack
                    localDataSource.saveSnack(snack)
                }
                result
            }
            is Result.Error -> result
        }
    }
}
```

---

## 7. Navigation Behavior

### 7.1 Navigation State
```kotlin
sealed class NavigationEvent {
    data class NavigateTo(val route: String) : NavigationEvent()
    object NavigateBack : NavigationEvent()
    data class NavigateToWithArgs(
        val route: String,
        val args: Map<String, Any>
    ) : NavigationEvent()
    object ClearBackStack : NavigationEvent()
}

// Route definitions
object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    const val PROFILE_SETUP = "profile_setup"
    const val DASHBOARD = "dashboard"
    const val SNACK_CART = "snackcart"
    const val SNACK_DETAIL = "snackcart/{snackId}"
    const val CART = "snackcart/cart"
    const val CHECKOUT = "snackcart/checkout"
    const val ORDER_HISTORY = "snackcart/orders"
    const val ROOMIE_SURVEY = "roomie/survey"
    const val ROOMIE_MATCHES = "roomie/matches"
    const val MATCH_DETAIL = "roomie/match/{studentId}"
    const val ADMIN_SNACKS = "admin/snacks"
    const val ADMIN_ORDERS = "admin/orders"
    const val ADMIN_ROOMIE = "admin/roomie"
}
```

### 7.2 Deep Link Handling
```kotlin
sealed class DeepLink {
    data class Order(val orderId: String) : DeepLink()
    data class Match(val studentId: String) : DeepLink()
    data class Snack(val snackId: String) : DeepLink()
}

fun parseDeepLink(uri: String): DeepLink? {
    return when {
        uri.startsWith("hosteldada://order/") -> {
            val orderId = uri.removePrefix("hosteldada://order/")
            DeepLink.Order(orderId)
        }
        uri.startsWith("hosteldada://match/") -> {
            val studentId = uri.removePrefix("hosteldada://match/")
            DeepLink.Match(studentId)
        }
        uri.startsWith("hosteldada://snack/") -> {
            val snackId = uri.removePrefix("hosteldada://snack/")
            DeepLink.Snack(snackId)
        }
        else -> null
    }
}
```

---

## 8. Real-time Updates Behavior

### 8.1 Firebase Realtime Sync
```kotlin
// Order updates listener
class OrderRealtimeListener(
    private val database: FirebaseDatabase
) {
    fun observeOrderStatus(
        orderId: String,
        onUpdate: (OrderStatus) -> Unit
    ): ListenerRegistration {
        return database.reference
            .child("orders")
            .child(orderId)
            .child("status")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(String::class.java)
                    status?.let { 
                        onUpdate(OrderStatus.valueOf(it))
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}

// Usage in ViewModel
class OrderDetailViewModel {
    private var listenerRegistration: ListenerRegistration? = null
    
    fun startObserving(orderId: String) {
        listenerRegistration = orderRealtimeListener.observeOrderStatus(orderId) { status ->
            _state.update { it.copy(currentStatus = status) }
        }
    }
    
    override fun onCleared() {
        listenerRegistration?.remove()
    }
}
```

### 8.2 Conflict Resolution
```kotlin
// Last-write-wins strategy for cart updates
suspend fun updateCartItem(
    userId: String,
    snackId: String,
    quantity: Int
): Result<Unit> {
    val ref = database.reference
        .child("carts")
        .child(userId)
        .child("items")
        .child(snackId)
    
    return try {
        // Optimistic update with timestamp
        ref.setValue(mapOf(
            "quantity" to quantity,
            "updatedAt" to ServerValue.TIMESTAMP
        )).await()
        
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Update failed", e)
    }
}
```

---

## 9. Testing Behavior

### 9.1 Test Categories
```kotlin
// Unit Test - ViewModel
class LoginViewModelTest {
    @Test
    fun `when email is invalid, show error`() {
        val viewModel = LoginViewModel(mockSignInUseCase)
        
        viewModel.handleIntent(LoginIntent.UpdateEmail("invalid"))
        
        assertEquals(
            "Must use NSUT email",
            viewModel.state.value.emailError
        )
    }
}

// Integration Test - UseCase with Repository
class SearchSnacksUseCaseTest {
    @Test
    fun `search returns matching snacks`() = runTest {
        val repository = FakeSnackRepository()
        repository.saveSnacks(listOf(
            Snack(id = "1", name = "Maggi"),
            Snack(id = "2", name = "Mango Juice")
        ))
        
        val useCase = SearchSnacksUseCase(repository)
        val result = useCase("mag")
        
        assertTrue(result is Result.Success)
        assertEquals(1, result.data.size)
        assertEquals("Maggi", result.data[0].name)
    }
}

// UI Test - Compose
class SnackCartScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `add to cart button is disabled when out of stock`() {
        val snack = Snack(
            id = "1",
            name = "Test Snack",
            stockQuantity = 0
        )
        
        composeTestRule.setContent {
            SnackCard(snack = snack, onAddToCart = {})
        }
        
        composeTestRule
            .onNodeWithText("Add to Cart")
            .assertIsNotEnabled()
    }
}
```

---

## 10. Performance Behavior

### 10.1 Lazy Loading
```kotlin
// Pagination for orders
class OrderPaginator(
    private val pageSize: Int = 20
) {
    private var lastDocumentId: String? = null
    private var hasMorePages = true
    
    suspend fun loadNextPage(): Result<List<SnackOrder>> {
        if (!hasMorePages) return Result.Success(emptyList())
        
        val orders = orderRepository.getOrders(
            limit = pageSize,
            startAfter = lastDocumentId
        )
        
        if (orders.size < pageSize) {
            hasMorePages = false
        }
        
        lastDocumentId = orders.lastOrNull()?.id
        return Result.Success(orders)
    }
}
```

### 10.2 Debounce for Search
```kotlin
// Search with debounce
class SearchDebouncer(
    private val delayMs: Long = 300
) {
    private var searchJob: Job? = null
    
    fun search(
        query: String,
        scope: CoroutineScope,
        onResult: (List<Snack>) -> Unit
    ) {
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(delayMs)
            val results = searchSnacksUseCase(query)
            if (results is Result.Success) {
                onResult(results.data)
            }
        }
    }
}
```

---

## Document History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024-01 | Initial React implementation |
| 2.0 | 2024-12 | KMP migration with Clean Architecture patterns |
