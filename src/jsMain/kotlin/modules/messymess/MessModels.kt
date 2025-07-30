package modules.messymess

import kotlinx.serialization.Serializable
import kotlin.math.*

/**
 * 🍽️ MessyMess Module - Food & Mess Management Data Models
 * 
 * Advanced mess management with DSA optimization:
 * - Rating aggregation algorithms with weighted scoring
 * - Nutritional analysis and recommendation systems
 * - Menu optimization based on preferences and health
 * - Waste reduction through predictive ordering
 */

/**
 * 🏢 Mess Facility Information
 */
@Serializable
data class MessFacility(
    val messId: String,
    val hostelId: String,
    val name: String,
    val location: String,
    val floor: Int,
    val capacity: Int,
    val mealTypes: List<MealType>,
    val cuisineTypes: List<CuisineType>,
    val operatingHours: MessOperatingHours,
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val monthlyBudget: Double,
    val currentOccupancy: Int = 0,
    val chefInfo: ChefInfo,
    val nutritionist: NutritionistInfo? = null,
    val facilities: List<String> = emptyList(), // AC, WiFi, TV, etc.
    val specialDiets: List<DietType> = emptyList(),
    val wasteManagement: WasteManagementInfo,
    val hygieneRating: Double = 0.0,
    val certifications: List<String> = emptyList(),
    val isActive: Boolean = true,
    val lastInspection: Long? = null,
    val nextInspection: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): MessFacility {
            return MessFacility(
                messId = map["messId"] as String,
                hostelId = map["hostelId"] as String,
                name = map["name"] as String,
                location = map["location"] as String,
                floor = (map["floor"] as Number).toInt(),
                capacity = (map["capacity"] as Number).toInt(),
                mealTypes = (map["mealTypes"] as List<String>).map { MealType.valueOf(it) },
                cuisineTypes = (map["cuisineTypes"] as List<String>).map { CuisineType.valueOf(it) },
                operatingHours = MessOperatingHours.fromMap(map["operatingHours"] as Map<String, Any>),
                averageRating = (map["averageRating"] as? Number)?.toDouble() ?: 0.0,
                totalReviews = (map["totalReviews"] as? Number)?.toInt() ?: 0,
                monthlyBudget = (map["monthlyBudget"] as Number).toDouble(),
                currentOccupancy = (map["currentOccupancy"] as? Number)?.toInt() ?: 0,
                chefInfo = ChefInfo.fromMap(map["chefInfo"] as Map<String, Any>),
                nutritionist = (map["nutritionist"] as? Map<String, Any>)?.let { 
                    NutritionistInfo.fromMap(it) 
                },
                facilities = (map["facilities"] as? List<String>) ?: emptyList(),
                specialDiets = (map["specialDiets"] as? List<String>)?.map { 
                    DietType.valueOf(it) 
                } ?: emptyList(),
                wasteManagement = WasteManagementInfo.fromMap(map["wasteManagement"] as Map<String, Any>),
                hygieneRating = (map["hygieneRating"] as? Number)?.toDouble() ?: 0.0,
                certifications = (map["certifications"] as? List<String>) ?: emptyList(),
                isActive = map["isActive"] as? Boolean ?: true,
                lastInspection = (map["lastInspection"] as? Number)?.toLong(),
                nextInspection = (map["nextInspection"] as? Number)?.toLong(),
                lastUpdated = (map["lastUpdated"] as Number).toLong()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("messId", messId)
            put("hostelId", hostelId)
            put("name", name)
            put("location", location)
            put("floor", floor)
            put("capacity", capacity)
            put("mealTypes", mealTypes.map { it.name })
            put("cuisineTypes", cuisineTypes.map { it.name })
            put("operatingHours", operatingHours.toMap())
            put("averageRating", averageRating)
            put("totalReviews", totalReviews)
            put("monthlyBudget", monthlyBudget)
            put("currentOccupancy", currentOccupancy)
            put("chefInfo", chefInfo.toMap())
            nutritionist?.let { put("nutritionist", it.toMap()) }
            put("facilities", facilities)
            put("specialDiets", specialDiets.map { it.name })
            put("wasteManagement", wasteManagement.toMap())
            put("hygieneRating", hygieneRating)
            put("certifications", certifications)
            put("isActive", isActive)
            lastInspection?.let { put("lastInspection", it) }
            nextInspection?.let { put("nextInspection", it) }
            put("lastUpdated", lastUpdated)
        }
    }
    
    fun getOccupancyPercentage(): Double = (currentOccupancy.toDouble() / capacity) * 100
    fun isNearCapacity(): Boolean = getOccupancyPercentage() > 80.0
    fun getOverallScore(): Double = (averageRating * 0.6) + (hygieneRating * 0.4)
}

/**
 * 🍳 Chef Information
 */
@Serializable
data class ChefInfo(
    val chefId: String,
    val name: String,
    val experience: Int, // years
    val specialties: List<CuisineType>,
    val certifications: List<String>,
    val rating: Double = 0.0,
    val workingHours: WorkingHours,
    val contactInfo: String,
    val joinedDate: Long,
    val languages: List<String> = emptyList()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): ChefInfo {
            return ChefInfo(
                chefId = map["chefId"] as String,
                name = map["name"] as String,
                experience = (map["experience"] as Number).toInt(),
                specialties = (map["specialties"] as List<String>).map { CuisineType.valueOf(it) },
                certifications = map["certifications"] as List<String>,
                rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
                workingHours = WorkingHours.fromMap(map["workingHours"] as Map<String, Any>),
                contactInfo = map["contactInfo"] as String,
                joinedDate = (map["joinedDate"] as Number).toLong(),
                languages = (map["languages"] as? List<String>) ?: emptyList()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "chefId" to chefId,
            "name" to name,
            "experience" to experience,
            "specialties" to specialties.map { it.name },
            "certifications" to certifications,
            "rating" to rating,
            "workingHours" to workingHours.toMap(),
            "contactInfo" to contactInfo,
            "joinedDate" to joinedDate,
            "languages" to languages
        )
    }
}

/**
 * 🥗 Nutritionist Information
 */
@Serializable
data class NutritionistInfo(
    val nutritionistId: String,
    val name: String,
    val qualification: String,
    val experience: Int,
    val specializations: List<String>, // weight management, sports nutrition, etc.
    val consultationHours: WorkingHours,
    val contactInfo: String,
    val rating: Double = 0.0,
    val isAvailableForConsultation: Boolean = true
) {
    companion object {
        fun fromMap(map: Map<String, Any>): NutritionistInfo {
            return NutritionistInfo(
                nutritionistId = map["nutritionistId"] as String,
                name = map["name"] as String,
                qualification = map["qualification"] as String,
                experience = (map["experience"] as Number).toInt(),
                specializations = map["specializations"] as List<String>,
                consultationHours = WorkingHours.fromMap(map["consultationHours"] as Map<String, Any>),
                contactInfo = map["contactInfo"] as String,
                rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
                isAvailableForConsultation = map["isAvailableForConsultation"] as? Boolean ?: true
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "nutritionistId" to nutritionistId,
            "name" to name,
            "qualification" to qualification,
            "experience" to experience,
            "specializations" to specializations,
            "consultationHours" to consultationHours.toMap(),
            "contactInfo" to contactInfo,
            "rating" to rating,
            "isAvailableForConsultation" to isAvailableForConsultation
        )
    }
}

/**
 * 🗑️ Waste Management Information
 */
@Serializable
data class WasteManagementInfo(
    val dailyWasteKg: Double,
    val composteableWasteKg: Double,
    val recycleableWasteKg: Double,
    val wasteReductionTarget: Double, // percentage
    val currentReductionPercentage: Double = 0.0,
    val wasteManagementPartner: String? = null,
    val lastAuditDate: Long? = null,
    val sustainabilityScore: Double = 0.0
) {
    companion object {
        fun fromMap(map: Map<String, Any>): WasteManagementInfo {
            return WasteManagementInfo(
                dailyWasteKg = (map["dailyWasteKg"] as Number).toDouble(),
                composteableWasteKg = (map["composteableWasteKg"] as Number).toDouble(),
                recycleableWasteKg = (map["recycleableWasteKg"] as Number).toDouble(),
                wasteReductionTarget = (map["wasteReductionTarget"] as Number).toDouble(),
                currentReductionPercentage = (map["currentReductionPercentage"] as? Number)?.toDouble() ?: 0.0,
                wasteManagementPartner = map["wasteManagementPartner"] as? String,
                lastAuditDate = (map["lastAuditDate"] as? Number)?.toLong(),
                sustainabilityScore = (map["sustainabilityScore"] as? Number)?.toDouble() ?: 0.0
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("dailyWasteKg", dailyWasteKg)
            put("composteableWasteKg", composteableWasteKg)
            put("recycleableWasteKg", recycleableWasteKg)
            put("wasteReductionTarget", wasteReductionTarget)
            put("currentReductionPercentage", currentReductionPercentage)
            wasteManagementPartner?.let { put("wasteManagementPartner", it) }
            lastAuditDate?.let { put("lastAuditDate", it) }
            put("sustainabilityScore", sustainabilityScore)
        }
    }
    
    fun getTotalWaste(): Double = dailyWasteKg
    fun getRecycleRate(): Double = (recycleableWasteKg / dailyWasteKg) * 100
    fun getCompostRate(): Double = (composteableWasteKg / dailyWasteKg) * 100
}

/**
 * 🍽️ Menu Item
 */
@Serializable
data class MenuItem(
    val itemId: String,
    val name: String,
    val description: String,
    val category: FoodCategory,
    val cuisineType: CuisineType,
    val mealType: MealType,
    val nutritionalInfo: NutritionalInfo,
    val ingredients: List<Ingredient>,
    val allergens: List<Allergen>,
    val dietTypes: List<DietType>,
    val preparationTime: Int, // minutes
    val servingSize: String,
    val costPerServing: Double,
    val popularity: Double = 0.0, // 0.0 to 1.0
    val averageRating: Double = 0.0,
    val totalReviews: Int = 0,
    val isAvailable: Boolean = true,
    val isSeasonalSpecial: Boolean = false,
    val healthScore: Double = 0.0, // calculated based on nutritional content
    val sustainabilityScore: Double = 0.0,
    val imageUrl: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): MenuItem {
            return MenuItem(
                itemId = map["itemId"] as String,
                name = map["name"] as String,
                description = map["description"] as String,
                category = FoodCategory.valueOf(map["category"] as String),
                cuisineType = CuisineType.valueOf(map["cuisineType"] as String),
                mealType = MealType.valueOf(map["mealType"] as String),
                nutritionalInfo = NutritionalInfo.fromMap(map["nutritionalInfo"] as Map<String, Any>),
                ingredients = (map["ingredients"] as List<Map<String, Any>>).map { 
                    Ingredient.fromMap(it) 
                },
                allergens = (map["allergens"] as List<String>).map { Allergen.valueOf(it) },
                dietTypes = (map["dietTypes"] as List<String>).map { DietType.valueOf(it) },
                preparationTime = (map["preparationTime"] as Number).toInt(),
                servingSize = map["servingSize"] as String,
                costPerServing = (map["costPerServing"] as Number).toDouble(),
                popularity = (map["popularity"] as? Number)?.toDouble() ?: 0.0,
                averageRating = (map["averageRating"] as? Number)?.toDouble() ?: 0.0,
                totalReviews = (map["totalReviews"] as? Number)?.toInt() ?: 0,
                isAvailable = map["isAvailable"] as? Boolean ?: true,
                isSeasonalSpecial = map["isSeasonalSpecial"] as? Boolean ?: false,
                healthScore = (map["healthScore"] as? Number)?.toDouble() ?: 0.0,
                sustainabilityScore = (map["sustainabilityScore"] as? Number)?.toDouble() ?: 0.0,
                imageUrl = map["imageUrl"] as? String,
                lastUpdated = (map["lastUpdated"] as Number).toLong()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("itemId", itemId)
            put("name", name)
            put("description", description)
            put("category", category.name)
            put("cuisineType", cuisineType.name)
            put("mealType", mealType.name)
            put("nutritionalInfo", nutritionalInfo.toMap())
            put("ingredients", ingredients.map { it.toMap() })
            put("allergens", allergens.map { it.name })
            put("dietTypes", dietTypes.map { it.name })
            put("preparationTime", preparationTime)
            put("servingSize", servingSize)
            put("costPerServing", costPerServing)
            put("popularity", popularity)
            put("averageRating", averageRating)
            put("totalReviews", totalReviews)
            put("isAvailable", isAvailable)
            put("isSeasonalSpecial", isSeasonalSpecial)
            put("healthScore", healthScore)
            put("sustainabilityScore", sustainabilityScore)
            imageUrl?.let { put("imageUrl", it) }
            put("lastUpdated", lastUpdated)
        }
    }
    
    fun getOverallScore(): Double {
        return (averageRating * 0.4) + (popularity * 100 * 0.3) + 
               (healthScore * 0.2) + (sustainabilityScore * 0.1)
    }
    
    fun isHealthy(): Boolean = healthScore >= 70.0
    fun isSustainable(): Boolean = sustainabilityScore >= 70.0
    fun isPopular(): Boolean = popularity >= 0.7
}

/**
 * 🥗 Nutritional Information
 */
@Serializable
data class NutritionalInfo(
    val calories: Double,
    val protein: Double, // grams
    val carbohydrates: Double, // grams
    val fat: Double, // grams
    val fiber: Double, // grams
    val sugar: Double, // grams
    val sodium: Double, // mg
    val vitamins: Map<String, Double> = emptyMap(), // vitamin -> amount
    val minerals: Map<String, Double> = emptyMap(), // mineral -> amount
    val saturatedFat: Double = 0.0,
    val transFat: Double = 0.0,
    val cholesterol: Double = 0.0, // mg
    val potassium: Double = 0.0, // mg
    val calcium: Double = 0.0, // mg
    val iron: Double = 0.0 // mg
) {
    companion object {
        fun fromMap(map: Map<String, Any>): NutritionalInfo {
            return NutritionalInfo(
                calories = (map["calories"] as Number).toDouble(),
                protein = (map["protein"] as Number).toDouble(),
                carbohydrates = (map["carbohydrates"] as Number).toDouble(),
                fat = (map["fat"] as Number).toDouble(),
                fiber = (map["fiber"] as Number).toDouble(),
                sugar = (map["sugar"] as Number).toDouble(),
                sodium = (map["sodium"] as Number).toDouble(),
                vitamins = (map["vitamins"] as? Map<String, Number>)?.mapValues { 
                    it.value.toDouble() 
                } ?: emptyMap(),
                minerals = (map["minerals"] as? Map<String, Number>)?.mapValues { 
                    it.value.toDouble() 
                } ?: emptyMap(),
                saturatedFat = (map["saturatedFat"] as? Number)?.toDouble() ?: 0.0,
                transFat = (map["transFat"] as? Number)?.toDouble() ?: 0.0,
                cholesterol = (map["cholesterol"] as? Number)?.toDouble() ?: 0.0,
                potassium = (map["potassium"] as? Number)?.toDouble() ?: 0.0,
                calcium = (map["calcium"] as? Number)?.toDouble() ?: 0.0,
                iron = (map["iron"] as? Number)?.toDouble() ?: 0.0
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("calories", calories)
            put("protein", protein)
            put("carbohydrates", carbohydrates)
            put("fat", fat)
            put("fiber", fiber)
            put("sugar", sugar)
            put("sodium", sodium)
            put("vitamins", vitamins)
            put("minerals", minerals)
            put("saturatedFat", saturatedFat)
            put("transFat", transFat)
            put("cholesterol", cholesterol)
            put("potassium", potassium)
            put("calcium", calcium)
            put("iron", iron)
        }
    }
    
    fun getHealthScore(): Double {
        // Calculate health score based on nutritional content
        var score = 100.0
        
        // Deduct for high sodium
        if (sodium > 1500) score -= (sodium - 1500) / 100
        
        // Deduct for high sugar
        if (sugar > 25) score -= (sugar - 25) * 2
        
        // Deduct for trans fat
        score -= transFat * 20
        
        // Add for protein
        score += min(protein * 2, 20.0)
        
        // Add for fiber
        score += min(fiber * 3, 15.0)
        
        return max(0.0, min(100.0, score))
    }
    
    fun getMacroDistribution(): Map<String, Double> {
        val totalMacros = (protein * 4) + (carbohydrates * 4) + (fat * 9)
        return mapOf(
            "protein" to ((protein * 4) / totalMacros * 100),
            "carbohydrates" to ((carbohydrates * 4) / totalMacros * 100),
            "fat" to ((fat * 9) / totalMacros * 100)
        )
    }
}

/**
 * 🥬 Ingredient Information
 */
@Serializable
data class Ingredient(
    val ingredientId: String,
    val name: String,
    val quantity: Double,
    val unit: String, // grams, ml, pieces, etc.
    val cost: Double,
    val supplier: String? = null,
    val isOrganic: Boolean = false,
    val isLocal: Boolean = false,
    val seasonality: List<String> = emptyList(), // months when available
    val shelfLife: Int = 0, // days
    val storageConditions: String? = null,
    val nutritionalContribution: Double = 0.0 // percentage of total nutrition
) {
    companion object {
        fun fromMap(map: Map<String, Any>): Ingredient {
            return Ingredient(
                ingredientId = map["ingredientId"] as String,
                name = map["name"] as String,
                quantity = (map["quantity"] as Number).toDouble(),
                unit = map["unit"] as String,
                cost = (map["cost"] as Number).toDouble(),
                supplier = map["supplier"] as? String,
                isOrganic = map["isOrganic"] as? Boolean ?: false,
                isLocal = map["isLocal"] as? Boolean ?: false,
                seasonality = (map["seasonality"] as? List<String>) ?: emptyList(),
                shelfLife = (map["shelfLife"] as? Number)?.toInt() ?: 0,
                storageConditions = map["storageConditions"] as? String,
                nutritionalContribution = (map["nutritionalContribution"] as? Number)?.toDouble() ?: 0.0
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("ingredientId", ingredientId)
            put("name", name)
            put("quantity", quantity)
            put("unit", unit)
            put("cost", cost)
            supplier?.let { put("supplier", it) }
            put("isOrganic", isOrganic)
            put("isLocal", isLocal)
            put("seasonality", seasonality)
            put("shelfLife", shelfLife)
            storageConditions?.let { put("storageConditions", it) }
            put("nutritionalContribution", nutritionalContribution)
        }
    }
    
    fun getSustainabilityScore(): Double {
        var score = 50.0 // base score
        if (isOrganic) score += 20.0
        if (isLocal) score += 15.0
        if (seasonality.isNotEmpty()) score += 10.0
        if (shelfLife > 7) score += 5.0
        return min(100.0, score)
    }
}

/**
 * 💬 Food Review
 */
@Serializable
data class FoodReview(
    val reviewId: String,
    val userId: String,
    val messId: String,
    val menuItemId: String? = null, // null for overall mess review
    val overallRating: Int, // 1-5 stars
    val tasteRating: Int,
    val qualityRating: Int,
    val serviceRating: Int,
    val valueRating: Int,
    val portionRating: Int,
    val hygieneRating: Int,
    val comment: String? = null,
    val pros: List<String> = emptyList(),
    val cons: List<String> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val photoUrls: List<String> = emptyList(),
    val isVerifiedReview: Boolean = false,
    val helpfulVotes: Int = 0,
    val totalVotes: Int = 0,
    val reviewDate: Long,
    val lastUpdated: Long = System.currentTimeMillis(),
    val mealType: MealType? = null,
    val dietaryRestrictions: List<DietType> = emptyList(),
    val responseFromManagement: String? = null,
    val responseDate: Long? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): FoodReview {
            return FoodReview(
                reviewId = map["reviewId"] as String,
                userId = map["userId"] as String,
                messId = map["messId"] as String,
                menuItemId = map["menuItemId"] as? String,
                overallRating = (map["overallRating"] as Number).toInt(),
                tasteRating = (map["tasteRating"] as Number).toInt(),
                qualityRating = (map["qualityRating"] as Number).toInt(),
                serviceRating = (map["serviceRating"] as Number).toInt(),
                valueRating = (map["valueRating"] as Number).toInt(),
                portionRating = (map["portionRating"] as Number).toInt(),
                hygieneRating = (map["hygieneRating"] as Number).toInt(),
                comment = map["comment"] as? String,
                pros = (map["pros"] as? List<String>) ?: emptyList(),
                cons = (map["cons"] as? List<String>) ?: emptyList(),
                suggestions = (map["suggestions"] as? List<String>) ?: emptyList(),
                photoUrls = (map["photoUrls"] as? List<String>) ?: emptyList(),
                isVerifiedReview = map["isVerifiedReview"] as? Boolean ?: false,
                helpfulVotes = (map["helpfulVotes"] as? Number)?.toInt() ?: 0,
                totalVotes = (map["totalVotes"] as? Number)?.toInt() ?: 0,
                reviewDate = (map["reviewDate"] as Number).toLong(),
                lastUpdated = (map["lastUpdated"] as Number).toLong(),
                mealType = (map["mealType"] as? String)?.let { MealType.valueOf(it) },
                dietaryRestrictions = (map["dietaryRestrictions"] as? List<String>)?.map { 
                    DietType.valueOf(it) 
                } ?: emptyList(),
                responseFromManagement = map["responseFromManagement"] as? String,
                responseDate = (map["responseDate"] as? Number)?.toLong()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("reviewId", reviewId)
            put("userId", userId)
            put("messId", messId)
            menuItemId?.let { put("menuItemId", it) }
            put("overallRating", overallRating)
            put("tasteRating", tasteRating)
            put("qualityRating", qualityRating)
            put("serviceRating", serviceRating)
            put("valueRating", valueRating)
            put("portionRating", portionRating)
            put("hygieneRating", hygieneRating)
            comment?.let { put("comment", it) }
            put("pros", pros)
            put("cons", cons)
            put("suggestions", suggestions)
            put("photoUrls", photoUrls)
            put("isVerifiedReview", isVerifiedReview)
            put("helpfulVotes", helpfulVotes)
            put("totalVotes", totalVotes)
            put("reviewDate", reviewDate)
            put("lastUpdated", lastUpdated)
            mealType?.let { put("mealType", it.name) }
            put("dietaryRestrictions", dietaryRestrictions.map { it.name })
            responseFromManagement?.let { put("responseFromManagement", it) }
            responseDate?.let { put("responseDate", it) }
        }
    }
    
    fun getDetailedRatingAverage(): Double {
        return (tasteRating + qualityRating + serviceRating + valueRating + 
                portionRating + hygieneRating).toDouble() / 6.0
    }
    
    fun getHelpfulnessRatio(): Double {
        return if (totalVotes > 0) {
            helpfulVotes.toDouble() / totalVotes
        } else 0.0
    }
    
    fun isHighQuality(): Boolean {
        return overallRating >= 4 && isVerifiedReview && comment?.length ?: 0 > 50
    }
}

/**
 * ⏰ Mess Operating Hours
 */
@Serializable
data class MessOperatingHours(
    val breakfast: MealHours,
    val lunch: MealHours,
    val snacks: MealHours? = null,
    val dinner: MealHours,
    val lateNight: MealHours? = null,
    val holidays: HolidayHours? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): MessOperatingHours {
            return MessOperatingHours(
                breakfast = MealHours.fromMap(map["breakfast"] as Map<String, Any>),
                lunch = MealHours.fromMap(map["lunch"] as Map<String, Any>),
                snacks = (map["snacks"] as? Map<String, Any>)?.let { MealHours.fromMap(it) },
                dinner = MealHours.fromMap(map["dinner"] as Map<String, Any>),
                lateNight = (map["lateNight"] as? Map<String, Any>)?.let { MealHours.fromMap(it) },
                holidays = (map["holidays"] as? Map<String, Any>)?.let { HolidayHours.fromMap(it) }
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("breakfast", breakfast.toMap())
            put("lunch", lunch.toMap())
            snacks?.let { put("snacks", it.toMap()) }
            put("dinner", dinner.toMap())
            lateNight?.let { put("lateNight", it.toMap()) }
            holidays?.let { put("holidays", it.toMap()) }
        }
    }
}

/**
 * 🍽️ Meal Hours
 */
@Serializable
data class MealHours(
    val startTime: String, // HH:mm format
    val endTime: String, // HH:mm format
    val isAvailable: Boolean = true,
    val lastOrderTime: String? = null, // last time to place order
    val specialTimings: Map<String, String> = emptyMap() // weekday -> timing variations
) {
    companion object {
        fun fromMap(map: Map<String, Any>): MealHours {
            return MealHours(
                startTime = map["startTime"] as String,
                endTime = map["endTime"] as String,
                isAvailable = map["isAvailable"] as? Boolean ?: true,
                lastOrderTime = map["lastOrderTime"] as? String,
                specialTimings = (map["specialTimings"] as? Map<String, String>) ?: emptyMap()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("startTime", startTime)
            put("endTime", endTime)
            put("isAvailable", isAvailable)
            lastOrderTime?.let { put("lastOrderTime", it) }
            put("specialTimings", specialTimings)
        }
    }
}

/**
 * 🎉 Holiday Hours
 */
@Serializable
data class HolidayHours(
    val isOpen: Boolean,
    val modifiedHours: MessOperatingHours? = null,
    val specialMenu: Boolean = false,
    val advanceBookingRequired: Boolean = false
) {
    companion object {
        fun fromMap(map: Map<String, Any>): HolidayHours {
            return HolidayHours(
                isOpen = map["isOpen"] as Boolean,
                modifiedHours = (map["modifiedHours"] as? Map<String, Any>)?.let { 
                    MessOperatingHours.fromMap(it) 
                },
                specialMenu = map["specialMenu"] as? Boolean ?: false,
                advanceBookingRequired = map["advanceBookingRequired"] as? Boolean ?: false
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("isOpen", isOpen)
            modifiedHours?.let { put("modifiedHours", it.toMap()) }
            put("specialMenu", specialMenu)
            put("advanceBookingRequired", advanceBookingRequired)
        }
    }
}

/**
 * 🕐 Working Hours
 */
@Serializable
data class WorkingHours(
    val monday: String,
    val tuesday: String,
    val wednesday: String,
    val thursday: String,
    val friday: String,
    val saturday: String,
    val sunday: String,
    val isFullTime: Boolean = true
) {
    companion object {
        fun fromMap(map: Map<String, Any>): WorkingHours {
            return WorkingHours(
                monday = map["monday"] as String,
                tuesday = map["tuesday"] as String,
                wednesday = map["wednesday"] as String,
                thursday = map["thursday"] as String,
                friday = map["friday"] as String,
                saturday = map["saturday"] as String,
                sunday = map["sunday"] as String,
                isFullTime = map["isFullTime"] as? Boolean ?: true
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "monday" to monday,
            "tuesday" to tuesday,
            "wednesday" to wednesday,
            "thursday" to thursday,
            "friday" to friday,
            "saturday" to saturday,
            "sunday" to sunday,
            "isFullTime" to isFullTime
        )
    }
}

/**
 * 🏷️ Enums for Mess System
 */
enum class MealType { BREAKFAST, LUNCH, SNACKS, DINNER, LATE_NIGHT }
enum class CuisineType { 
    NORTH_INDIAN, SOUTH_INDIAN, CHINESE, CONTINENTAL, ITALIAN, MEXICAN, 
    GUJARATI, PUNJABI, BENGALI, MAHARASHTRIAN, RAJASTHANI, OTHER 
}
enum class FoodCategory { 
    APPETIZER, MAIN_COURSE, DESSERT, BEVERAGE, SALAD, SOUP, BREAD, RICE, 
    CURRY, SNACK, SWEET, SPECIAL 
}
enum class DietType { 
    VEGETARIAN, VEGAN, NON_VEGETARIAN, JAIN, GLUTEN_FREE, DAIRY_FREE, 
    KETO, LOW_CARB, HIGH_PROTEIN, DIABETIC_FRIENDLY 
}
enum class Allergen { 
    NUTS, DAIRY, GLUTEN, SOY, EGGS, SHELLFISH, FISH, SESAME, SULFITES 
}
