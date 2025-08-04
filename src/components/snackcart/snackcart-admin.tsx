'use client'

import { useState, useEffect } from 'react'
import { Package, Plus, Edit, Trash2, Users, TrendingUp, DollarSign, UserCheck } from 'lucide-react'
import { Snack, SnackOrder, SnackOrderItem, SnackStats } from '@/lib/snackcart-models'
import { snackCartService } from '@/lib/snackcart-service'
import { UserManagement } from '@/components/admin/user-management'

interface SnackFormData {
  name: string
  description: string
  category: string
  costPrice: number
  sellingPrice: number
  quantity: number
  imageUrl: string
}

function SnackForm({ 
  snack, 
  onSave, 
  onCancel 
}: { 
  snack?: Snack
  onSave: (data: SnackFormData) => void
  onCancel: () => void 
}) {
  const [formData, setFormData] = useState<SnackFormData>({
    name: snack?.name || '',
    description: snack?.description || '',
    category: snack?.category || '',
    costPrice: snack?.costPrice || 0,
    sellingPrice: snack?.sellingPrice || 0,
    quantity: snack?.quantity || 0,
    imageUrl: snack?.imageUrl || ''
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    onSave(formData)
  }

  const categories = ['Beverages', 'Snacks', 'Instant Food', 'Chocolates', 'Chips', 'Biscuits', 'Other']

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md mx-4">
        <h3 className="text-lg font-semibold mb-4">
          {snack ? 'Edit Snack' : 'Add New Snack'}
        </h3>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
            <input
              type="text"
              required
              value={formData.name}
              onChange={(e) => setFormData({...formData, name: e.target.value})}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              required
              value={formData.description}
              onChange={(e) => setFormData({...formData, description: e.target.value})}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              rows={3}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
            <select
              required
              value={formData.category}
              onChange={(e) => setFormData({...formData, category: e.target.value})}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Select Category</option>
              {categories.map(cat => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Cost Price</label>
              <input
                type="number"
                required
                min="0"
                step="0.01"
                value={formData.costPrice}
                onChange={(e) => setFormData({...formData, costPrice: parseFloat(e.target.value)})}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Selling Price</label>
              <input
                type="number"
                required
                min="0"
                step="0.01"
                value={formData.sellingPrice}
                onChange={(e) => setFormData({...formData, sellingPrice: parseFloat(e.target.value)})}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Stock Quantity</label>
            <input
              type="number"
              required
              min="0"
              value={formData.quantity}
              onChange={(e) => setFormData({...formData, quantity: parseInt(e.target.value)})}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Image URL (optional)</label>
            <input
              type="url"
              value={formData.imageUrl}
              onChange={(e) => setFormData({...formData, imageUrl: e.target.value})}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="submit"
              className="flex-1 bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700"
            >
              {snack ? 'Update' : 'Add'} Snack
            </button>
            <button
              type="button"
              onClick={onCancel}
              className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export function SnackCartAdmin() {
  const [snacks, setSnacks] = useState<Snack[]>([])
  const [orders, setOrders] = useState<SnackOrder[]>([])
  const [stats, setStats] = useState<SnackStats | null>(null)
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editingSnack, setEditingSnack] = useState<Snack | undefined>()
  const [activeTab, setActiveTab] = useState<'inventory' | 'orders' | 'stats' | 'users'>('inventory')

  useEffect(() => {
    initializeAdmin()
  }, [])

  const initializeAdmin = async () => {
    try {
      await snackCartService.initializeInventory()
      
      // Subscribe to real-time updates
      const unsubscribeInventory = snackCartService.subscribeToInventoryUpdates((updatedSnacks) => {
        setSnacks(updatedSnacks)
      })

      const unsubscribeOrders = snackCartService.subscribeToOrderUpdates((updatedOrders) => {
        setOrders(updatedOrders)
      })

      // Get stats
      const currentStats = await snackCartService.getStats()
      setStats(currentStats)

      setLoading(false)

      // Cleanup subscriptions on unmount
      return () => {
        unsubscribeInventory()
        unsubscribeOrders()
      }
    } catch (error) {
      console.error('Error initializing admin panel:', error)
      setLoading(false)
    }
  }

  const handleAddSnack = async (data: SnackFormData) => {
    try {
      await snackCartService.addSnack(data)
      setShowForm(false)
    } catch (error) {
      console.error('Error adding snack:', error)
    }
  }

  const handleUpdateSnack = async (data: SnackFormData) => {
    if (!editingSnack) return
    
    try {
      await snackCartService.updateSnack(editingSnack.id, data)
      setEditingSnack(undefined)
      setShowForm(false)
    } catch (error) {
      console.error('Error updating snack:', error)
    }
  }

  const handleDeleteSnack = async (snackId: string) => {
    if (!confirm('Are you sure you want to delete this snack?')) return
    
    try {
      await snackCartService.deleteSnack(snackId)
    } catch (error) {
      console.error('Error deleting snack:', error)
    }
  }

  const handleCompleteOrder = async (orderId: string) => {
    try {
      await snackCartService.completeOrder(orderId)
    } catch (error) {
      console.error('Error completing order:', error)
    }
  }

  const pendingOrders = orders.filter(order => order.status === 'pending')
  const completedOrders = orders.filter(order => order.status === 'completed')

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <span className="ml-2 text-gray-600">Loading admin panel...</span>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto p-4">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">🛠️ SnackCart Admin</h1>
          <p className="text-gray-600">Manage inventory, orders, and analytics</p>
        </div>
        
        <button
          onClick={() => setShowForm(true)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          Add Snack
        </button>
      </div>

      {/* Stats Overview */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white rounded-lg shadow-sm p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Total Revenue</p>
                <p className="text-2xl font-bold text-green-600">₹{stats.totalRevenue.toFixed(2)}</p>
              </div>
              <DollarSign className="w-8 h-8 text-green-600" />
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Total Orders</p>
                <p className="text-2xl font-bold text-blue-600">{stats.totalOrders}</p>
              </div>
              <Package className="w-8 h-8 text-blue-600" />
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Active Users</p>
                <p className="text-2xl font-bold text-purple-600">{stats.activeUsers}</p>
              </div>
              <Users className="w-8 h-8 text-purple-600" />
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Avg. Order Value</p>
                <p className="text-2xl font-bold text-orange-600">₹{stats.averageOrderValue.toFixed(2)}</p>
              </div>
              <TrendingUp className="w-8 h-8 text-orange-600" />
            </div>
          </div>
        </div>
      )}

      {/* Tabs */}
      <div className="bg-white rounded-lg shadow-sm mb-6">
        <div className="border-b">
          <nav className="flex space-x-8 px-6">
            {[
              { id: 'inventory', label: 'Inventory Management', count: snacks.length },
              { id: 'orders', label: 'Order Management', count: pendingOrders.length },
              { id: 'stats', label: 'Analytics', count: null },
              { id: 'users', label: 'User Management', count: null }
            ].map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as any)}
                className={`py-4 px-2 border-b-2 font-medium text-sm ${
                  activeTab === tab.id
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700'
                }`}
              >
                {tab.label}
                {tab.count !== null && (
                  <span className="ml-2 bg-gray-100 text-gray-900 py-0.5 px-2 rounded-full text-xs">
                    {tab.count}
                  </span>
                )}
              </button>
            ))}
          </nav>
        </div>

        <div className="p-6">
          {activeTab === 'inventory' && (
            <div className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {snacks.map(snack => (
                  <div key={snack.id} className="border rounded-lg p-4">
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="font-semibold">{snack.name}</h3>
                      <div className="flex gap-2">
                        <button
                          onClick={() => {
                            setEditingSnack(snack)
                            setShowForm(true)
                          }}
                          className="text-blue-600 hover:text-blue-800"
                        >
                          <Edit className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDeleteSnack(snack.id)}
                          className="text-red-600 hover:text-red-800"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </div>
                    
                    <p className="text-sm text-gray-600 mb-2">{snack.description}</p>
                    
                    <div className="space-y-1 text-sm">
                      <div className="flex justify-between">
                        <span>Category:</span>
                        <span className="font-medium">{snack.category}</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Cost Price:</span>
                        <span>₹{snack.costPrice}</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Selling Price:</span>
                        <span className="font-medium">₹{snack.sellingPrice}</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Stock:</span>
                        <span className={snack.quantity > 5 ? 'text-green-600' : 'text-red-600'}>
                          {snack.quantity}
                        </span>
                      </div>
                      <div className="flex justify-between">
                        <span>Profit Margin:</span>
                        <span className="text-green-600">
                          {((snack.sellingPrice - snack.costPrice) / snack.costPrice * 100).toFixed(1)}%
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === 'orders' && (
            <div className="space-y-6">
              {/* Pending Orders */}
              <div>
                <h3 className="text-lg font-semibold mb-3">Pending Orders ({pendingOrders.length})</h3>
                <div className="space-y-3">
                  {pendingOrders.map(order => (
                    <div key={order.id} className="border rounded-lg p-4 bg-yellow-50">
                      <div className="flex justify-between items-start">
                        <div>
                          <p className="font-medium">Order #{order.id.slice(0, 8)}</p>
                          <p className="text-sm text-gray-600">User: {order.userId}</p>
                          <p className="text-sm text-gray-600">
                            {order.items.map(item => `${item.quantity}x ${item.snackName}`).join(', ')}
                          </p>
                          <p className="font-semibold">Total: ₹{order.totalAmount}</p>
                        </div>
                        <button
                          onClick={() => handleCompleteOrder(order.id)}
                          className="bg-green-600 text-white px-3 py-1 rounded text-sm hover:bg-green-700"
                        >
                          Mark Complete
                        </button>
                      </div>
                    </div>
                  ))}
                  {pendingOrders.length === 0 && (
                    <p className="text-gray-500">No pending orders</p>
                  )}
                </div>
              </div>

              {/* Completed Orders */}
              <div>
                <h3 className="text-lg font-semibold mb-3">Recent Completed Orders</h3>
                <div className="space-y-3">
                  {completedOrders.slice(0, 10).map(order => (
                    <div key={order.id} className="border rounded-lg p-4 bg-green-50">
                      <div className="flex justify-between items-start">
                        <div>
                          <p className="font-medium">Order #{order.id.slice(0, 8)}</p>
                          <p className="text-sm text-gray-600">User: {order.userId}</p>
                          <p className="text-sm text-gray-600">
                            {order.items.map(item => `${item.quantity}x ${item.snackName}`).join(', ')}
                          </p>
                          <p className="font-semibold">Total: ₹{order.totalAmount}</p>
                        </div>
                        <span className="text-green-600 text-sm font-medium">Completed</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'stats' && stats && (
            <div className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Top Selling Snacks */}
                <div className="border rounded-lg p-4">
                  <h3 className="text-lg font-semibold mb-3">Top Selling Snacks</h3>
                  <div className="space-y-2">
                    {stats.topSellingSnacks.map((item, index) => (
                      <div key={item.snackId} className="flex justify-between items-center">
                        <span className="text-sm">{index + 1}. {item.snackName}</span>
                        <span className="text-sm font-medium">{item.totalSold} sold</span>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Revenue by Category */}
                <div className="border rounded-lg p-4">
                  <h3 className="text-lg font-semibold mb-3">Revenue by Category</h3>
                  <div className="space-y-2">
                    {Object.entries(stats.revenueByCategory).map(([category, revenue]) => (
                      <div key={category} className="flex justify-between items-center">
                        <span className="text-sm">{category}</span>
                        <span className="text-sm font-medium">₹{revenue.toFixed(2)}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'users' && (
            <UserManagement />
          )}
        </div>
      </div>

      {/* Form Modal */}
      {showForm && (
        <SnackForm
          snack={editingSnack}
          onSave={editingSnack ? handleUpdateSnack : handleAddSnack}
          onCancel={() => {
            setShowForm(false)
            setEditingSnack(undefined)
          }}
        />
      )}

      {/* DSA Info Panel */}
      <div className="bg-blue-50 rounded-lg p-4">
        <h3 className="font-semibold text-blue-900 mb-2">🧠 DSA Implementation - Admin Features</h3>
        <div className="text-sm text-blue-800 space-y-1">
          <p>• <strong>Hash Map Indexing:</strong> O(1) inventory management and category aggregation</p>
          <p>• <strong>Merge Sort Analytics:</strong> Stable sorting for top-selling snacks and revenue ranking</p>
          <p>• <strong>Real-time Subscriptions:</strong> Firebase listeners with efficient data synchronization</p>
          <p>• <strong>Statistical Aggregation:</strong> Mathematical functions for revenue analysis and trends</p>
        </div>
      </div>
    </div>
  )
}
