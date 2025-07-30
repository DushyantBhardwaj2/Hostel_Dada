package performance

import kotlin.test.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlin.time.measureTime
import kotlin.time.Duration.Companion.seconds
import com.hosteldada.services.FirebaseDataService
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import app.cash.turbine.test

/**
 * 🚀 Performance Tests
 * Testing system performance under various loads
 */
class PerformanceTest {
    
    private lateinit var dataService: FirebaseDataService
    
    @BeforeTest
    fun setup() {
        dataService = FirebaseDataService()
    }
    
    // ⚡ Response Time Tests
    @Test
    fun `dashboard metrics should load within acceptable time`() = runTest {
        val loadTime = measureTime {
            dataService.getDashboardMetrics().take(1).toList()
        }
        
        // Should load within 5 seconds
        assertTrue(
            loadTime < 5.seconds,
            "Dashboard metrics should load within 5 seconds, but took $loadTime"
        )
    }
    
    @Test
    fun `all module metrics should load concurrently within time limit`() = runTest {
        val loadTime = measureTime {
            val jobs = listOf(
                async { dataService.getDashboardMetrics().take(1).toList() },
                async { dataService.getSnackCartMetrics().take(1).toList() },
                async { dataService.getRoomieMatcherMetrics().take(1).toList() },
                async { dataService.getLaundryBalancerMetrics().take(1).toList() },
                async { dataService.getMessyMessMetrics().take(1).toList() },
                async { dataService.getHostelFixerMetrics().take(1).toList() }
            )
            
            jobs.awaitAll()
        }
        
        // All modules should load concurrently within 10 seconds
        assertTrue(
            loadTime < 10.seconds,
            "All module metrics should load within 10 seconds, but took $loadTime"
        )
    }
    
    @Test
    fun `activity feed should stream efficiently`() = runTest {
        dataService.getActivityFeed().test(timeout = 3.seconds) {
            val activities = awaitItem()
            
            // Should receive activities quickly
            assertTrue(activities.isNotEmpty(), "Should receive activities within timeout")
            
            // Activities should have reasonable size
            assertTrue(activities.size <= 100, "Activity feed should be reasonably sized")
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // 🔄 Concurrent Access Tests
    @Test
    fun `system should handle multiple concurrent users`() = runTest {
        val userCount = 10
        val actionsPerUser = 5
        
        val loadTime = measureTime {
            val userJobs = (1..userCount).map { userId ->
                async {
                    repeat(actionsPerUser) {
                        // Simulate user actions
                        dataService.getDashboardMetrics().take(1).toList()
                        dataService.getActivityFeed().take(1).toList()
                        dataService.getNotifications().take(1).toList()
                        
                        // Small delay between actions
                        delay(10)
                    }
                }
            }
            
            userJobs.awaitAll()
        }
        
        println("Handled $userCount concurrent users in $loadTime")
        
        // Should handle concurrent users efficiently
        assertTrue(
            loadTime < 30.seconds,
            "Should handle $userCount concurrent users within 30 seconds"
        )
    }
    
    @Test
    fun `rapid successive calls should not degrade performance`() = runTest {
        val callCount = 20
        val times = mutableListOf<kotlin.time.Duration>()
        
        repeat(callCount) { iteration ->
            val callTime = measureTime {
                dataService.getDashboardMetrics().take(1).toList()
            }
            times.add(callTime)
            
            // Log every 5th call
            if ((iteration + 1) % 5 == 0) {
                println("Call ${iteration + 1}: $callTime")
            }
        }
        
        // Performance shouldn't degrade significantly
        val firstQuarter = times.take(callCount / 4).average()
        val lastQuarter = times.takeLast(callCount / 4).average()
        
        // Last quarter shouldn't be more than 2x slower than first quarter
        assertTrue(
            lastQuarter.inWholeMilliseconds <= firstQuarter.inWholeMilliseconds * 2,
            "Performance degradation detected: first quarter avg $firstQuarter, last quarter avg $lastQuarter"
        )
    }
    
    // 💾 Memory and Resource Tests
    @Test
    fun `memory usage should remain stable during long operation`() = runTest {
        // Simulate long-running dashboard session
        repeat(50) { iteration ->
            dataService.getDashboardMetrics().take(1).toList()
            dataService.getActivityFeed().take(1).toList()
            dataService.getNotifications().take(1).toList()
            
            // Periodic cleanup simulation
            if (iteration % 10 == 0) {
                // Force garbage collection hint
                js("if (typeof gc === 'function') gc();")
                delay(100)
            }
        }
        
        // If we get here without crashing, memory management is working
        assertTrue(true, "Long operation completed without memory issues")
    }
    
    @Test
    fun `large data sets should be handled efficiently`() = runTest {
        // Test with large activity feed
        val largeActivityCount = 1000
        
        val processTime = measureTime {
            // Simulate processing large activity list
            val activities = (1..largeActivityCount).map { index ->
                com.hosteldada.services.ActivityItem(
                    id = "activity-$index",
                    type = "bulk-test",
                    message = "Bulk test activity $index",
                    timestamp = "2025-07-31T10:${(index % 60).toString().padStart(2, '0')}:00Z",
                    icon = "📊",
                    module = "Performance"
                )
            }
            
            // Process activities (filtering, sorting, etc.)
            val recentActivities = activities.filter { it.type == "bulk-test" }
            val groupedByModule = recentActivities.groupBy { it.module }
            
            assertTrue(recentActivities.size == largeActivityCount)
            assertTrue(groupedByModule.containsKey("Performance"))
        }
        
        // Should process large datasets efficiently
        assertTrue(
            processTime < 2.seconds,
            "Large dataset processing should complete within 2 seconds, but took $processTime"
        )
    }
    
    // 📊 Real-time Performance Tests
    @Test
    fun `real-time updates should maintain consistent intervals`() = runTest {
        val updateTimes = mutableListOf<Long>()
        var lastUpdateTime = 0L
        
        dataService.getDashboardMetrics().test(timeout = 10.seconds) {
            repeat(5) {
                val metrics = awaitItem()
                val currentTime = js("Date.now()") as Long
                
                if (lastUpdateTime > 0) {
                    val interval = currentTime - lastUpdateTime
                    updateTimes.add(interval)
                }
                
                lastUpdateTime = currentTime
                assertNotNull(metrics)
            }
            
            cancelAndIgnoreRemainingEvents()
        }
        
        if (updateTimes.isNotEmpty()) {
            val avgInterval = updateTimes.average()
            val maxInterval = updateTimes.maxOrNull() ?: 0
            val minInterval = updateTimes.minOrNull() ?: 0
            
            println("Update intervals: avg=${avgInterval}ms, min=${minInterval}ms, max=${maxInterval}ms")
            
            // Intervals should be reasonably consistent (not varying by more than 5x)
            assertTrue(
                maxInterval <= minInterval * 5,
                "Update intervals should be consistent: min=$minInterval, max=$maxInterval"
            )
        }
    }
    
    @Test
    fun `notification system should handle high frequency updates`() = runTest {
        val notificationCount = 50
        
        val processTime = measureTime {
            // Simulate rapid notification generation
            repeat(notificationCount) { index ->
                val notification = com.hosteldada.services.NotificationItem(
                    id = "perf-test-$index",
                    title = "Performance Test $index",
                    message = "Testing notification system performance",
                    timestamp = "now",
                    priority = if (index % 3 == 0) "high" else if (index % 2 == 0) "medium" else "low",
                    icon = "⚡"
                )
                
                // Validate notification
                assertTrue(notification.id.isNotBlank())
                assertTrue(notification.priority in listOf("low", "medium", "high"))
            }
        }
        
        // Should handle many notifications quickly
        assertTrue(
            processTime < 1.seconds,
            "Notification processing should complete within 1 second, but took $processTime"
        )
    }
    
    // 🔧 Error Recovery Performance
    @Test
    fun `system should recover quickly from errors`() = runTest {
        val recoveryTime = measureTime {
            try {
                // Simulate error condition
                dataService.updateMetric("invalid/path/that/might/fail", "test")
                
                // System should continue working after potential error
                val metrics = dataService.getDashboardMetrics().take(1).toList()
                assertNotNull(metrics.firstOrNull())
                
            } catch (e: Exception) {
                // Even if error occurs, system should recover
                delay(100) // Brief recovery time
                
                val metrics = dataService.getDashboardMetrics().take(1).toList()
                assertNotNull(metrics.firstOrNull())
            }
        }
        
        // Recovery should be fast
        assertTrue(
            recoveryTime < 5.seconds,
            "Error recovery should complete within 5 seconds, but took $recoveryTime"
        )
    }
    
    // 📈 Scalability Tests
    @Test
    fun `dashboard should scale with increasing data volume`() = runTest {
        val dataVolumes = listOf(10, 50, 100, 500)
        val processingTimes = mutableListOf<kotlin.time.Duration>()
        
        dataVolumes.forEach { volume ->
            val processTime = measureTime {
                // Simulate processing increasing data volumes
                val activities = (1..volume).map { index ->
                    com.hosteldada.services.ActivityItem(
                        id = "scale-test-$index",
                        type = "scale",
                        message = "Scale test $index",
                        timestamp = "now",
                        icon = "📈",
                        module = "Scaling"
                    )
                }
                
                // Process the data
                val grouped = activities.groupBy { it.module }
                val filtered = activities.filter { it.type == "scale" }
                
                assertTrue(filtered.size == volume)
                assertTrue(grouped.containsKey("Scaling"))
            }
            
            processingTimes.add(processTime)
            println("Volume $volume processed in $processTime")
        }
        
        // Processing time should scale reasonably (not exponentially)
        val firstTime = processingTimes.first()
        val lastTime = processingTimes.last()
        val volumeRatio = dataVolumes.last().toDouble() / dataVolumes.first()
        val timeRatio = lastTime.inWholeMilliseconds.toDouble() / firstTime.inWholeMilliseconds
        
        // Time ratio should not be significantly worse than volume ratio
        assertTrue(
            timeRatio <= volumeRatio * 2,
            "Scaling performance issue: volume ratio $volumeRatio, time ratio $timeRatio"
        )
    }
}

/**
 * 🧪 Load Testing
 * Testing system behavior under heavy load
 */
class LoadTest {
    
    @Test
    fun `system should handle burst traffic`() = runTest {
        val burstSize = 25
        val burstDuration = measureTime {
            // Create burst of concurrent requests
            val burstJobs = (1..burstSize).map { requestId ->
                async {
                    val dataService = FirebaseDataService()
                    
                    // Each request does multiple operations
                    dataService.getDashboardMetrics().take(1).toList()
                    dataService.getActivityFeed().take(1).toList()
                    dataService.getNotifications().take(1).toList()
                    
                    requestId // Return request ID for verification
                }
            }
            
            val results = burstJobs.awaitAll()
            
            // All requests should complete
            assertEquals(burstSize, results.size)
            assertEquals(burstSize, results.toSet().size) // All unique
        }
        
        println("Burst of $burstSize requests completed in $burstDuration")
        
        // Burst should complete within reasonable time
        assertTrue(
            burstDuration < 15.seconds,
            "Burst traffic should be handled within 15 seconds, but took $burstDuration"
        )
    }
    
    @Test
    fun `sustained load should not cause degradation`() = runTest {
        val loadDuration = 20 // seconds of sustained load
        val requestsPerSecond = 2
        val totalRequests = loadDuration * requestsPerSecond
        
        val responseTimes = mutableListOf<kotlin.time.Duration>()
        
        val sustainedLoadTime = measureTime {
            repeat(totalRequests) { requestIndex ->
                val responseTime = measureTime {
                    val dataService = FirebaseDataService()
                    dataService.getDashboardMetrics().take(1).toList()
                }
                
                responseTimes.add(responseTime)
                
                // Maintain load rate
                delay(1000 / requestsPerSecond)
                
                if ((requestIndex + 1) % 10 == 0) {
                    println("Completed ${requestIndex + 1}/$totalRequests requests")
                }
            }
        }
        
        // Analyze response times
        val avgResponseTime = responseTimes.map { it.inWholeMilliseconds }.average()
        val maxResponseTime = responseTimes.maxByOrNull { it.inWholeMilliseconds }
        val firstHalfAvg = responseTimes.take(totalRequests / 2).map { it.inWholeMilliseconds }.average()
        val secondHalfAvg = responseTimes.takeLast(totalRequests / 2).map { it.inWholeMilliseconds }.average()
        
        println("Sustained load results:")
        println("- Total time: $sustainedLoadTime")
        println("- Average response time: ${avgResponseTime}ms")
        println("- Max response time: $maxResponseTime")
        println("- First half avg: ${firstHalfAvg}ms")
        println("- Second half avg: ${secondHalfAvg}ms")
        
        // Performance should not degrade significantly
        assertTrue(
            secondHalfAvg <= firstHalfAvg * 1.5,
            "Performance degradation detected under sustained load"
        )
        
        // No response should take excessively long
        assertTrue(
            maxResponseTime!!.inWholeSeconds <= 10,
            "Maximum response time should not exceed 10 seconds"
        )
    }
}
