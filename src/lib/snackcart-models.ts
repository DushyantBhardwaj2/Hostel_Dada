// SnackCart Types and Models with DSA implementations

export interface Snack {
  id: string
  name: string
  description: string
  costPrice: number
  sellingPrice: number
  quantity: number
  category: string
  imageUrl?: string
  createdAt: Date
  updatedAt: Date
}

export interface SnackOrderItem {
  snackId: string
  snackName: string
  quantity: number
  unitPrice: number
  totalPrice: number
}

export interface SnackOrder {
  id: string
  userId: string
  snackId: string
  quantity: number
  items: SnackOrderItem[]
  totalAmount: number
  status: 'pending' | 'completed' | 'cancelled'
  paymentMethod: 'cod' | 'online'
  createdAt: Date
  deliveredAt?: Date
}

export interface SnackStats {
  totalSold: number
  totalProfit: number
  popularityScore: number
  totalRevenue: number
  totalOrders: number
  activeUsers: number
  averageOrderValue: number
  topSellingSnacks: Array<{
    snackId: string
    snackName: string
    totalSold: number
  }>
  revenueByCategory: Record<string, number>
}

// DSA Implementation: Trie for efficient search
export class TrieNode {
  children: Map<string, TrieNode>
  isEndOfWord: boolean
  snackIds: Set<string>

  constructor() {
    this.children = new Map()
    this.isEndOfWord = false
    this.snackIds = new Set()
  }
}

export class SnackSearchTrie {
  private root: TrieNode

  constructor() {
    this.root = new TrieNode()
  }

  // Insert snack name into trie for search
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

  // Search for snacks by prefix
  searchByPrefix(prefix: string): Set<string> {
    let current = this.root
    const normalizedPrefix = prefix.toLowerCase()

    for (const char of normalizedPrefix) {
      if (!current.children.has(char)) {
        return new Set()
      }
      current = current.children.get(char)!
    }

    return current.snackIds
  }

  // Remove snack from trie
  remove(word: string, snackId: string): void {
    this._removeHelper(this.root, word.toLowerCase(), 0, snackId)
  }

  private _removeHelper(node: TrieNode, word: string, index: number, snackId: string): boolean {
    if (index === word.length) {
      node.snackIds.delete(snackId)
      return node.snackIds.size === 0 && node.children.size === 0
    }

    const char = word[index]
    const nextNode = node.children.get(char)
    
    if (!nextNode) return false

    const shouldDeleteChild = this._removeHelper(nextNode, word, index + 1, snackId)
    nextNode.snackIds.delete(snackId)

    if (shouldDeleteChild) {
      node.children.delete(char)
    }

    return node.snackIds.size === 0 && node.children.size === 0
  }
}

// DSA Implementation: Hash Map for O(1) inventory lookup
export class SnackInventory {
  private snacks: Map<string, Snack>
  private categoryIndex: Map<string, Set<string>>
  private searchTrie: SnackSearchTrie
  private stats: Map<string, SnackStats>

  constructor() {
    this.snacks = new Map()
    this.categoryIndex = new Map()
    this.searchTrie = new SnackSearchTrie()
    this.stats = new Map()
  }

  // Add snack to inventory - O(1) insertion
  addSnack(snack: Snack): void {
    this.snacks.set(snack.id, snack)
    
    // Update category index
    if (!this.categoryIndex.has(snack.category)) {
      this.categoryIndex.set(snack.category, new Set())
    }
    this.categoryIndex.get(snack.category)!.add(snack.id)

    // Add to search trie
    this.searchTrie.insert(snack.name, snack.id)
    
    // Initialize stats
    this.stats.set(snack.id, {
      totalSold: 0,
      totalProfit: 0,
      popularityScore: 0,
      totalRevenue: 0,
      totalOrders: 0,
      activeUsers: 0,
      averageOrderValue: 0,
      topSellingSnacks: [],
      revenueByCategory: {}
    })
  }

  // Get snack by ID - O(1) lookup
  getSnack(id: string): Snack | undefined {
    return this.snacks.get(id)
  }

  // Search snacks by name - O(prefix length) using Trie
  searchSnacks(query: string): Snack[] {
    const snackIds = this.searchTrie.searchByPrefix(query)
    return Array.from(snackIds)
      .map(id => this.snacks.get(id))
      .filter((snack): snack is Snack => snack !== undefined && snack.quantity > 0)
  }

  // Get snacks by category - O(category size)
  getSnacksByCategory(category: string): Snack[] {
    const snackIds = this.categoryIndex.get(category) || new Set()
    return Array.from(snackIds)
      .map(id => this.snacks.get(id))
      .filter((snack): snack is Snack => snack !== undefined && snack.quantity > 0)
  }

  // Update inventory after order
  updateInventory(snackId: string, quantitySold: number): boolean {
    const snack = this.snacks.get(snackId)
    if (!snack || snack.quantity < quantitySold) {
      return false
    }

    snack.quantity -= quantitySold
    snack.updatedAt = new Date()

    // Update stats
    const stats = this.stats.get(snackId)!
    stats.totalSold += quantitySold
    stats.totalProfit += (snack.sellingPrice - snack.costPrice) * quantitySold
    stats.popularityScore += quantitySold * 10 // Weight factor

    return true
  }

  // Get all available snacks
  getAvailableSnacks(): Snack[] {
    return Array.from(this.snacks.values()).filter(snack => snack.quantity > 0)
  }

  // Get top selling snacks - uses sorting algorithm
  getTopSellingSnacks(limit: number = 10): { snack: Snack; stats: SnackStats }[] {
    const snackStatsArray = Array.from(this.snacks.values())
      .map(snack => ({
        snack,
        stats: this.stats.get(snack.id)!
      }))
      .filter(item => item.stats.totalSold > 0)

    // Merge Sort implementation for stable sorting
    return this.mergeSort(snackStatsArray, (a, b) => b.stats.totalSold - a.stats.totalSold)
      .slice(0, limit)
  }

  // Get most profitable snacks
  getMostProfitableSnacks(limit: number = 10): { snack: Snack; stats: SnackStats }[] {
    const snackStatsArray = Array.from(this.snacks.values())
      .map(snack => ({
        snack,
        stats: this.stats.get(snack.id)!
      }))
      .filter(item => item.stats.totalProfit > 0)

    return this.mergeSort(snackStatsArray, (a, b) => b.stats.totalProfit - a.stats.totalProfit)
      .slice(0, limit)
  }

  // DSA Implementation: Merge Sort for stable sorting
  private mergeSort<T>(arr: T[], compareFn: (a: T, b: T) => number): T[] {
    if (arr.length <= 1) return arr

    const mid = Math.floor(arr.length / 2)
    const left = this.mergeSort(arr.slice(0, mid), compareFn)
    const right = this.mergeSort(arr.slice(mid), compareFn)

    return this.merge(left, right, compareFn)
  }

  private merge<T>(left: T[], right: T[], compareFn: (a: T, b: T) => number): T[] {
    const result: T[] = []
    let leftIndex = 0
    let rightIndex = 0

    while (leftIndex < left.length && rightIndex < right.length) {
      if (compareFn(left[leftIndex], right[rightIndex]) <= 0) {
        result.push(left[leftIndex])
        leftIndex++
      } else {
        result.push(right[rightIndex])
        rightIndex++
      }
    }

    return result.concat(left.slice(leftIndex)).concat(right.slice(rightIndex))
  }

  // Remove snack from inventory
  removeSnack(id: string): boolean {
    const snack = this.snacks.get(id)
    if (!snack) return false

    this.snacks.delete(id)
    this.categoryIndex.get(snack.category)?.delete(id)
    this.searchTrie.remove(snack.name, id)
    this.stats.delete(id)

    return true
  }

  // Get inventory statistics
  getInventoryStats() {
    const totalSnacks = this.snacks.size
    const totalValue = Array.from(this.snacks.values())
      .reduce((sum, snack) => sum + (snack.sellingPrice * snack.quantity), 0)
    const totalProfit = Array.from(this.stats.values())
      .reduce((sum, stats) => sum + stats.totalProfit, 0)
    const outOfStock = Array.from(this.snacks.values())
      .filter(snack => snack.quantity === 0).length

    return {
      totalSnacks,
      totalValue,
      totalProfit,
      outOfStock,
      categories: this.categoryIndex.size
    }
  }

  // Get stats for a specific snack
  getSnackStats(snackId: string): SnackStats | undefined {
    return this.stats.get(snackId)
  }
}
