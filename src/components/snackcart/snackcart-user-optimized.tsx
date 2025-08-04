'use client'

import { useState, useEffect, useMemo } from 'react'
import { Search, ShoppingCart, Filter, Star, Plus, Minus, Heart, Clock, Tag, TrendingUp } from 'lucide-react'
import { Snack } from '@/lib/snackcart-models'
import { snackCartService } from '@/lib/snackcart-service'
import { useAuth } from '@/lib/firebase-context'

interface SnackCardProps {
  snack: Snack
  onReserve: (snackId: string, quantity: number) => void
}

function OptimizedSnackCard({ snack, onReserve }: SnackCardProps) {
  const [quantity, setQuantity] = useState(1)
  const [isReserving, setIsReserving] = useState(false)
  const [isFavorite, setIsFavorite] = useState(false)

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
  const isLowStock = snack.quantity <= 5
  const totalPrice = snack.sellingPrice * quantity

  return (
    <div className="group bg-white rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 p-4 border border-gray-100 hover:border-blue-200 relative overflow-hidden">
      {/* Stock Status Badge */}
      {snack.quantity === 0 && (
        <div className="absolute top-3 left-3 bg-red-500 text-white text-xs px-2 py-1 rounded-full font-medium z-10">
          Out of Stock
        </div>
      )}
      {isLowStock && snack.quantity > 0 && (
        <div className="absolute top-3 left-3 bg-orange-500 text-white text-xs px-2 py-1 rounded-full font-medium z-10">
          Only {snack.quantity} left
        </div>
      )}

      {/* Favorite Button */}
      <button
        onClick={() => setIsFavorite(!isFavorite)}
        className="absolute top-3 right-3 p-1.5 rounded-full bg-white/80 hover:bg-white transition-colors z-10"
      >
        <Heart className={`w-4 h-4 ${isFavorite ? 'text-red-500 fill-current' : 'text-gray-400'}`} />
      </button>

      {/* Image */}
      <div className="aspect-square bg-gradient-to-br from-gray-100 to-gray-200 rounded-lg mb-3 flex items-center justify-center overflow-hidden group-hover:scale-105 transition-transform duration-300">
        {snack.imageUrl ? (
          <img 
            src={snack.imageUrl} 
            alt={snack.name}
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="text-gray-400 text-5xl">🍿</div>
        )}
      </div>
      
      {/* Content */}
      <div className="space-y-3">
        {/* Header */}
        <div className="space-y-2">
          <div className="flex justify-between items-start gap-2">
            <h3 className="font-semibold text-gray-900 text-sm leading-tight line-clamp-2">{snack.name}</h3>
            <span className="bg-blue-50 text-blue-700 text-xs px-2 py-1 rounded-full font-medium whitespace-nowrap">
              {snack.category}
            </span>
          </div>
          
          <p className="text-gray-600 text-xs line-clamp-2 leading-relaxed">{snack.description}</p>
        </div>
        
        {/* Price & Stats */}
        <div className="space-y-2">
          <div className="flex justify-between items-center">
            <div className="space-y-1">
              <div className="flex items-baseline gap-1">
                <span className="font-bold text-green-600 text-lg">₹{snack.sellingPrice}</span>
                <span className="text-xs text-gray-500">each</span>
              </div>
              <div className="flex items-center gap-2 text-xs">
                <span className="text-gray-500">+{profit}% profit</span>
                <span className="text-gray-300">•</span>
                <span className="text-gray-600">{snack.quantity} in stock</span>
              </div>
            </div>
            
            {/* Rating/Popularity placeholder */}
            <div className="flex items-center gap-1 text-xs text-yellow-600">
              <Star className="w-3 h-3 fill-current" />
              <span>4.2</span>
            </div>
          </div>
        </div>

        {/* Quantity & Action */}
        <div className="flex items-center gap-3 pt-2 border-t border-gray-100">
          {/* Quantity Selector */}
          <div className="flex items-center bg-gray-50 rounded-lg">
            <button 
              onClick={() => setQuantity(Math.max(1, quantity - 1))}
              className="p-2 text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-l-lg transition-colors"
            >
              <Minus className="w-3 h-3" />
            </button>
            <span className="px-3 py-2 text-sm font-medium min-w-[3rem] text-center">{quantity}</span>
            <button 
              onClick={() => setQuantity(Math.min(snack.quantity, quantity + 1))}
              className="p-2 text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-r-lg transition-colors"
              disabled={quantity >= snack.quantity}
            >
              <Plus className="w-3 h-3" />
            </button>
          </div>
          
          {/* Reserve Button */}
          <button
            onClick={handleReserve}
            disabled={isReserving || snack.quantity === 0}
            className="flex-1 bg-gradient-to-r from-blue-600 to-blue-700 text-white py-2.5 px-4 rounded-lg text-sm font-medium hover:from-blue-700 hover:to-blue-800 disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200 flex items-center justify-center gap-2 shadow-sm hover:shadow-md"
          >
            {isReserving ? (
              <>
                <div className="w-3 h-3 border border-white/30 border-t-white rounded-full animate-spin"></div>
                Reserving...
              </>
            ) : snack.quantity === 0 ? (
              'Out of Stock'
            ) : (
              <>
                <ShoppingCart className="w-3 h-3" />
                ₹{totalPrice.toFixed(2)}
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  )
}

export function SnackCartUserOptimized() {
  const { user } = useAuth()
  const [snacks, setSnacks] = useState<Snack[]>([])
  const [filteredSnacks, setFilteredSnacks] = useState<Snack[]>([])
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('')
  const [categories, setCategories] = useState<string[]>([])
  const [sortBy, setSortBy] = useState<'name' | 'price' | 'popularity' | 'stock'>('popularity')
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 1000])
  const [loading, setLoading] = useState(true)
  const [reservationMessage, setReservationMessage] = useState('')
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid')
  const [showFilters, setShowFilters] = useState(false)

  useEffect(() => {
    initializeSnackCart()
  }, [])

  useEffect(() => {
    filterAndSortSnacks()
  }, [snacks, searchQuery, selectedCategory, sortBy, priceRange])

  const initializeSnackCart = async () => {
    try {
      await snackCartService.initializeInventory()
      
      // Subscribe to real-time updates
      const unsubscribe = snackCartService.subscribeToInventoryUpdates((updatedSnacks) => {
        setSnacks(updatedSnacks)
        // Calculate price range
        if (updatedSnacks.length > 0) {
          const prices = updatedSnacks.map(s => s.sellingPrice)
          setPriceRange([Math.min(...prices), Math.max(...prices)])
        }
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
      filtered = filtered.filter(snack => snack.category === selectedCategory)
    }

    // Filter by price range
    filtered = filtered.filter(snack => 
      snack.sellingPrice >= priceRange[0] && snack.sellingPrice <= priceRange[1]
    )

    // Sort snacks
    filtered.sort((a, b) => {
      switch (sortBy) {
        case 'price':
          return a.sellingPrice - b.sellingPrice
        case 'stock':
          return b.quantity - a.quantity
        case 'popularity':
          // Mock popularity based on stock sold (inverse of current stock + random factor)
          const aPopularity = (100 - a.quantity) + Math.random() * 10
          const bPopularity = (100 - b.quantity) + Math.random() * 10
          return bPopularity - aPopularity
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
      setReservationMessage(`🎉 Snack reserved successfully! Order ID: ${orderId.slice(0, 8)}...`)
      setTimeout(() => setReservationMessage(''), 5000)
    } catch (error: any) {
      setReservationMessage(`❌ Error: ${error.message}`)
      setTimeout(() => setReservationMessage(''), 5000)
    }
  }

  const clearFilters = () => {
    setSearchQuery('')
    setSelectedCategory('')
    setPriceRange([0, 1000])
    setSortBy('popularity')
  }

  const categoryStats = useMemo(() => {
    return categories.map(category => ({
      name: category,
      count: snacks.filter(s => s.category === category).length,
      avgPrice: snacks.filter(s => s.category === category).reduce((sum, s) => sum + s.sellingPrice, 0) / snacks.filter(s => s.category === category).length || 0
    }))
  }, [snacks, categories])

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center h-64 space-y-4">
        <div className="relative">
          <div className="w-16 h-16 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin"></div>
          <div className="absolute inset-0 flex items-center justify-center text-2xl">🍿</div>
        </div>
        <div className="text-center">
          <h3 className="text-lg font-medium text-gray-900">Loading SnackCart</h3>
          <p className="text-gray-600">Fetching fresh snacks for you...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto p-4 space-y-6">
      {/* Header */}
      <div className="text-center space-y-2">
        <h1 className="text-3xl font-bold text-gray-900 bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
          🍿 SnackCart
        </h1>
        <p className="text-gray-600 max-w-2xl mx-auto">
          Discover and reserve your favorite snacks with instant COD delivery to your hostel room
        </p>
      </div>

      {/* Stats Row */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-gradient-to-r from-green-500 to-green-600 text-white p-4 rounded-xl">
          <div className="flex items-center gap-2">
            <TrendingUp className="w-5 h-5" />
            <div>
              <p className="text-green-100 text-sm">Available</p>
              <p className="text-xl font-bold">{snacks.filter(s => s.quantity > 0).length}</p>
            </div>
          </div>
        </div>
        <div className="bg-gradient-to-r from-blue-500 to-blue-600 text-white p-4 rounded-xl">
          <div className="flex items-center gap-2">
            <Tag className="w-5 h-5" />
            <div>
              <p className="text-blue-100 text-sm">Categories</p>
              <p className="text-xl font-bold">{categories.length}</p>
            </div>
          </div>
        </div>
        <div className="bg-gradient-to-r from-purple-500 to-purple-600 text-white p-4 rounded-xl">
          <div className="flex items-center gap-2">
            <Star className="w-5 h-5" />
            <div>
              <p className="text-purple-100 text-sm">Top Rated</p>
              <p className="text-xl font-bold">4.2★</p>
            </div>
          </div>
        </div>
        <div className="bg-gradient-to-r from-orange-500 to-orange-600 text-white p-4 rounded-xl">
          <div className="flex items-center gap-2">
            <Clock className="w-5 h-5" />
            <div>
              <p className="text-orange-100 text-sm">Delivery</p>
              <p className="text-xl font-bold">15 min</p>
            </div>
          </div>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="bg-white rounded-xl shadow-sm p-6 space-y-4">
        {/* Main Search */}
        <div className="relative">
          <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <input
            type="text"
            placeholder="Search for snacks, brands, or descriptions..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-12 pr-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent text-gray-900 placeholder-gray-500"
          />
        </div>

        {/* Filter Toggle & Quick Actions */}
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <button
              onClick={() => setShowFilters(!showFilters)}
              className="flex items-center gap-2 px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
            >
              <Filter className="w-4 h-4" />
              Filters
              {(selectedCategory || priceRange[0] > 0 || priceRange[1] < 1000) && (
                <span className="bg-blue-600 text-white text-xs px-2 py-1 rounded-full">!</span>
              )}
            </button>
            
            {(selectedCategory || searchQuery || priceRange[0] > 0 || priceRange[1] < 1000) && (
              <button
                onClick={clearFilters}
                className="text-blue-600 hover:text-blue-800 text-sm font-medium"
              >
                Clear all filters
              </button>
            )}
          </div>

          <div className="flex items-center gap-4">
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value as any)}
              className="px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white"
            >
              <option value="popularity">Most Popular</option>
              <option value="name">Name A-Z</option>
              <option value="price">Price Low-High</option>
              <option value="stock">Most Stock</option>
            </select>

            <div className="text-sm text-gray-600 bg-gray-50 px-3 py-2 rounded-lg">
              {filteredSnacks.length} results
            </div>
          </div>
        </div>

        {/* Expandable Filters */}
        {showFilters && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-4 border-t border-gray-100">
            {/* Categories */}
            <div>
              <h4 className="font-medium text-gray-900 mb-3">Categories</h4>
              <div className="space-y-2 max-h-48 overflow-y-auto">
                {categoryStats.map(category => (
                  <label key={category.name} className="flex items-center justify-between p-2 hover:bg-gray-50 rounded-lg cursor-pointer">
                    <div className="flex items-center gap-2">
                      <input
                        type="radio"
                        name="category"
                        value={category.name}
                        checked={selectedCategory === category.name}
                        onChange={(e) => setSelectedCategory(e.target.value)}
                        className="text-blue-600 focus:ring-blue-500"
                      />
                      <span className="text-sm">{category.name}</span>
                    </div>
                    <span className="text-xs text-gray-500">({category.count})</span>
                  </label>
                ))}
                {selectedCategory && (
                  <button
                    onClick={() => setSelectedCategory('')}
                    className="text-xs text-blue-600 hover:text-blue-800 ml-6"
                  >
                    Clear selection
                  </button>
                )}
              </div>
            </div>

            {/* Price Range */}
            <div>
              <h4 className="font-medium text-gray-900 mb-3">Price Range</h4>
              <div className="space-y-3">
                <div className="flex items-center gap-2">
                  <input
                    type="number"
                    value={priceRange[0]}
                    onChange={(e) => setPriceRange([parseInt(e.target.value) || 0, priceRange[1]])}
                    className="w-full px-3 py-2 border border-gray-200 rounded text-sm"
                    placeholder="Min"
                  />
                  <span className="text-gray-400">to</span>
                  <input
                    type="number"
                    value={priceRange[1]}
                    onChange={(e) => setPriceRange([priceRange[0], parseInt(e.target.value) || 1000])}
                    className="w-full px-3 py-2 border border-gray-200 rounded text-sm"
                    placeholder="Max"
                  />
                </div>
                <div className="text-xs text-gray-500">
                  ₹{priceRange[0]} - ₹{priceRange[1]}
                </div>
              </div>
            </div>

            {/* Quick Filters */}
            <div>
              <h4 className="font-medium text-gray-900 mb-3">Quick Filters</h4>
              <div className="space-y-2">
                <button className="w-full text-left px-3 py-2 bg-gray-50 hover:bg-gray-100 rounded-lg text-sm transition-colors">
                  In Stock Only
                </button>
                <button className="w-full text-left px-3 py-2 bg-gray-50 hover:bg-gray-100 rounded-lg text-sm transition-colors">
                  Low Stock Items
                </button>
                <button className="w-full text-left px-3 py-2 bg-gray-50 hover:bg-gray-100 rounded-lg text-sm transition-colors">
                  High Profit Items
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Reservation Message */}
      {reservationMessage && (
        <div className={`p-4 rounded-xl border ${
          reservationMessage.includes('Error') || reservationMessage.includes('❌')
            ? 'bg-red-50 text-red-800 border-red-200' 
            : 'bg-green-50 text-green-800 border-green-200'
        } animate-in slide-in-from-top-2 duration-300`}>
          <div className="flex items-center gap-2">
            <div className="flex-1">{reservationMessage}</div>
            <button 
              onClick={() => setReservationMessage('')}
              className="text-current opacity-60 hover:opacity-100"
            >
              ✕
            </button>
          </div>
        </div>
      )}

      {/* Results */}
      {filteredSnacks.length === 0 ? (
        <div className="text-center py-16 space-y-4">
          <div className="text-8xl">🔍</div>
          <h3 className="text-xl font-semibold text-gray-900">No snacks found</h3>
          <p className="text-gray-600 max-w-md mx-auto">
            {searchQuery || selectedCategory 
              ? 'Try adjusting your search terms or filters to find what you\'re looking for.' 
              : 'No snacks are currently available. Check back later for fresh inventory!'
            }
          </p>
          {(searchQuery || selectedCategory) && (
            <button
              onClick={clearFilters}
              className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors"
            >
              Clear All Filters
            </button>
          )}
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {filteredSnacks.map(snack => (
            <OptimizedSnackCard 
              key={snack.id} 
              snack={snack} 
              onReserve={handleReserve}
            />
          ))}
        </div>
      )}

      {/* Enhanced DSA Info Panel */}
      <div className="bg-gradient-to-r from-blue-50 to-purple-50 rounded-xl p-6 border border-blue-100">
        <h3 className="font-semibold text-blue-900 mb-4 flex items-center gap-2">
          🧠 DSA Implementation Highlights
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
          <div className="space-y-3">
            <div className="flex items-start gap-3">
              <div className="bg-blue-500 text-white w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold">T</div>
              <div>
                <p className="font-medium text-blue-900">Trie Data Structure</p>
                <p className="text-blue-700">O(prefix length) search complexity for instant results</p>
              </div>
            </div>
            <div className="flex items-start gap-3">
              <div className="bg-green-500 text-white w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold">H</div>
              <div>
                <p className="font-medium text-blue-900">Hash Maps</p>
                <p className="text-blue-700">O(1) inventory lookup and category indexing</p>
              </div>
            </div>
          </div>
          <div className="space-y-3">
            <div className="flex items-start gap-3">
              <div className="bg-purple-500 text-white w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold">M</div>
              <div>
                <p className="font-medium text-blue-900">Merge Sort</p>
                <p className="text-blue-700">Stable O(n log n) sorting for popularity rankings</p>
              </div>
            </div>
            <div className="flex items-start gap-3">
              <div className="bg-orange-500 text-white w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold">R</div>
              <div>
                <p className="font-medium text-blue-900">Real-time Updates</p>
                <p className="text-blue-700">Firebase listeners with local cache optimization</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}