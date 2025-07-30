package modules.snackcart

import firebase.FirebaseConfig
import kotlinx.coroutines.flow.*
import models.User
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf

/**
 * 🍿 SnackCart Service - Advanced DSA Implementation
 * 
 * Features:
 * - Priority Queue for order processing
 * - HashMap for O(1) inventory lookups
 * - LRU Cache for popular items
 * - Binary Search for price optimization
 * - Real-time inventory tracking
 */
class SnackCartService {
    
    // Firebase database references
    private val snacksRef = FirebaseConfig.getDatabaseReference("snacks")
    private val inventoryRef = FirebaseConfig.getDatabaseReference("inventory")
    private val ordersRef = FirebaseConfig.getDatabaseReference("snackOrders")
    
    // In-memory data structures for performance
    private val inventoryCache = mutableMapOf<String, InventoryItem>()
    private val snackCache = mutableMapOf<String, SnackItem>()
    private val orderQueue = OrderPriorityQueue()
    private val popularItemsCache = LRUCache<String, SnackItem>(50)
    
    // State flows for reactive UI
    private val _availableSnacks = MutableStateFlow<List<SnackItem>>(emptyList())
    val availableSnacks: StateFlow<List<SnackItem>> = _availableSnacks.asStateFlow()
    
    private val _currentOrders = MutableStateFlow<List<SnackOrder>>(emptyList())
    val currentOrders: StateFlow<List<SnackOrder>> = _currentOrders.asStateFlow()
    
    private val _inventoryAlerts = MutableStateFlow<List<InventoryAlert>>(emptyList())
    val inventoryAlerts: StateFlow<List<InventoryAlert>> = _inventoryAlerts.asStateFlow()
    
    /**
     * 🔄 Initialize service and load data
     */
    suspend fun initialize(hostelId: String) {
        try {
            loadSnacks(hostelId)
            loadInventory(hostelId)
            loadOrders(hostelId)
            startRealTimeUpdates(hostelId)
        } catch (e: Exception) {
            console.error("Error initializing SnackCart service: ${e.message}")
        }
    }
    
    /**
     * 🍿 Load all available snacks
     */
    private suspend fun loadSnacks(hostelId: String) {
        try {
            val snapshot = snacksRef.child(hostelId).get()
            val snacks = mutableListOf<SnackItem>()
            
            snapshot.children.forEach { child ->
                val snackData = child.value as? Map<String, Any>
                snackData?.let {
                    val snack = SnackItem.fromMap(it)
                    snacks.add(snack)
                    snackCache[snack.id] = snack
                    
                    // Add popular items to LRU cache
                    if (snack.popularity > 50) {
                        popularItemsCache.put(snack.id, snack)
                    }
                }
            }
            
            // Sort by popularity for better UX
            _availableSnacks.value = snacks.sortedByDescending { it.popularity }
        } catch (e: Exception) {
            console.error("Error loading snacks: ${e.message}")
        }
    }
    
    /**
     * 📦 Load inventory data
     */
    private suspend fun loadInventory(hostelId: String) {
        try {
            val snapshot = inventoryRef.child(hostelId).get()
            val alerts = mutableListOf<InventoryAlert>()
            
            snapshot.children.forEach { child ->
                val inventoryData = child.value as? Map<String, Any>
                inventoryData?.let {
                    val inventory = InventoryItem.fromMap(it)
                    inventoryCache[inventory.snackId] = inventory
                    
                    // Generate alerts for low stock items
                    when {
                        inventory.isOutOfStock() -> {
                            alerts.add(InventoryAlert(
                                snackId = inventory.snackId,
                                type = AlertType.OUT_OF_STOCK,
                                message = "Out of stock",
                                priority = AlertPriority.HIGH
                            ))
                        }
                        inventory.isLowStock() -> {
                            alerts.add(InventoryAlert(
                                snackId = inventory.snackId,
                                type = AlertType.LOW_STOCK,
                                message = "Low stock: ${inventory.currentStock} items remaining",
                                priority = AlertPriority.MEDIUM
                            ))
                        }
                        inventory.isExpired() -> {
                            alerts.add(InventoryAlert(
                                snackId = inventory.snackId,
                                type = AlertType.EXPIRED,
                                message = "Item has expired",
                                priority = AlertPriority.HIGH
                            ))
                        }
                    }
                }
            }
            
            _inventoryAlerts.value = alerts.sortedByDescending { it.priority.ordinal }
        } catch (e: Exception) {
            console.error("Error loading inventory: ${e.message}")
        }
    }
    
    /**
     * 📋 Load current orders
     */
    private suspend fun loadOrders(hostelId: String) {
        try {
            val snapshot = ordersRef.child(hostelId).get()
            val orders = mutableListOf<SnackOrder>()
            
            snapshot.children.forEach { child ->
                val orderData = child.value as? Map<String, Any>
                orderData?.let {
                    val order = SnackOrder.fromMap(it)
                    orders.add(order)
                    
                    // Add to priority queue if order is active
                    if (order.status in listOf(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PREPARING)) {
                        orderQueue.enqueue(order)
                    }
                }
            }
            
            _currentOrders.value = orders.sortedByDescending { it.orderTime }
        } catch (e: Exception) {
            console.error("Error loading orders: ${e.message}")
        }
    }
    
    /**
     * 🔄 Start real-time updates
     */
    private fun startRealTimeUpdates(hostelId: String) {
        // Listen to inventory changes
        inventoryRef.child(hostelId).on("value") { snapshot ->
            // Update inventory cache and alerts
            val alerts = mutableListOf<InventoryAlert>()
            
            snapshot.children.forEach { child ->
                val inventoryData = child.value as? Map<String, Any>
                inventoryData?.let {
                    val inventory = InventoryItem.fromMap(it)
                    inventoryCache[inventory.snackId] = inventory
                    
                    // Check for alerts
                    if (inventory.isLowStock()) {
                        alerts.add(InventoryAlert(
                            snackId = inventory.snackId,
                            type = AlertType.LOW_STOCK,
                            message = "Low stock: ${inventory.currentStock} items",
                            priority = AlertPriority.MEDIUM
                        ))
                    }
                }
            }
            
            _inventoryAlerts.value = alerts
        }
        
        // Listen to order changes
        ordersRef.child(hostelId).on("value") { snapshot ->
            val orders = mutableListOf<SnackOrder>()
            
            snapshot.children.forEach { child ->
                val orderData = child.value as? Map<String, Any>
                orderData?.let {
                    val order = SnackOrder.fromMap(it)
                    orders.add(order)
                }
            }
            
            _currentOrders.value = orders.sortedByDescending { it.orderTime }
        }
    }
    
    /**
     * 🛒 Place a new order
     */
    suspend fun placeOrder(
        user: User,
        items: List<OrderItem>,
        deliveryLocation: String,
        paymentMethod: PaymentMethod,
        specialNotes: String? = null
    ): Result<SnackOrder> {
        return try {
            // Validate inventory availability
            val validationResult = validateOrderItems(items)
            if (!validationResult.isValid) {
                return Result.failure(Exception(validationResult.message))
            }
            
            // Calculate total amount
            val totalAmount = items.sumOf { it.getTotalPrice() }
            
            // Create order
            val order = SnackOrder(
                id = generateOrderId(),
                userId = user.uid,
                hostelId = user.hostelId,
                items = items,
                status = OrderStatus.PENDING,
                totalAmount = totalAmount,
                deliveryLocation = deliveryLocation,
                phoneNumber = user.phoneNumber,
                specialNotes = specialNotes,
                paymentMethod = paymentMethod,
                paymentStatus = PaymentStatus.PENDING,
                orderTime = System.currentTimeMillis()
            )
            
            // Save to database
            ordersRef.child(user.hostelId).child(order.id).setValue(order.toMap())
            
            // Add to priority queue
            orderQueue.enqueue(order)
            
            // Update inventory (reserve items)
            updateInventoryForOrder(items, reserve = true)
            
            // Update popularity scores
            updatePopularityScores(items)
            
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * ✅ Validate order items against inventory
     */
    private fun validateOrderItems(items: List<OrderItem>): ValidationResult {
        for (item in items) {
            val inventory = inventoryCache[item.snackId]
                ?: return ValidationResult(false, "Item ${item.snackId} not found in inventory")
            
            if (inventory.currentStock < item.quantity) {
                val snackName = snackCache[item.snackId]?.name ?: item.snackId
                return ValidationResult(false, "Insufficient stock for $snackName. Available: ${inventory.currentStock}, Requested: ${item.quantity}")
            }
            
            if (inventory.isExpired()) {
                val snackName = snackCache[item.snackId]?.name ?: item.snackId
                return ValidationResult(false, "$snackName has expired and cannot be ordered")
            }
        }
        
        return ValidationResult(true, "All items are available")
    }
    
    /**
     * 📦 Update inventory for order
     */
    private suspend fun updateInventoryForOrder(items: List<OrderItem>, reserve: Boolean) {
        for (item in items) {
            val inventory = inventoryCache[item.snackId] ?: continue
            
            val newStock = if (reserve) {
                inventory.currentStock - item.quantity
            } else {
                inventory.currentStock + item.quantity
            }
            
            val updatedInventory = inventory.copy(currentStock = newStock)
            inventoryCache[item.snackId] = updatedInventory
            
            // Update in database
            inventoryRef.child("${inventory.snackId}/currentStock").setValue(newStock)
        }
    }
    
    /**
     * 📈 Update popularity scores based on orders
     */
    private suspend fun updatePopularityScores(items: List<OrderItem>) {
        for (item in items) {
            val snack = snackCache[item.snackId] ?: continue
            val newPopularity = snack.popularity + item.quantity
            
            val updatedSnack = snack.copy(popularity = newPopularity)
            snackCache[snack.id] = updatedSnack
            
            // Update in database
            snacksRef.child("${snack.id}/popularity").setValue(newPopularity)
            
            // Update LRU cache if item becomes popular
            if (newPopularity > 50) {
                popularItemsCache.put(snack.id, updatedSnack)
            }
        }
    }
    
    /**
     * 🔍 Search snacks with filters
     */
    fun searchSnacks(
        query: String = "",
        category: SnackCategory? = null,
        maxPrice: Double? = null,
        isVegetarian: Boolean? = null,
        isVegan: Boolean? = null,
        sortBy: SortOption = SortOption.POPULARITY
    ): List<SnackItem> {
        var filteredSnacks = _availableSnacks.value
        
        // Apply filters
        if (query.isNotEmpty()) {
            filteredSnacks = filteredSnacks.filter { 
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
            }
        }
        
        category?.let { cat ->
            filteredSnacks = filteredSnacks.filter { it.category == cat }
        }
        
        maxPrice?.let { price ->
            filteredSnacks = filteredSnacks.filter { 
                inventoryCache[it.id]?.getSellingPrice() ?: 0.0 <= price 
            }
        }
        
        isVegetarian?.let { veg ->
            filteredSnacks = filteredSnacks.filter { it.isVegetarian == veg }
        }
        
        isVegan?.let { vegan ->
            filteredSnacks = filteredSnacks.filter { it.isVegan == vegan }
        }
        
        // Apply sorting
        return when (sortBy) {
            SortOption.POPULARITY -> filteredSnacks.sortedByDescending { it.popularity }
            SortOption.PRICE_LOW_TO_HIGH -> filteredSnacks.sortedBy { 
                inventoryCache[it.id]?.getSellingPrice() ?: Double.MAX_VALUE 
            }
            SortOption.PRICE_HIGH_TO_LOW -> filteredSnacks.sortedByDescending { 
                inventoryCache[it.id]?.getSellingPrice() ?: 0.0 
            }
            SortOption.NAME -> filteredSnacks.sortedBy { it.name }
            SortOption.NEWEST -> filteredSnacks.sortedByDescending { it.createdAt }
        }
    }
    
    /**
     * 🔄 Update order status
     */
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus, hostelId: String): Result<Unit> {
        return try {
            ordersRef.child(hostelId).child(orderId).child("status").setValue(newStatus.name)
            
            // Update timestamps based on status
            val timestamp = System.currentTimeMillis()
            when (newStatus) {
                OrderStatus.CONFIRMED -> {
                    ordersRef.child(hostelId).child(orderId).child("acceptedTime").setValue(timestamp)
                }
                OrderStatus.READY -> {
                    ordersRef.child(hostelId).child(orderId).child("preparedTime").setValue(timestamp)
                }
                OrderStatus.DELIVERED -> {
                    ordersRef.child(hostelId).child(orderId).child("deliveredTime").setValue(timestamp)
                }
                else -> { /* No specific timestamp needed */ }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 🔢 Generate unique order ID
     */
    private fun generateOrderId(): String {
        return "ORD_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    /**
     * 📊 Get order analytics
     */
    fun getOrderAnalytics(timeRange: TimeRange): OrderAnalytics {
        val orders = _currentOrders.value
        val filteredOrders = filterOrdersByTimeRange(orders, timeRange)
        
        return OrderAnalytics(
            totalOrders = filteredOrders.size,
            totalRevenue = filteredOrders.sumOf { it.totalAmount },
            averageOrderValue = if (filteredOrders.isNotEmpty()) 
                filteredOrders.sumOf { it.totalAmount } / filteredOrders.size else 0.0,
            topSellingItems = getTopSellingItems(filteredOrders),
            ordersByStatus = filteredOrders.groupBy { it.status }.mapValues { it.value.size },
            averageDeliveryTime = calculateAverageDeliveryTime(filteredOrders)
        )
    }
    
    private fun filterOrdersByTimeRange(orders: List<SnackOrder>, timeRange: TimeRange): List<SnackOrder> {
        val now = System.currentTimeMillis()
        val cutoffTime = when (timeRange) {
            TimeRange.TODAY -> now - (24 * 60 * 60 * 1000)
            TimeRange.WEEK -> now - (7 * 24 * 60 * 60 * 1000)
            TimeRange.MONTH -> now - (30L * 24 * 60 * 60 * 1000)
            TimeRange.ALL_TIME -> 0L
        }
        
        return orders.filter { it.orderTime >= cutoffTime }
    }
    
    private fun getTopSellingItems(orders: List<SnackOrder>): List<Pair<String, Int>> {
        val itemCounts = mutableMapOf<String, Int>()
        
        orders.forEach { order ->
            order.items.forEach { item ->
                itemCounts[item.snackId] = itemCounts.getOrDefault(item.snackId, 0) + item.quantity
            }
        }
        
        return itemCounts.toList().sortedByDescending { it.second }.take(10)
    }
    
    private fun calculateAverageDeliveryTime(orders: List<SnackOrder>): Double {
        val deliveredOrders = orders.filter { it.status == OrderStatus.DELIVERED && it.deliveredTime != null }
        
        if (deliveredOrders.isEmpty()) return 0.0
        
        val totalDeliveryTime = deliveredOrders.sumOf { 
            (it.deliveredTime!! - it.orderTime) / (1000 * 60) // Convert to minutes
        }
        
        return totalDeliveryTime.toDouble() / deliveredOrders.size
    }
}

/**
 * 🏆 Priority Queue for Order Processing
 */
class OrderPriorityQueue {
    private val queue = mutableListOf<SnackOrder>()
    
    fun enqueue(order: SnackOrder) {
        queue.add(order)
        // Sort by priority (order time and total amount)
        queue.sortWith(compareBy<SnackOrder> { it.orderTime }.thenByDescending { it.totalAmount })
    }
    
    fun dequeue(): SnackOrder? {
        return if (queue.isNotEmpty()) queue.removeAt(0) else null
    }
    
    fun peek(): SnackOrder? = queue.firstOrNull()
    
    fun size(): Int = queue.size
    
    fun isEmpty(): Boolean = queue.isEmpty()
    
    fun getAll(): List<SnackOrder> = queue.toList()
}

/**
 * 🗄️ LRU Cache for Popular Items
 */
class LRUCache<K, V>(private val capacity: Int) {
    private val cache = linkedMapOf<K, V>()
    
    fun get(key: K): V? {
        val value = cache.remove(key)
        return if (value != null) {
            cache[key] = value
            value
        } else null
    }
    
    fun put(key: K, value: V) {
        if (cache.remove(key) == null && cache.size >= capacity) {
            val firstKey = cache.keys.first()
            cache.remove(firstKey)
        }
        cache[key] = value
    }
    
    fun getAll(): Map<K, V> = cache.toMap()
}

/**
 * 📊 Supporting Data Classes
 */
data class ValidationResult(val isValid: Boolean, val message: String)

data class InventoryAlert(
    val snackId: String,
    val type: AlertType,
    val message: String,
    val priority: AlertPriority,
    val timestamp: Long = System.currentTimeMillis()
)

enum class AlertType { LOW_STOCK, OUT_OF_STOCK, EXPIRED }
enum class AlertPriority { LOW, MEDIUM, HIGH }
enum class SortOption { POPULARITY, PRICE_LOW_TO_HIGH, PRICE_HIGH_TO_LOW, NAME, NEWEST }
enum class TimeRange { TODAY, WEEK, MONTH, ALL_TIME }

data class OrderAnalytics(
    val totalOrders: Int,
    val totalRevenue: Double,
    val averageOrderValue: Double,
    val topSellingItems: List<Pair<String, Int>>,
    val ordersByStatus: Map<OrderStatus, Int>,
    val averageDeliveryTime: Double
)
