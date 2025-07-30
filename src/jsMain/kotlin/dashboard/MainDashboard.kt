package dashboard

import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*
import firebase.FirebaseConfig
import modules.snackcart.SnackCartService
import modules.roomiematcher.RoommateMatcherService
import modules.laundrybalancer.LaundryBalancerService
import modules.messymess.MessyMessService
import modules.hostelfixer.HostelFixerService
import com.hosteldada.services.FirebaseDataService
import com.hosteldada.services.DashboardMetrics as RealtimeDashboardMetrics
import com.hosteldada.services.ActivityItem as RealtimeActivityItem
import com.hosteldada.services.NotificationItem as RealtimeNotificationItem
import kotlinx.coroutines.flow.catch

/**
 * 🏠 Hostel Dada - Unified Dashboard
 * 
 * Main dashboard integrating all 5 modules with real-time metrics:
 * - SnackCart: Inventory & Orders
 * - RoomieMatcher: Room Allocation
 * - LaundryBalancer: Machine Status
 * - MessyMess: Food Reviews
 * - HostelFixer: Maintenance Issues
 */

@Composable
fun MainDashboard() {
    var selectedModule by remember { mutableStateOf(DashboardModule.OVERVIEW) }
    var dashboardMetrics by remember { mutableStateOf<DashboardMetrics?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // 🔥 Real-time Firebase data states
    var realtimeMetrics by remember { mutableStateOf(RealtimeDashboardMetrics()) }
    var activityFeed by remember { mutableStateOf<List<RealtimeActivityItem>>(emptyList()) }
    var notifications by remember { mutableStateOf<List<RealtimeNotificationItem>>(emptyList()) }
    var showNotifications by remember { mutableStateOf(false) }
    
    // 📊 Firebase data service
    val dataService = remember { FirebaseDataService() }
    
    // Load dashboard data (existing logic)
    LaunchedEffect(Unit) {
        loadDashboardData { metrics ->
            dashboardMetrics = metrics
            isLoading = false
        }
    }
    
    // 🔄 Real-time data collection
    LaunchedEffect(Unit) {
        // Collect real-time dashboard metrics
        dataService.getDashboardMetrics()
            .catch { console.error("Error fetching realtime metrics", it) }
            .collect { metrics ->
                realtimeMetrics = metrics
            }
    }
    
    LaunchedEffect(Unit) {
        // Collect activity feed
        dataService.getActivityFeed()
            .catch { console.error("Error fetching activity feed", it) }
            .collect { activities ->
                activityFeed = activities
            }
    }
    
    LaunchedEffect(Unit) {
        // Collect notifications
        dataService.getNotifications()
            .catch { console.error("Error fetching notifications", it) }
            .collect { notifs ->
                notifications = notifs.filter { !it.dismissed }
            }
    }
    
    Div({ classes("dashboard-container") }) {
        
        // Header with navigation
        DashboardHeader(
            selectedModule = selectedModule,
            onModuleSelect = { selectedModule = it },
            notificationCount = notifications.size,
            onToggleNotifications = { showNotifications = !showNotifications }
        )
        
        // Main content area
        Div({ classes("dashboard-content") }) {
            
            if (isLoading) {
                LoadingSpinner()
            } else {
                when (selectedModule) {
                    DashboardModule.OVERVIEW -> OverviewDashboard(dashboardMetrics, realtimeMetrics, activityFeed)
                    DashboardModule.SNACK_CART -> SnackCartDashboard()
                    DashboardModule.ROOMIE_MATCHER -> RoomieDashboard()
                    DashboardModule.LAUNDRY_BALANCER -> LaundryDashboard()
                    DashboardModule.MESSY_MESS -> MessDashboard()
                    DashboardModule.HOSTEL_FIXER -> MaintenanceDashboard()
                }
            }
        }
        
        // Real-time notifications panel
        RealtimeNotificationPanel(
            notifications = notifications,
            showNotifications = showNotifications,
            onDismissNotification = { notificationId ->
                // Remove notification from local state
                notifications = notifications.filter { it.id != notificationId }
                // Update Firebase
                LaunchedEffect(notificationId) {
                    dataService.dismissNotification(notificationId)
                }
            }
        )
    }
}

@Composable
fun DashboardHeader(
    selectedModule: DashboardModule,
    onModuleSelect: (DashboardModule) -> Unit,
    notificationCount: Int = 0,
    onToggleNotifications: () -> Unit = {}
) {
    Header({ classes("dashboard-header") }) {
        
        // Logo and title
        Div({ classes("header-brand") }) {
            H1 { Text("🏠 Hostel Dada") }
            Span({ classes("version") }) { Text("v2.0 - Smart Campus Management") }
        }
        
        // Module navigation
        Nav({ classes("module-nav") }) {
            DashboardModule.values().forEach { module ->
                Button({
                    classes(if (selectedModule == module) "nav-btn active" else "nav-btn")
                    onClick { onModuleSelect(module) }
                }) {
                    Text("${module.icon} ${module.displayName}")
                }
            }
        }
        
        // User profile and settings
        Div({ classes("header-actions") }) {
            // Notification button with count
            Button({ 
                classes("notification-btn")
                onClick { onToggleNotifications() }
            }) {
                Text("🔔")
                if (notificationCount > 0) {
                    Span({ classes("notification-badge") }) {
                        Text(notificationCount.toString())
                    }
                }
            }
            
            Button({ classes("profile-btn") }) {
                Text("👤 Profile")
            }
            Button({ classes("settings-btn") }) {
                Text("⚙️ Settings")
            }
        }
    }
}

@Composable
fun OverviewDashboard(
    metrics: DashboardMetrics?, 
    realtimeMetrics: RealtimeDashboardMetrics,
    activityFeed: List<RealtimeActivityItem>
) {
    Div({ classes("overview-dashboard") }) {
        
        H2 { Text("📊 Campus Overview") }
        
        // 🔥 Real-time metrics section
        Div({ classes("realtime-metrics") }) {
            H3 { Text("⚡ Real-time Metrics") }
            
            Div({ classes("metrics-grid") }) {
                
                RealtimeMetricCard(
                    title = "👥 Total Users",
                    value = realtimeMetrics.totalUsers.toString(),
                    subtitle = "Active users",
                    trend = "up",
                    color = "blue"
                )
                
                RealtimeMetricCard(
                    title = "🛒 Active Orders",
                    value = realtimeMetrics.activeOrders.toString(),
                    subtitle = "In progress",
                    trend = "up",
                    color = "green"
                )
                
                RealtimeMetricCard(
                    title = "🏠 Available Rooms",
                    value = realtimeMetrics.availableRooms.toString(),
                    subtitle = "Ready for booking",
                    trend = "stable",
                    color = "purple"
                )
                
                RealtimeMetricCard(
                    title = "👕 Laundry Queue",
                    value = realtimeMetrics.laundryQueue.toString(),
                    subtitle = "Waiting",
                    trend = "down",
                    color = "orange"
                )
                
                RealtimeMetricCard(
                    title = "⭐ Average Rating",
                    value = String.format("%.1f", realtimeMetrics.averageRating),
                    subtitle = "Overall satisfaction",
                    trend = "up",
                    color = "yellow"
                )
                
                RealtimeMetricCard(
                    title = "🔧 Open Issues",
                    value = realtimeMetrics.openComplaints.toString(),
                    subtitle = "Pending resolution",
                    trend = "down",
                    color = "red"
                )
            }
        }
        
        // 📈 System health indicator
        Div({ classes("system-health") }) {
            H3 { Text("🖥️ System Health") }
            SystemHealthIndicator(realtimeMetrics.systemHealth)
        }
        
        // 📝 Real-time activity feed
        Div({ classes("realtime-activity-feed") }) {
            H3 { Text("🔄 Live Activity Feed") }
            
            if (activityFeed.isNotEmpty()) {
                activityFeed.take(10).forEach { activity ->
                    RealtimeActivityItem(activity)
                }
            } else {
                Div({ classes("no-activity") }) {
                    Text("No recent activity")
                }
            }
        }
        
        metrics?.let { data ->
            
            // Key metrics cards
            Div({ classes("metrics-grid") }) {
                
                MetricCard(
                    title = "🛒 Snack Orders",
                    value = data.snackCart.totalOrders.toString(),
                    subtitle = "₹${data.snackCart.totalRevenue} revenue",
                    trend = data.snackCart.orderTrend,
                    color = "blue"
                )
                
                MetricCard(
                    title = "🏠 Room Matches",
                    value = data.roomieMatcher.successfulMatches.toString(),
                    subtitle = "${data.roomieMatcher.pendingRequests} pending",
                    trend = data.roomieMatcher.matchTrend,
                    color = "green"
                )
                
                MetricCard(
                    title = "👕 Laundry Usage",
                    value = "${data.laundryBalancer.occupancyRate}%",
                    subtitle = "${data.laundryBalancer.availableMachines} available",
                    trend = data.laundryBalancer.usageTrend,
                    color = "purple"
                )
                
                MetricCard(
                    title = "🍽️ Mess Rating",
                    value = String.format("%.1f", data.messyMess.averageRating),
                    subtitle = "${data.messyMess.totalReviews} reviews",
                    trend = data.messyMess.ratingTrend,
                    color = "orange"
                )
                
                MetricCard(
                    title = "🔧 Open Issues",
                    value = data.hostelFixer.openIssues.toString(),
                    subtitle = "${data.hostelFixer.criticalIssues} critical",
                    trend = data.hostelFixer.resolutionTrend,
                    color = "red"
                )
            }
            
            // Quick actions section
            Div({ classes("quick-actions") }) {
                H3 { Text("⚡ Quick Actions") }
                
                Div({ classes("action-buttons") }) {
                    ActionButton("🛒 Order Snacks", "Place a new snack order")
                    ActionButton("🏠 Find Roommate", "Browse compatible roommates")
                    ActionButton("👕 Book Laundry", "Reserve laundry machine")
                    ActionButton("🍽️ Rate Meal", "Leave mess feedback")
                    ActionButton("🔧 Report Issue", "Submit maintenance request")
                }
            }
            
            // Recent activity feed
            Div({ classes("activity-feed") }) {
                H3 { Text("📝 Recent Activity") }
                
                data.recentActivity.forEach { activity ->
                    ActivityItem(activity)
                }
            }
            
            // Real-time charts
            Div({ classes("charts-section") }) {
                H3 { Text("📈 Real-time Analytics") }
                
                Div({ classes("charts-grid") }) {
                    ChartCard("Daily Usage Trends", data.usageChartData)
                    ChartCard("Revenue Analytics", data.revenueChartData)
                    ChartCard("User Satisfaction", data.satisfactionChartData)
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    trend: TrendDirection,
    color: String
) {
    Div({ classes("metric-card", "metric-$color") }) {
        Div({ classes("metric-header") }) {
            H4 { Text(title) }
            Span({ classes("trend", "trend-${trend.name.lowercase()}") }) {
                Text(when(trend) {
                    TrendDirection.UP -> "📈 +${(Math.random() * 10).toInt()}%"
                    TrendDirection.DOWN -> "📉 -${(Math.random() * 5).toInt()}%"
                    TrendDirection.STABLE -> "➡️ Stable"
                })
            }
        }
        
        Div({ classes("metric-value") }) {
            Text(value)
        }
        
        Div({ classes("metric-subtitle") }) {
            Text(subtitle)
        }
    }
}

@Composable
fun ActionButton(title: String, description: String) {
    Button({ classes("action-button") }) {
        Div({ classes("action-content") }) {
            Div({ classes("action-title") }) { Text(title) }
            Div({ classes("action-description") }) { Text(description) }
        }
    }
}

@Composable
fun ActivityItem(activity: ActivityData) {
    Div({ classes("activity-item") }) {
        Div({ classes("activity-icon") }) {
            Text(activity.icon)
        }
        Div({ classes("activity-content") }) {
            Div({ classes("activity-text") }) { Text(activity.description) }
            Div({ classes("activity-time") }) { Text(activity.timeAgo) }
        }
    }
}

@Composable
fun ChartCard(title: String, data: ChartData) {
    Div({ classes("chart-card") }) {
        H4 { Text(title) }
        Div({ classes("chart-placeholder") }) {
            Text("📊 Chart: ${data.dataPoints.size} points")
            // Real charts would be implemented with a charting library
        }
    }
}

@Composable
fun NotificationPanel() {
    var isExpanded by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf<List<NotificationData>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        // Load notifications
        notifications = loadNotifications()
    }
    
    Div({ classes("notification-panel", if (isExpanded) "expanded" else "collapsed") }) {
        
        Button({ 
            classes("notification-toggle")
            onClick { isExpanded = !isExpanded }
        }) {
            Text("🔔 ${notifications.size}")
        }
        
        if (isExpanded) {
            Div({ classes("notification-list") }) {
                if (notifications.isEmpty()) {
                    Div({ classes("no-notifications") }) {
                        Text("✅ All caught up!")
                    }
                } else {
                    notifications.forEach { notification ->
                        NotificationItem(notification)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationData) {
    Div({ classes("notification-item", "priority-${notification.priority.name.lowercase()}") }) {
        Div({ classes("notification-icon") }) {
            Text(notification.icon)
        }
        Div({ classes("notification-content") }) {
            Div({ classes("notification-title") }) { Text(notification.title) }
            Div({ classes("notification-message") }) { Text(notification.message) }
            Div({ classes("notification-time") }) { Text(notification.timeAgo) }
        }
        Button({ classes("notification-dismiss") }) {
            Text("✕")
        }
    }
}

@Composable
fun LoadingSpinner() {
    Div({ classes("loading-container") }) {
        Div({ classes("spinner") })
        Text("Loading dashboard...")
    }
}

// Individual module dashboards (simplified for now)
@Composable fun SnackCartDashboard() {
    Div({ classes("module-dashboard") }) {
        H2 { Text("🛒 SnackCart Dashboard") }
        Text("Advanced inventory management and order processing")
        // Full SnackCart interface would go here
    }
}

@Composable fun RoomieDashboard() {
    Div({ classes("module-dashboard") }) {
        H2 { Text("🏠 RoomieMatcher Dashboard") }
        Text("Intelligent roommate matching and room allocation")
    }
}

@Composable fun LaundryDashboard() {
    Div({ classes("module-dashboard") }) {
        H2 { Text("👕 LaundryBalancer Dashboard") }
        Text("Smart laundry scheduling and machine management")
    }
}

@Composable fun MessDashboard() {
    Div({ classes("module-dashboard") }) {
        H2 { Text("🍽️ MessyMess Dashboard") }
        Text("Food quality management and nutritional tracking")
    }
}

@Composable fun MaintenanceDashboard() {
    Div({ classes("module-dashboard") }) {
        H2 { Text("🔧 HostelFixer Dashboard") }
        Text("Maintenance request tracking and issue resolution")
    }
}

// Data models for dashboard
data class DashboardMetrics(
    val snackCart: SnackCartMetrics,
    val roomieMatcher: RoommateMetrics,
    val laundryBalancer: LaundryMetrics,
    val messyMess: MessMetrics,
    val hostelFixer: MaintenanceMetrics,
    val recentActivity: List<ActivityData>,
    val usageChartData: ChartData,
    val revenueChartData: ChartData,
    val satisfactionChartData: ChartData
)

data class SnackCartMetrics(
    val totalOrders: Int,
    val totalRevenue: Double,
    val orderTrend: TrendDirection
)

data class RoommateMetrics(
    val successfulMatches: Int,
    val pendingRequests: Int,
    val matchTrend: TrendDirection
)

data class LaundryMetrics(
    val occupancyRate: Int,
    val availableMachines: Int,
    val usageTrend: TrendDirection
)

data class MessMetrics(
    val averageRating: Double,
    val totalReviews: Int,
    val ratingTrend: TrendDirection
)

data class MaintenanceMetrics(
    val openIssues: Int,
    val criticalIssues: Int,
    val resolutionTrend: TrendDirection
)

data class ActivityData(
    val icon: String,
    val description: String,
    val timeAgo: String
)

data class ChartData(
    val dataPoints: List<Double>
)

data class NotificationData(
    val icon: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val priority: NotificationPriority
)

enum class DashboardModule(val displayName: String, val icon: String) {
    OVERVIEW("Overview", "📊"),
    SNACK_CART("SnackCart", "🛒"),
    ROOMIE_MATCHER("RoomieMatcher", "🏠"),
    LAUNDRY_BALANCER("LaundryBalancer", "👕"),
    MESSY_MESS("MessyMess", "🍽️"),
    HOSTEL_FIXER("HostelFixer", "🔧")
}

enum class TrendDirection { UP, DOWN, STABLE }
enum class NotificationPriority { HIGH, MEDIUM, LOW }

// Helper functions
suspend fun loadDashboardData(callback: (DashboardMetrics) -> Unit) {
    // Simulate loading real-time data
    kotlinx.coroutines.delay(1000)
    
    val metrics = DashboardMetrics(
        snackCart = SnackCartMetrics(142, 8450.0, TrendDirection.UP),
        roomieMatcher = RoommateMetrics(28, 7, TrendDirection.UP),
        laundryBalancer = LaundryMetrics(67, 12, TrendDirection.STABLE),
        messyMess = MessMetrics(4.2, 234, TrendDirection.UP),
        hostelFixer = MaintenanceMetrics(5, 1, TrendDirection.DOWN),
        recentActivity = listOf(
            ActivityData("🛒", "New snack order placed by Rahul", "2 min ago"),
            ActivityData("🏠", "Roommate match found for Priya", "5 min ago"),
            ActivityData("🔧", "Plumbing issue resolved in Room 204", "10 min ago"),
            ActivityData("👕", "Laundry booking confirmed for 3 PM", "15 min ago"),
            ActivityData("🍽️", "New mess review: 5 stars", "18 min ago")
        ),
        usageChartData = ChartData(listOf(12.0, 15.0, 18.0, 22.0, 25.0)),
        revenueChartData = ChartData(listOf(8450.0, 9200.0, 8800.0, 9500.0)),
        satisfactionChartData = ChartData(listOf(4.1, 4.2, 4.0, 4.3, 4.2))
    )
    
    callback(metrics)
}

suspend fun loadNotifications(): List<NotificationData> {
    return listOf(
        NotificationData("🔧", "Critical Issue", "Water leak in Block A", "5 min ago", NotificationPriority.HIGH),
        NotificationData("🛒", "Low Stock", "Maggi packets running low", "1 hour ago", NotificationPriority.MEDIUM),
        NotificationData("👕", "Maintenance", "Washing machine #3 needs service", "2 hours ago", NotificationPriority.LOW)
    )
}

// 🔥 Real-time Components

@Composable
fun RealtimeMetricCard(
    title: String,
    value: String,
    subtitle: String,
    trend: String,
    color: String
) {
    Div({ 
        classes("metric-card", "metric-$color")
        style {
            animation("pulse 2s infinite") // Real-time indicator
        }
    }) {
        Div({ classes("metric-header") }) {
            H4 { Text(title) }
            Span({ 
                classes(when(trend) {
                    "up" -> "trend trend-up"
                    "down" -> "trend trend-down"
                    else -> "trend trend-stable"
                })
            }) {
                Text(when(trend) {
                    "up" -> "↗️ +5%"
                    "down" -> "↘️ -3%"
                    else -> "➡️ 0%"
                })
            }
        }
        
        Div({ classes("metric-value") }) {
            Text(value)
        }
        
        Div({ classes("metric-subtitle") }) {
            Text(subtitle)
        }
    }
}

@Composable
fun SystemHealthIndicator(health: Double) {
    Div({ classes("health-indicator") }) {
        Div({ classes("health-bar") }) {
            Div({ 
                classes("health-fill")
                style {
                    width(health.toInt().percent)
                    backgroundColor(when {
                        health >= 90 -> Color.green
                        health >= 70 -> Color.orange
                        else -> Color.red
                    })
                }
            })
        }
        Text("${health.toInt()}% System Health")
    }
}

@Composable
fun RealtimeActivityItem(activity: RealtimeActivityItem) {
    Div({ 
        classes("activity-item")
        style {
            animation("slideInRight 0.5s ease-out")
        }
    }) {
        Span({ classes("activity-icon") }) {
            Text(activity.icon)
        }
        
        Div({ classes("activity-content") }) {
            Div({ classes("activity-text") }) {
                Text(activity.message)
            }
            Div({ classes("activity-time") }) {
                Text("${activity.timestamp} • ${activity.module}")
            }
        }
    }
}

@Composable
fun RealtimeNotificationPanel(
    notifications: List<RealtimeNotificationItem>,
    showNotifications: Boolean,
    onDismissNotification: (String) -> Unit
) {
    if (showNotifications) {
        Div({ 
            classes("notification-panel")
            style {
                position(Position.Fixed)
                top(100.px)
                right(20.px)
                zIndex(1000)
            }
        }) {
            Div({ classes("notification-list") }) {
                if (notifications.isNotEmpty()) {
                    notifications.forEach { notification ->
                        RealtimeNotificationItem(
                            notification = notification,
                            onDismiss = { onDismissNotification(notification.id) }
                        )
                    }
                } else {
                    Div({ classes("no-notifications") }) {
                        Text("No new notifications")
                    }
                }
            }
        }
    }
}

@Composable
fun RealtimeNotificationItem(
    notification: RealtimeNotificationItem,
    onDismiss: () -> Unit
) {
    Div({ 
        classes("notification-item", "priority-${notification.priority}")
        style {
            animation("fadeInRight 0.3s ease-out")
        }
    }) {
        Span({ classes("notification-icon") }) {
            Text(notification.icon)
        }
        
        Div({ classes("notification-content") }) {
            Div({ classes("notification-title") }) {
                Text(notification.title)
            }
            Div({ classes("notification-message") }) {
                Text(notification.message)
            }
            Div({ classes("notification-time") }) {
                Text(notification.timestamp)
            }
        }
        
        Button({ 
            classes("notification-dismiss")
            onClick { onDismiss() }
        }) {
            Text("✕")
        }
    }
}
