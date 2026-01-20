package com.hosteldada.core.domain.repository

import com.hosteldada.core.common.result.Result
import com.hosteldada.core.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * ============================================
 * REPOSITORY INTERFACES
 * ============================================
 * 
 * Defined in Domain layer - implementations in Data layer.
 * Following Dependency Inversion Principle.
 */

// ==========================================
// AUTHENTICATION
// ==========================================

interface AuthRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun validateNsutDomain(email: String): Boolean
}

// ==========================================
// USER PROFILE
// ==========================================

interface ProfileRepository {
    fun observeProfile(userId: String): Flow<UserProfile?>
    suspend fun getProfile(userId: String): Result<UserProfile?>
    suspend fun saveProfile(profile: UserProfile): Result<Unit>
    suspend fun updateProfileFields(userId: String, fields: Map<String, Any>): Result<Unit>
    suspend fun profileExists(userId: String): Boolean
    suspend fun deleteProfile(userId: String): Result<Unit>
    suspend fun isProfileComplete(userId: String): Boolean
}

// ==========================================
// SNACK CART
// ==========================================

interface SnackRepository {
    fun observeSnacks(): Flow<List<Snack>>
    suspend fun getAllSnacks(): Result<List<Snack>>
    suspend fun getSnackById(id: String): Result<Snack?>
    suspend fun getSnacksByCategory(category: SnackCategory): Result<List<Snack>>
    suspend fun searchSnacks(query: String): Result<List<Snack>>
    
    // Admin operations
    suspend fun addSnack(snack: Snack): Result<String>
    suspend fun updateSnack(snack: Snack): Result<Unit>
    suspend fun deleteSnack(id: String): Result<Unit>
    suspend fun updateStock(id: String, quantity: Int): Result<Unit>
    suspend fun bulkUpdateStock(updates: Map<String, Int>): Result<Unit>
}

interface CartRepository {
    fun observeCart(userId: String): Flow<Cart>
    suspend fun getCart(userId: String): Result<Cart>
    suspend fun addToCart(userId: String, snack: Snack, quantity: Int): Result<Unit>
    suspend fun updateQuantity(userId: String, snackId: String, quantity: Int): Result<Unit>
    suspend fun removeFromCart(userId: String, snackId: String): Result<Unit>
    suspend fun clearCart(userId: String): Result<Unit>
}

interface OrderRepository {
    fun observeOrders(userId: String): Flow<List<SnackOrder>>
    suspend fun getOrders(userId: String): Result<List<SnackOrder>>
    suspend fun getOrderById(orderId: String): Result<SnackOrder?>
    suspend fun placeOrder(order: SnackOrder): Result<String>
    suspend fun cancelOrder(orderId: String): Result<Unit>
    
    // Admin
    fun observeAllOrders(): Flow<List<SnackOrder>>
    suspend fun getAllOrders(): Result<List<SnackOrder>>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>
    suspend fun getOrdersByStatus(status: OrderStatus): Result<List<SnackOrder>>
}

// ==========================================
// ROOMIE MATCHER
// ==========================================

interface StudentRepository {
    suspend fun getStudent(userId: String): Result<Student?>
    suspend fun saveStudent(student: Student): Result<Unit>
    suspend fun getAllStudents(): Result<List<Student>>
}

interface SurveyRepository {
    fun observeSurvey(userId: String, semester: String): Flow<RoommateSurvey?>
    suspend fun getSurvey(userId: String, semester: String): Result<RoommateSurvey?>
    suspend fun submitSurvey(survey: RoommateSurvey): Result<String>
    suspend fun updateSurvey(survey: RoommateSurvey): Result<Unit>
    suspend fun deleteSurvey(surveyId: String): Result<Unit>
    suspend fun getAllSurveys(semester: String): Result<List<RoommateSurvey>>
    suspend fun getSurveyCount(semester: String): Result<Int>
}

interface RoomRepository {
    fun observeRooms(): Flow<List<Room>>
    suspend fun getAllRooms(): Result<List<Room>>
    suspend fun getAvailableRooms(): Result<List<Room>>
    suspend fun getRoomById(roomId: String): Result<Room?>
    suspend fun addRoom(room: Room): Result<String>
    suspend fun updateRoom(room: Room): Result<Unit>
    suspend fun assignStudentToRoom(roomId: String, studentId: String): Result<Unit>
    suspend fun removeStudentFromRoom(roomId: String, studentId: String): Result<Unit>
}

interface AssignmentRepository {
    fun observeAssignments(semester: String): Flow<List<RoomAssignment>>
    suspend fun getAssignments(semester: String): Result<List<RoomAssignment>>
    suspend fun getAssignmentById(id: String): Result<RoomAssignment?>
    suspend fun createAssignment(assignment: RoomAssignment): Result<String>
    suspend fun updateAssignmentStatus(id: String, status: AssignmentStatus): Result<Unit>
    suspend fun deleteAssignment(id: String): Result<Unit>
    suspend fun getStudentAssignment(studentId: String, semester: String): Result<RoomAssignment?>
}

interface CompatibilityRepository {
    suspend fun getCompatibilityScore(studentId1: String, studentId2: String): Result<CompatibilityScore?>
    suspend fun saveCompatibilityScore(score: CompatibilityScore): Result<Unit>
    suspend fun getTopMatches(studentId: String, limit: Int): Result<List<CompatibilityScore>>
    suspend fun generateAllCompatibilities(semester: String): Result<List<CompatibilityScore>>
}

// ==========================================
// FUTURE MODULES
// ==========================================

interface LaundryRepository {
    fun observeSlots(date: String): Flow<List<LaundrySlot>>
    suspend fun getAvailableSlots(date: String): Result<List<LaundrySlot>>
    suspend fun bookSlot(slotId: String, userId: String): Result<Unit>
    suspend fun cancelBooking(slotId: String): Result<Unit>
    suspend fun getUserBookings(userId: String): Result<List<LaundrySlot>>
}

interface MaintenanceRepository {
    fun observeRequests(userId: String): Flow<List<MaintenanceRequest>>
    suspend fun submitRequest(request: MaintenanceRequest): Result<String>
    suspend fun updateRequestStatus(id: String, status: MaintenanceStatus): Result<Unit>
    suspend fun getAllRequests(): Result<List<MaintenanceRequest>>
    suspend fun getRequestsByStatus(status: MaintenanceStatus): Result<List<MaintenanceRequest>>
}

interface MessRepository {
    fun observeMenu(date: String): Flow<MessMenu?>
    suspend fun getWeeklyMenu(): Result<List<MessMenu>>
    suspend fun submitFeedback(feedback: MessFeedback): Result<String>
    suspend fun getFeedbackForMenu(menuId: String): Result<List<MessFeedback>>
}

// ==========================================
// ADMIN
// ==========================================

interface AdminRepository {
    suspend fun isAdmin(email: String): Result<Boolean>
    suspend fun getAdminModules(email: String): Result<List<String>>
    suspend fun isGlobalAdmin(email: String): Result<Boolean>
}
