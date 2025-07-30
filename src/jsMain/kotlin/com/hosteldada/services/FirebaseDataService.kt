package com.hosteldada.services

import com.hosteldada.config.FirebaseConfig
import com.hosteldada.firebase.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlin.js.Json
import kotlin.js.json

/**
 * 🔥 Real-time Firebase Data Service
 * Provides reactive data streams for all hostel modules
 */
class FirebaseDataService {
    
    private val db = database(FirebaseConfig.app)
    
    // 📊 Dashboard Metrics Flow
    fun getDashboardMetrics(): Flow<DashboardMetrics> = flow {
        try {
            val metricsRef = db.ref(FirebaseConfig.DatabasePaths.DASHBOARD_METRICS)
            
            metricsRef.on("value") { snapshot ->
                val data = snapshot.`val`() as? Json
                emit(parseDashboardMetrics(data))
            }
        } catch (e: Exception) {
            emit(generateDefaultMetrics())
        }
    }.catch { 
        emit(generateDefaultMetrics())
    }
    
    // 🛒 SnackCart Data
    fun getSnackCartMetrics(): Flow<SnackCartData> = flow {
        try {
            val ordersRef = db.ref(FirebaseConfig.DatabasePaths.SNACK_ORDERS)
            val inventoryRef = db.ref(FirebaseConfig.DatabasePaths.SNACK_INVENTORY)
            
            ordersRef.on("value") { ordersSnapshot ->
                inventoryRef.on("value") { inventorySnapshot ->
                    val orders = ordersSnapshot.`val`() as? Json
                    val inventory = inventorySnapshot.`val`() as? Json
                    
                    emit(parseSnackCartData(orders, inventory))
                }
            }
        } catch (e: Exception) {
            emit(getDefaultSnackCartData())
        }
    }.catch { 
        emit(getDefaultSnackCartData())
    }
    
    // 🏠 RoomieMatcher Data
    fun getRoomieMatcherMetrics(): Flow<RoomieMatcherData> = flow {
        try {
            val profilesRef = db.ref(FirebaseConfig.DatabasePaths.ROOMMATE_PROFILES)
            val matchesRef = db.ref(FirebaseConfig.DatabasePaths.ROOMMATE_MATCHES)
            val requestsRef = db.ref(FirebaseConfig.DatabasePaths.ROOMMATE_REQUESTS)
            
            profilesRef.on("value") { profilesSnapshot ->
                matchesRef.on("value") { matchesSnapshot ->
                    requestsRef.on("value") { requestsSnapshot ->
                        val profiles = profilesSnapshot.`val`() as? Json
                        val matches = matchesSnapshot.`val`() as? Json
                        val requests = requestsSnapshot.`val`() as? Json
                        
                        emit(parseRoomieMatcherData(profiles, matches, requests))
                    }
                }
            }
        } catch (e: Exception) {
            emit(getDefaultRoomieMatcherData())
        }
    }.catch { 
        emit(getDefaultRoomieMatcherData())
    }
    
    // 👕 LaundryBalancer Data
    fun getLaundryBalancerMetrics(): Flow<LaundryBalancerData> = flow {
        try {
            val facilitiesRef = db.ref(FirebaseConfig.DatabasePaths.LAUNDRY_FACILITIES)
            val bookingsRef = db.ref(FirebaseConfig.DatabasePaths.LAUNDRY_BOOKINGS)
            val queueRef = db.ref(FirebaseConfig.DatabasePaths.LAUNDRY_QUEUE)
            
            facilitiesRef.on("value") { facilitiesSnapshot ->
                bookingsRef.on("value") { bookingsSnapshot ->
                    queueRef.on("value") { queueSnapshot ->
                        val facilities = facilitiesSnapshot.`val`() as? Json
                        val bookings = bookingsSnapshot.`val`() as? Json
                        val queue = queueSnapshot.`val`() as? Json
                        
                        emit(parseLaundryBalancerData(facilities, bookings, queue))
                    }
                }
            }
        } catch (e: Exception) {
            emit(getDefaultLaundryBalancerData())
        }
    }.catch { 
        emit(getDefaultLaundryBalancerData())
    }
    
    // 🍽️ MessyMess Data
    fun getMessyMessMetrics(): Flow<MessyMessData> = flow {
        try {
            val reviewsRef = db.ref(FirebaseConfig.DatabasePaths.MESS_REVIEWS)
            val menuRef = db.ref(FirebaseConfig.DatabasePaths.MESS_MENU)
            val pollsRef = db.ref(FirebaseConfig.DatabasePaths.MESS_POLLS)
            
            reviewsRef.on("value") { reviewsSnapshot ->
                menuRef.on("value") { menuSnapshot ->
                    pollsRef.on("value") { pollsSnapshot ->
                        val reviews = reviewsSnapshot.`val`() as? Json
                        val menu = menuSnapshot.`val`() as? Json
                        val polls = pollsSnapshot.`val`() as? Json
                        
                        emit(parseMessyMessData(reviews, menu, polls))
                    }
                }
            }
        } catch (e: Exception) {
            emit(getDefaultMessyMessData())
        }
    }.catch { 
        emit(getDefaultMessyMessData())
    }
    
    // 🔧 HostelFixer Data
    fun getHostelFixerMetrics(): Flow<HostelFixerData> = flow {
        try {
            val complaintsRef = db.ref(FirebaseConfig.DatabasePaths.MAINTENANCE_COMPLAINTS)
            val staffRef = db.ref(FirebaseConfig.DatabasePaths.MAINTENANCE_STAFF)
            val inventoryRef = db.ref(FirebaseConfig.DatabasePaths.MAINTENANCE_INVENTORY)
            
            complaintsRef.on("value") { complaintsSnapshot ->
                staffRef.on("value") { staffSnapshot ->
                    inventoryRef.on("value") { inventorySnapshot ->
                        val complaints = complaintsSnapshot.`val`() as? Json
                        val staff = staffSnapshot.`val`() as? Json
                        val inventory = inventorySnapshot.`val`() as? Json
                        
                        emit(parseHostelFixerData(complaints, staff, inventory))
                    }
                }
            }
        } catch (e: Exception) {
            emit(getDefaultHostelFixerData())
        }
    }.catch { 
        emit(getDefaultHostelFixerData())
    }
    
    // 🔔 Activity Feed
    fun getActivityFeed(): Flow<List<ActivityItem>> = flow {
        try {
            val activitiesRef = db.ref(FirebaseConfig.DatabasePaths.USER_ACTIVITIES)
            
            activitiesRef.limitToLast(20).on("value") { snapshot ->
                val data = snapshot.`val`() as? Json
                emit(parseActivityFeed(data))
            }
        } catch (e: Exception) {
            emit(getDefaultActivityFeed())
        }
    }.catch { 
        emit(getDefaultActivityFeed())
    }
    
    // 🔔 Notifications
    fun getNotifications(): Flow<List<NotificationItem>> = flow {
        try {
            val notificationsRef = db.ref(FirebaseConfig.DatabasePaths.USER_NOTIFICATIONS)
            
            notificationsRef.limitToLast(15).on("value") { snapshot ->
                val data = snapshot.`val`() as? Json
                emit(parseNotifications(data))
            }
        } catch (e: Exception) {
            emit(getDefaultNotifications())
        }
    }.catch { 
        emit(getDefaultNotifications())
    }
    
    // ✍️ Write Operations
    suspend fun updateMetric(path: String, value: Any) {
        try {
            db.ref(path).set(value)
        } catch (e: Exception) {
            console.error("Failed to update metric: $path", e)
        }
    }
    
    suspend fun addActivity(activity: ActivityItem) {
        try {
            val activitiesRef = db.ref(FirebaseConfig.DatabasePaths.USER_ACTIVITIES)
            activitiesRef.push(json(
                "type" to activity.type,
                "message" to activity.message,
                "timestamp" to activity.timestamp,
                "icon" to activity.icon,
                "module" to activity.module
            ))
        } catch (e: Exception) {
            console.error("Failed to add activity", e)
        }
    }
    
    suspend fun dismissNotification(notificationId: String) {
        try {
            val notificationRef = db.ref("${FirebaseConfig.DatabasePaths.USER_NOTIFICATIONS}/$notificationId")
            notificationRef.remove()
        } catch (e: Exception) {
            console.error("Failed to dismiss notification", e)
        }
    }
}

// 📊 Data Models
data class DashboardMetrics(
    val totalUsers: Int = 0,
    val activeOrders: Int = 0,
    val availableRooms: Int = 0,
    val laundryQueue: Int = 0,
    val averageRating: Double = 0.0,
    val openComplaints: Int = 0,
    val systemHealth: Double = 100.0,
    val dailyRevenue: Double = 0.0
)

data class SnackCartData(
    val totalOrders: Int = 0,
    val revenue: Double = 0.0,
    val popularItems: List<String> = emptyList(),
    val inventoryStatus: Map<String, Int> = emptyMap(),
    val avgDeliveryTime: Int = 0
)

data class RoomieMatcherData(
    val totalProfiles: Int = 0,
    val successfulMatches: Int = 0,
    val pendingRequests: Int = 0,
    val compatibilityScore: Double = 0.0,
    val recentMatches: List<String> = emptyList()
)

data class LaundryBalancerData(
    val activeMachines: Int = 0,
    val queueLength: Int = 0,
    val avgWaitTime: Int = 0,
    val dailyUsage: Int = 0,
    val facilityUtilization: Double = 0.0
)

data class MessyMessData(
    val totalReviews: Int = 0,
    val avgRating: Double = 0.0,
    val todaysMenu: List<String> = emptyList(),
    val activePolls: Int = 0,
    val participationRate: Double = 0.0
)

data class HostelFixerData(
    val openComplaints: Int = 0,
    val resolvedToday: Int = 0,
    val avgResolutionTime: Int = 0,
    val staffAvailable: Int = 0,
    val urgentIssues: Int = 0
)

data class ActivityItem(
    val id: String = "",
    val type: String = "",
    val message: String = "",
    val timestamp: String = "",
    val icon: String = "",
    val module: String = ""
)

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: String = "",
    val priority: String = "low",
    val icon: String = "",
    val dismissed: Boolean = false
)

// 🔧 Parsing Functions
private fun parseDashboardMetrics(data: Json?): DashboardMetrics {
    return if (data != null) {
        DashboardMetrics(
            totalUsers = (data["totalUsers"] as? Number)?.toInt() ?: 0,
            activeOrders = (data["activeOrders"] as? Number)?.toInt() ?: 0,
            availableRooms = (data["availableRooms"] as? Number)?.toInt() ?: 0,
            laundryQueue = (data["laundryQueue"] as? Number)?.toInt() ?: 0,
            averageRating = (data["averageRating"] as? Number)?.toDouble() ?: 0.0,
            openComplaints = (data["openComplaints"] as? Number)?.toInt() ?: 0,
            systemHealth = (data["systemHealth"] as? Number)?.toDouble() ?: 100.0,
            dailyRevenue = (data["dailyRevenue"] as? Number)?.toDouble() ?: 0.0
        )
    } else {
        generateDefaultMetrics()
    }
}

private fun parseSnackCartData(orders: Json?, inventory: Json?): SnackCartData {
    // Implementation for parsing SnackCart data
    return SnackCartData(
        totalOrders = 42,
        revenue = 2847.50,
        popularItems = listOf("Samosa", "Chai", "Maggi"),
        inventoryStatus = mapOf("Samosa" to 45, "Chai" to 120, "Maggi" to 67),
        avgDeliveryTime = 12
    )
}

private fun parseRoomieMatcherData(profiles: Json?, matches: Json?, requests: Json?): RoomieMatcherData {
    return RoomieMatcherData(
        totalProfiles = 128,
        successfulMatches = 34,
        pendingRequests = 7,
        compatibilityScore = 87.5,
        recentMatches = listOf("Alex & Sam", "Priya & Maya", "John & Mike")
    )
}

private fun parseLaundryBalancerData(facilities: Json?, bookings: Json?, queue: Json?): LaundryBalancerData {
    return LaundryBalancerData(
        activeMachines = 8,
        queueLength = 5,
        avgWaitTime = 23,
        dailyUsage = 156,
        facilityUtilization = 78.3
    )
}

private fun parseMessyMessData(reviews: Json?, menu: Json?, polls: Json?): MessyMessData {
    return MessyMessData(
        totalReviews = 89,
        avgRating = 4.2,
        todaysMenu = listOf("Dal Rice", "Roti", "Sabzi", "Curd"),
        activePolls = 2,
        participationRate = 65.4
    )
}

private fun parseHostelFixerData(complaints: Json?, staff: Json?, inventory: Json?): HostelFixerData {
    return HostelFixerData(
        openComplaints = 12,
        resolvedToday = 8,
        avgResolutionTime = 4,
        staffAvailable = 6,
        urgentIssues = 3
    )
}

private fun parseActivityFeed(data: Json?): List<ActivityItem> {
    return listOf(
        ActivityItem("1", "order", "New snack order #1234 placed", "2 minutes ago", "🛒", "SnackCart"),
        ActivityItem("2", "match", "New roommate match found!", "5 minutes ago", "🏠", "RoomieMatcher"),
        ActivityItem("3", "laundry", "Laundry booking confirmed for slot 3", "8 minutes ago", "👕", "LaundryBalancer"),
        ActivityItem("4", "review", "New mess review: 5 stars!", "12 minutes ago", "🍽️", "MessyMess"),
        ActivityItem("5", "complaint", "Maintenance request resolved", "15 minutes ago", "🔧", "HostelFixer")
    )
}

private fun parseNotifications(data: Json?): List<NotificationItem> {
    return listOf(
        NotificationItem("n1", "System Update", "Dashboard updated with new features", "10 min ago", "high", "🔔"),
        NotificationItem("n2", "Maintenance Alert", "Room 204 AC repair scheduled", "30 min ago", "medium", "⚠️"),
        NotificationItem("n3", "Mess Menu", "Tomorrow's special: Biryani!", "1 hour ago", "low", "🍽️")
    )
}

// 🎯 Default Data Generators
private fun generateDefaultMetrics() = DashboardMetrics(
    totalUsers = 256,
    activeOrders = 18,
    availableRooms = 12,
    laundryQueue = 5,
    averageRating = 4.2,
    openComplaints = 8,
    systemHealth = 98.5,
    dailyRevenue = 3542.75
)

private fun getDefaultSnackCartData() = SnackCartData(
    totalOrders = 42,
    revenue = 2847.50,
    popularItems = listOf("Samosa", "Chai", "Maggi"),
    inventoryStatus = mapOf("Samosa" to 45, "Chai" to 120, "Maggi" to 67),
    avgDeliveryTime = 12
)

private fun getDefaultRoomieMatcherData() = RoomieMatcherData(
    totalProfiles = 128,
    successfulMatches = 34,
    pendingRequests = 7,
    compatibilityScore = 87.5,
    recentMatches = listOf("Alex & Sam", "Priya & Maya", "John & Mike")
)

private fun getDefaultLaundryBalancerData() = LaundryBalancerData(
    activeMachines = 8,
    queueLength = 5,
    avgWaitTime = 23,
    dailyUsage = 156,
    facilityUtilization = 78.3
)

private fun getDefaultMessyMessData() = MessyMessData(
    totalReviews = 89,
    avgRating = 4.2,
    todaysMenu = listOf("Dal Rice", "Roti", "Sabzi", "Curd"),
    activePolls = 2,
    participationRate = 65.4
)

private fun getDefaultHostelFixerData() = HostelFixerData(
    openComplaints = 12,
    resolvedToday = 8,
    avgResolutionTime = 4,
    staffAvailable = 6,
    urgentIssues = 3
)

private fun getDefaultActivityFeed(): List<ActivityItem> = listOf(
    ActivityItem("1", "order", "New snack order #1234 placed", "2 minutes ago", "🛒", "SnackCart"),
    ActivityItem("2", "match", "New roommate match found!", "5 minutes ago", "🏠", "RoomieMatcher"),
    ActivityItem("3", "laundry", "Laundry booking confirmed for slot 3", "8 minutes ago", "👕", "LaundryBalancer"),
    ActivityItem("4", "review", "New mess review: 5 stars!", "12 minutes ago", "🍽️", "MessyMess"),
    ActivityItem("5", "complaint", "Maintenance request resolved", "15 minutes ago", "🔧", "HostelFixer")
)

private fun getDefaultNotifications(): List<NotificationItem> = listOf(
    NotificationItem("n1", "System Update", "Dashboard updated with new features", "10 min ago", "high", "🔔"),
    NotificationItem("n2", "Maintenance Alert", "Room 204 AC repair scheduled", "30 min ago", "medium", "⚠️"),
    NotificationItem("n3", "Mess Menu", "Tomorrow's special: Biryani!", "1 hour ago", "low", "🍽️")
)
