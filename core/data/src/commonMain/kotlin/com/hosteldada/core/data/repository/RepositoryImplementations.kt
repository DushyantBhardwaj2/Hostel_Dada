package com.hosteldada.core.data.repository

import com.hosteldada.core.common.Result
import com.hosteldada.core.data.local.*
import com.hosteldada.core.data.remote.*
import com.hosteldada.core.domain.model.*
import com.hosteldada.core.domain.repository.*

/**
 * Repository implementation for Authentication
 * Handles Firebase Auth operations
 */
class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: UserLocalDataSource
) : AuthRepository {
    
    override suspend fun signInWithEmailPassword(email: String, password: String): Result<User> {
        return when (val result = remoteDataSource.signInWithEmailPassword(email, password)) {
            is Result.Success -> {
                localDataSource.saveUser(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun signUpWithEmailPassword(
        email: String, 
        password: String, 
        displayName: String
    ): Result<User> {
        return when (val result = remoteDataSource.signUpWithEmailPassword(email, password, displayName)) {
            is Result.Success -> {
                localDataSource.saveUser(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return when (val result = remoteDataSource.signInWithGoogle(idToken)) {
            is Result.Success -> {
                localDataSource.saveUser(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun signOut(): Result<Unit> {
        localDataSource.deleteUser()
        return remoteDataSource.signOut()
    }
    
    override suspend fun resetPassword(email: String): Result<Unit> {
        return remoteDataSource.resetPassword(email)
    }
    
    override suspend fun getCurrentUser(): User? {
        // First check local cache
        localDataSource.getCurrentUser()?.let { return it }
        // Then check Firebase
        return remoteDataSource.getCurrentUser()?.also { 
            localDataSource.saveUser(it) 
        }
    }
    
    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> {
        return remoteDataSource.updateProfile(displayName, photoUrl)
    }
    
    override suspend fun deleteAccount(): Result<Unit> {
        return remoteDataSource.deleteAccount().also {
            if (it is Result.Success) {
                localDataSource.deleteUser()
            }
        }
    }
}

/**
 * Repository implementation for User Profiles
 * Implements offline-first with sync
 */
class ProfileRepositoryImpl(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val localDataSource: UserLocalDataSource
) : ProfileRepository {
    
    override suspend fun getProfile(userId: String): UserProfile? {
        // Try local first
        localDataSource.getProfile(userId)?.let { return it }
        
        // Fetch from remote
        return when (val result = remoteDataSource.getProfile(userId)) {
            is Result.Success -> result.data?.also { profile ->
                localDataSource.saveProfile(profile)
            }
            is Result.Error -> null
        }
    }
    
    override suspend fun createProfile(profile: UserProfile): Result<UserProfile> {
        return when (val result = remoteDataSource.createProfile(profile)) {
            is Result.Success -> {
                localDataSource.saveProfile(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun updateProfile(profile: UserProfile): Result<UserProfile> {
        return when (val result = remoteDataSource.updateProfile(profile)) {
            is Result.Success -> {
                localDataSource.saveProfile(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun deleteProfile(userId: String): Result<Unit> {
        localDataSource.deleteProfile(userId)
        return remoteDataSource.deleteProfile(userId)
    }
    
    override suspend fun isProfileComplete(userId: String): Boolean {
        val profile = getProfile(userId)
        return profile?.isComplete ?: false
    }
}

/**
 * Repository implementation for Snacks
 * Implements caching with Trie search index
 */
class SnackRepositoryImpl(
    private val remoteDataSource: SnackRemoteDataSource,
    private val localDataSource: SnackLocalDataSource
) : SnackRepository {
    
    override suspend fun getAllSnacks(cachePolicy: CachePolicy): Result<List<Snack>> {
        // Check cache validity
        if (!cachePolicy.isExpired(localDataSource.getLastSyncTimestamp())) {
            val cached = localDataSource.getAllSnacks()
            if (cached.isNotEmpty()) {
                return Result.Success(cached)
            }
        }
        
        // Fetch from remote
        return when (val result = remoteDataSource.getAllSnacks()) {
            is Result.Success -> {
                localDataSource.deleteAll()
                localDataSource.saveSnacks(result.data)
                localDataSource.setLastSyncTimestamp(System.currentTimeMillis())
                result
            }
            is Result.Error -> {
                // Return cached data as fallback
                val cached = localDataSource.getAllSnacks()
                if (cached.isNotEmpty()) {
                    Result.Success(cached)
                } else {
                    result
                }
            }
        }
    }
    
    override suspend fun getSnackById(id: String): Snack? {
        // Check local first
        localDataSource.getSnackById(id)?.let { return it }
        
        // Fetch from remote
        return when (val result = remoteDataSource.getSnackById(id)) {
            is Result.Success -> result.data?.also { snack ->
                localDataSource.saveSnack(snack)
            }
            is Result.Error -> null
        }
    }
    
    override suspend fun getSnacksByCategory(category: SnackCategory): Result<List<Snack>> {
        return remoteDataSource.getSnacksByCategory(category)
    }
    
    override suspend fun getAvailableSnacks(): Result<List<Snack>> {
        return remoteDataSource.getAvailableSnacks()
    }
    
    override suspend fun searchSnacks(query: String): Result<List<Snack>> {
        // Local search first for instant results
        val localResults = localDataSource.searchSnacks(query)
        if (localResults.isNotEmpty()) {
            return Result.Success(localResults)
        }
        return remoteDataSource.searchSnacks(query)
    }
    
    override suspend fun createSnack(snack: Snack): Result<Snack> {
        return when (val result = remoteDataSource.createSnack(snack)) {
            is Result.Success -> {
                localDataSource.saveSnack(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun updateSnack(snack: Snack): Result<Snack> {
        return when (val result = remoteDataSource.updateSnack(snack)) {
            is Result.Success -> {
                localDataSource.saveSnack(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun updateStockQuantity(snackId: String, quantity: Int): Result<Unit> {
        return remoteDataSource.updateStockQuantity(snackId, quantity)
    }
    
    override suspend fun toggleAvailability(snackId: String, isAvailable: Boolean): Result<Unit> {
        return remoteDataSource.toggleAvailability(snackId, isAvailable)
    }
    
    override suspend fun deleteSnack(snackId: String): Result<Unit> {
        localDataSource.deleteSnack(snackId)
        return remoteDataSource.deleteSnack(snackId)
    }
}

/**
 * Repository implementation for Cart
 */
class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
    private val localDataSource: CartLocalDataSource
) : CartRepository {
    
    override suspend fun getCart(userId: String): Cart? {
        // Check local first
        localDataSource.getCart(userId)?.let { return it }
        
        // Fetch from remote
        return when (val result = remoteDataSource.getCart(userId)) {
            is Result.Success -> result.data?.also { cart ->
                localDataSource.saveCart(userId, cart)
            }
            is Result.Error -> null
        }
    }
    
    override suspend fun addToCart(userId: String, item: CartItem): Result<Cart> {
        return when (val result = remoteDataSource.addToCart(userId, item)) {
            is Result.Success -> {
                localDataSource.saveCart(userId, result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun updateCartItem(userId: String, snackId: String, quantity: Int): Result<Cart> {
        return when (val result = remoteDataSource.updateCartItem(userId, snackId, quantity)) {
            is Result.Success -> {
                localDataSource.saveCart(userId, result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun removeFromCart(userId: String, snackId: String): Result<Cart> {
        return when (val result = remoteDataSource.removeFromCart(userId, snackId)) {
            is Result.Success -> {
                localDataSource.saveCart(userId, result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun clearCart(userId: String): Result<Unit> {
        localDataSource.clearCart(userId)
        return remoteDataSource.clearCart(userId)
    }
    
    override suspend fun getCartTotal(userId: String): Double {
        return getCart(userId)?.totalAmount ?: 0.0
    }
}

/**
 * Repository implementation for Orders
 */
class OrderRepositoryImpl(
    private val remoteDataSource: OrderRemoteDataSource,
    private val localDataSource: OrderLocalDataSource
) : OrderRepository {
    
    override suspend fun createOrder(order: SnackOrder): Result<SnackOrder> {
        return when (val result = remoteDataSource.createOrder(order)) {
            is Result.Success -> {
                localDataSource.saveOrder(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun getOrderById(orderId: String): SnackOrder? {
        // Check local first
        localDataSource.getOrderById(orderId)?.let { return it }
        
        // Fetch from remote
        return when (val result = remoteDataSource.getOrderById(orderId)) {
            is Result.Success -> result.data?.also { order ->
                localDataSource.saveOrder(order)
            }
            is Result.Error -> null
        }
    }
    
    override suspend fun getOrdersByUser(userId: String): Result<List<SnackOrder>> {
        return when (val result = remoteDataSource.getOrdersByUser(userId)) {
            is Result.Success -> {
                localDataSource.saveOrders(result.data)
                result
            }
            is Result.Error -> {
                // Return cached data as fallback
                val cached = localDataSource.getOrdersByUser(userId)
                if (cached.isNotEmpty()) {
                    Result.Success(cached)
                } else {
                    result
                }
            }
        }
    }
    
    override suspend fun getAllOrders(): Result<List<SnackOrder>> {
        return remoteDataSource.getAllOrders()
    }
    
    override suspend fun getOrdersByStatus(status: OrderStatus): Result<List<SnackOrder>> {
        return remoteDataSource.getOrdersByStatus(status)
    }
    
    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<SnackOrder> {
        return when (val result = remoteDataSource.updateOrderStatus(orderId, status)) {
            is Result.Success -> {
                localDataSource.saveOrder(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun cancelOrder(orderId: String): Result<SnackOrder> {
        return when (val result = remoteDataSource.cancelOrder(orderId)) {
            is Result.Success -> {
                localDataSource.saveOrder(result.data)
                result
            }
            is Result.Error -> result
        }
    }
}

/**
 * Repository implementation for Surveys
 */
class SurveyRepositoryImpl(
    private val remoteDataSource: SurveyRemoteDataSource,
    private val localDataSource: SurveyLocalDataSource
) : SurveyRepository {
    
    override suspend fun createSurvey(survey: RoommateSurvey): Result<String> {
        return when (val result = remoteDataSource.createSurvey(survey)) {
            is Result.Success -> {
                localDataSource.saveSurvey(survey.copy(id = result.data))
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun updateSurvey(survey: RoommateSurvey): Result<Unit> {
        return when (val result = remoteDataSource.updateSurvey(survey)) {
            is Result.Success -> {
                localDataSource.saveSurvey(survey)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun getSurveyById(surveyId: String): RoommateSurvey? {
        // Check local first
        localDataSource.getSurveyById(surveyId)?.let { return it }
        
        // Fetch from remote
        return when (val result = remoteDataSource.getSurveyById(surveyId)) {
            is Result.Success -> result.data?.also { survey ->
                localDataSource.saveSurvey(survey)
            }
            is Result.Error -> null
        }
    }
    
    override suspend fun getSurveyByStudentAndSemester(studentId: String, semester: String): RoommateSurvey? {
        // Check local first
        localDataSource.getSurveyByStudentAndSemester(studentId, semester)?.let { return it }
        
        // Fetch from remote
        return when (val result = remoteDataSource.getSurveyByStudentAndSemester(studentId, semester)) {
            is Result.Success -> result.data?.also { survey ->
                localDataSource.saveSurvey(survey)
            }
            is Result.Error -> null
        }
    }
    
    override suspend fun getSurveysBySemester(semester: String): List<RoommateSurvey> {
        // Fetch from remote
        return when (val result = remoteDataSource.getSurveysBySemester(semester)) {
            is Result.Success -> {
                result.data.forEach { localDataSource.saveSurvey(it) }
                result.data
            }
            is Result.Error -> {
                // Return cached data as fallback
                localDataSource.getSurveysBySemester(semester)
            }
        }
    }
    
    override suspend fun getAllSurveys(): List<RoommateSurvey> {
        return when (val result = remoteDataSource.getAllSurveys()) {
            is Result.Success -> result.data
            is Result.Error -> localDataSource.getAllSurveys()
        }
    }
    
    override suspend fun deleteSurvey(surveyId: String): Result<Unit> {
        localDataSource.deleteSurvey(surveyId)
        return remoteDataSource.deleteSurvey(surveyId)
    }
}

/**
 * Repository implementation for Rooms
 */
class RoomRepositoryImpl(
    private val remoteDataSource: RoomRemoteDataSource,
    private val localDataSource: RoomLocalDataSource
) : RoomRepository {
    
    override suspend fun createRoom(room: Room): Result<Room> {
        return when (val result = remoteDataSource.createRoom(room)) {
            is Result.Success -> {
                localDataSource.saveRoom(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun updateRoom(room: Room): Result<Room> {
        return when (val result = remoteDataSource.updateRoom(room)) {
            is Result.Success -> {
                localDataSource.saveRoom(result.data)
                result
            }
            is Result.Error -> result
        }
    }
    
    override suspend fun getRoomById(roomId: String): Room? {
        localDataSource.getRoomById(roomId)?.let { return it }
        
        return when (val result = remoteDataSource.getRoomById(roomId)) {
            is Result.Success -> result.data?.also { room ->
                localDataSource.saveRoom(room)
            }
            is Result.Error -> null
        }
    }
    
    override suspend fun getAllRooms(): List<Room> {
        return when (val result = remoteDataSource.getAllRooms()) {
            is Result.Success -> {
                localDataSource.saveRooms(result.data)
                result.data
            }
            is Result.Error -> localDataSource.getAllRooms()
        }
    }
    
    override suspend fun getAvailableRooms(): List<Room> {
        return when (val result = remoteDataSource.getAvailableRooms()) {
            is Result.Success -> result.data
            is Result.Error -> localDataSource.getAvailableRooms()
        }
    }
    
    override suspend fun getRoomsByBlock(block: String): List<Room> {
        return when (val result = remoteDataSource.getRoomsByBlock(block)) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }
    
    override suspend fun updateRoomOccupancy(roomId: String, change: Int): Result<Unit> {
        return remoteDataSource.updateRoomOccupancy(roomId, change)
    }
    
    override suspend fun deleteRoom(roomId: String): Result<Unit> {
        localDataSource.deleteRoom(roomId)
        return remoteDataSource.deleteRoom(roomId)
    }
}

/**
 * Repository implementation for Room Assignments
 */
class AssignmentRepositoryImpl(
    private val remoteDataSource: AssignmentRemoteDataSource
) : AssignmentRepository {
    
    override suspend fun createAssignment(assignment: RoomAssignment): Result<RoomAssignment> {
        return remoteDataSource.createAssignment(assignment)
    }
    
    override suspend fun getAssignmentById(assignmentId: String): RoomAssignment? {
        return when (val result = remoteDataSource.getAssignmentById(assignmentId)) {
            is Result.Success -> result.data
            is Result.Error -> null
        }
    }
    
    override suspend fun getAssignmentsByRoom(roomId: String): List<RoomAssignment> {
        return when (val result = remoteDataSource.getAssignmentsByRoom(roomId)) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }
    
    override suspend fun getAssignmentsByStudent(studentId: String): List<RoomAssignment> {
        return when (val result = remoteDataSource.getAssignmentsByStudent(studentId)) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }
    
    override suspend fun getAssignmentsBySemester(semester: String): List<RoomAssignment> {
        return when (val result = remoteDataSource.getAssignmentsBySemester(semester)) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }
    
    override suspend fun updateAssignmentStatus(
        assignmentId: String, 
        status: AssignmentStatus
    ): Result<RoomAssignment> {
        return remoteDataSource.updateAssignmentStatus(assignmentId, status)
    }
    
    override suspend fun deleteAssignment(assignmentId: String): Result<Unit> {
        return remoteDataSource.deleteAssignment(assignmentId)
    }
}

/**
 * Repository implementation for Compatibility Scores
 */
class CompatibilityRepositoryImpl(
    private val remoteDataSource: CompatibilityRemoteDataSource,
    private val localDataSource: CompatibilityLocalDataSource
) : CompatibilityRepository {
    
    override suspend fun saveCompatibility(score: CompatibilityScore): Result<Unit> {
        localDataSource.saveCompatibility(score)
        return remoteDataSource.saveCompatibility(score)
    }
    
    override suspend fun getCompatibility(studentId1: String, studentId2: String): CompatibilityScore? {
        // Check local first
        localDataSource.getCompatibility(studentId1, studentId2)?.let { return it }
        localDataSource.getCompatibility(studentId2, studentId1)?.let { return it }
        
        // Fetch from remote
        return when (val result = remoteDataSource.getCompatibility(studentId1, studentId2)) {
            is Result.Success -> result.data?.also { score ->
                localDataSource.saveCompatibility(score)
            }
            is Result.Error -> null
        }
    }
    
    override suspend fun getCompatibilitiesForStudent(studentId: String): List<CompatibilityScore> {
        // Fetch from remote
        return when (val result = remoteDataSource.getCompatibilitiesForStudent(studentId)) {
            is Result.Success -> {
                result.data.forEach { localDataSource.saveCompatibility(it) }
                result.data
            }
            is Result.Error -> {
                localDataSource.getCompatibilitiesForStudent(studentId)
            }
        }
    }
    
    override suspend fun deleteCompatibility(studentId1: String, studentId2: String): Result<Unit> {
        localDataSource.deleteCompatibility(studentId1, studentId2)
        return remoteDataSource.deleteCompatibility(studentId1, studentId2)
    }
}
