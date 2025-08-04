import { 
  ref,
  get,
  set,
  update,
  remove,
  push,
  query,
  orderByChild,
  equalTo,
  onValue,
  off,
  DataSnapshot
} from 'firebase/database'
import { realtimeDb } from './firebase'
import { 
  Snack, 
  SnackOrder, 
  SnackOrderItem,
  SnackStats,
  SnackSearchTrie, 
  SnackInventory 
} from './snackcart-models'

export class SnackCartService {
  private inventory: SnackInventory
  private snacksRef = ref(realtimeDb, 'snacks')
  private ordersRef = ref(realtimeDb, 'snackOrders')

  constructor() {
    this.inventory = new SnackInventory()
  }

  // Initialize inventory from Firebase
  async initializeInventory(): Promise<void> {
    try {
      const snapshot = await get(this.snacksRef)
      if (snapshot.exists()) {
        const snacksData = snapshot.val()
        Object.keys(snacksData).forEach(snackId => {
          const data = snacksData[snackId]
          const snack: Snack = {
            id: snackId,
            name: data.name,
            description: data.description,
            costPrice: data.costPrice,
            sellingPrice: data.sellingPrice,
            quantity: data.quantity,
            category: data.category,
            imageUrl: data.imageUrl,
            createdAt: data.createdAt ? new Date(data.createdAt) : new Date(),
            updatedAt: data.updatedAt ? new Date(data.updatedAt) : new Date()
          }
          this.inventory.addSnack(snack)
        })
      }
    } catch (error) {
      console.error('Error initializing inventory:', error)
      throw error
    }
  }

  // Real-time inventory updates
  subscribeToInventoryUpdates(callback: (snacks: Snack[]) => void): () => void {
    const unsubscribe = onValue(this.snacksRef, (snapshot) => {
      // Clear and rebuild inventory
      this.inventory = new SnackInventory()
      
      if (snapshot.exists()) {
        const snacksData = snapshot.val()
        Object.keys(snacksData).forEach(snackId => {
          const data = snacksData[snackId]
          const snack: Snack = {
            id: snackId,
            name: data.name,
            description: data.description,
            costPrice: data.costPrice,
            sellingPrice: data.sellingPrice,
            quantity: data.quantity,
            category: data.category,
            imageUrl: data.imageUrl,
            createdAt: data.createdAt ? new Date(data.createdAt) : new Date(),
            updatedAt: data.updatedAt ? new Date(data.updatedAt) : new Date()
          }
          this.inventory.addSnack(snack)
        })
      }

      callback(this.inventory.getAvailableSnacks())
    })

    return () => off(this.snacksRef, 'value', unsubscribe)
  }

  // Add new snack (Admin only)
  async addSnack(snackData: Omit<Snack, 'id' | 'createdAt' | 'updatedAt'>): Promise<string> {
    try {
      const newSnack = {
        ...snackData,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }

      const newSnackRef = push(this.snacksRef)
      await set(newSnackRef, newSnack)
      
      // Add to local inventory
      const snack: Snack = {
        ...snackData,
        id: newSnackRef.key!,
        createdAt: new Date(),
        updatedAt: new Date()
      }
      this.inventory.addSnack(snack)

      return newSnackRef.key!
    } catch (error) {
      console.error('Error adding snack:', error)
      throw error
    }
  }

  // Update snack (Admin only)
  async updateSnack(id: string, updates: Partial<Snack>): Promise<void> {
    try {
      const snackRef = ref(realtimeDb, `snacks/${id}`)
      const updateData = {
        ...updates,
        updatedAt: new Date().toISOString()
      }
      
      await update(snackRef, updateData)
      
      // Update local inventory
      const currentSnack = this.inventory.getSnack(id)
      if (currentSnack) {
        const updatedSnack = { ...currentSnack, ...updates, updatedAt: new Date() }
        this.inventory.removeSnack(id)
        this.inventory.addSnack(updatedSnack)
      }
    } catch (error) {
      console.error('Error updating snack:', error)
      throw error
    }
  }

  // Delete snack (Admin only)
  async deleteSnack(id: string): Promise<void> {
    try {
      const snackRef = ref(realtimeDb, `snacks/${id}`)
      await remove(snackRef)
      
      // Remove from local inventory
      this.inventory.removeSnack(id)
    } catch (error) {
      console.error('Error deleting snack:', error)
      throw error
    }
  }

  // Search snacks using Trie
  searchSnacks(query: string): Snack[] {
    if (!query.trim()) {
      return this.inventory.getAvailableSnacks()
    }
    return this.inventory.searchSnacks(query)
  }

  // Get snacks by category
  getSnacksByCategory(category: string): Snack[] {
    return this.inventory.getSnacksByCategory(category)
  }

  // Get all available snacks
  getAvailableSnacks(): Snack[] {
    return this.inventory.getAvailableSnacks()
  }

  // Reserve snack (User)
  async reserveSnack(userId: string, snackId: string, quantity: number): Promise<string> {
    try {
      const snack = this.inventory.getSnack(snackId)
      if (!snack) {
        throw new Error('Snack not found')
      }

      if (snack.quantity < quantity) {
        throw new Error('Insufficient quantity available')
      }

      const totalAmount = snack.sellingPrice * quantity
      
      // Create order items array
      const orderItems: SnackOrderItem[] = [{
        snackId: snack.id,
        snackName: snack.name,
        quantity: quantity,
        unitPrice: snack.sellingPrice,
        totalPrice: totalAmount
      }]

      const orderData = {
        userId,
        snackId, // Keep for backward compatibility
        quantity, // Keep for backward compatibility
        items: orderItems,
        totalAmount,
        status: 'pending' as const,
        paymentMethod: 'cod' as const,
        createdAt: new Date().toISOString()
      }

      const newOrderRef = push(this.ordersRef)
      await set(newOrderRef, orderData)

      // Update inventory
      await this.updateSnack(snackId, { 
        quantity: snack.quantity - quantity 
      })

      return newOrderRef.key!
    } catch (error) {
      console.error('Error reserving snack:', error)
      throw error
    }
  }

  // Cancel reservation (User/Admin)
  async cancelReservation(orderId: string): Promise<void> {
    try {
      const orderRef = ref(realtimeDb, `snackOrders/${orderId}`)
      
      // Get order details first
      const orderSnapshot = await get(orderRef)
      
      if (!orderSnapshot.exists()) {
        throw new Error('Order not found')
      }

      const orderData = orderSnapshot.val()
      const snack = this.inventory.getSnack(orderData.snackId)
      
      if (snack) {
        // Return quantity to inventory
        await this.updateSnack(orderData.snackId, {
          quantity: snack.quantity + orderData.quantity
        })
      }

      // Update order status
      await update(orderRef, {
        status: 'cancelled',
        updatedAt: new Date().toISOString()
      })
    } catch (error) {
      console.error('Error cancelling reservation:', error)
      throw error
    }
  }

  // Mark order as delivered (Admin)
  async markAsDelivered(orderId: string): Promise<void> {
    try {
      const orderRef = ref(realtimeDb, `snackOrders/${orderId}`)
      await update(orderRef, {
        status: 'delivered',
        deliveredAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      })

      // Update inventory stats for delivered order
      const orderSnapshot = await get(orderRef)
      
      if (orderSnapshot.exists()) {
        const orderData = orderSnapshot.val()
        this.inventory.updateInventory(orderData.snackId, orderData.quantity)
      }
    } catch (error) {
      console.error('Error marking order as delivered:', error)
      throw error
    }
  }

  // Get user orders
  async getUserOrders(userId: string): Promise<SnackOrder[]> {
    try {
      const snapshot = await get(this.ordersRef)
      if (!snapshot.exists()) {
        return []
      }

      const ordersData = snapshot.val()
      const userOrders: SnackOrder[] = []

      Object.keys(ordersData).forEach(orderId => {
        const orderData = ordersData[orderId]
        if (orderData.userId === userId) {
          userOrders.push({
            id: orderId,
            ...orderData,
            createdAt: orderData.createdAt ? new Date(orderData.createdAt) : new Date(),
            deliveredAt: orderData.deliveredAt ? new Date(orderData.deliveredAt) : undefined
          } as SnackOrder)
        }
      })

      // Sort by createdAt desc
      return userOrders.sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime())
    } catch (error) {
      console.error('Error getting user orders:', error)
      throw error
    }
  }

  // Get all orders (Admin)
  async getAllOrders(): Promise<SnackOrder[]> {
    try {
      const snapshot = await get(this.ordersRef)
      if (!snapshot.exists()) {
        return []
      }

      const ordersData = snapshot.val()
      const allOrders: SnackOrder[] = []

      Object.keys(ordersData).forEach(orderId => {
        const orderData = ordersData[orderId]
        allOrders.push({
          id: orderId,
          ...orderData,
          createdAt: orderData.createdAt ? new Date(orderData.createdAt) : new Date(),
          deliveredAt: orderData.deliveredAt ? new Date(orderData.deliveredAt) : undefined
        } as SnackOrder)
      })

      // Sort by createdAt desc
      return allOrders.sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime())
    } catch (error) {
      console.error('Error getting all orders:', error)
      throw error
    }
  }

  // Get top selling snacks (using merge sort)
  getTopSellingSnacks(limit: number = 10) {
    return this.inventory.getTopSellingSnacks(limit)
  }

  // Get most profitable snacks
  getMostProfitableSnacks(limit: number = 10) {
    return this.inventory.getMostProfitableSnacks(limit)
  }

  // Get inventory statistics
  getInventoryStats() {
    return this.inventory.getInventoryStats()
  }

  // Get categories
  getCategories(): string[] {
    const snacks = this.inventory.getAvailableSnacks()
    const categories = new Set(snacks.map(snack => snack.category))
    return Array.from(categories).sort()
  }

  // Subscribe to order updates for admin panel
  subscribeToOrderUpdates(callback: (orders: SnackOrder[]) => void): () => void {
    return onValue(this.ordersRef, (snapshot) => {
      const orders: SnackOrder[] = []
      if (snapshot.exists()) {
        const ordersData = snapshot.val()
        Object.keys(ordersData).forEach(orderId => {
          const data = ordersData[orderId]
          orders.push({
            id: orderId,
            ...data,
            items: data.items || [{ 
              snackId: data.snackId, 
              snackName: data.snackName || 'Unknown', 
              quantity: data.quantity,
              unitPrice: data.totalAmount / data.quantity,
              totalPrice: data.totalAmount
            }],
            createdAt: data.createdAt ? new Date(data.createdAt) : new Date(),
            deliveredAt: data.deliveredAt ? new Date(data.deliveredAt) : undefined
          } as SnackOrder)
        })
      }
      
      // Sort by createdAt desc
      const sortedOrders = orders.sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime())
      callback(sortedOrders)
    })
  }

  // Get statistics for admin dashboard
  async getStats(): Promise<SnackStats> {
    try {
      // Get all orders to calculate stats
      const ordersSnapshot = await get(this.ordersRef)
      const orders: SnackOrder[] = []
      
      if (ordersSnapshot.exists()) {
        const ordersData = ordersSnapshot.val()
        Object.keys(ordersData).forEach(orderId => {
          const data = ordersData[orderId]
          orders.push({
            id: orderId,
            ...data,
            items: data.items || [{ 
              snackId: data.snackId, 
              snackName: data.snackName || 'Unknown', 
              quantity: data.quantity,
              unitPrice: data.totalAmount / data.quantity,
              totalPrice: data.totalAmount
            }],
            createdAt: data.createdAt ? new Date(data.createdAt) : new Date(),
            deliveredAt: data.deliveredAt ? new Date(data.deliveredAt) : undefined
          } as SnackOrder)
        })
      }

      // Calculate statistics
      const totalOrders = orders.length
      const completedOrders = orders.filter(order => order.status === 'completed')
      const totalRevenue = completedOrders.reduce((sum, order) => sum + order.totalAmount, 0)
      const averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0
      
      // Get unique users
      const uniqueUsers = new Set(orders.map(order => order.userId))
      const activeUsers = uniqueUsers.size

      // Calculate top selling snacks
      const snackSales = new Map<string, { name: string, sold: number }>()
      const revenueByCategory: Record<string, number> = {}

      completedOrders.forEach(order => {
        order.items?.forEach(item => {
          const current = snackSales.get(item.snackId) || { name: item.snackName, sold: 0 }
          snackSales.set(item.snackId, {
            name: item.snackName,
            sold: current.sold + item.quantity
          })
        })
      })

      // Get snacks to calculate category revenue
      const snacksSnapshot = await get(this.snacksRef)
      const snackCategories = new Map<string, string>()
      if (snacksSnapshot.exists()) {
        const snacksData = snacksSnapshot.val()
        Object.keys(snacksData).forEach(snackId => {
          const snack = snacksData[snackId] as Snack
          snackCategories.set(snackId, snack.category)
        })
      }

      // Calculate revenue by category
      completedOrders.forEach(order => {
        order.items?.forEach(item => {
          const category = snackCategories.get(item.snackId) || 'Other'
          revenueByCategory[category] = (revenueByCategory[category] || 0) + item.totalPrice
        })
      })

      const topSellingSnacks = Array.from(snackSales.entries())
        .map(([snackId, data]) => ({
          snackId,
          snackName: data.name,
          totalSold: data.sold
        }))
        .sort((a, b) => b.totalSold - a.totalSold)
        .slice(0, 5)

      return {
        totalSold: completedOrders.reduce((sum, order) => 
          sum + (order.items?.reduce((itemSum, item) => itemSum + item.quantity, 0) || order.quantity), 0),
        totalProfit: 0, // Calculate based on cost vs selling price
        popularityScore: 0, // Implement popularity algorithm
        totalRevenue,
        totalOrders,
        activeUsers,
        averageOrderValue,
        topSellingSnacks,
        revenueByCategory
      }
    } catch (error) {
      console.error('Error getting stats:', error)
      throw error
    }
  }

  // Complete an order (admin function)
  async completeOrder(orderId: string): Promise<void> {
    try {
      const orderRef = ref(realtimeDb, `snackOrders/${orderId}`)
      await update(orderRef, {
        status: 'completed',
        deliveredAt: new Date().toISOString()
      })
    } catch (error) {
      console.error('Error completing order:', error)
      throw error
    }
  }
}

// Singleton instance
export const snackCartService = new SnackCartService()
