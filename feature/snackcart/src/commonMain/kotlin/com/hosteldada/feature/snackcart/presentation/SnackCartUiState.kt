package com.hosteldada.feature.snackcart.presentation

import com.hosteldada.core.domain.model.*

/**
 * ============================================
 * SNACKCART UI STATES
 * ============================================
 */

// ==========================================
// USER UI STATE
// ==========================================

data class SnackCartUiState(
    val isLoading: Boolean = false,
    val selectedTab: SnackCartTab = SnackCartTab.MENU,
    
    // Menu
    val snacks: List<Snack> = emptyList(),
    val filteredSnacks: List<Snack> = emptyList(),
    val selectedCategory: SnackCategory? = null,
    val searchQuery: String = "",
    val searchResults: List<Snack> = emptyList(),
    
    // Cart
    val cart: Cart = Cart(),
    
    // Orders
    val orders: List<SnackOrder> = emptyList(),
    val activeOrder: SnackOrder? = null,
    
    // Checkout
    val showCheckout: Boolean = false,
    val deliveryLocation: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val orderNotes: String = "",
    
    // Status
    val error: String? = null,
    val successMessage: String? = null
)

enum class SnackCartTab {
    MENU, CART, ORDERS
}

// ==========================================
// ADMIN UI STATE
// ==========================================

data class SnackCartAdminUiState(
    val isLoading: Boolean = false,
    val selectedTab: AdminTab = AdminTab.ORDERS,
    
    // Orders management
    val orders: List<SnackOrder> = emptyList(),
    val pendingOrders: List<SnackOrder> = emptyList(),
    val selectedOrder: SnackOrder? = null,
    
    // Inventory management
    val snacks: List<Snack> = emptyList(),
    val lowStockSnacks: List<Snack> = emptyList(),
    val editingSnack: Snack? = null,
    val showAddSnackDialog: Boolean = false,
    
    // Statistics
    val stats: AdminStats = AdminStats(),
    
    // Status
    val error: String? = null,
    val successMessage: String? = null
)

enum class AdminTab {
    ORDERS, INVENTORY, STATS
}

data class AdminStats(
    val totalOrders: Int = 0,
    val pendingOrders: Int = 0,
    val completedOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val todayOrders: Int = 0,
    val todayRevenue: Double = 0.0
)

// ==========================================
// USER INTENTS
// ==========================================

sealed interface SnackCartIntent {
    // Navigation
    data class SelectTab(val tab: SnackCartTab) : SnackCartIntent
    
    // Menu
    data class SelectCategory(val category: SnackCategory?) : SnackCartIntent
    data class SearchSnacks(val query: String) : SnackCartIntent
    object ClearSearch : SnackCartIntent
    
    // Cart
    data class AddToCart(val snack: Snack) : SnackCartIntent
    data class UpdateQuantity(val snackId: String, val quantity: Int) : SnackCartIntent
    data class RemoveFromCart(val snackId: String) : SnackCartIntent
    object ClearCart : SnackCartIntent
    
    // Checkout
    object ShowCheckout : SnackCartIntent
    object HideCheckout : SnackCartIntent
    data class UpdateDeliveryLocation(val location: String) : SnackCartIntent
    data class UpdatePaymentMethod(val method: PaymentMethod) : SnackCartIntent
    data class UpdateOrderNotes(val notes: String) : SnackCartIntent
    object PlaceOrder : SnackCartIntent
    
    // Orders
    data class CancelOrder(val orderId: String) : SnackCartIntent
    data class ViewOrderDetails(val order: SnackOrder) : SnackCartIntent
    
    // Error handling
    object ClearError : SnackCartIntent
    object ClearSuccess : SnackCartIntent
}

// ==========================================
// ADMIN INTENTS
// ==========================================

sealed interface SnackCartAdminIntent {
    // Navigation
    data class SelectTab(val tab: AdminTab) : SnackCartAdminIntent
    
    // Orders
    data class SelectOrder(val order: SnackOrder) : SnackCartAdminIntent
    data class UpdateOrderStatus(val orderId: String, val status: OrderStatus) : SnackCartAdminIntent
    object RefreshOrders : SnackCartAdminIntent
    
    // Inventory
    object ShowAddSnack : SnackCartAdminIntent
    object HideAddSnack : SnackCartAdminIntent
    data class EditSnack(val snack: Snack) : SnackCartAdminIntent
    data class SaveSnack(val snack: Snack) : SnackCartAdminIntent
    data class DeleteSnack(val snackId: String) : SnackCartAdminIntent
    data class UpdateStock(val snackId: String, val quantity: Int) : SnackCartAdminIntent
    
    // Error handling
    object ClearError : SnackCartAdminIntent
    object ClearSuccess : SnackCartAdminIntent
}
