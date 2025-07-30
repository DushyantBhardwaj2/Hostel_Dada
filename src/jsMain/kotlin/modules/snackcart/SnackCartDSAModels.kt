package modules.snackcart

import kotlinx.serialization.Serializable

/**
 * 🍿 SnackCart DSA-Focused Data Models
 * 
 * Simple yet powerful models focusing on:
 * - Hash Maps for O(1) inventory lookup
 * - Trie for efficient name searching
 * - Sorting algorithms for profit/popularity analysis
 * - Real-time inventory management
 */

@Serializable
data class Snack(
    val id: String,
    val name: String,
    val costPrice: Double,      // CP - What admin pays to buy it
    val sellingPrice: Double,   // SP - What users pay to buy it
    val quantity: Int,
    val description: String = "",
    val category: String = "General",
    val addedAt: Long = System.currentTimeMillis(),
    val addedBy: String,        // Admin who added this snack
    val isActive: Boolean = true
) {
    /**
     * Calculate profit per unit
     */
    fun getProfitPerUnit(): Double = sellingPrice - costPrice
    
    /**
     * Calculate total profit for current stock
     */
    fun getTotalPotentialProfit(): Double = getProfitPerUnit() * quantity
    
    /**
     * Check if item is in stock
     */
    fun isInStock(): Boolean = quantity > 0 && isActive
}

@Serializable
data class SnackOrder(
    val orderId: String,
    val userId: String,
    val userEmail: String,
    val snackId: String,
    val snackName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalAmount: Double,
    val status: OrderStatus,
    val orderedAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null,
    val cancelledAt: Long? = null,
    val adminNotes: String = ""
) {
    /**
     * Calculate profit from this order
     */
    fun getProfit(snackCostPrice: Double): Double {
        return if (status == OrderStatus.DELIVERED) {
            (unitPrice - snackCostPrice) * quantity
        } else 0.0
    }
}

@Serializable
enum class OrderStatus {
    RESERVED,      // User has reserved but not collected
    DELIVERED,     // User has collected and paid (COD)
    CANCELLED      // Order was cancelled, stock returned
}

@Serializable
data class SnackInventoryStats(
    val totalSnacks: Int,
    val totalValue: Double,
    val totalPotentialProfit: Double,
    val topSellingSnacks: List<SnackSalesData>,
    val lowStockSnacks: List<Snack>,
    val totalRevenue: Double,
    val totalProfit: Double
)

@Serializable
data class SnackSalesData(
    val snackId: String,
    val snackName: String,
    val totalQuantitySold: Int,
    val totalRevenue: Double,
    val totalProfit: Double
)

/**
 * 🔍 Trie Node for efficient snack name searching
 */
class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
    var isEndOfWord = false
    val snackIds = mutableSetOf<String>()
}

/**
 * 🔍 Trie data structure for snack name searching
 * Provides O(m) search time where m is the length of search term
 */
class SnackSearchTrie {
    private val root = TrieNode()
    
    /**
     * Insert snack name into trie
     */
    fun insert(snackName: String, snackId: String) {
        var current = root
        val normalizedName = snackName.lowercase().trim()
        
        for (char in normalizedName) {
            current = current.children.getOrPut(char) { TrieNode() }
            current.snackIds.add(snackId)
        }
        current.isEndOfWord = true
    }
    
    /**
     * Search for snacks by prefix
     */
    fun searchByPrefix(prefix: String): Set<String> {
        var current = root
        val normalizedPrefix = prefix.lowercase().trim()
        
        // Navigate to the prefix node
        for (char in normalizedPrefix) {
            current = current.children[char] ?: return emptySet()
        }
        
        // Collect all snack IDs under this prefix
        return current.snackIds
    }
    
    /**
     * Get autocomplete suggestions
     */
    fun getAutocompleteSuggestions(prefix: String, maxSuggestions: Int = 5): List<String> {
        val snackIds = searchByPrefix(prefix)
        return snackIds.take(maxSuggestions).toList()
    }
    
    /**
     * Remove snack from trie
     */
    fun remove(snackName: String, snackId: String) {
        val normalizedName = snackName.lowercase().trim()
        removeHelper(root, normalizedName, snackId, 0)
    }
    
    private fun removeHelper(node: TrieNode, name: String, snackId: String, index: Int): Boolean {
        if (index == name.length) {
            node.snackIds.remove(snackId)
            return node.snackIds.isEmpty() && node.children.isEmpty()
        }
        
        val char = name[index]
        val childNode = node.children[char] ?: return false
        
        val shouldDeleteChild = removeHelper(childNode, name, snackId, index + 1)
        
        if (shouldDeleteChild) {
            node.children.remove(char)
        }
        
        return node.children.isEmpty() && node.snackIds.isEmpty()
    }
}
