package com.hosteldada.core.data.remote

import com.hosteldada.core.common.Result
import com.hosteldada.core.domain.model.*

/**
 * Firebase Remote Data Source interface for Authentication
 */
interface AuthRemoteDataSource {
    suspend fun signInWithEmailPassword(email: String, password: String): Result<User>
    suspend fun signUpWithEmailPassword(email: String, password: String, displayName: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun getCurrentUser(): User?
    suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
}

/**
 * Firebase Remote Data Source interface for User Profiles
 */
interface ProfileRemoteDataSource {
    suspend fun getProfile(userId: String): Result<UserProfile?>
    suspend fun createProfile(profile: UserProfile): Result<UserProfile>
    suspend fun updateProfile(profile: UserProfile): Result<UserProfile>
    suspend fun deleteProfile(userId: String): Result<Unit>
    suspend fun observeProfile(userId: String, onUpdate: (UserProfile?) -> Unit): () -> Unit
}

/**
 * Firebase Remote Data Source interface for Snacks
 */
interface SnackRemoteDataSource {
    suspend fun getAllSnacks(): Result<List<Snack>>
    suspend fun getSnackById(id: String): Result<Snack?>
    suspend fun getSnacksByCategory(category: SnackCategory): Result<List<Snack>>
    suspend fun getAvailableSnacks(): Result<List<Snack>>
    suspend fun searchSnacks(query: String): Result<List<Snack>>
    suspend fun createSnack(snack: Snack): Result<Snack>
    suspend fun updateSnack(snack: Snack): Result<Snack>
    suspend fun updateStockQuantity(snackId: String, quantity: Int): Result<Unit>
    suspend fun toggleAvailability(snackId: String, isAvailable: Boolean): Result<Unit>
    suspend fun deleteSnack(snackId: String): Result<Unit>
    suspend fun observeSnacks(onUpdate: (List<Snack>) -> Unit): () -> Unit
}

/**
 * Firebase Remote Data Source interface for Cart
 */
interface CartRemoteDataSource {
    suspend fun getCart(userId: String): Result<Cart?>
    suspend fun addToCart(userId: String, item: CartItem): Result<Cart>
    suspend fun updateCartItem(userId: String, snackId: String, quantity: Int): Result<Cart>
    suspend fun removeFromCart(userId: String, snackId: String): Result<Cart>
    suspend fun clearCart(userId: String): Result<Unit>
    suspend fun observeCart(userId: String, onUpdate: (Cart?) -> Unit): () -> Unit
}

/**
 * Firebase Remote Data Source interface for Orders
 */
interface OrderRemoteDataSource {
    suspend fun createOrder(order: SnackOrder): Result<SnackOrder>
    suspend fun getOrderById(orderId: String): Result<SnackOrder?>
    suspend fun getOrdersByUser(userId: String): Result<List<SnackOrder>>
    suspend fun getAllOrders(): Result<List<SnackOrder>>
    suspend fun getOrdersByStatus(status: OrderStatus): Result<List<SnackOrder>>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<SnackOrder>
    suspend fun cancelOrder(orderId: String): Result<SnackOrder>
    suspend fun observeOrder(orderId: String, onUpdate: (SnackOrder?) -> Unit): () -> Unit
    suspend fun observeUserOrders(userId: String, onUpdate: (List<SnackOrder>) -> Unit): () -> Unit
}

/**
 * Firebase Remote Data Source interface for Surveys
 */
interface SurveyRemoteDataSource {
    suspend fun createSurvey(survey: RoommateSurvey): Result<String>
    suspend fun updateSurvey(survey: RoommateSurvey): Result<Unit>
    suspend fun getSurveyById(surveyId: String): Result<RoommateSurvey?>
    suspend fun getSurveyByStudentAndSemester(studentId: String, semester: String): Result<RoommateSurvey?>
    suspend fun getSurveysBySemester(semester: String): Result<List<RoommateSurvey>>
    suspend fun getAllSurveys(): Result<List<RoommateSurvey>>
    suspend fun deleteSurvey(surveyId: String): Result<Unit>
}

/**
 * Firebase Remote Data Source interface for Rooms
 */
interface RoomRemoteDataSource {
    suspend fun createRoom(room: Room): Result<Room>
    suspend fun updateRoom(room: Room): Result<Room>
    suspend fun getRoomById(roomId: String): Result<Room?>
    suspend fun getAllRooms(): Result<List<Room>>
    suspend fun getAvailableRooms(): Result<List<Room>>
    suspend fun getRoomsByBlock(block: String): Result<List<Room>>
    suspend fun updateRoomOccupancy(roomId: String, change: Int): Result<Unit>
    suspend fun deleteRoom(roomId: String): Result<Unit>
}

/**
 * Firebase Remote Data Source interface for Room Assignments
 */
interface AssignmentRemoteDataSource {
    suspend fun createAssignment(assignment: RoomAssignment): Result<RoomAssignment>
    suspend fun getAssignmentById(assignmentId: String): Result<RoomAssignment?>
    suspend fun getAssignmentsByRoom(roomId: String): Result<List<RoomAssignment>>
    suspend fun getAssignmentsByStudent(studentId: String): Result<List<RoomAssignment>>
    suspend fun getAssignmentsBySemester(semester: String): Result<List<RoomAssignment>>
    suspend fun updateAssignmentStatus(assignmentId: String, status: AssignmentStatus): Result<RoomAssignment>
    suspend fun deleteAssignment(assignmentId: String): Result<Unit>
}

/**
 * Firebase Remote Data Source interface for Compatibility Scores
 */
interface CompatibilityRemoteDataSource {
    suspend fun saveCompatibility(score: CompatibilityScore): Result<Unit>
    suspend fun getCompatibility(studentId1: String, studentId2: String): Result<CompatibilityScore?>
    suspend fun getCompatibilitiesForStudent(studentId: String): Result<List<CompatibilityScore>>
    suspend fun deleteCompatibility(studentId1: String, studentId2: String): Result<Unit>
    suspend fun clearAllCompatibilities(): Result<Unit>
}

/**
 * Firebase Remote Data Source interface for Admin Configuration
 */
interface AdminConfigRemoteDataSource {
    suspend fun isAdmin(email: String, module: String): Result<Boolean>
    suspend fun isSuperAdmin(email: String): Result<Boolean>
    suspend fun getModuleAdmins(module: String): Result<List<String>>
    suspend fun addModuleAdmin(email: String, module: String): Result<Unit>
    suspend fun removeModuleAdmin(email: String, module: String): Result<Unit>
}
