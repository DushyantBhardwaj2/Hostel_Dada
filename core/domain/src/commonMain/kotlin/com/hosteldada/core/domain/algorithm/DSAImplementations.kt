package com.hosteldada.core.domain.algorithm

import com.hosteldada.core.domain.model.*

/**
 * ============================================
 * DATA STRUCTURE & ALGORITHM IMPLEMENTATIONS
 * ============================================
 * 
 * Custom DSA implementations for optimal performance.
 * Designed for placement interviews demonstration.
 */

// ==========================================
// 1. TRIE - Snack Search
// ==========================================

/**
 * Trie (Prefix Tree) for efficient snack search.
 * 
 * Time Complexity:
 * - Insert: O(k) where k is word length
 * - Search: O(k + m) where m is number of results
 * - Space: O(n * k) where n is number of words
 * 
 * Use Case: Instant search suggestions as user types
 */
class SnackSearchTrie {
    
    private class TrieNode {
        val children = mutableMapOf<Char, TrieNode>()
        val snacks = mutableListOf<Snack>()
        var isEndOfWord = false
    }
    
    private var root = TrieNode()
    private var size = 0
    
    /**
     * Insert a snack into the trie.
     * Indexes by name and tags for comprehensive search.
     * 
     * Time: O(k) where k is name length
     */
    fun insert(snack: Snack) {
        // Insert by name
        insertWord(snack.name.lowercase(), snack)
        
        // Insert by tags for better searchability
        snack.tags.forEach { tag ->
            insertWord(tag.lowercase(), snack)
        }
        
        // Insert by category
        insertWord(snack.category.name.lowercase(), snack)
        
        size++
    }
    
    private fun insertWord(word: String, snack: Snack) {
        var current = root
        
        for (char in word) {
            current = current.children.getOrPut(char) { TrieNode() }
            // Add snack at each prefix node for prefix matching
            if (!current.snacks.contains(snack)) {
                current.snacks.add(snack)
            }
        }
        
        current.isEndOfWord = true
    }
    
    /**
     * Search for snacks by prefix.
     * Returns all snacks whose name/tag starts with the prefix.
     * 
     * Time: O(k + m) where k is prefix length, m is results
     */
    fun search(prefix: String): List<Snack> {
        if (prefix.isBlank()) return emptyList()
        
        var current = root
        val lowerPrefix = prefix.lowercase()
        
        // Navigate to prefix end node
        for (char in lowerPrefix) {
            current = current.children[char] ?: return emptyList()
        }
        
        // Return all snacks at this node (includes all with this prefix)
        return current.snacks
            .filter { it.isAvailable }
            .distinctBy { it.id }
            .sortedBy { it.name }
    }
    
    /**
     * Get autocomplete suggestions.
     * Returns word completions for the given prefix.
     * 
     * Time: O(k + n) where n is number of words with prefix
     */
    fun getSuggestions(prefix: String, limit: Int = 10): List<String> {
        if (prefix.isBlank()) return emptyList()
        
        var current = root
        val lowerPrefix = prefix.lowercase()
        
        for (char in lowerPrefix) {
            current = current.children[char] ?: return emptyList()
        }
        
        val suggestions = mutableListOf<String>()
        collectWords(current, StringBuilder(lowerPrefix), suggestions, limit)
        return suggestions
    }
    
    private fun collectWords(
        node: TrieNode,
        currentWord: StringBuilder,
        results: MutableList<String>,
        limit: Int
    ) {
        if (results.size >= limit) return
        
        if (node.isEndOfWord) {
            results.add(currentWord.toString())
        }
        
        for ((char, child) in node.children) {
            currentWord.append(char)
            collectWords(child, currentWord, results, limit)
            currentWord.deleteAt(currentWord.length - 1)
        }
    }
    
    /**
     * Clear the trie.
     * Time: O(1)
     */
    fun clear() {
        root = TrieNode()
        size = 0
    }
    
    fun getSize(): Int = size
}

// ==========================================
// 2. GRAPH - Compatibility Matching
// ==========================================

/**
 * Weighted Graph for roommate compatibility matching.
 * Uses adjacency list representation.
 * 
 * Scoring Algorithm:
 * - Lifestyle: 20% weight
 * - Study Habits: 20% weight
 * - Cleanliness: 20% weight
 * - Social: 15% weight
 * - Sleep: 15% weight
 * - Personality: 10% weight
 * 
 * Time Complexity:
 * - Add student: O(1)
 * - Calculate edge: O(1)
 * - Get matches: O(n log n) due to sorting
 * - Generate all: O(n²)
 */
class CompatibilityGraph {
    
    // Graph node representing a student with their survey
    private data class StudentNode(
        val studentId: String,
        val survey: RoommateSurvey,
        val edges: MutableMap<String, CompatibilityScore> = mutableMapOf()
    )
    
    // Adjacency list representation
    private val students = mutableMapOf<String, StudentNode>()
    
    // Scoring weights (total = 100%)
    private object Weights {
        const val LIFESTYLE = 0.20
        const val STUDY = 0.20
        const val CLEANLINESS = 0.20
        const val SOCIAL = 0.15
        const val SLEEP = 0.15
        const val PERSONALITY = 0.10
    }
    
    /**
     * Add a student to the graph.
     * Time: O(1)
     */
    fun addStudent(studentId: String, survey: RoommateSurvey) {
        students[studentId] = StudentNode(studentId, survey)
    }
    
    /**
     * Calculate compatibility edge between two students.
     * Time: O(1)
     */
    fun calculateEdge(studentId1: String, studentId2: String): CompatibilityScore? {
        val node1 = students[studentId1] ?: return null
        val node2 = students[studentId2] ?: return null
        
        // Check cache first
        node1.edges[studentId2]?.let { return it }
        
        // Calculate individual scores
        val lifestyleScore = calculateLifestyleScore(node1.survey.lifestyle, node2.survey.lifestyle)
        val studyScore = calculateStudyScore(node1.survey.studyHabits, node2.survey.studyHabits)
        val cleanlinessScore = calculateCleanlinessScore(node1.survey.cleanliness, node2.survey.cleanliness)
        val socialScore = calculateSocialScore(node1.survey.socialPreferences, node2.survey.socialPreferences)
        val sleepScore = calculateSleepScore(node1.survey.sleepSchedule, node2.survey.sleepSchedule)
        val personalityScore = calculatePersonalityScore(node1.survey.personalityTraits, node2.survey.personalityTraits)
        
        // Weighted overall score
        val overallScore = (
            lifestyleScore * Weights.LIFESTYLE +
            studyScore * Weights.STUDY +
            cleanlinessScore * Weights.CLEANLINESS +
            socialScore * Weights.SOCIAL +
            sleepScore * Weights.SLEEP +
            personalityScore * Weights.PERSONALITY
        ).toInt()
        
        // Generate match reasons and warnings
        val matchReasons = generateMatchReasons(
            lifestyleScore, studyScore, cleanlinessScore, 
            socialScore, sleepScore, personalityScore
        )
        val warnings = generateWarnings(node1.survey, node2.survey)
        
        val score = CompatibilityScore(
            id = "${studentId1}_${studentId2}",
            studentId1 = studentId1,
            studentId2 = studentId2,
            overallScore = overallScore,
            lifestyleScore = lifestyleScore,
            studyScore = studyScore,
            cleanlinessScore = cleanlinessScore,
            socialScore = socialScore,
            sleepScore = sleepScore,
            personalityScore = personalityScore,
            matchReasons = matchReasons,
            warnings = warnings,
            calculatedAt = System.currentTimeMillis()
        )
        
        // Cache the edge (bidirectional)
        node1.edges[studentId2] = score
        node2.edges[studentId1] = score
        
        return score
    }
    
    /**
     * Get top matches for a student.
     * Time: O(n log n) due to sorting
     */
    fun getTopMatches(studentId: String, limit: Int = 10): List<CompatibilityScore> {
        val node = students[studentId] ?: return emptyList()
        
        // Calculate edges with all other students if not already done
        students.keys
            .filter { it != studentId && !node.edges.containsKey(it) }
            .forEach { otherId ->
                calculateEdge(studentId, otherId)
            }
        
        return node.edges.values
            .sortedByDescending { it.overallScore }
            .take(limit)
    }
    
    /**
     * Get all edges (compatibility scores) in the graph.
     * Time: O(n)
     */
    fun getAllEdges(): List<CompatibilityScore> {
        val allEdges = mutableSetOf<CompatibilityScore>()
        students.values.forEach { node ->
            allEdges.addAll(node.edges.values)
        }
        return allEdges.toList()
    }
    
    /**
     * Clear the graph.
     */
    fun clear() {
        students.clear()
    }
    
    // ==========================================
    // SCORING ALGORITHMS
    // ==========================================
    
    private fun calculateLifestyleScore(l1: LifestylePreferences, l2: LifestylePreferences): Int {
        var score = 0
        
        // Sleep time similarity (0-25 points)
        val sleepDiff = kotlin.math.abs(parseTime(l1.sleepTime) - parseTime(l2.sleepTime))
        score += maxOf(0, 25 - sleepDiff / 30) // Lose 1 point per 30 min difference
        
        // Wake time similarity (0-25 points)
        val wakeDiff = kotlin.math.abs(parseTime(l1.wakeTime) - parseTime(l2.wakeTime))
        score += maxOf(0, 25 - wakeDiff / 30)
        
        // Food preference (0-20 points)
        score += if (l1.foodPreference == l2.foodPreference) 20 else 10
        
        // Smoking/Drinking habits (0-30 points)
        if (l1.smokingHabit == l2.smokingHabit) score += 15
        if (l1.drinkingHabit == l2.drinkingHabit) score += 15
        
        return score
    }
    
    private fun calculateStudyScore(s1: StudyHabits, s2: StudyHabits): Int {
        var score = 0
        
        // Study style match (0-30 points)
        score += if (s1.studyStyle == s2.studyStyle) 30 else 15
        
        // Quiet environment needs (0-25 points)
        score += if (s1.needsQuietEnvironment == s2.needsQuietEnvironment) 25 else 10
        
        // Study time preference (0-25 points)
        score += if (s1.preferredStudyTime == s2.preferredStudyTime) 25 else 12
        
        // Music while studying (0-20 points)
        score += if (s1.musicWhileStudying == s2.musicWhileStudying) 20 else 10
        
        return score
    }
    
    private fun calculateCleanlinessScore(c1: CleanlinessPreferences, c2: CleanlinessPreferences): Int {
        var score = 0
        
        // Cleaning frequency (0-35 points)
        score += if (c1.cleaningFrequency == c2.cleaningFrequency) 35 else 17
        
        // Organization level similarity (0-35 points)
        val orgDiff = kotlin.math.abs(c1.organizationLevel - c2.organizationLevel)
        score += maxOf(0, 35 - orgDiff * 10)
        
        // Shared items comfort (0-30 points)
        val sharedDiff = kotlin.math.abs(c1.sharedItemsComfort - c2.sharedItemsComfort)
        score += maxOf(0, 30 - sharedDiff * 8)
        
        return score
    }
    
    private fun calculateSocialScore(s1: SocialPreferences, s2: SocialPreferences): Int {
        var score = 0
        
        // Visitor frequency (0-35 points)
        score += if (s1.visitorFrequency == s2.visitorFrequency) 35 else 17
        
        // Party attitude (0-30 points)
        score += if (s1.partyAttitude == s2.partyAttitude) 30 else 15
        
        // Privacy needs similarity (0-35 points)
        val privacyDiff = kotlin.math.abs(s1.privacyNeeds - s2.privacyNeeds)
        score += maxOf(0, 35 - privacyDiff * 10)
        
        return score
    }
    
    private fun calculateSleepScore(s1: SleepSchedule, s2: SleepSchedule): Int {
        var score = 0
        
        // Bedtime similarity (0-35 points)
        val bedDiff = kotlin.math.abs(parseTime(s1.typicalBedtime) - parseTime(s2.typicalBedtime))
        score += maxOf(0, 35 - bedDiff / 20)
        
        // Wake time similarity (0-35 points)
        val wakeDiff = kotlin.math.abs(parseTime(s1.typicalWakeTime) - parseTime(s2.typicalWakeTime))
        score += maxOf(0, 35 - wakeDiff / 20)
        
        // Sleep sensitivity (0-30 points)
        score += if (s1.sleepSensitivity == s2.sleepSensitivity) 30 else 15
        
        return score
    }
    
    private fun calculatePersonalityScore(p1: PersonalityTraits, p2: PersonalityTraits): Int {
        var score = 0
        
        // Introvert/Extrovert compatibility (0-40 points)
        // Similar levels or complementary (both moderate) work best
        val ieDiff = kotlin.math.abs(p1.introvertExtrovert - p2.introvertExtrovert)
        score += maxOf(0, 40 - ieDiff * 12)
        
        // Conflict resolution (0-30 points)
        score += if (p1.conflictResolution == p2.conflictResolution) 30 else 15
        
        // Adaptability (0-30 points)
        val adaptDiff = kotlin.math.abs(p1.adaptability - p2.adaptability)
        score += maxOf(0, 30 - adaptDiff * 8)
        
        return score
    }
    
    // Helper to parse time string to minutes
    private fun parseTime(time: String): Int {
        // Format: "11:00 PM" or "7:00 AM"
        val parts = time.replace(" AM", "").replace(" PM", "").split(":")
        var hours = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
        
        if (time.contains("PM") && hours != 12) hours += 12
        if (time.contains("AM") && hours == 12) hours = 0
        
        return hours * 60 + minutes
    }
    
    private fun generateMatchReasons(
        lifestyle: Int, study: Int, cleanliness: Int,
        social: Int, sleep: Int, personality: Int
    ): List<String> {
        val reasons = mutableListOf<String>()
        
        if (lifestyle >= 80) reasons.add("🏠 Similar lifestyle habits")
        if (study >= 80) reasons.add("📚 Compatible study preferences")
        if (cleanliness >= 80) reasons.add("🧹 Similar cleanliness standards")
        if (social >= 80) reasons.add("👥 Matching social preferences")
        if (sleep >= 80) reasons.add("😴 Compatible sleep schedules")
        if (personality >= 80) reasons.add("🧠 Complementary personalities")
        
        return reasons
    }
    
    private fun generateWarnings(s1: RoommateSurvey, s2: RoommateSurvey): List<String> {
        val warnings = mutableListOf<String>()
        
        // Smoking conflict
        if (s1.lifestyle.smokingHabit != s2.lifestyle.smokingHabit) {
            warnings.add("⚠️ Different smoking habits")
        }
        
        // Food preference conflict
        if (s1.lifestyle.foodPreference != s2.lifestyle.foodPreference) {
            warnings.add("⚠️ Different food preferences")
        }
        
        // Extreme sleep schedule difference
        val bedDiff = kotlin.math.abs(
            parseTime(s1.sleepSchedule.typicalBedtime) - 
            parseTime(s2.sleepSchedule.typicalBedtime)
        )
        if (bedDiff > 180) { // More than 3 hours
            warnings.add("⚠️ Significantly different sleep schedules")
        }
        
        return warnings
    }
}

// ==========================================
// 3. PRIORITY QUEUE - Maintenance Requests
// ==========================================

/**
 * Max-Heap Priority Queue for maintenance request handling.
 * Higher priority requests are processed first.
 * 
 * Time Complexity:
 * - Insert: O(log n)
 * - Extract Max: O(log n)
 * - Peek: O(1)
 * - Space: O(n)
 * 
 * Use Case: Prioritize urgent maintenance requests
 */
class MaintenancePriorityQueue {
    
    private val heap = mutableListOf<MaintenanceRequest>()
    
    /**
     * Insert a request into the queue.
     * Time: O(log n)
     */
    fun insert(request: MaintenanceRequest) {
        heap.add(request)
        heapifyUp(heap.size - 1)
    }
    
    /**
     * Extract the highest priority request.
     * Time: O(log n)
     */
    fun extractMax(): MaintenanceRequest? {
        if (heap.isEmpty()) return null
        if (heap.size == 1) return heap.removeAt(0)
        
        val max = heap[0]
        heap[0] = heap.removeAt(heap.size - 1)
        heapifyDown(0)
        
        return max
    }
    
    /**
     * Peek at the highest priority request without removing.
     * Time: O(1)
     */
    fun peek(): MaintenanceRequest? = heap.firstOrNull()
    
    /**
     * Get all requests sorted by priority.
     * Time: O(n log n)
     */
    fun getAllSorted(): List<MaintenanceRequest> {
        return heap.sortedByDescending { getPriority(it) }
    }
    
    /**
     * Remove a specific request.
     * Time: O(n)
     */
    fun remove(requestId: String): Boolean {
        val index = heap.indexOfFirst { it.id == requestId }
        if (index == -1) return false
        
        val lastIndex = heap.size - 1
        if (index != lastIndex) {
            heap[index] = heap.removeAt(lastIndex)
            heapifyDown(index)
            heapifyUp(index)
        } else {
            heap.removeAt(index)
        }
        
        return true
    }
    
    /**
     * Update priority of an existing request.
     * Time: O(n + log n)
     */
    fun updatePriority(requestId: String, newPriority: Int): Boolean {
        val index = heap.indexOfFirst { it.id == requestId }
        if (index == -1) return false
        
        val oldRequest = heap[index]
        heap[index] = oldRequest.copy(priority = newPriority)
        
        // Re-heapify
        heapifyUp(index)
        heapifyDown(index)
        
        return true
    }
    
    fun size(): Int = heap.size
    
    fun isEmpty(): Boolean = heap.isEmpty()
    
    fun clear() = heap.clear()
    
    // ==========================================
    // HEAP OPERATIONS
    // ==========================================
    
    private fun heapifyUp(index: Int) {
        var currentIndex = index
        
        while (currentIndex > 0) {
            val parentIndex = (currentIndex - 1) / 2
            
            if (getPriority(heap[currentIndex]) > getPriority(heap[parentIndex])) {
                swap(currentIndex, parentIndex)
                currentIndex = parentIndex
            } else {
                break
            }
        }
    }
    
    private fun heapifyDown(index: Int) {
        var currentIndex = index
        val size = heap.size
        
        while (true) {
            val leftChild = 2 * currentIndex + 1
            val rightChild = 2 * currentIndex + 2
            var largest = currentIndex
            
            if (leftChild < size && 
                getPriority(heap[leftChild]) > getPriority(heap[largest])) {
                largest = leftChild
            }
            
            if (rightChild < size && 
                getPriority(heap[rightChild]) > getPriority(heap[largest])) {
                largest = rightChild
            }
            
            if (largest != currentIndex) {
                swap(currentIndex, largest)
                currentIndex = largest
            } else {
                break
            }
        }
    }
    
    private fun swap(i: Int, j: Int) {
        val temp = heap[i]
        heap[i] = heap[j]
        heap[j] = temp
    }
    
    /**
     * Calculate effective priority.
     * Considers:
     * - Base priority (1-5)
     * - Category urgency multiplier
     * - Age of request (older = higher priority)
     */
    private fun getPriority(request: MaintenanceRequest): Int {
        val basePriority = request.priority * 100
        
        // Category urgency multiplier
        val categoryMultiplier = when (request.category) {
            MaintenanceCategory.ELECTRICAL -> 1.5
            MaintenanceCategory.PLUMBING -> 1.4
            MaintenanceCategory.AC_COOLING -> 1.2
            MaintenanceCategory.FURNITURE -> 1.0
            MaintenanceCategory.CLEANING -> 0.8
            MaintenanceCategory.OTHER -> 0.9
        }
        
        // Age bonus (older requests get slightly higher priority)
        val ageInHours = (System.currentTimeMillis() - request.createdAt) / (1000 * 60 * 60)
        val ageBonus = minOf(ageInHours.toInt(), 50) // Cap at 50 bonus points
        
        return ((basePriority * categoryMultiplier) + ageBonus).toInt()
    }
}

// ==========================================
// 4. HASH MAP UTILITIES
// ==========================================

/**
 * LRU Cache for frequently accessed data.
 * Time: O(1) for get/put operations.
 */
class LRUCache<K, V>(private val capacity: Int) {
    
    private val cache = linkedMapOf<K, V>()
    
    fun get(key: K): V? {
        val value = cache.remove(key) ?: return null
        cache[key] = value // Move to end (most recently used)
        return value
    }
    
    fun put(key: K, value: V) {
        cache.remove(key)
        if (cache.size >= capacity) {
            val eldest = cache.keys.first()
            cache.remove(eldest)
        }
        cache[key] = value
    }
    
    fun remove(key: K) {
        cache.remove(key)
    }
    
    fun clear() = cache.clear()
    
    fun size() = cache.size
}
