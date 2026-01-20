package com.hosteldada.feature.snackcart.domain

import com.hosteldada.core.common.result.Result
import com.hosteldada.core.domain.model.*
import com.hosteldada.core.domain.repository.*
import kotlinx.coroutines.flow.Flow

/**
 * ============================================
 * SNACKCART USE CASES
 * ============================================
 */

// ==========================================
// SNACK OPERATIONS
// ==========================================

class GetAllSnacksUseCase(
    private val snackRepository: SnackRepository
) {
    suspend operator fun invoke(): Result<List<Snack>> {
        return snackRepository.getAllSnacks()
    }
}

class GetSnacksByCategoryUseCase(
    private val snackRepository: SnackRepository
) {
    suspend operator fun invoke(category: SnackCategory): Result<List<Snack>> {
        return snackRepository.getSnacksByCategory(category)
    }
}

class SearchSnacksUseCase(
    private val snackRepository: SnackRepository
) {
    /**
     * Search snacks using Trie-based search.
     * Time: O(k) where k is query length.
     */
    suspend operator fun invoke(query: String): Result<List<Snack>> {
        if (query.isBlank()) {
            return snackRepository.getAllSnacks()
        }
        return snackRepository.searchSnacks(query)
    }
}

class ObserveSnacksUseCase(
    private val snackRepository: SnackRepository
) {
    operator fun invoke(): Flow<List<Snack>> {
        return snackRepository.observeSnacks()
    }
}

// ==========================================
// CART OPERATIONS
// ==========================================

class GetCartUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(userId: String): Result<Cart> {
        return cartRepository.getCart(userId)
    }
}

class AddToCartUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(userId: String, snack: Snack, quantity: Int = 1): Result<Unit> {
        if (quantity <= 0) {
            return Result.Error(IllegalArgumentException("Quantity must be positive"))
        }
        if (!snack.isAvailable) {
            return Result.Error(IllegalStateException("${snack.name} is not available"))
        }
        if (snack.stockQuantity < quantity) {
            return Result.Error(IllegalStateException("Not enough stock available"))
        }
        return cartRepository.addToCart(userId, snack, quantity)
    }
}

class UpdateCartQuantityUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(userId: String, snackId: String, quantity: Int): Result<Unit> {
        return if (quantity <= 0) {
            cartRepository.removeFromCart(userId, snackId)
        } else {
            cartRepository.updateQuantity(userId, snackId, quantity)
        }
    }
}

class RemoveFromCartUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(userId: String, snackId: String): Result<Unit> {
        return cartRepository.removeFromCart(userId, snackId)
    }
}

class ClearCartUseCase(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return cartRepository.clearCart(userId)
    }
}

class ObserveCartUseCase(
    private val cartRepository: CartRepository
) {
    operator fun invoke(userId: String): Flow<Cart> {
        return cartRepository.observeCart(userId)
    }
}

// ==========================================
// ORDER OPERATIONS
// ==========================================

class PlaceOrderUseCase(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(
        userId: String,
        userEmail: String,
        userName: String,
        deliveryLocation: String,
        paymentMethod: PaymentMethod = PaymentMethod.CASH,
        notes: String = ""
    ): Result<String> {
        // Get current cart
        val cartResult = cartRepository.getCart(userId)
        val cart = when (cartResult) {
            is Result.Success -> cartResult.data
            is Result.Error -> return cartResult
            is Result.Loading -> return Result.Loading
        }
        
        if (cart.isEmpty) {
            return Result.Error(IllegalStateException("Cart is empty"))
        }
        
        if (deliveryLocation.isBlank()) {
            return Result.Error(IllegalArgumentException("Delivery location is required"))
        }
        
        // Create order
        val order = SnackOrder(
            userId = userId,
            userEmail = userEmail,
            userName = userName,
            items = cart.items,
            totalAmount = cart.totalAmount,
            status = OrderStatus.PENDING,
            paymentMethod = paymentMethod,
            deliveryLocation = deliveryLocation,
            notes = notes,
            estimatedDelivery = System.currentTimeMillis() + (20 * 60 * 1000) // 20 mins
        )
        
        // Place order
        val result = orderRepository.placeOrder(order)
        
        // Clear cart on success
        if (result is Result.Success) {
            cartRepository.clearCart(userId)
        }
        
        return result
    }
}

class GetUserOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(userId: String): Result<List<SnackOrder>> {
        return orderRepository.getOrders(userId)
    }
}

class CancelOrderUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String): Result<Unit> {
        return orderRepository.cancelOrder(orderId)
    }
}

class ObserveUserOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(userId: String): Flow<List<SnackOrder>> {
        return orderRepository.observeOrders(userId)
    }
}

// ==========================================
// ADMIN OPERATIONS
// ==========================================

class AddSnackUseCase(
    private val snackRepository: SnackRepository
) {
    suspend operator fun invoke(snack: Snack): Result<String> {
        if (snack.name.isBlank()) {
            return Result.Error(IllegalArgumentException("Snack name is required"))
        }
        if (snack.price <= 0) {
            return Result.Error(IllegalArgumentException("Price must be positive"))
        }
        return snackRepository.addSnack(snack)
    }
}

class UpdateSnackUseCase(
    private val snackRepository: SnackRepository
) {
    suspend operator fun invoke(snack: Snack): Result<Unit> {
        return snackRepository.updateSnack(snack)
    }
}

class DeleteSnackUseCase(
    private val snackRepository: SnackRepository
) {
    suspend operator fun invoke(snackId: String): Result<Unit> {
        return snackRepository.deleteSnack(snackId)
    }
}

class UpdateStockUseCase(
    private val snackRepository: SnackRepository
) {
    suspend operator fun invoke(snackId: String, quantity: Int): Result<Unit> {
        if (quantity < 0) {
            return Result.Error(IllegalArgumentException("Stock cannot be negative"))
        }
        return snackRepository.updateStock(snackId, quantity)
    }
}

class GetAllOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): Result<List<SnackOrder>> {
        return orderRepository.getAllOrders()
    }
}

class UpdateOrderStatusUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String, status: OrderStatus): Result<Unit> {
        return orderRepository.updateOrderStatus(orderId, status)
    }
}

class GetSnackStatsUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): Result<SnackStats> {
        return when (val result = orderRepository.getAllOrders()) {
            is Result.Success -> {
                val orders = result.data
                val completed = orders.filter { it.status == OrderStatus.DELIVERED }
                
                // Calculate stats
                val totalRevenue = completed.sumOf { it.totalAmount }
                val snackCounts = mutableMapOf<String, Int>()
                
                completed.forEach { order ->
                    order.items.forEach { item ->
                        snackCounts[item.snackId] = (snackCounts[item.snackId] ?: 0) + item.quantity
                    }
                }
                
                val topSellers = snackCounts.entries
                    .sortedByDescending { it.value }
                    .take(5)
                    .map { TopSellerInfo(it.key, it.value) }
                
                Result.Success(SnackStats(
                    totalOrders = orders.size,
                    completedOrders = completed.size,
                    totalRevenue = totalRevenue,
                    topSellers = topSellers
                ))
            }
            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}

data class SnackStats(
    val totalOrders: Int,
    val completedOrders: Int,
    val totalRevenue: Double,
    val topSellers: List<TopSellerInfo>
)

data class TopSellerInfo(
    val snackId: String,
    val orderCount: Int
)
