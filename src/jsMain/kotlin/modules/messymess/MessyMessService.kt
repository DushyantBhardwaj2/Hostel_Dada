package modules.messymess

import modules.messymess.*
import kotlin.js.Promise
import kotlin.math.*
import kotlin.collections.*
import kotlin.random.Random

/**
 * 🍽️ MessyMess Service - Advanced Food & Feedback Management
 * 
 * DSA Features:
 * - Weighted rating aggregation with anti-spam algorithms
 * - Nutritional optimization using linear programming principles
 * - Menu recommendation engine with collaborative filtering
 * - Sentiment analysis for review classification
 * - Inventory prediction using time-series analysis
 */

class MessyMessService {
    
    // 📊 Advanced Rating Aggregation System
    class RatingAggregator {
        
        // Weighted rating calculation with spam detection
        fun calculateWeightedRating(reviews: List<FoodReview>): ReviewAnalytics {
            if (reviews.isEmpty()) {
                return ReviewAnalytics(0.0, 0, emptyMap(), emptyMap())
            }
            
            val filteredReviews = filterSpamReviews(reviews)
            val weights = calculateReviewWeights(filteredReviews)
            
            var weightedSum = 0.0
            var totalWeight = 0.0
            
            filteredReviews.forEachIndexed { index, review ->
                val weight = weights[index]
                weightedSum += review.overallRating * weight
                totalWeight += weight
            }
            
            val overallRating = if (totalWeight > 0) weightedSum / totalWeight else 0.0
            
            return ReviewAnalytics(
                overallRating = overallRating,
                totalReviews = filteredReviews.size,
                aspectRatings = calculateAspectRatings(filteredReviews, weights),
                sentimentDistribution = analyzeSentimentDistribution(filteredReviews),
                trendAnalysis = analyzeTrends(filteredReviews),
                topKeywords = extractKeywords(filteredReviews),
                qualityScore = calculateQualityScore(filteredReviews)
            )
        }
        
        private fun filterSpamReviews(reviews: List<FoodReview>): List<FoodReview> {
            return reviews.filter { review ->
                // Anti-spam filters
                !isSpamReview(review)
            }
        }
        
        private fun isSpamReview(review: FoodReview): Boolean {
            // Multiple spam detection criteria
            val spamIndicators = buildList {
                // Too short reviews with extreme ratings
                if ((review.comment?.length ?: 0) < 10 && 
                    (review.overallRating == 1 || review.overallRating == 5)) {
                    add("short_extreme")
                }
                
                // All ratings exactly the same (unlikely for genuine reviews)
                if (review.tasteRating == review.qualityRating && 
                    review.qualityRating == review.serviceRating &&
                    review.serviceRating == review.valueRating) {
                    add("uniform_ratings")
                }
                
                // Excessive use of certain phrases (simplified detection)
                val comment = review.comment?.lowercase() ?: ""
                if (comment.contains("amazing") && comment.contains("best") && 
                    comment.contains("perfect")) {
                    add("excessive_praise")
                }
                
                // Very low helpfulness ratio for older reviews
                if (review.totalVotes > 10 && review.getHelpfulnessRatio() < 0.1) {
                    add("low_helpfulness")
                }
            }
            
            return spamIndicators.size >= 2 // Flag as spam if multiple indicators
        }
        
        private fun calculateReviewWeights(reviews: List<FoodReview>): List<Double> {
            return reviews.map { review ->
                var weight = 1.0
                
                // Higher weight for verified reviews
                if (review.isVerifiedReview) weight *= 1.5
                
                // Higher weight for detailed reviews
                val commentLength = review.comment?.length ?: 0
                when {
                    commentLength > 200 -> weight *= 1.3
                    commentLength > 100 -> weight *= 1.1
                    commentLength < 20 -> weight *= 0.7
                }
                
                // Weight based on helpfulness
                if (review.totalVotes > 0) {
                    weight *= (0.5 + review.getHelpfulnessRatio())
                }
                
                // Recent reviews get slightly higher weight
                val daysSinceReview = (System.currentTimeMillis() - review.reviewDate) / (1000 * 60 * 60 * 24)
                if (daysSinceReview < 30) {
                    weight *= 1.1
                }
                
                // Photos add credibility
                if (review.photoUrls.isNotEmpty()) {
                    weight *= 1.2
                }
                
                weight
            }
        }
        
        private fun calculateAspectRatings(
            reviews: List<FoodReview>, 
            weights: List<Double>
        ): Map<String, Double> {
            val aspects = mapOf(
                "taste" to reviews.map { it.tasteRating },
                "quality" to reviews.map { it.qualityRating },
                "service" to reviews.map { it.serviceRating },
                "value" to reviews.map { it.valueRating },
                "portion" to reviews.map { it.portionRating },
                "hygiene" to reviews.map { it.hygieneRating }
            )
            
            return aspects.mapValues { (_, ratings) ->
                var weightedSum = 0.0
                var totalWeight = 0.0
                
                ratings.forEachIndexed { index, rating ->
                    val weight = weights[index]
                    weightedSum += rating * weight
                    totalWeight += weight
                }
                
                if (totalWeight > 0) weightedSum / totalWeight else 0.0
            }
        }
        
        private fun analyzeSentimentDistribution(reviews: List<FoodReview>): Map<String, Double> {
            val sentiments = reviews.map { review ->
                when {
                    review.overallRating >= 4 -> "positive"
                    review.overallRating >= 3 -> "neutral"
                    else -> "negative"
                }
            }
            
            val total = sentiments.size.toDouble()
            return sentiments.groupingBy { it }.eachCount()
                .mapValues { (_, count) -> count / total * 100 }
        }
        
        private fun analyzeTrends(reviews: List<FoodReview>): Map<String, Any> {
            val sortedReviews = reviews.sortedBy { it.reviewDate }
            val recentReviews = sortedReviews.takeLast(10)
            val olderReviews = sortedReviews.dropLast(10).takeLast(10)
            
            val recentAvg = if (recentReviews.isNotEmpty()) {
                recentReviews.map { it.overallRating }.average()
            } else 0.0
            
            val olderAvg = if (olderReviews.isNotEmpty()) {
                olderReviews.map { it.overallRating }.average()
            } else recentAvg
            
            val trend = when {
                recentAvg > olderAvg + 0.3 -> "improving"
                recentAvg < olderAvg - 0.3 -> "declining"
                else -> "stable"
            }
            
            return mapOf(
                "trend" to trend,
                "recentAverage" to recentAvg,
                "previousAverage" to olderAvg,
                "changePercent" to if (olderAvg > 0) ((recentAvg - olderAvg) / olderAvg * 100) else 0.0
            )
        }
        
        private fun extractKeywords(reviews: List<FoodReview>): List<KeywordFrequency> {
            val wordCounts = mutableMapOf<String, Int>()
            
            reviews.forEach { review ->
                val words = (review.comment ?: "").lowercase()
                    .split(Regex("[^a-zA-Z]+"))
                    .filter { it.length > 3 && !isStopWord(it) }
                
                words.forEach { word ->
                    wordCounts[word] = (wordCounts[word] ?: 0) + 1
                }
                
                // Include pros and cons
                review.pros.forEach { pro ->
                    pro.lowercase().split(" ").forEach { word ->
                        if (word.length > 3) {
                            wordCounts[word] = (wordCounts[word] ?: 0) + 2 // Higher weight for pros/cons
                        }
                    }
                }
                
                review.cons.forEach { con ->
                    con.lowercase().split(" ").forEach { word ->
                        if (word.length > 3) {
                            wordCounts[word] = (wordCounts[word] ?: 0) + 2
                        }
                    }
                }
            }
            
            return wordCounts.toList()
                .sortedByDescending { it.second }
                .take(20)
                .map { (word, count) -> KeywordFrequency(word, count) }
        }
        
        private fun calculateQualityScore(reviews: List<FoodReview>): Double {
            if (reviews.isEmpty()) return 0.0
            
            val verifiedReviewsRatio = reviews.count { it.isVerifiedReview }.toDouble() / reviews.size
            val detailedReviewsRatio = reviews.count { (it.comment?.length ?: 0) > 50 }.toDouble() / reviews.size
            val helpfulReviewsRatio = reviews.count { it.getHelpfulnessRatio() > 0.5 }.toDouble() / reviews.size
            
            return (verifiedReviewsRatio * 0.4 + detailedReviewsRatio * 0.3 + helpfulReviewsRatio * 0.3) * 100
        }
        
        private fun isStopWord(word: String): Boolean {
            val stopWords = setOf(
                "the", "and", "for", "are", "but", "not", "you", "all", "can", "had", 
                "will", "been", "this", "that", "with", "have", "they", "from", "very",
                "good", "nice", "food", "mess", "hostel"
            )
            return word in stopWords
        }
        
        data class ReviewAnalytics(
            val overallRating: Double,
            val totalReviews: Int,
            val aspectRatings: Map<String, Double>,
            val sentimentDistribution: Map<String, Double>,
            val trendAnalysis: Map<String, Any> = emptyMap(),
            val topKeywords: List<KeywordFrequency> = emptyList(),
            val qualityScore: Double = 0.0
        )
        
        data class KeywordFrequency(
            val keyword: String,
            val frequency: Int
        )
    }
    
    // 🧠 Nutritional Optimization Engine
    class NutritionalOptimizer {
        
        fun optimizeMenuForNutrition(
            menuItems: List<MenuItem>,
            targetNutrition: NutritionalTargets,
            constraints: MenuConstraints
        ): OptimizedMenu {
            
            // Simplified linear programming approach for menu optimization
            val selectedItems = mutableListOf<MenuItem>()
            var currentNutrition = NutritionalInfo(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            var currentCost = 0.0
            
            // Sort items by nutritional efficiency score
            val sortedItems = menuItems.sortedByDescending { item ->
                calculateNutritionalEfficiency(item, targetNutrition)
            }
            
            for (item in sortedItems) {
                if (selectedItems.size >= constraints.maxItems) break
                if (currentCost + item.costPerServing > constraints.maxBudget) continue
                
                // Check if adding this item improves nutritional profile
                val projectedNutrition = addNutrition(currentNutrition, item.nutritionalInfo)
                val improvementScore = calculateImprovementScore(
                    currentNutrition, projectedNutrition, targetNutrition
                )
                
                if (improvementScore > 0.1) { // Threshold for meaningful improvement
                    selectedItems.add(item)
                    currentNutrition = projectedNutrition
                    currentCost += item.costPerServing
                }
            }
            
            return OptimizedMenu(
                selectedItems = selectedItems,
                nutritionalProfile = currentNutrition,
                totalCost = currentCost,
                optimizationScore = calculateOptimizationScore(currentNutrition, targetNutrition),
                recommendations = generateNutritionalRecommendations(currentNutrition, targetNutrition)
            )
        }
        
        private fun calculateNutritionalEfficiency(
            item: MenuItem, 
            targets: NutritionalTargets
        ): Double {
            val nutrition = item.nutritionalInfo
            var score = 0.0
            
            // Score based on how well the item meets nutritional targets
            score += min(nutrition.protein / targets.protein, 1.0) * 25
            score += min(nutrition.fiber / targets.fiber, 1.0) * 20
            score += (1.0 - min(nutrition.sodium / targets.maxSodium, 1.0)) * 15
            score += (1.0 - min(nutrition.sugar / targets.maxSugar, 1.0)) * 15
            score += min(nutrition.vitamins.values.sum() / 100, 1.0) * 15
            score += min(nutrition.minerals.values.sum() / 100, 1.0) * 10
            
            // Adjust for cost efficiency
            score = score / item.costPerServing * 10
            
            return score
        }
        
        private fun addNutrition(base: NutritionalInfo, addition: NutritionalInfo): NutritionalInfo {
            return NutritionalInfo(
                calories = base.calories + addition.calories,
                protein = base.protein + addition.protein,
                carbohydrates = base.carbohydrates + addition.carbohydrates,
                fat = base.fat + addition.fat,
                fiber = base.fiber + addition.fiber,
                sugar = base.sugar + addition.sugar,
                sodium = base.sodium + addition.sodium,
                vitamins = mergeNutrientMaps(base.vitamins, addition.vitamins),
                minerals = mergeNutrientMaps(base.minerals, addition.minerals)
            )
        }
        
        private fun mergeNutrientMaps(
            map1: Map<String, Double>, 
            map2: Map<String, Double>
        ): Map<String, Double> {
            val result = map1.toMutableMap()
            map2.forEach { (key, value) ->
                result[key] = (result[key] ?: 0.0) + value
            }
            return result
        }
        
        private fun calculateImprovementScore(
            current: NutritionalInfo,
            projected: NutritionalInfo,
            targets: NutritionalTargets
        ): Double {
            val currentDistance = calculateNutritionalDistance(current, targets)
            val projectedDistance = calculateNutritionalDistance(projected, targets)
            return currentDistance - projectedDistance // Positive means improvement
        }
        
        private fun calculateNutritionalDistance(
            nutrition: NutritionalInfo,
            targets: NutritionalTargets
        ): Double {
            var distance = 0.0
            
            distance += abs(nutrition.protein - targets.protein) / targets.protein
            distance += abs(nutrition.fiber - targets.fiber) / targets.fiber
            distance += max(0.0, nutrition.sodium - targets.maxSodium) / targets.maxSodium
            distance += max(0.0, nutrition.sugar - targets.maxSugar) / targets.maxSugar
            
            return distance
        }
        
        private fun calculateOptimizationScore(
            actual: NutritionalInfo,
            targets: NutritionalTargets
        ): Double {
            val maxDistance = 4.0 // Maximum possible distance
            val actualDistance = calculateNutritionalDistance(actual, targets)
            return max(0.0, (maxDistance - actualDistance) / maxDistance * 100)
        }
        
        private fun generateNutritionalRecommendations(
            actual: NutritionalInfo,
            targets: NutritionalTargets
        ): List<String> {
            val recommendations = mutableListOf<String>()
            
            if (actual.protein < targets.protein * 0.8) {
                recommendations.add("Increase protein-rich foods like lentils, paneer, and nuts")
            }
            
            if (actual.fiber < targets.fiber * 0.8) {
                recommendations.add("Add more fiber through whole grains, vegetables, and fruits")
            }
            
            if (actual.sodium > targets.maxSodium) {
                recommendations.add("Reduce sodium by using herbs and spices instead of salt")
            }
            
            if (actual.sugar > targets.maxSugar) {
                recommendations.add("Limit sugary drinks and desserts, opt for natural sweeteners")
            }
            
            return recommendations
        }
        
        data class NutritionalTargets(
            val protein: Double,
            val fiber: Double,
            val maxSodium: Double,
            val maxSugar: Double,
            val vitamins: Map<String, Double> = emptyMap(),
            val minerals: Map<String, Double> = emptyMap()
        )
        
        data class MenuConstraints(
            val maxItems: Int,
            val maxBudget: Double,
            val dietaryRestrictions: List<DietType> = emptyList(),
            val excludeAllergens: List<Allergen> = emptyList()
        )
        
        data class OptimizedMenu(
            val selectedItems: List<MenuItem>,
            val nutritionalProfile: NutritionalInfo,
            val totalCost: Double,
            val optimizationScore: Double,
            val recommendations: List<String>
        )
    }
    
    // 🎯 Menu Recommendation Engine
    class MenuRecommendationEngine {
        private val userPreferences = mutableMapOf<String, UserFoodProfile>()
        private val itemSimilarityMatrix = mutableMapOf<String, Map<String, Double>>()
        
        fun getPersonalizedRecommendations(
            userId: String,
            availableItems: List<MenuItem>,
            mealType: MealType,
            limit: Int = 10
        ): List<RecommendedItem> {
            
            val userProfile = userPreferences[userId] ?: return getPopularityBasedRecommendations(availableItems, mealType, limit)
            
            val filteredItems = availableItems.filter { item ->
                item.mealType == mealType &&
                item.isAvailable &&
                isDietCompatible(item, userProfile.dietaryRestrictions) &&
                !hasAllergens(item, userProfile.allergens)
            }
            
            return filteredItems.map { item ->
                RecommendedItem(
                    item = item,
                    score = calculateRecommendationScore(item, userProfile),
                    reason = generateRecommendationReason(item, userProfile)
                )
            }.sortedByDescending { it.score }.take(limit)
        }
        
        private fun calculateRecommendationScore(item: MenuItem, profile: UserFoodProfile): Double {
            var score = 0.0
            
            // Cuisine preference score
            if (item.cuisineType in profile.preferredCuisines) {
                score += 30.0
            }
            
            // Category preference score
            if (item.category in profile.preferredCategories) {
                score += 20.0
            }
            
            // Health score alignment
            if (profile.healthConscious && item.isHealthy()) {
                score += 25.0
            }
            
            // Sustainability score alignment
            if (profile.sustainabilityConscious && item.isSustainable()) {
                score += 15.0
            }
            
            // Popularity and rating
            score += item.popularity * 20
            score += (item.averageRating / 5.0) * 15
            
            // Price sensitivity
            val priceScore = when (profile.priceRange) {
                PriceRange.BUDGET -> if (item.costPerServing <= 50) 10.0 else -10.0
                PriceRange.MODERATE -> if (item.costPerServing <= 100) 5.0 else -5.0
                PriceRange.PREMIUM -> 0.0
            }
            score += priceScore
            
            // Collaborative filtering score
            score += getCollaborativeFilteringScore(item.itemId, profile.userId)
            
            return max(0.0, min(100.0, score))
        }
        
        private fun getCollaborativeFilteringScore(itemId: String, userId: String): Double {
            // Simplified collaborative filtering
            val similarities = itemSimilarityMatrix[itemId] ?: return 0.0
            
            // Find items user has rated highly and see if they're similar to current item
            val userProfile = userPreferences[userId] ?: return 0.0
            var totalSimilarity = 0.0
            var count = 0
            
            userProfile.ratedItems.forEach { (ratedItemId, rating) ->
                if (rating >= 4) {
                    val similarity = similarities[ratedItemId] ?: 0.0
                    totalSimilarity += similarity
                    count++
                }
            }
            
            return if (count > 0) (totalSimilarity / count) * 20 else 0.0
        }
        
        private fun isDietCompatible(item: MenuItem, restrictions: List<DietType>): Boolean {
            return restrictions.isEmpty() || restrictions.any { it in item.dietTypes }
        }
        
        private fun hasAllergens(item: MenuItem, userAllergens: List<Allergen>): Boolean {
            return item.allergens.any { it in userAllergens }
        }
        
        private fun generateRecommendationReason(item: MenuItem, profile: UserFoodProfile): String {
            val reasons = mutableListOf<String>()
            
            if (item.cuisineType in profile.preferredCuisines) {
                reasons.add("matches your cuisine preference")
            }
            
            if (profile.healthConscious && item.isHealthy()) {
                reasons.add("healthy choice")
            }
            
            if (item.isPopular()) {
                reasons.add("popular among students")
            }
            
            if (item.averageRating >= 4.0) {
                reasons.add("highly rated")
            }
            
            return if (reasons.isNotEmpty()) {
                "Recommended because it ${reasons.joinToString(", ")}"
            } else {
                "Based on your preferences"
            }
        }
        
        private fun getPopularityBasedRecommendations(
            items: List<MenuItem>,
            mealType: MealType,
            limit: Int
        ): List<RecommendedItem> {
            return items.filter { it.mealType == mealType && it.isAvailable }
                .sortedByDescending { it.getOverallScore() }
                .take(limit)
                .map { item ->
                    RecommendedItem(
                        item = item,
                        score = item.getOverallScore(),
                        reason = "Popular choice"
                    )
                }
        }
        
        fun updateUserProfile(userId: String, profile: UserFoodProfile) {
            userPreferences[userId] = profile
        }
        
        fun recordUserRating(userId: String, itemId: String, rating: Int) {
            val profile = userPreferences.getOrPut(userId) { 
                UserFoodProfile(userId = userId) 
            }
            profile.ratedItems[itemId] = rating
        }
        
        data class UserFoodProfile(
            val userId: String,
            val preferredCuisines: List<CuisineType> = emptyList(),
            val preferredCategories: List<FoodCategory> = emptyList(),
            val dietaryRestrictions: List<DietType> = emptyList(),
            val allergens: List<Allergen> = emptyList(),
            val priceRange: PriceRange = PriceRange.MODERATE,
            val healthConscious: Boolean = false,
            val sustainabilityConscious: Boolean = false,
            val ratedItems: MutableMap<String, Int> = mutableMapOf()
        )
        
        data class RecommendedItem(
            val item: MenuItem,
            val score: Double,
            val reason: String
        )
        
        enum class PriceRange { BUDGET, MODERATE, PREMIUM }
    }
    
    // 📱 Main Service Implementation
    private val ratingAggregator = RatingAggregator()
    private val nutritionalOptimizer = NutritionalOptimizer()
    private val recommendationEngine = MenuRecommendationEngine()
    
    // Cache for frequently accessed data
    private val messCache = mutableMapOf<String, MessFacility>()
    private val menuItemsCache = mutableMapOf<String, List<MenuItem>>()
    private val reviewsCache = mutableMapOf<String, List<FoodReview>>()
    
    /**
     * 📊 Analyze reviews and get comprehensive analytics
     */
    fun analyzeReviews(messId: String, itemId: String? = null): Promise<RatingAggregator.ReviewAnalytics> {
        return Promise { resolve, reject ->
            try {
                val reviews = if (itemId != null) {
                    reviewsCache.values.flatten().filter { it.messId == messId && it.menuItemId == itemId }
                } else {
                    reviewsCache.values.flatten().filter { it.messId == messId }
                }
                
                val analytics = ratingAggregator.calculateWeightedRating(reviews)
                resolve(analytics)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 🥗 Optimize menu for nutritional targets
     */
    fun optimizeMenuNutrition(
        messId: String,
        targets: NutritionalOptimizer.NutritionalTargets,
        constraints: NutritionalOptimizer.MenuConstraints
    ): Promise<NutritionalOptimizer.OptimizedMenu> {
        return Promise { resolve, reject ->
            try {
                val menuItems = menuItemsCache[messId] ?: emptyList()
                val optimizedMenu = nutritionalOptimizer.optimizeMenuForNutrition(
                    menuItems, targets, constraints
                )
                resolve(optimizedMenu)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 🎯 Get personalized menu recommendations
     */
    fun getPersonalizedRecommendations(
        userId: String,
        messId: String,
        mealType: MealType,
        limit: Int = 10
    ): Promise<List<MenuRecommendationEngine.RecommendedItem>> {
        return Promise { resolve, reject ->
            try {
                val availableItems = menuItemsCache[messId] ?: emptyList()
                val recommendations = recommendationEngine.getPersonalizedRecommendations(
                    userId, availableItems, mealType, limit
                )
                resolve(recommendations)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 💬 Submit detailed review with automatic analysis
     */
    fun submitReview(review: FoodReview): Promise<Boolean> {
        return Promise { resolve, reject ->
            try {
                // Add review to cache
                val messReviews = reviewsCache.getOrPut(review.messId) { mutableListOf() }
                (messReviews as MutableList).add(review)
                
                // Update recommendation engine with user rating
                if (review.menuItemId != null) {
                    recommendationEngine.recordUserRating(
                        review.userId, review.menuItemId, review.overallRating
                    )
                }
                
                // Update menu item ratings
                if (review.menuItemId != null) {
                    updateMenuItemRating(review.messId, review.menuItemId, review.overallRating)
                }
                
                resolve(true)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 📈 Get comprehensive mess analytics
     */
    fun getMessAnalytics(messId: String): Promise<Map<String, Any>> {
        return Promise { resolve, reject ->
            try {
                val mess = messCache[messId]
                if (mess == null) {
                    reject(Exception("Mess not found"))
                    return@Promise
                }
                
                val reviews = reviewsCache[messId] ?: emptyList()
                val menuItems = menuItemsCache[messId] ?: emptyList()
                val analytics = ratingAggregator.calculateWeightedRating(reviews)
                
                val result = mapOf(
                    "messInfo" to mess.toMap(),
                    "reviewAnalytics" to mapOf(
                        "overallRating" to analytics.overallRating,
                        "totalReviews" to analytics.totalReviews,
                        "aspectRatings" to analytics.aspectRatings,
                        "sentimentDistribution" to analytics.sentimentDistribution,
                        "trendAnalysis" to analytics.trendAnalysis,
                        "qualityScore" to analytics.qualityScore
                    ),
                    "menuStatistics" to mapOf(
                        "totalItems" to menuItems.size,
                        "availableItems" to menuItems.count { it.isAvailable },
                        "healthyItems" to menuItems.count { it.isHealthy() },
                        "popularItems" to menuItems.count { it.isPopular() },
                        "averageCost" to menuItems.map { it.costPerServing }.average(),
                        "cuisineDistribution" to menuItems.groupingBy { it.cuisineType }.eachCount(),
                        "categoryDistribution" to menuItems.groupingBy { it.category }.eachCount()
                    ),
                    "operationalMetrics" to mapOf(
                        "occupancyRate" to mess.getOccupancyPercentage(),
                        "overallScore" to mess.getOverallScore(),
                        "wasteManagement" to mess.wasteManagement.toMap(),
                        "monthlyRevenue" to calculateMonthlyRevenue(messId),
                        "customerSatisfaction" to analytics.overallRating
                    )
                )
                
                resolve(result)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    /**
     * 🍽️ Get trending menu items
     */
    fun getTrendingItems(messId: String, period: String = "week"): Promise<List<MenuItem>> {
        return Promise { resolve, reject ->
            try {
                val menuItems = menuItemsCache[messId] ?: emptyList()
                val reviews = reviewsCache[messId] ?: emptyList()
                
                // Calculate trending score based on recent reviews and ratings
                val trendingItems = menuItems.map { item ->
                    val itemReviews = reviews.filter { it.menuItemId == item.itemId }
                    val recentReviews = itemReviews.filter { 
                        val daysSince = (System.currentTimeMillis() - it.reviewDate) / (1000 * 60 * 60 * 24)
                        when (period) {
                            "day" -> daysSince <= 1
                            "week" -> daysSince <= 7
                            "month" -> daysSince <= 30
                            else -> daysSince <= 7
                        }
                    }
                    
                    val trendingScore = if (recentReviews.isNotEmpty()) {
                        recentReviews.map { it.overallRating }.average() * recentReviews.size
                    } else {
                        item.popularity * 10
                    }
                    
                    item to trendingScore
                }.sortedByDescending { it.second }
                    .take(10)
                    .map { it.first }
                
                resolve(trendingItems)
                
            } catch (e: Exception) {
                reject(e)
            }
        }
    }
    
    // 🛠️ Helper Methods
    
    private fun updateMenuItemRating(messId: String, itemId: String, newRating: Int) {
        val menuItems = menuItemsCache[messId]?.toMutableList() ?: return
        val itemIndex = menuItems.indexOfFirst { it.itemId == itemId }
        
        if (itemIndex != -1) {
            val item = menuItems[itemIndex]
            val totalReviews = item.totalReviews + 1
            val newAverage = ((item.averageRating * item.totalReviews) + newRating) / totalReviews
            
            menuItems[itemIndex] = item.copy(
                averageRating = newAverage,
                totalReviews = totalReviews
            )
            
            menuItemsCache[messId] = menuItems
        }
    }
    
    private fun calculateMonthlyRevenue(messId: String): Double {
        // Simplified revenue calculation
        val menuItems = menuItemsCache[messId] ?: return 0.0
        val mess = messCache[messId] ?: return 0.0
        
        return menuItems.sumOf { it.costPerServing * it.popularity * 30 } * (mess.currentOccupancy / mess.capacity.toDouble())
    }
    
    private fun generateFeedbackId(): String {
        return "FB${System.currentTimeMillis()}${Random.nextInt(1000, 9999)}"
    }
    
    // 📊 Cache Management
    fun updateMessCache(mess: MessFacility) {
        messCache[mess.messId] = mess
    }
    
    fun updateMenuItemsCache(messId: String, items: List<MenuItem>) {
        menuItemsCache[messId] = items
    }
    
    fun updateReviewsCache(messId: String, reviews: List<FoodReview>) {
        reviewsCache[messId] = reviews
    }
    
    fun clearCache() {
        messCache.clear()
        menuItemsCache.clear()
        reviewsCache.clear()
    }
}
