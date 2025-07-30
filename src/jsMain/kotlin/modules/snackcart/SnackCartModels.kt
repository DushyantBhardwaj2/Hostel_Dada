package modules.snackcart

import kotlinx.serialization.Serializable
import kotlin.math.min

/**
 * 🍿 SnackCart Module - Data Structures
 * 
 * Advanced inventory management with DSA optimization:
 * - Priority Queue for order processing
 * - HashMap for O(1) inventory lookups
 * - Binary Search for price optimization
 * - LRU Cache for popular items
 */

/**
 * 🥤 Snack Item Data Model
 */
@Serializable
data class SnackItem(
    val id: String,
    val name: String,
    val category: SnackCategory,
    val price: Double,
    val description: String,
    val imageUrl: String? = null,
    val nutritionalInfo: NutritionalInfo? = null,
    val tags: List<String> = emptyList(),
    val isVegetarian: Boolean = true,
    val isVegan: Boolean = false,
    val allergens: List<String> = emptyList(),
    val preparationTime: Int = 0, // minutes
    val popularity: Int = 0, // popularity score
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): SnackItem {
            return SnackItem(
                id = map["id"] as String,
                name = map["name"] as String,
                category = SnackCategory.valueOf(map["category"] as String),
                price = (map["price"] as Number).toDouble(),
                description = map["description"] as String,
                imageUrl = map["imageUrl"] as? String,
                nutritionalInfo = (map["nutritionalInfo"] as? Map<String, Any>)?.let { 
                    NutritionalInfo.fromMap(it) 
                },
                tags = (map["tags"] as? List<String>) ?: emptyList(),
                isVegetarian = map["isVegetarian"] as? Boolean ?: true,
                isVegan = map["isVegan"] as? Boolean ?: false,
                allergens = (map["allergens"] as? List<String>) ?: emptyList(),
                preparationTime = (map["preparationTime"] as? Number)?.toInt() ?: 0,
                popularity = (map["popularity"] as? Number)?.toInt() ?: 0,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("id", id)
            put("name", name)
            put("category", category.name)
            put("price", price)
            put("description", description)
            imageUrl?.let { put("imageUrl", it) }
            nutritionalInfo?.let { put("nutritionalInfo", it.toMap()) }
            put("tags", tags)
            put("isVegetarian", isVegetarian)
            put("isVegan", isVegan)
            put("allergens", allergens)
            put("preparationTime", preparationTime)
            put("popularity", popularity)
            put("createdAt", createdAt)
        }
    }
}

/**
 * 🏷️ Snack Categories
 */
enum class SnackCategory {
    BEVERAGES,      // Tea, Coffee, Juices
    SNACKS,         // Chips, Biscuits, Nuts
    INSTANT_FOOD,   // Noodles, Ready meals
    FRUITS,         // Fresh fruits
    DAIRY,          // Milk, Yogurt, Cheese
    SWEETS,         // Chocolates, Candies
    HEALTHY         // Protein bars, Nuts
}

/**
 * 🥗 Nutritional Information
 */
@Serializable
data class NutritionalInfo(
    val calories: Int,
    val protein: Double,     // grams
    val carbs: Double,       // grams
    val fat: Double,         // grams
    val fiber: Double,       // grams
    val sugar: Double,       // grams
    val sodium: Double       // mg
) {
    companion object {
        fun fromMap(map: Map<String, Any>): NutritionalInfo {
            return NutritionalInfo(
                calories = (map["calories"] as Number).toInt(),
                protein = (map["protein"] as Number).toDouble(),
                carbs = (map["carbs"] as Number).toDouble(),
                fat = (map["fat"] as Number).toDouble(),
                fiber = (map["fiber"] as Number).toDouble(),
                sugar = (map["sugar"] as Number).toDouble(),
                sodium = (map["sodium"] as Number).toDouble()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "calories" to calories,
            "protein" to protein,
            "carbs" to carbs,
            "fat" to fat,
            "fiber" to fiber,
            "sugar" to sugar,
            "sodium" to sodium
        )
    }
}

/**
 * 📦 Inventory Item with Stock Management
 */
@Serializable
data class InventoryItem(
    val snackId: String,
    val currentStock: Int,
    val minStock: Int,
    val maxStock: Int,
    val reorderPoint: Int,
    val lastRestocked: Long,
    val expiryDate: Long? = null,
    val supplier: String? = null,
    val costPrice: Double,
    val profitMargin: Double,
    val location: String? = null // Storage location
) {
    companion object {
        fun fromMap(map: Map<String, Any>): InventoryItem {
            return InventoryItem(
                snackId = map["snackId"] as String,
                currentStock = (map["currentStock"] as Number).toInt(),
                minStock = (map["minStock"] as Number).toInt(),
                maxStock = (map["maxStock"] as Number).toInt(),
                reorderPoint = (map["reorderPoint"] as Number).toInt(),
                lastRestocked = (map["lastRestocked"] as Number).toLong(),
                expiryDate = (map["expiryDate"] as? Number)?.toLong(),
                supplier = map["supplier"] as? String,
                costPrice = (map["costPrice"] as Number).toDouble(),
                profitMargin = (map["profitMargin"] as Number).toDouble(),
                location = map["location"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("snackId", snackId)
            put("currentStock", currentStock)
            put("minStock", minStock)
            put("maxStock", maxStock)
            put("reorderPoint", reorderPoint)
            put("lastRestocked", lastRestocked)
            expiryDate?.let { put("expiryDate", it) }
            supplier?.let { put("supplier", it) }
            put("costPrice", costPrice)
            put("profitMargin", profitMargin)
            location?.let { put("location", it) }
        }
    }
    
    fun isLowStock(): Boolean = currentStock <= reorderPoint
    fun isOutOfStock(): Boolean = currentStock <= 0
    fun needsReorder(): Boolean = currentStock <= minStock
    fun isExpired(): Boolean = expiryDate?.let { it < System.currentTimeMillis() } ?: false
    fun getSellingPrice(): Double = costPrice * (1 + profitMargin)
}

/**
 * 🛒 Order Item
 */
@Serializable
data class OrderItem(
    val snackId: String,
    val quantity: Int,
    val unitPrice: Double,
    val specialInstructions: String? = null
) {
    fun getTotalPrice(): Double = quantity * unitPrice
    
    companion object {
        fun fromMap(map: Map<String, Any>): OrderItem {
            return OrderItem(
                snackId = map["snackId"] as String,
                quantity = (map["quantity"] as Number).toInt(),
                unitPrice = (map["unitPrice"] as Number).toDouble(),
                specialInstructions = map["specialInstructions"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("snackId", snackId)
            put("quantity", quantity)
            put("unitPrice", unitPrice)
            specialInstructions?.let { put("specialInstructions", it) }
        }
    }
}

/**
 * 📋 Snack Order
 */
@Serializable
data class SnackOrder(
    val id: String,
    val userId: String,
    val hostelId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalAmount: Double,
    val deliveryLocation: String,
    val phoneNumber: String,
    val specialNotes: String? = null,
    val estimatedDeliveryTime: Long? = null,
    val actualDeliveryTime: Long? = null,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus,
    val orderTime: Long,
    val acceptedTime: Long? = null,
    val preparedTime: Long? = null,
    val deliveredTime: Long? = null,
    val rating: Int? = null,
    val feedback: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): SnackOrder {
            return SnackOrder(
                id = map["id"] as String,
                userId = map["userId"] as String,
                hostelId = map["hostelId"] as String,
                items = (map["items"] as List<Map<String, Any>>).map { OrderItem.fromMap(it) },
                status = OrderStatus.valueOf(map["status"] as String),
                totalAmount = (map["totalAmount"] as Number).toDouble(),
                deliveryLocation = map["deliveryLocation"] as String,
                phoneNumber = map["phoneNumber"] as String,
                specialNotes = map["specialNotes"] as? String,
                estimatedDeliveryTime = (map["estimatedDeliveryTime"] as? Number)?.toLong(),
                actualDeliveryTime = (map["actualDeliveryTime"] as? Number)?.toLong(),
                paymentMethod = PaymentMethod.valueOf(map["paymentMethod"] as String),
                paymentStatus = PaymentStatus.valueOf(map["paymentStatus"] as String),
                orderTime = (map["orderTime"] as Number).toLong(),
                acceptedTime = (map["acceptedTime"] as? Number)?.toLong(),
                preparedTime = (map["preparedTime"] as? Number)?.toLong(),
                deliveredTime = (map["deliveredTime"] as? Number)?.toLong(),
                rating = (map["rating"] as? Number)?.toInt(),
                feedback = map["feedback"] as? String
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("id", id)
            put("userId", userId)
            put("hostelId", hostelId)
            put("items", items.map { it.toMap() })
            put("status", status.name)
            put("totalAmount", totalAmount)
            put("deliveryLocation", deliveryLocation)
            put("phoneNumber", phoneNumber)
            specialNotes?.let { put("specialNotes", it) }
            estimatedDeliveryTime?.let { put("estimatedDeliveryTime", it) }
            actualDeliveryTime?.let { put("actualDeliveryTime", it) }
            put("paymentMethod", paymentMethod.name)
            put("paymentStatus", paymentStatus.name)
            put("orderTime", orderTime)
            acceptedTime?.let { put("acceptedTime", it) }
            preparedTime?.let { put("preparedTime", it) }
            deliveredTime?.let { put("deliveredTime", it) }
            rating?.let { put("rating", it) }
            feedback?.let { put("feedback", it) }
        }
    }
    
    fun getDeliveryTimeInMinutes(): Int? {
        return if (deliveredTime != null && orderTime != null) {
            ((deliveredTime - orderTime) / (1000 * 60)).toInt()
        } else null
    }
}

/**
 * 📊 Order Status Enum
 */
enum class OrderStatus {
    PENDING,        // Order placed, waiting for confirmation
    CONFIRMED,      // Order confirmed by vendor
    PREPARING,      // Order being prepared
    READY,          // Order ready for pickup/delivery
    OUT_FOR_DELIVERY, // Order out for delivery
    DELIVERED,      // Order delivered successfully
    CANCELLED,      // Order cancelled
    REFUNDED        // Order refunded
}

/**
 * 💳 Payment Method Enum
 */
enum class PaymentMethod {
    CASH,           // Cash on delivery
    UPI,            // UPI payment
    CARD,           // Credit/Debit card
    WALLET,         // Digital wallet
    HOSTEL_CREDIT   // Hostel credit system
}

/**
 * 💰 Payment Status Enum
 */
enum class PaymentStatus {
    PENDING,        // Payment not yet made
    PAID,           // Payment completed
    FAILED,         // Payment failed
    REFUNDED        // Payment refunded
}
