package com.hosteldada.android.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.hosteldada.android.di.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Firebase Repository Implementations
 * 
 * These classes implement the domain repository interfaces using Firebase SDKs.
 * They demonstrate:
 * - Clean Architecture data layer implementation
 * - Coroutines integration with Firebase
 * - Error handling with Result pattern
 * - Offline-first data strategy
 */

// ============================================================================
// AUTH REPOSITORY IMPLEMENTATION
// ============================================================================

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val dispatchers: DispatcherProvider
) : AuthRepository {
    
    /**
     * Login with email and password
     * Uses Firebase Auth SDK
     */
    suspend fun login(email: String, password: String): Result<User> = 
        withContext(dispatchers.io) {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { firebaseUser ->
                    Result.success(firebaseUser.toUser())
                } ?: Result.failure(Exception("Login failed: No user returned"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Register new user with email and password
     */
    suspend fun register(
        email: String, 
        password: String, 
        displayName: String
    ): Result<User> = withContext(dispatchers.io) {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                // Update display name
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
                Result.success(firebaseUser.toUser())
            } ?: Result.failure(Exception("Registration failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Google Sign-In
     * Takes ID token from Google Sign-In flow
     */
    suspend fun signInWithGoogle(idToken: String): Result<User> = 
        withContext(dispatchers.io) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()
                result.user?.let { firebaseUser ->
                    Result.success(firebaseUser.toUser())
                } ?: Result.failure(Exception("Google sign-in failed"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Logout current user
     */
    suspend fun logout(): Result<Unit> = withContext(dispatchers.io) {
        try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get current authenticated user
     */
    fun getCurrentUser(): User? = firebaseAuth.currentUser?.toUser()
    
    /**
     * Observe auth state changes
     */
    fun observeAuthState(): Flow<User?> = flow {
        firebaseAuth.addAuthStateListener { auth ->
            // This would need a callback flow for proper implementation
        }
    }
    
    // Extension to convert Firebase User to domain User
    private fun com.google.firebase.auth.FirebaseUser.toUser() = User(
        id = uid,
        email = email ?: "",
        displayName = displayName ?: "",
        photoUrl = photoUrl?.toString(),
        isEmailVerified = isEmailVerified
    )
}

// User domain model
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false
)

// ============================================================================
// USER PROFILE REPOSITORY IMPLEMENTATION
// ============================================================================

class FirebaseUserRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val realtimeDb: FirebaseDatabase,
    private val dispatchers: DispatcherProvider
) : UserRepository {
    
    private val usersCollection = firestore.collection("users")
    
    /**
     * Get user profile by ID
     */
    suspend fun getUserProfile(userId: String): Result<UserProfile> = 
        withContext(dispatchers.io) {
            try {
                val snapshot = usersCollection.document(userId).get().await()
                snapshot.toObject(UserProfile::class.java)?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Profile not found"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Save/Update user profile
     */
    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> = 
        withContext(dispatchers.io) {
            try {
                usersCollection.document(profile.userId).set(profile).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val hostelName: String = "",
    val roomNumber: String = "",
    val branch: String = "",
    val year: Int = 1,
    val phoneNumber: String = "",
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// ============================================================================
// SNACKCART REPOSITORY IMPLEMENTATION
// ============================================================================

class FirebaseSnackCartRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val realtimeDb: FirebaseDatabase,
    private val dispatchers: DispatcherProvider
) : SnackCartRepository {
    
    private val snacksCollection = firestore.collection("snacks")
    private val ordersCollection = firestore.collection("orders")
    private val cartRef = realtimeDb.getReference("carts")
    
    /**
     * Get all available snacks
     * Uses Firestore for persistence
     */
    suspend fun getSnacks(): Result<List<Snack>> = withContext(dispatchers.io) {
        try {
            val snapshot = snacksCollection
                .whereEqualTo("available", true)
                .get()
                .await()
            
            val snacks = snapshot.documents.mapNotNull { 
                it.toObject(Snack::class.java)?.copy(id = it.id)
            }
            Result.success(snacks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Search snacks by name
     * This could use the Trie data structure for efficient prefix search
     */
    suspend fun searchSnacks(query: String): Result<List<Snack>> = 
        withContext(dispatchers.io) {
            try {
                // Firebase doesn't support full-text search natively
                // In production, use Algolia/Elasticsearch or client-side filtering
                val allSnacks = getSnacks().getOrThrow()
                val filtered = allSnacks.filter { snack ->
                    snack.name.contains(query, ignoreCase = true) ||
                    snack.category.contains(query, ignoreCase = true)
                }
                Result.success(filtered)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Get user's cart
     * Uses Realtime Database for real-time sync
     */
    suspend fun getCart(userId: String): Result<Cart> = withContext(dispatchers.io) {
        try {
            val snapshot = cartRef.child(userId).get().await()
            snapshot.getValue(Cart::class.java)?.let {
                Result.success(it)
            } ?: Result.success(Cart(userId = userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add item to cart
     */
    suspend fun addToCart(
        userId: String, 
        snackId: String, 
        quantity: Int
    ): Result<Unit> = withContext(dispatchers.io) {
        try {
            val cartItemRef = cartRef.child(userId).child("items").child(snackId)
            cartItemRef.setValue(CartItem(snackId = snackId, quantity = quantity)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Place order
     * Uses Firestore for order persistence
     */
    suspend fun placeOrder(order: Order): Result<String> = withContext(dispatchers.io) {
        try {
            val docRef = ordersCollection.add(order).await()
            // Clear cart after successful order
            cartRef.child(order.userId).removeValue().await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get user's order history
     */
    suspend fun getOrders(userId: String): Result<List<Order>> = 
        withContext(dispatchers.io) {
            try {
                val snapshot = ordersCollection
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val orders = snapshot.documents.mapNotNull {
                    it.toObject(Order::class.java)?.copy(id = it.id)
                }
                Result.success(orders)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

// SnackCart domain models
data class Snack(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val available: Boolean = true,
    val stock: Int = 0
)

data class CartItem(
    val snackId: String = "",
    val quantity: Int = 0
)

data class Cart(
    val userId: String = "",
    val items: Map<String, CartItem> = emptyMap()
)

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val deliveryLocation: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class OrderItem(
    val snackId: String = "",
    val snackName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)

enum class OrderStatus {
    PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
}

// ============================================================================
// ROOMIE MATCHER REPOSITORY IMPLEMENTATION
// ============================================================================

class FirebaseRoomieRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val realtimeDb: FirebaseDatabase,
    private val dispatchers: DispatcherProvider
) : RoomieRepository {
    
    private val profilesCollection = firestore.collection("roomie_profiles")
    private val matchRequestsRef = realtimeDb.getReference("match_requests")
    
    /**
     * Get roomie profile
     */
    suspend fun getRoomieProfile(userId: String): Result<RoomieProfile> = 
        withContext(dispatchers.io) {
            try {
                val snapshot = profilesCollection.document(userId).get().await()
                snapshot.toObject(RoomieProfile::class.java)?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Profile not found"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Save roomie profile with preferences
     */
    suspend fun saveRoomieProfile(profile: RoomieProfile): Result<Unit> = 
        withContext(dispatchers.io) {
            try {
                profilesCollection.document(profile.userId).set(profile).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Find compatible roommates
     * Uses weighted graph algorithm for compatibility scoring
     */
    suspend fun findMatches(userId: String): Result<List<RoomieMatch>> = 
        withContext(dispatchers.io) {
            try {
                val currentProfile = getRoomieProfile(userId).getOrThrow()
                
                // Get all profiles except current user
                val snapshot = profilesCollection
                    .whereNotEqualTo("userId", userId)
                    .get()
                    .await()
                
                val matches = snapshot.documents
                    .mapNotNull { it.toObject(RoomieProfile::class.java) }
                    .map { otherProfile ->
                        RoomieMatch(
                            profile = otherProfile,
                            compatibilityScore = calculateCompatibility(
                                currentProfile, 
                                otherProfile
                            )
                        )
                    }
                    .sortedByDescending { it.compatibilityScore }
                
                Result.success(matches)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    
    /**
     * Calculate compatibility score using weighted attributes
     * This implements the Graph-based compatibility algorithm
     */
    private fun calculateCompatibility(
        profile1: RoomieProfile, 
        profile2: RoomieProfile
    ): Double {
        var score = 0.0
        var maxScore = 0.0
        
        // Sleep schedule compatibility (weight: 25)
        val sleepWeight = 25.0
        maxScore += sleepWeight
        if (profile1.sleepSchedule == profile2.sleepSchedule) {
            score += sleepWeight
        } else if (areSleepSchedulesCompatible(profile1.sleepSchedule, profile2.sleepSchedule)) {
            score += sleepWeight * 0.5
        }
        
        // Cleanliness level (weight: 20)
        val cleanWeight = 20.0
        maxScore += cleanWeight
        val cleanDiff = kotlin.math.abs(profile1.cleanlinessLevel - profile2.cleanlinessLevel)
        score += cleanWeight * (1 - cleanDiff / 5.0)
        
        // Study habits (weight: 20)
        val studyWeight = 20.0
        maxScore += studyWeight
        if (profile1.studyHabits == profile2.studyHabits) {
            score += studyWeight
        }
        
        // Guest policy (weight: 15)
        val guestWeight = 15.0
        maxScore += guestWeight
        if (profile1.guestPolicy == profile2.guestPolicy) {
            score += guestWeight
        }
        
        // Noise tolerance (weight: 10)
        val noiseWeight = 10.0
        maxScore += noiseWeight
        val noiseDiff = kotlin.math.abs(profile1.noiseTolerance - profile2.noiseTolerance)
        score += noiseWeight * (1 - noiseDiff / 5.0)
        
        // Common interests (weight: 10)
        val interestWeight = 10.0
        maxScore += interestWeight
        val commonInterests = profile1.interests.intersect(profile2.interests.toSet()).size
        val totalInterests = profile1.interests.union(profile2.interests.toSet()).size
        if (totalInterests > 0) {
            score += interestWeight * (commonInterests.toDouble() / totalInterests)
        }
        
        return (score / maxScore) * 100
    }
    
    private fun areSleepSchedulesCompatible(s1: SleepSchedule, s2: SleepSchedule): Boolean {
        return when {
            s1 == SleepSchedule.FLEXIBLE || s2 == SleepSchedule.FLEXIBLE -> true
            else -> false
        }
    }
    
    /**
     * Send match request to another user
     */
    suspend fun sendMatchRequest(
        fromUserId: String, 
        toUserId: String, 
        message: String
    ): Result<Unit> = withContext(dispatchers.io) {
        try {
            val request = MatchRequest(
                fromUserId = fromUserId,
                toUserId = toUserId,
                message = message,
                status = MatchRequestStatus.PENDING,
                createdAt = System.currentTimeMillis()
            )
            matchRequestsRef.child(toUserId).child(fromUserId).setValue(request).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Roomie domain models
data class RoomieProfile(
    val userId: String = "",
    val name: String = "",
    val bio: String = "",
    val sleepSchedule: SleepSchedule = SleepSchedule.FLEXIBLE,
    val cleanlinessLevel: Int = 3, // 1-5
    val studyHabits: StudyHabits = StudyHabits.MODERATE,
    val guestPolicy: GuestPolicy = GuestPolicy.OCCASIONAL,
    val noiseTolerance: Int = 3, // 1-5
    val interests: List<String> = emptyList(),
    val preferredHostel: String = "",
    val budget: IntRange = 0..0,
    val moveInDate: Long = 0,
    val profileImageUrl: String = ""
)

data class RoomieMatch(
    val profile: RoomieProfile,
    val compatibilityScore: Double
)

data class MatchRequest(
    val fromUserId: String = "",
    val toUserId: String = "",
    val message: String = "",
    val status: MatchRequestStatus = MatchRequestStatus.PENDING,
    val createdAt: Long = 0
)

enum class SleepSchedule {
    EARLY_BIRD, NIGHT_OWL, FLEXIBLE
}

enum class StudyHabits {
    INTENSIVE, MODERATE, CASUAL
}

enum class GuestPolicy {
    NO_GUESTS, OCCASIONAL, FREQUENT, OPEN
}

enum class MatchRequestStatus {
    PENDING, ACCEPTED, REJECTED
}
