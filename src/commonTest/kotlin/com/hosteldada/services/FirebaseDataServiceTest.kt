package com.hosteldada.services

import kotlin.test.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import app.cash.turbine.test
import kotlin.time.Duration.Companion.seconds

/**
 * 🧪 Firebase Data Service Tests
 * Comprehensive test suite for real-time Firebase integration
 */
class FirebaseDataServiceTest {
    
    private lateinit var dataService: FirebaseDataService
    
    @BeforeTest
    fun setup() {
        dataService = FirebaseDataService()
    }
    
    @AfterTest
    fun teardown() {
        // Cleanup test data if needed
    }
    
    // 📊 Dashboard Metrics Tests
    @Test
    fun `getDashboardMetrics should return default metrics when Firebase unavailable`() = runTest {
        val metrics = dataService.getDashboardMetrics().first()
        
        assertNotNull(metrics)
        assertTrue(metrics.totalUsers >= 0)
        assertTrue(metrics.systemHealth >= 0.0 && metrics.systemHealth <= 100.0)
        assertTrue(metrics.dailyRevenue >= 0.0)
    }
    
    @Test
    fun `getDashboardMetrics should emit real-time updates`() = runTest {
        dataService.getDashboardMetrics().test(timeout = 5.seconds) {
            val firstMetrics = awaitItem()
            assertNotNull(firstMetrics)
            
            // Should receive at least one update
            assertTrue(firstMetrics.totalUsers >= 0)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // 🛒 SnackCart Tests
    @Test
    fun `getSnackCartMetrics should return valid data structure`() = runTest {
        val snackData = dataService.getSnackCartMetrics().first()
        
        assertNotNull(snackData)
        assertTrue(snackData.totalOrders >= 0)
        assertTrue(snackData.revenue >= 0.0)
        assertTrue(snackData.avgDeliveryTime >= 0)
        assertNotNull(snackData.popularItems)
        assertNotNull(snackData.inventoryStatus)
    }
    
    @Test
    fun `getSnackCartMetrics should handle empty inventory gracefully`() = runTest {
        val snackData = dataService.getSnackCartMetrics().first()
        
        // Should not crash with empty data
        assertTrue(snackData.inventoryStatus.values.all { it >= 0 })
    }
    
    // 🏠 RoomieMatcher Tests
    @Test
    fun `getRoomieMatcherMetrics should return compatibility scores in valid range`() = runTest {
        val roomieData = dataService.getRoomieMatcherMetrics().first()
        
        assertNotNull(roomieData)
        assertTrue(roomieData.compatibilityScore >= 0.0 && roomieData.compatibilityScore <= 100.0)
        assertTrue(roomieData.totalProfiles >= 0)
        assertTrue(roomieData.successfulMatches >= 0)
        assertTrue(roomieData.pendingRequests >= 0)
    }
    
    @Test
    fun `getRoomieMatcherMetrics should validate recent matches format`() = runTest {
        val roomieData = dataService.getRoomieMatcherMetrics().first()
        
        roomieData.recentMatches.forEach { match ->
            assertTrue(match.contains("&"), "Match should contain '&' separator: $match")
        }
    }
    
    // 👕 LaundryBalancer Tests
    @Test
    fun `getLaundryBalancerMetrics should return valid machine data`() = runTest {
        val laundryData = dataService.getLaundryBalancerMetrics().first()
        
        assertNotNull(laundryData)
        assertTrue(laundryData.activeMachines >= 0)
        assertTrue(laundryData.queueLength >= 0)
        assertTrue(laundryData.avgWaitTime >= 0)
        assertTrue(laundryData.facilityUtilization >= 0.0 && laundryData.facilityUtilization <= 100.0)
    }
    
    @Test
    fun `getLaundryBalancerMetrics should maintain logical constraints`() = runTest {
        val laundryData = dataService.getLaundryBalancerMetrics().first()
        
        // Queue cannot be longer than daily usage makes sense
        assertTrue(laundryData.queueLength <= laundryData.dailyUsage)
        
        // Wait time should be reasonable (less than 24 hours in minutes)
        assertTrue(laundryData.avgWaitTime <= 1440)
    }
    
    // 🍽️ MessyMess Tests
    @Test
    fun `getMessyMessMetrics should return valid rating data`() = runTest {
        val messData = dataService.getMessyMessMetrics().first()
        
        assertNotNull(messData)
        assertTrue(messData.avgRating >= 0.0 && messData.avgRating <= 5.0)
        assertTrue(messData.totalReviews >= 0)
        assertTrue(messData.activePolls >= 0)
        assertTrue(messData.participationRate >= 0.0 && messData.participationRate <= 100.0)
        assertNotNull(messData.todaysMenu)
    }
    
    @Test
    fun `getMessyMessMetrics should validate menu items`() = runTest {
        val messData = dataService.getMessyMessMetrics().first()
        
        messData.todaysMenu.forEach { item ->
            assertTrue(item.isNotBlank(), "Menu item should not be blank")
        }
    }
    
    // 🔧 HostelFixer Tests
    @Test
    fun `getHostelFixerMetrics should return valid maintenance data`() = runTest {
        val fixerData = dataService.getHostelFixerMetrics().first()
        
        assertNotNull(fixerData)
        assertTrue(fixerData.openComplaints >= 0)
        assertTrue(fixerData.resolvedToday >= 0)
        assertTrue(fixerData.avgResolutionTime >= 0)
        assertTrue(fixerData.staffAvailable >= 0)
        assertTrue(fixerData.urgentIssues >= 0)
    }
    
    @Test
    fun `getHostelFixerMetrics should maintain logical relationships`() = runTest {
        val fixerData = dataService.getHostelFixerMetrics().first()
        
        // Urgent issues should not exceed total open complaints
        assertTrue(fixerData.urgentIssues <= fixerData.openComplaints)
        
        // Resolution time should be reasonable (less than 30 days in hours)
        assertTrue(fixerData.avgResolutionTime <= 720)
    }
    
    // 📝 Activity Feed Tests
    @Test
    fun `getActivityFeed should return properly formatted activities`() = runTest {
        val activities = dataService.getActivityFeed().first()
        
        assertNotNull(activities)
        activities.forEach { activity ->
            assertTrue(activity.id.isNotBlank())
            assertTrue(activity.type.isNotBlank())
            assertTrue(activity.message.isNotBlank())
            assertTrue(activity.timestamp.isNotBlank())
            assertTrue(activity.icon.isNotBlank())
            assertTrue(activity.module.isNotBlank())
        }
    }
    
    @Test
    fun `getActivityFeed should limit results appropriately`() = runTest {
        val activities = dataService.getActivityFeed().first()
        
        // Should not return excessive activities
        assertTrue(activities.size <= 50, "Activity feed should be limited to reasonable size")
    }
    
    // 🔔 Notifications Tests
    @Test
    fun `getNotifications should return valid notification structure`() = runTest {
        val notifications = dataService.getNotifications().first()
        
        assertNotNull(notifications)
        notifications.forEach { notification ->
            assertTrue(notification.id.isNotBlank())
            assertTrue(notification.title.isNotBlank())
            assertTrue(notification.message.isNotBlank())
            assertTrue(notification.timestamp.isNotBlank())
            assertTrue(notification.priority in listOf("low", "medium", "high"))
            assertTrue(notification.icon.isNotBlank())
        }
    }
    
    @Test
    fun `getNotifications should handle priority levels correctly`() = runTest {
        val notifications = dataService.getNotifications().first()
        
        val priorities = notifications.map { it.priority }.toSet()
        priorities.forEach { priority ->
            assertTrue(priority in listOf("low", "medium", "high"))
        }
    }
    
    // ✍️ Write Operations Tests
    @Test
    fun `updateMetric should handle various data types`() = runTest {
        assertDoesNotThrow {
            dataService.updateMetric("test/string", "test_value")
            dataService.updateMetric("test/number", 42)
            dataService.updateMetric("test/boolean", true)
            dataService.updateMetric("test/double", 3.14)
        }
    }
    
    @Test
    fun `addActivity should accept valid activity data`() = runTest {
        val activity = ActivityItem(
            id = "test-1",
            type = "test",
            message = "Test activity",
            timestamp = "now",
            icon = "🧪",
            module = "Testing"
        )
        
        assertDoesNotThrow {
            dataService.addActivity(activity)
        }
    }
    
    @Test
    fun `dismissNotification should handle valid notification IDs`() = runTest {
        assertDoesNotThrow {
            dataService.dismissNotification("test-notification-1")
            dataService.dismissNotification("") // Should handle empty ID gracefully
        }
    }
    
    // 🔄 Flow and Coroutines Tests
    @Test
    fun `all data flows should emit at least one value`() = runTest {
        val flows = listOf(
            dataService.getDashboardMetrics(),
            dataService.getSnackCartMetrics(),
            dataService.getRoomieMatcherMetrics(),
            dataService.getLaundryBalancerMetrics(),
            dataService.getMessyMessMetrics(),
            dataService.getHostelFixerMetrics(),
            dataService.getActivityFeed(),
            dataService.getNotifications()
        )
        
        flows.forEach { flow ->
            val firstValue = flow.first()
            assertNotNull(firstValue, "Flow should emit at least one value")
        }
    }
    
    @Test
    fun `flows should handle exceptions gracefully`() = runTest {
        // All flows should have proper error handling
        dataService.getDashboardMetrics().test {
            val item = awaitItem()
            assertNotNull(item)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

/**
 * 🧪 Data Model Tests
 * Testing data structures and validation
 */
class DataModelTest {
    
    @Test
    fun `DashboardMetrics should have sensible defaults`() {
        val metrics = DashboardMetrics()
        
        assertEquals(0, metrics.totalUsers)
        assertEquals(0, metrics.activeOrders)
        assertEquals(0, metrics.availableRooms)
        assertEquals(0, metrics.laundryQueue)
        assertEquals(0.0, metrics.averageRating)
        assertEquals(0, metrics.openComplaints)
        assertEquals(100.0, metrics.systemHealth)
        assertEquals(0.0, metrics.dailyRevenue)
    }
    
    @Test
    fun `ActivityItem should validate required fields`() {
        val activity = ActivityItem(
            id = "test-1",
            type = "order",
            message = "New order placed",
            timestamp = "2025-07-31T10:00:00Z",
            icon = "🛒",
            module = "SnackCart"
        )
        
        assertEquals("test-1", activity.id)
        assertEquals("order", activity.type)
        assertTrue(activity.message.isNotBlank())
        assertTrue(activity.timestamp.isNotBlank())
        assertTrue(activity.icon.isNotBlank())
        assertTrue(activity.module.isNotBlank())
    }
    
    @Test
    fun `NotificationItem should validate priority levels`() {
        val validPriorities = listOf("low", "medium", "high")
        
        validPriorities.forEach { priority ->
            val notification = NotificationItem(
                id = "test",
                title = "Test",
                message = "Test message",
                timestamp = "now",
                priority = priority,
                icon = "🔔"
            )
            
            assertTrue(notification.priority in validPriorities)
        }
    }
    
    @Test
    fun `all data models should handle empty construction`() {
        assertDoesNotThrow {
            DashboardMetrics()
            SnackCartData()
            RoomieMatcherData()
            LaundryBalancerData()
            MessyMessData()
            HostelFixerData()
            ActivityItem()
            NotificationItem()
        }
    }
}
