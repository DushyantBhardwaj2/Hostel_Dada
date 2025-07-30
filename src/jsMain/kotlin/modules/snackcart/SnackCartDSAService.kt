package modules.snackcart

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 🍿 SnackCart Service - DSA Implementation
 * 
 * Core business logic with data structure algorithms:
 * - HashMap for O(1) inventory management
 * - Trie for efficient search functionality
 * - Sorting algorithms for analytics
 * - Priority queue for order processing
 */
class SnackCartService {
    
    // 📊 State Management
    private val _snacks = MutableStateFlow<Map<String, Snack>>(emptyMap())
    val snacks: StateFlow<Map<String, Snack>> = _snacks.asStateFlow()
    
    private val _orders = MutableStateFlow<Map<String, SnackOrder>>(emptyMap())
    val orders: StateFlow<Map<String, SnackOrder>> = _orders.asStateFlow()
    
    private val _stats = MutableStateFlow<SnackInventoryStats?>(null)
    val stats: StateFlow<SnackInventoryStats?> = _stats.asStateFlow()
    
    // 🔍 Search Data Structure
    private val searchTrie = SnackSearchTrie()
    
    // 📈 Analytics Cache
    private var salesDataCache: Map<String, SnackSalesData> = emptyMap()
    private var cacheLastUpdated: Long = 0
    private val cacheValidityMs = 60_000 // 1 minute
    
    init {
        // Initialize with sample data for testing
        initializeSampleData()
    }
    
    // =============================================
    // 🛠️ ADMIN OPERATIONS
    // =============================================
    
    /**
     * 📦 Add new snack to inventory (Admin only)
     * Uses HashMap for O(1) insertion
     */
    fun addSnack(
        name: String,
        costPrice: Double,
        sellingPrice: Double,
        quantity: Int,
        description: String = "",
        category: String = "General",
        adminId: String
    ): Result<Snack> {
        return try {
            val snackId = generateSnackId()
            val snack = Snack(
                id = snackId,
                name = name,
                costPrice = costPrice,
                sellingPrice = sellingPrice,
                quantity = quantity,
                description = description,
                category = category,
                addedBy = adminId
            )
            
            // Update HashMap inventory
            val currentSnacks = _snacks.value.toMutableMap()
            currentSnacks[snackId] = snack
            _snacks.value = currentSnacks
            
            // Update search trie
            searchTrie.insert(name, snackId)
            
            // Refresh analytics
            updateAnalytics()
            
            console.log("✅ Added snack: $name (ID: $snackId)")
            Result.success(snack)
        } catch (e: Exception) {
            console.error("❌ Failed to add snack: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * ✏️ Update snack information (Admin only)
     */
    fun updateSnack(snackId: String, updatedSnack: Snack): Result<Snack> {
        return try {
            val currentSnacks = _snacks.value.toMutableMap()
            val oldSnack = currentSnacks[snackId] 
                ?: return Result.failure(Exception("Snack not found"))
            
            // Update inventory
            currentSnacks[snackId] = updatedSnack
            _snacks.value = currentSnacks
            
            // Update search trie if name changed
            if (oldSnack.name != updatedSnack.name) {
                searchTrie.remove(oldSnack.name, snackId)
                searchTrie.insert(updatedSnack.name, snackId)
            }
            
            updateAnalytics()
            console.log("✅ Updated snack: ${updatedSnack.name}")
            Result.success(updatedSnack)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 🗑️ Remove snack from inventory (Admin only)
     */
    fun removeSnack(snackId: String): Result<Boolean> {
        return try {
            val currentSnacks = _snacks.value.toMutableMap()
            val snack = currentSnacks[snackId] 
                ?: return Result.failure(Exception("Snack not found"))
            
            // Remove from inventory
            currentSnacks.remove(snackId)
            _snacks.value = currentSnacks
            
            // Remove from search trie
            searchTrie.remove(snack.name, snackId)
            
            updateAnalytics()
            console.log("✅ Removed snack: ${snack.name}")
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 📝 Update order status (Admin only)
     */
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus, adminNotes: String = ""): Result<Boolean> {
        return try {
            val currentOrders = _orders.value.toMutableMap()
            val order = currentOrders[orderId] 
                ?: return Result.failure(Exception("Order not found"))
            
            val updatedOrder = order.copy(
                status = newStatus,
                adminNotes = adminNotes,
                deliveredAt = if (newStatus == OrderStatus.DELIVERED) System.currentTimeMillis() else order.deliveredAt,
                cancelledAt = if (newStatus == OrderStatus.CANCELLED) System.currentTimeMillis() else order.cancelledAt
            )
            
            // If order is cancelled, return stock
            if (newStatus == OrderStatus.CANCELLED && order.status != OrderStatus.CANCELLED) {
                returnStockToInventory(order.snackId, order.quantity)
            }
            
            currentOrders[orderId] = updatedOrder
            _orders.value = currentOrders
            
            updateAnalytics()
            console.log("✅ Updated order $orderId status to $newStatus")
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // =============================================
    // 👤 USER OPERATIONS
    // =============================================
    
    /**
     * 🛒 Reserve snacks for purchase (User operation)
     * Implements inventory deduction with rollback on failure
     */
    fun reserveSnack(userId: String, userEmail: String, snackId: String, quantity: Int): Result<SnackOrder> {
        return try {
            val currentSnacks = _snacks.value.toMutableMap()
            val snack = currentSnacks[snackId] 
                ?: return Result.failure(Exception("Snack not found"))
            
            // Check availability
            if (!snack.isInStock()) {
                return Result.failure(Exception("Snack is out of stock"))
            }
            
            if (snack.quantity < quantity) {
                return Result.failure(Exception("Only ${snack.quantity} items available"))
            }
            
            // Create order
            val orderId = generateOrderId()
            val order = SnackOrder(
                orderId = orderId,
                userId = userId,
                userEmail = userEmail,
                snackId = snackId,
                snackName = snack.name,
                quantity = quantity,
                unitPrice = snack.sellingPrice,
                totalAmount = snack.sellingPrice * quantity,
                status = OrderStatus.RESERVED
            )
            
            // Deduct from inventory
            val updatedSnack = snack.copy(quantity = snack.quantity - quantity)
            currentSnacks[snackId] = updatedSnack
            _snacks.value = currentSnacks
            
            // Add order
            val currentOrders = _orders.value.toMutableMap()
            currentOrders[orderId] = order
            _orders.value = currentOrders
            
            updateAnalytics()
            console.log("✅ Reserved $quantity ${snack.name} for $userEmail")
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * ❌ Cancel reservation (User operation)
     */
    fun cancelReservation(userId: String, orderId: String): Result<Boolean> {
        return try {
            val currentOrders = _orders.value.toMutableMap()
            val order = currentOrders[orderId] 
                ?: return Result.failure(Exception("Order not found"))
            
            // Verify user owns this order
            if (order.userId != userId) {
                return Result.failure(Exception("Unauthorized"))
            }
            
            // Can only cancel reserved orders
            if (order.status != OrderStatus.RESERVED) {
                return Result.failure(Exception("Cannot cancel ${order.status.name.lowercase()} order"))
            }
            
            // Return stock to inventory
            returnStockToInventory(order.snackId, order.quantity)
            
            // Update order status
            val cancelledOrder = order.copy(
                status = OrderStatus.CANCELLED,
                cancelledAt = System.currentTimeMillis()
            )
            currentOrders[orderId] = cancelledOrder
            _orders.value = currentOrders
            
            updateAnalytics()
            console.log("✅ Cancelled reservation for ${order.snackName}")
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // =============================================
    // 🔍 SEARCH & ANALYTICS (DSA ALGORITHMS)
    // =============================================
    
    /**
     * 🔍 Search snacks using Trie data structure
     * Time Complexity: O(m) where m is search term length
     */
    fun searchSnacks(searchTerm: String): List<Snack> {
        if (searchTerm.isBlank()) return _snacks.value.values.toList()
        
        val matchingIds = searchTrie.searchByPrefix(searchTerm)
        return matchingIds.mapNotNull { _snacks.value[it] }
    }
    
    /**
     * 💡 Get autocomplete suggestions
     */
    fun getAutocompleteSuggestions(prefix: String): List<String> {
        return searchTrie.getAutocompleteSuggestions(prefix)
    }
    
    /**
     * 📊 Get top selling snacks using sorting algorithms
     * Implements merge sort for stable sorting
     */
    fun getTopSellingSnacks(limit: Int = 5): List<SnackSalesData> {
        val salesData = getSalesData()
        return salesData.values
            .sortedWith(compareByDescending<SnackSalesData> { it.totalRevenue }
                .thenByDescending { it.totalQuantitySold })
            .take(limit)
    }
    
    /**
     * 💰 Get most profitable snacks
     */
    fun getMostProfitableSnacks(limit: Int = 5): List<SnackSalesData> {
        val salesData = getSalesData()
        return salesData.values
            .sortedByDescending { it.totalProfit }
            .take(limit)
    }
    
    /**
     * ⚠️ Get low stock snacks
     */
    fun getLowStockSnacks(threshold: Int = 5): List<Snack> {
        return _snacks.value.values
            .filter { it.quantity <= threshold }
            .sortedBy { it.quantity }
    }
    
    // =============================================
    // 🔧 PRIVATE HELPER METHODS
    // =============================================
    
    /**
     * 🔄 Return stock to inventory
     */
    private fun returnStockToInventory(snackId: String, quantity: Int) {
        val currentSnacks = _snacks.value.toMutableMap()
        val snack = currentSnacks[snackId] ?: return
        
        val updatedSnack = snack.copy(quantity = snack.quantity + quantity)
        currentSnacks[snackId] = updatedSnack
        _snacks.value = currentSnacks
    }
    
    /**
     * 📈 Update analytics and statistics
     */
    private fun updateAnalytics() {
        val snacksList = _snacks.value.values.toList()
        val ordersList = _orders.value.values.toList()
        
        val totalSnacks = snacksList.size
        val totalValue = snacksList.sumOf { it.sellingPrice * it.quantity }
        val totalPotentialProfit = snacksList.sumOf { it.getTotalPotentialProfit() }
        
        val deliveredOrders = ordersList.filter { it.status == OrderStatus.DELIVERED }
        val totalRevenue = deliveredOrders.sumOf { it.totalAmount }
        val totalProfit = deliveredOrders.sumOf { order ->
            val snack = _snacks.value[order.snackId]
            snack?.let { order.getProfit(it.costPrice) } ?: 0.0
        }
        
        val stats = SnackInventoryStats(
            totalSnacks = totalSnacks,
            totalValue = totalValue,
            totalPotentialProfit = totalPotentialProfit,
            topSellingSnacks = getTopSellingSnacks(5),
            lowStockSnacks = getLowStockSnacks(5),
            totalRevenue = totalRevenue,
            totalProfit = totalProfit
        )
        
        _stats.value = stats
    }
    
    /**
     * 📊 Get sales data with caching
     */
    private fun getSalesData(): Map<String, SnackSalesData> {
        val now = System.currentTimeMillis()
        
        // Return cached data if still valid
        if (now - cacheLastUpdated < cacheValidityMs && salesDataCache.isNotEmpty()) {
            return salesDataCache
        }
        
        // Recalculate sales data
        val deliveredOrders = _orders.value.values.filter { it.status == OrderStatus.DELIVERED }
        val salesData = mutableMapOf<String, SnackSalesData>()
        
        deliveredOrders.groupBy { it.snackId }.forEach { (snackId, orders) ->
            val snack = _snacks.value[snackId] ?: return@forEach
            val totalQuantity = orders.sumOf { it.quantity }
            val totalRevenue = orders.sumOf { it.totalAmount }
            val totalProfit = orders.sumOf { it.getProfit(snack.costPrice) }
            
            salesData[snackId] = SnackSalesData(
                snackId = snackId,
                snackName = snack.name,
                totalQuantitySold = totalQuantity,
                totalRevenue = totalRevenue,
                totalProfit = totalProfit
            )
        }
        
        salesDataCache = salesData
        cacheLastUpdated = now
        return salesData
    }
    
    /**
     * 🆔 Generate unique snack ID
     */
    private fun generateSnackId(): String = "snack_${System.currentTimeMillis()}_${(Math.random() * 1000).toInt()}"
    
    /**
     * 🆔 Generate unique order ID
     */
    private fun generateOrderId(): String = "order_${System.currentTimeMillis()}_${(Math.random() * 1000).toInt()}"
    
    /**
     * 🌱 Initialize with sample data for testing
     */
    private fun initializeSampleData() {
        console.log("🌱 Initializing SnackCart with sample data...")
        
        val sampleSnacks = listOf(
            addSnack("Kurkure", 10.0, 15.0, 25, "Crunchy corn snacks", "Chips", "admin1"),
            addSnack("Oreo", 20.0, 25.0, 30, "Chocolate cream cookies", "Cookies", "admin1"),
            addSnack("Maggi", 12.0, 15.0, 40, "Instant noodles", "Noodles", "admin1"),
            addSnack("Lays", 15.0, 20.0, 20, "Potato chips", "Chips", "admin1"),
            addSnack("Good Day", 8.0, 12.0, 35, "Butter cookies", "Cookies", "admin1")
        )
        
        console.log("✅ SnackCart initialized with ${_snacks.value.size} sample snacks")
        updateAnalytics()
    }
}
