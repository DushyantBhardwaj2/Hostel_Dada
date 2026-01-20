package com.hosteldada.core.data.local

import com.hosteldada.core.domain.model.*

/**
 * SQLDelight Local Data Source interface for User data
 * Used for offline-first caching
 */
interface UserLocalDataSource {
    suspend fun getCurrentUser(): User?
    suspend fun saveUser(user: User)
    suspend fun deleteUser()
    suspend fun getProfile(userId: String): UserProfile?
    suspend fun saveProfile(profile: UserProfile)
    suspend fun deleteProfile(userId: String)
}

/**
 * SQLDelight Local Data Source interface for Snacks
 * Implements offline caching with sync
 */
interface SnackLocalDataSource {
    suspend fun getAllSnacks(): List<Snack>
    suspend fun getSnackById(id: String): Snack?
    suspend fun getSnacksByCategory(category: SnackCategory): List<Snack>
    suspend fun getAvailableSnacks(): List<Snack>
    suspend fun searchSnacks(query: String): List<Snack>
    suspend fun saveSnack(snack: Snack)
    suspend fun saveSnacks(snacks: List<Snack>)
    suspend fun deleteSnack(snackId: String)
    suspend fun deleteAll()
    suspend fun getLastSyncTimestamp(): Long?
    suspend fun setLastSyncTimestamp(timestamp: Long)
}

/**
 * SQLDelight Local Data Source interface for Cart
 */
interface CartLocalDataSource {
    suspend fun getCart(userId: String): Cart?
    suspend fun saveCart(userId: String, cart: Cart)
    suspend fun clearCart(userId: String)
    suspend fun getPendingSyncItems(userId: String): List<CartItem>
    suspend fun markSynced(userId: String)
}

/**
 * SQLDelight Local Data Source interface for Orders
 */
interface OrderLocalDataSource {
    suspend fun getOrderById(orderId: String): SnackOrder?
    suspend fun getOrdersByUser(userId: String): List<SnackOrder>
    suspend fun getAllOrders(): List<SnackOrder>
    suspend fun saveOrder(order: SnackOrder)
    suspend fun saveOrders(orders: List<SnackOrder>)
    suspend fun deleteOrder(orderId: String)
    suspend fun deleteAll()
    suspend fun getLastSyncTimestamp(): Long?
    suspend fun setLastSyncTimestamp(timestamp: Long)
}

/**
 * SQLDelight Local Data Source interface for Surveys
 */
interface SurveyLocalDataSource {
    suspend fun getSurveyById(surveyId: String): RoommateSurvey?
    suspend fun getSurveyByStudentAndSemester(studentId: String, semester: String): RoommateSurvey?
    suspend fun getSurveysBySemester(semester: String): List<RoommateSurvey>
    suspend fun getAllSurveys(): List<RoommateSurvey>
    suspend fun saveSurvey(survey: RoommateSurvey)
    suspend fun deleteSurvey(surveyId: String)
    suspend fun deleteAll()
}

/**
 * SQLDelight Local Data Source interface for Rooms
 */
interface RoomLocalDataSource {
    suspend fun getRoomById(roomId: String): Room?
    suspend fun getAllRooms(): List<Room>
    suspend fun getAvailableRooms(): List<Room>
    suspend fun saveRoom(room: Room)
    suspend fun saveRooms(rooms: List<Room>)
    suspend fun deleteRoom(roomId: String)
    suspend fun deleteAll()
}

/**
 * SQLDelight Local Data Source interface for Compatibility
 */
interface CompatibilityLocalDataSource {
    suspend fun getCompatibility(studentId1: String, studentId2: String): CompatibilityScore?
    suspend fun getCompatibilitiesForStudent(studentId: String): List<CompatibilityScore>
    suspend fun saveCompatibility(score: CompatibilityScore)
    suspend fun deleteCompatibility(studentId1: String, studentId2: String)
    suspend fun deleteAllForStudent(studentId: String)
    suspend fun deleteAll()
}

/**
 * Cache policy configuration
 */
data class CachePolicy(
    val maxAgeMillis: Long = 5 * 60 * 1000, // 5 minutes default
    val forceRefresh: Boolean = false
) {
    companion object {
        val DEFAULT = CachePolicy()
        val FORCE_REFRESH = CachePolicy(forceRefresh = true)
        val LONG_CACHE = CachePolicy(maxAgeMillis = 30 * 60 * 1000) // 30 minutes
        val SHORT_CACHE = CachePolicy(maxAgeMillis = 1 * 60 * 1000) // 1 minute
    }
    
    fun isExpired(lastSyncTimestamp: Long?): Boolean {
        if (forceRefresh) return true
        if (lastSyncTimestamp == null) return true
        return System.currentTimeMillis() - lastSyncTimestamp > maxAgeMillis
    }
}
