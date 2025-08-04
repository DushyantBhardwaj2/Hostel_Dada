// Advanced DSA implementations for SnackCart
// Enhanced Trie with fuzzy search, autocomplete, and advanced search features

import { 
  Snack, 
  SnackOrder, 
  SnackOrderItem, 
  SnackStats, 
  TrieNode, 
  SnackSearchTrie, 
  SnackInventory 
} from './snackcart-models'

export interface FuzzySearchResult {
  snack: Snack
  score: number
  matches: string[]
}

export interface SearchAnalytics {
  totalSearches: number
  popularQueries: Map<string, number>
  averageResponseTime: number
  zeroResultQueries: string[]
}

// Advanced Trie with fuzzy search capabilities
export class AdvancedSearchTrie {
  private root: TrieNode
  private analytics: SearchAnalytics

  constructor() {
    this.root = new TrieNode()
    this.analytics = {
      totalSearches: 0,
      popularQueries: new Map(),
      averageResponseTime: 0,
      zeroResultQueries: []
    }
  }

  // Basic insert method for trie
  insert(word: string, snackId: string): void {
    let current = this.root
    const normalizedWord = word.toLowerCase()

    for (const char of normalizedWord) {
      if (!current.children.has(char)) {
        current.children.set(char, new TrieNode())
      }
      current = current.children.get(char)!
      current.snackIds.add(snackId)
    }
    current.isEndOfWord = true
  }

  // Insert with multiple search terms (name, category, description keywords)
  insertMultiTerms(snack: Snack): void {
    const terms = [
      snack.name,
      snack.category,
      ...snack.description.split(' ').filter(word => word.length > 2)
    ]

    terms.forEach(term => {
      this.insert(term.toLowerCase(), snack.id)
    })
  }

  // Advanced fuzzy search with Levenshtein distance
  fuzzySearch(query: string, maxDistance: number = 2): FuzzySearchResult[] {
    const startTime = performance.now()
    const normalizedQuery = query.toLowerCase()
    
    this.analytics.totalSearches++
    this.analytics.popularQueries.set(query, (this.analytics.popularQueries.get(query) || 0) + 1)

    const results: FuzzySearchResult[] = []
    this._fuzzySearchHelper(this.root, normalizedQuery, '', 0, maxDistance, results)

    const endTime = performance.now()
    this.analytics.averageResponseTime = 
      (this.analytics.averageResponseTime * (this.analytics.totalSearches - 1) + (endTime - startTime)) / this.analytics.totalSearches

    if (results.length === 0) {
      this.analytics.zeroResultQueries.push(query)
    }

    return results.sort((a, b) => b.score - a.score)
  }

  private _fuzzySearchHelper(
    node: TrieNode,
    query: string,
    currentWord: string,
    queryIndex: number,
    maxDistance: number,
    results: FuzzySearchResult[]
  ): void {
    // If we've built a complete word and it's close enough
    if (node.isEndOfWord && currentWord.length > 0) {
      const distance = this.levenshteinDistance(query, currentWord)
      if (distance <= maxDistance) {
        const score = this.calculateScore(query, currentWord, distance)
        node.snackIds.forEach(snackId => {
          results.push({
            snack: snackId as any, // Will be resolved later
            score,
            matches: [currentWord]
          })
        })
      }
    }

    // Continue exploring the trie
    for (const [char, childNode] of Array.from(node.children.entries())) {
      // Direct match
      if (queryIndex < query.length && query[queryIndex] === char) {
        this._fuzzySearchHelper(childNode, query, currentWord + char, queryIndex + 1, maxDistance, results)
      }
      
      // Allow insertions, deletions, substitutions within distance limit
      if (maxDistance > 0) {
        // Insertion (skip character in trie)
        this._fuzzySearchHelper(childNode, query, currentWord + char, queryIndex, maxDistance - 1, results)
        
        // Deletion (skip character in query)
        if (queryIndex < query.length) {
          this._fuzzySearchHelper(node, query, currentWord, queryIndex + 1, maxDistance - 1, results)
        }
        
        // Substitution
        if (queryIndex < query.length) {
          this._fuzzySearchHelper(childNode, query, currentWord + char, queryIndex + 1, maxDistance - 1, results)
        }
      }
    }
  }

  // Levenshtein distance calculation
  private levenshteinDistance(str1: string, str2: string): number {
    const matrix: number[][] = []
    
    for (let i = 0; i <= str2.length; i++) {
      matrix[i] = [i]
    }
    
    for (let j = 0; j <= str1.length; j++) {
      matrix[0][j] = j
    }
    
    for (let i = 1; i <= str2.length; i++) {
      for (let j = 1; j <= str1.length; j++) {
        if (str2.charAt(i - 1) === str1.charAt(j - 1)) {
          matrix[i][j] = matrix[i - 1][j - 1]
        } else {
          matrix[i][j] = Math.min(
            matrix[i - 1][j - 1] + 1, // substitution
            matrix[i][j - 1] + 1,     // insertion
            matrix[i - 1][j] + 1      // deletion
          )
        }
      }
    }
    
    return matrix[str2.length][str1.length]
  }

  private calculateScore(query: string, match: string, distance: number): number {
    const exactMatch = query === match ? 100 : 0
    const prefixMatch = match.startsWith(query) ? 50 : 0
    const lengthPenalty = Math.abs(query.length - match.length) * 2
    const distancePenalty = distance * 10
    
    return Math.max(0, exactMatch + prefixMatch - lengthPenalty - distancePenalty)
  }

  // Get search suggestions/autocomplete
  getAutocompleteSuggestions(prefix: string, limit: number = 5): string[] {
    const suggestions: string[] = []
    const node = this._findPrefixNode(prefix.toLowerCase())
    
    if (node) {
      this._collectSuggestions(node, prefix.toLowerCase(), suggestions, limit)
    }
    
    return suggestions
  }

  private _findPrefixNode(prefix: string): TrieNode | null {
    let current = this.root
    
    for (const char of prefix) {
      if (!current.children.has(char)) {
        return null
      }
      current = current.children.get(char)!
    }
    
    return current
  }

  private _collectSuggestions(node: TrieNode, currentPrefix: string, suggestions: string[], limit: number): void {
    if (suggestions.length >= limit) return
    
    if (node.isEndOfWord) {
      suggestions.push(currentPrefix)
    }
    
    for (const [char, childNode] of Array.from(node.children.entries())) {
      this._collectSuggestions(childNode, currentPrefix + char, suggestions, limit)
    }
  }

  getAnalytics(): SearchAnalytics {
    return { ...this.analytics }
  }
}

// Advanced inventory with machine learning-like features
export class AdvancedInventoryManager extends SnackInventory {
  private demandPrediction: Map<string, number[]>
  private priceOptimization: Map<string, { optimalPrice: number; demandElasticity: number }>
  private customerSegments: Map<string, string[]> // userId -> preferences

  constructor() {
    super()
    this.demandPrediction = new Map()
    this.priceOptimization = new Map()
    this.customerSegments = new Map()
  }

  // Advanced analytics with time-series analysis
  predictDemand(snackId: string, days: number = 7): number[] {
    const historicalData = this.demandPrediction.get(snackId) || []
    
    if (historicalData.length < 3) {
      return new Array(days).fill(0)
    }

    // Simple moving average prediction (can be enhanced with more complex algorithms)
    const windowSize = Math.min(7, historicalData.length)
    const recentData = historicalData.slice(-windowSize)
    const average = recentData.reduce((sum, val) => sum + val, 0) / recentData.length
    
    // Add trend analysis
    const trend = this.calculateTrend(recentData)
    
    const predictions: number[] = []
    for (let i = 0; i < days; i++) {
      const predicted = Math.max(0, average + (trend * i))
      predictions.push(Math.round(predicted))
    }
    
    return predictions
  }

  private calculateTrend(data: number[]): number {
    if (data.length < 2) return 0
    
    let sumX = 0, sumY = 0, sumXY = 0, sumXX = 0
    const n = data.length
    
    for (let i = 0; i < n; i++) {
      sumX += i
      sumY += data[i]
      sumXY += i * data[i]
      sumXX += i * i
    }
    
    return (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
  }

  // Dynamic pricing optimization
  optimizePrice(snackId: string, currentPrice: number, demandData: number[]): number {
    if (demandData.length < 2) return currentPrice
    
    // Calculate price elasticity of demand
    const elasticity = this.calculatePriceElasticity(demandData)
    
    // Optimize for maximum revenue (price * demand)
    let optimalPrice = currentPrice
    let maxRevenue = currentPrice * (demandData[demandData.length - 1] || 0)
    
    // Test price variations
    for (let priceMultiplier = 0.8; priceMultiplier <= 1.5; priceMultiplier += 0.1) {
      const testPrice = currentPrice * priceMultiplier
      const predictedDemand = this.predictDemandAtPrice(demandData, elasticity, priceMultiplier)
      const revenue = testPrice * predictedDemand
      
      if (revenue > maxRevenue) {
        maxRevenue = revenue
        optimalPrice = testPrice
      }
    }
    
    this.priceOptimization.set(snackId, {
      optimalPrice,
      demandElasticity: elasticity
    })
    
    return Math.round(optimalPrice * 100) / 100
  }

  private calculatePriceElasticity(demandData: number[]): number {
    // Simplified elasticity calculation
    if (demandData.length < 2) return -1
    
    const recent = demandData.slice(-5)
    const changeInDemand = (recent[recent.length - 1] - recent[0]) / recent[0]
    const changeInPrice = 0.1 // Assume 10% price change
    
    return changeInDemand / changeInPrice
  }

  private predictDemandAtPrice(historicalDemand: number[], elasticity: number, priceChange: number): number {
    const baselineDemand = historicalDemand[historicalDemand.length - 1] || 0
    const demandChange = elasticity * (priceChange - 1)
    return Math.max(0, baselineDemand * (1 + demandChange))
  }

  // Customer segmentation and recommendation
  analyzeCustomerPreferences(userId: string, orderHistory: SnackOrder[]): string[] {
    const categoryFrequency = new Map<string, number>()
    const priceRangePreference = { low: 0, medium: 0, high: 0 }
    
    orderHistory.forEach(order => {
      order.items.forEach(item => {
        const snack = this.getSnack(item.snackId)
        if (snack) {
          categoryFrequency.set(snack.category, (categoryFrequency.get(snack.category) || 0) + 1)
          
          if (snack.sellingPrice < 20) priceRangePreference.low++
          else if (snack.sellingPrice < 50) priceRangePreference.medium++
          else priceRangePreference.high++
        }
      })
    })
    
    // Determine customer segment
    const preferences: string[] = []
    
    // Top categories
    const sortedCategories = Array.from(categoryFrequency.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 3)
      .map(([category]) => category)
    
    preferences.push(...sortedCategories)
    
    // Price preference
    const maxPriceRange = Object.entries(priceRangePreference)
      .reduce((max, [range, count]) => count > max.count ? { range, count } : max, { range: 'medium', count: 0 })
    
    preferences.push(`price_${maxPriceRange.range}`)
    
    this.customerSegments.set(userId, preferences)
    return preferences
  }

  // Generate personalized recommendations
  getPersonalizedRecommendations(userId: string, limit: number = 5): Snack[] {
    const preferences = this.customerSegments.get(userId) || []
    const availableSnacks = this.getAvailableSnacks()
    
    if (preferences.length === 0) {
      // Return popular items for new users
      return this.getTopSellingSnacks(limit).map(item => item.snack)
    }
    
    // Score snacks based on user preferences
    const scoredSnacks = availableSnacks.map(snack => {
      let score = 0
      
      // Category preference
      if (preferences.includes(snack.category)) {
        score += 10
      }
      
      // Price preference
      const priceRange = snack.sellingPrice < 20 ? 'low' : snack.sellingPrice < 50 ? 'medium' : 'high'
      if (preferences.includes(`price_${priceRange}`)) {
        score += 5
      }
      
      // Popularity bonus
      const stats = this.getSnackStats(snack.id)
      if (stats) {
        score += Math.log(stats.totalSold + 1)
      }
      
      return { snack, score }
    })
    
    return scoredSnacks
      .sort((a, b) => b.score - a.score)
      .slice(0, limit)
      .map(item => item.snack)
  }
}

export type { Snack, SnackOrder, SnackOrderItem, SnackStats, TrieNode, SnackSearchTrie, SnackInventory }
