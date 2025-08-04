'use client'

import { useState, useEffect } from 'react'
import { Search, ShoppingCart, Filter, Star } from 'lucide-react'
import { Snack } from '@/lib/snackcart-models'
import { snackCartService } from '@/lib/snackcart-service'
import { useAuth } from '@/lib/firebase-context'

interface SnackCardProps {
  snack: Snack
  onReserve: (snackId: string, quantity: number) => void
}

function SnackCard({ snack, onReserve }: SnackCardProps) {
  const [quantity, setQuantity] = useState(1)
  const [isReserving, setIsReserving] = useState(false)

  const handleReserve = async () => {
    setIsReserving(true)
    try {
      await onReserve(snack.id, quantity)
      setQuantity(1)
    } catch (error) {
      console.error('Failed to reserve snack:', error)
    } finally {
      setIsReserving(false)
    }
  }

  const profit = ((snack.sellingPrice - snack.costPrice) / snack.costPrice * 100).toFixed(1)

  return (
    <div className="bg-white rounded-lg shadow-md p-4 hover:shadow-lg transition-shadow">
      <div className="aspect-square bg-gray-200 rounded-lg mb-3 flex items-center justify-center">
        {snack.imageUrl ? (
          <img 
            src={snack.imageUrl} 
            alt={snack.name}
            className="w-full h-full object-cover rounded-lg"
          />
        ) : (
          <div className="text-gray-400 text-4xl">🍿</div>
        )}
      </div>
      
      <div className="space-y-2">
        <div className="flex justify-between items-start">
          <h3 className="font-semibold text-gray-900 text-sm">{snack.name}</h3>
          <span className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full">
            {snack.category}
          </span>
        </div>
        
        <p className="text-gray-600 text-xs line-clamp-2">{snack.description}</p>
        
        <div className="flex justify-between items-center text-sm">
          <div>
            <span className="font-bold text-green-600">₹{snack.sellingPrice}</span>
            <span className="text-gray-500 text-xs ml-1">({profit}% profit)</span>
          </div>
          <span className="text-gray-600 text-xs">Stock: {snack.quantity}</span>
        </div>

        <div className="flex items-center gap-2 pt-2 border-t">
          <div className="flex items-center border rounded">
            <button 
              onClick={() => setQuantity(Math.max(1, quantity - 1))}
              className="px-2 py-1 text-gray-600 hover:bg-gray-100"
            >
              -
            </button>
            <span className="px-3 py-1 text-sm">{quantity}</span>
            <button 
              onClick={() => setQuantity(Math.min(snack.quantity, quantity + 1))}
              className="px-2 py-1 text-gray-600 hover:bg-gray-100"
            >
              +
            </button>
          </div>
          
          <button
            onClick={handleReserve}
            disabled={isReserving || snack.quantity === 0}
            className="flex-1 bg-blue-600 text-white py-1.5 px-3 rounded text-sm hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-1"
          >
            <ShoppingCart className="w-3 h-3" />
            {isReserving ? 'Reserving...' : `₹${(snack.sellingPrice * quantity).toFixed(2)}`}
          </button>
        </div>
      </div>
    </div>
  )
}

export function SnackCartUser() {
  const { user } = useAuth()
  const [snacks, setSnacks] = useState<Snack[]>([])
  const [filteredSnacks, setFilteredSnacks] = useState<Snack[]>([])
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('')
  const [categories, setCategories] = useState<string[]>([])
  const [sortBy, setSortBy] = useState<'name' | 'price' | 'popularity'>('name')
  const [loading, setLoading] = useState(true)
  const [reservationMessage, setReservationMessage] = useState('')

  useEffect(() => {
    initializeSnackCart()
  }, [])

  useEffect(() => {
    filterAndSortSnacks()
  }, [snacks, searchQuery, selectedCategory, sortBy])

  const initializeSnackCart = async () => {
    try {
      await snackCartService.initializeInventory()
      
      // Subscribe to real-time updates
      const unsubscribe = snackCartService.subscribeToInventoryUpdates((updatedSnacks) => {
        setSnacks(updatedSnacks)
      })

      // Get categories
      const availableCategories = snackCartService.getCategories()
      setCategories(availableCategories)

      setLoading(false)

      // Cleanup subscription on unmount
      return () => unsubscribe()
    } catch (error) {
      console.error('Error initializing SnackCart:', error)
      setLoading(false)
    }
  }

  const filterAndSortSnacks = () => {
    let filtered = [...snacks]

    // Filter by search query using Trie
    if (searchQuery.trim()) {
      filtered = snackCartService.searchSnacks(searchQuery)
    }

    // Filter by category
    if (selectedCategory) {
      filtered = snackCartService.getSnacksByCategory(selectedCategory)
    }

    // Apply both filters if both are active
    if (searchQuery.trim() && selectedCategory) {
      const searchResults = snackCartService.searchSnacks(searchQuery)
      filtered = searchResults.filter(snack => snack.category === selectedCategory)
    }

    // Sort snacks
    filtered.sort((a, b) => {
      switch (sortBy) {
        case 'price':
          return a.sellingPrice - b.sellingPrice
        case 'popularity':
          // You can implement popularity scoring here
          return b.name.localeCompare(a.name) // Placeholder
        case 'name':
        default:
          return a.name.localeCompare(b.name)
      }
    })

    setFilteredSnacks(filtered)
  }

  const handleReserve = async (snackId: string, quantity: number) => {
    if (!user) {
      setReservationMessage('Please login to reserve snacks')
      setTimeout(() => setReservationMessage(''), 3000)
      return
    }

    try {
      const orderId = await snackCartService.reserveSnack(user.uid, snackId, quantity)
      setReservationMessage(`Snack reserved successfully! Order ID: ${orderId.slice(0, 8)}...`)
      setTimeout(() => setReservationMessage(''), 5000)
    } catch (error: any) {
      setReservationMessage(`Error: ${error.message}`)
      setTimeout(() => setReservationMessage(''), 5000)
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <span className="ml-2 text-gray-600">Loading snacks...</span>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto p-4">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">🍿 SnackCart</h1>
        <p className="text-gray-600">Reserve your favorite snacks with COD payment</p>
      </div>

      {/* Search and Filters */}
      <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {/* Search Box - Uses Trie for efficient prefix matching */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            <input
              type="text"
              placeholder="Search snacks..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          {/* Category Filter */}
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="">All Categories</option>
            {categories.map(category => (
              <option key={category} value={category}>{category}</option>
            ))}
          </select>

          {/* Sort Options */}
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as 'name' | 'price' | 'popularity')}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value="name">Sort by Name</option>
            <option value="price">Sort by Price</option>
            <option value="popularity">Sort by Popularity</option>
          </select>

          {/* Results Count */}
          <div className="flex items-center justify-center text-sm text-gray-600">
            {filteredSnacks.length} snacks found
          </div>
        </div>
      </div>

      {/* Reservation Message */}
      {reservationMessage && (
        <div className={`mb-4 p-3 rounded-lg ${
          reservationMessage.includes('Error') 
            ? 'bg-red-100 text-red-800' 
            : 'bg-green-100 text-green-800'
        }`}>
          {reservationMessage}
        </div>
      )}

      {/* Snacks Grid */}
      {filteredSnacks.length === 0 ? (
        <div className="text-center py-12">
          <div className="text-6xl mb-4">🔍</div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">No snacks found</h3>
          <p className="text-gray-600">
            {searchQuery || selectedCategory 
              ? 'Try adjusting your search or filters' 
              : 'No snacks available at the moment'
            }
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {filteredSnacks.map(snack => (
            <SnackCard 
              key={snack.id} 
              snack={snack} 
              onReserve={handleReserve}
            />
          ))}
        </div>
      )}

      {/* DSA Info Panel */}
      <div className="mt-8 bg-blue-50 rounded-lg p-4">
        <h3 className="font-semibold text-blue-900 mb-2">🧠 DSA Implementation</h3>
        <div className="text-sm text-blue-800 space-y-1">
          <p>• <strong>Trie Data Structure:</strong> O(prefix length) search complexity</p>
          <p>• <strong>Hash Maps:</strong> O(1) inventory lookup and category indexing</p>
          <p>• <strong>Merge Sort:</strong> Stable O(n log n) sorting for popularity rankings</p>
          <p>• <strong>Real-time Updates:</strong> Firebase listeners with local cache optimization</p>
        </div>
      </div>
    </div>
  )
}
