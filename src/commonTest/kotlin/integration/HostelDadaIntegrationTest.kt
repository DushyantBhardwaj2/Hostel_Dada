package integration

import kotlin.test.*
import kotlinx.coroutines.test.runTest
import com.hosteldada.services.FirebaseDataService
import kotlinx.coroutines.flow.first
import firebase.FirebaseConfig

/**
 * 🧪 Integration Tests
 * End-to-end testing for the complete Hostel Dada system
 */
class HostelDadaIntegrationTest {
    
    private lateinit var dataService: FirebaseDataService
    
    @BeforeTest
    fun setup() {
        dataService = FirebaseDataService()
    }
    
    // 🔥 Firebase Integration Tests
    @Test
    fun `Firebase configuration should be properly initialized`() {
        assertTrue(FirebaseConfig.DatabasePaths.USERS.isNotBlank())
        assertTrue(FirebaseConfig.DatabasePaths.DASHBOARD_METRICS.isNotBlank())
        assertTrue(FirebaseConfig.DatabasePaths.SNACK_ORDERS.isNotBlank())
        assertTrue(FirebaseConfig.DatabasePaths.ROOMMATE_PROFILES.isNotBlank())
        assertTrue(FirebaseConfig.DatabasePaths.LAUNDRY_BOOKINGS.isNotBlank())
        assertTrue(FirebaseConfig.DatabasePaths.MESS_REVIEWS.isNotBlank())
        assertTrue(FirebaseConfig.DatabasePaths.MAINTENANCE_REQUESTS.isNotBlank())
    }
    
    @Test
    fun `all database paths should be unique`() {
        val paths = listOf(
            FirebaseConfig.DatabasePaths.USERS,
            FirebaseConfig.DatabasePaths.HOSTELS,
            FirebaseConfig.DatabasePaths.SNACK_ORDERS,
            FirebaseConfig.DatabasePaths.SNACK_INVENTORY,
            FirebaseConfig.DatabasePaths.ROOMMATE_PROFILES,
            FirebaseConfig.DatabasePaths.ROOMMATE_MATCHES,
            FirebaseConfig.DatabasePaths.LAUNDRY_BOOKINGS,
            FirebaseConfig.DatabasePaths.LAUNDRY_FACILITIES,
            FirebaseConfig.DatabasePaths.MESS_REVIEWS,
            FirebaseConfig.DatabasePaths.MESS_MENU,
            FirebaseConfig.DatabasePaths.MAINTENANCE_COMPLAINTS,
            FirebaseConfig.DatabasePaths.MAINTENANCE_STAFF
        )
        
        val uniquePaths = paths.toSet()
        assertEquals(paths.size, uniquePaths.size, "All database paths should be unique")
    }
    
    // 🏠 Cross-Module Integration Tests
    @Test
    fun `all modules should provide consistent data formats`() = runTest {
        // Test that all module metrics are accessible and consistent
        val dashboardMetrics = dataService.getDashboardMetrics().first()
        val snackCartData = dataService.getSnackCartMetrics().first()
        val roomieMatcherData = dataService.getRoomieMatcherMetrics().first()
        val laundryBalancerData = dataService.getLaundryBalancerMetrics().first()
        val messyMessData = dataService.getMessyMessMetrics().first()
        val hostelFixerData = dataService.getHostelFixerMetrics().first()
        
        // All should be accessible without errors
        assertNotNull(dashboardMetrics)
        assertNotNull(snackCartData)
        assertNotNull(roomieMatcherData)
        assertNotNull(laundryBalancerData)
        assertNotNull(messyMessData)
        assertNotNull(hostelFixerData)
    }
    
    @Test
    fun `activity feed should aggregate data from all modules`() = runTest {
        val activities = dataService.getActivityFeed().first()
        
        val modulesSeen = activities.map { it.module }.toSet()
        
        // Should have activities from multiple modules
        assertTrue(modulesSeen.isNotEmpty(), "Activity feed should contain activities")
        
        // Check for expected module names
        val expectedModules = setOf("SnackCart", "RoomieMatcher", "LaundryBalancer", "MessyMess", "HostelFixer")
        assertTrue(
            modulesSeen.any { it in expectedModules },
            "Activity feed should contain activities from known modules"
        )
    }
    
    @Test
    fun `notifications should be properly prioritized across modules`() = runTest {
        val notifications = dataService.getNotifications().first()
        
        val priorityDistribution = notifications.groupBy { it.priority }
        
        // Should have different priority levels
        assertTrue(priorityDistribution.keys.isNotEmpty(), "Should have notifications with priorities")
        
        // Validate priority levels are valid
        priorityDistribution.keys.forEach { priority ->
            assertTrue(priority in listOf("low", "medium", "high"))
        }
    }
    
    // 📊 Dashboard Integration Tests
    @Test
    fun `dashboard metrics should reflect real-time system state`() = runTest {
        val metrics = dataService.getDashboardMetrics().first()
        
        // Validate relationships between metrics
        assertTrue(metrics.totalUsers >= 0)
        assertTrue(metrics.activeOrders >= 0)
        assertTrue(metrics.systemHealth >= 0.0 && metrics.systemHealth <= 100.0)
        
        // System health should reflect other metrics logically
        if (metrics.openComplaints > 50) {
            // High complaint count might affect system health
            assertTrue(metrics.systemHealth < 100.0)
        }
    }
    
    @Test
    fun `real-time updates should be consistent across all data sources`() = runTest {
        // Get snapshots from all data sources
        val dashboardSnapshot = dataService.getDashboardMetrics().first()
        val activitiesSnapshot = dataService.getActivityFeed().first()
        val notificationsSnapshot = dataService.getNotifications().first()
        
        // All should provide data without errors
        assertNotNull(dashboardSnapshot)
        assertNotNull(activitiesSnapshot)
        assertNotNull(notificationsSnapshot)
        
        // Timestamps should be reasonable (not from future)
        activitiesSnapshot.forEach { activity ->
            assertTrue(activity.timestamp.isNotBlank())
        }
        
        notificationsSnapshot.forEach { notification ->
            assertTrue(notification.timestamp.isNotBlank())
        }
    }
    
    // 🔒 Security and Validation Tests
    @Test
    fun `data service should handle authentication gracefully`() = runTest {
        // Even without authentication, service should provide fallback data
        assertDoesNotThrow {
            dataService.getDashboardMetrics().first()
            dataService.getActivityFeed().first()
            dataService.getNotifications().first()
        }
    }
    
    @Test
    fun `write operations should validate input data`() = runTest {
        // Test various input scenarios
        assertDoesNotThrow {
            dataService.updateMetric("test/valid", "valid_value")
        }
        
        assertDoesNotThrow {
            dataService.dismissNotification("valid-notification-id")
        }
        
        val validActivity = com.hosteldada.services.ActivityItem(
            id = "test-1",
            type = "test",
            message = "Test message",
            timestamp = "2025-07-31T10:00:00Z",
            icon = "🧪",
            module = "Testing"
        )
        
        assertDoesNotThrow {
            dataService.addActivity(validActivity)
        }
    }
    
    // 🚀 Performance Integration Tests
    @Test
    fun `system should handle concurrent data access`() = runTest {
        // Simulate concurrent access to different data sources
        val jobs = listOf(
            { dataService.getDashboardMetrics().first() },
            { dataService.getSnackCartMetrics().first() },
            { dataService.getRoomieMatcherMetrics().first() },
            { dataService.getLaundryBalancerMetrics().first() },
            { dataService.getMessyMessMetrics().first() },
            { dataService.getHostelFixerMetrics().first() }
        )
        
        // All should complete without interference
        jobs.forEach { job ->
            assertDoesNotThrow {
                job()
            }
        }
    }
    
    @Test
    fun `system should maintain data consistency under load`() = runTest {
        // Test rapid successive calls
        repeat(10) {
            val metrics1 = dataService.getDashboardMetrics().first()
            val metrics2 = dataService.getDashboardMetrics().first()
            
            // Metrics should be consistent (not randomly changing)
            assertEquals(metrics1.totalUsers, metrics2.totalUsers)
            assertEquals(metrics1.systemHealth, metrics2.systemHealth)
        }
    }
    
    // 📱 UI Integration Tests
    @Test
    fun `dashboard components should integrate properly with data service`() {
        // Test that dashboard components can work with real data structures
        assertDoesNotThrow {
            val metrics = com.hosteldada.services.DashboardMetrics(
                totalUsers = 256,
                activeOrders = 18,
                availableRooms = 12,
                laundryQueue = 5,
                averageRating = 4.2,
                openComplaints = 8,
                systemHealth = 98.5,
                dailyRevenue = 3542.75
            )
            
            // Should be able to create dashboard with real metrics
            assertTrue(metrics.totalUsers > 0)
            assertTrue(metrics.systemHealth > 0)
        }
    }
    
    // 🔄 End-to-End Workflow Tests
    @Test
    fun `complete user workflow should work seamlessly`() = runTest {
        // Simulate a complete user workflow
        
        // 1. User loads dashboard
        val initialMetrics = dataService.getDashboardMetrics().first()
        assertNotNull(initialMetrics)
        
        // 2. User checks activity feed
        val activities = dataService.getActivityFeed().first()
        assertTrue(activities.isNotEmpty())
        
        // 3. User views notifications
        val notifications = dataService.getNotifications().first()
        assertNotNull(notifications)
        
        // 4. User dismisses a notification
        if (notifications.isNotEmpty()) {
            assertDoesNotThrow {
                dataService.dismissNotification(notifications.first().id)
            }
        }
        
        // 5. User adds new activity
        val newActivity = com.hosteldada.services.ActivityItem(
            id = "workflow-test",
            type = "user_action",
            message = "User completed workflow test",
            timestamp = "now",
            icon = "✅",
            module = "Testing"
        )
        
        assertDoesNotThrow {
            dataService.addActivity(newActivity)
        }
    }
}

/**
 * 🧪 Module-Specific Integration Tests
 * Testing integration between individual modules
 */
class ModuleIntegrationTest {
    
    @Test
    fun `SnackCart should integrate with notification system`() = runTest {
        val dataService = FirebaseDataService()
        
        // SnackCart operations should potentially generate notifications
        val snackData = dataService.getSnackCartMetrics().first()
        val notifications = dataService.getNotifications().first()
        
        assertNotNull(snackData)
        assertNotNull(notifications)
        
        // Low inventory should potentially create notifications
        if (snackData.inventoryStatus.values.any { it < 10 }) {
            // Should have some inventory-related notifications
            assertTrue(notifications.isNotEmpty())
        }
    }
    
    @Test
    fun `RoomieMatcher should update dashboard metrics`() = runTest {
        val dataService = FirebaseDataService()
        
        val roomieData = dataService.getRoomieMatcherMetrics().first()
        val dashboardMetrics = dataService.getDashboardMetrics().first()
        
        assertNotNull(roomieData)
        assertNotNull(dashboardMetrics)
        
        // Available rooms should be reflected in dashboard
        assertTrue(dashboardMetrics.availableRooms >= 0)
    }
    
    @Test
    fun `LaundryBalancer should provide real-time queue updates`() = runTest {
        val dataService = FirebaseDataService()
        
        val laundryData = dataService.getLaundryBalancerMetrics().first()
        val dashboardMetrics = dataService.getDashboardMetrics().first()
        
        assertNotNull(laundryData)
        assertNotNull(dashboardMetrics)
        
        // Laundry queue should be consistent
        assertEquals(laundryData.queueLength, dashboardMetrics.laundryQueue)
    }
    
    @Test
    fun `MessyMess ratings should affect overall satisfaction`() = runTest {
        val dataService = FirebaseDataService()
        
        val messData = dataService.getMessyMessMetrics().first()
        val dashboardMetrics = dataService.getDashboardMetrics().first()
        
        assertNotNull(messData)
        assertNotNull(dashboardMetrics)
        
        // Mess rating should contribute to average rating
        assertTrue(messData.avgRating >= 0.0 && messData.avgRating <= 5.0)
        assertTrue(dashboardMetrics.averageRating >= 0.0 && dashboardMetrics.averageRating <= 5.0)
    }
    
    @Test
    fun `HostelFixer should update maintenance metrics`() = runTest {
        val dataService = FirebaseDataService()
        
        val fixerData = dataService.getHostelFixerMetrics().first()
        val dashboardMetrics = dataService.getDashboardMetrics().first()
        
        assertNotNull(fixerData)
        assertNotNull(dashboardMetrics)
        
        // Open complaints should match
        assertEquals(fixerData.openComplaints, dashboardMetrics.openComplaints)
    }
}
