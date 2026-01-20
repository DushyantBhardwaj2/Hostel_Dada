package com.hosteldada.core.domain.model

import kotlinx.serialization.Serializable

/**
 * ============================================
 * COMPLETE DOMAIN ENTITIES
 * ============================================
 * 
 * All domain models for Hostel Dada application.
 * Migrated from TypeScript with Kotlin optimizations.
 */

// ==========================================
// USER & AUTHENTICATION
// ==========================================

@Serializable
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val createdAt: Long = 0,
    val lastLoginAt: Long = 0
)

@Serializable
data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    
    // NSUT specific
    val rollNumber: String = "",
    val branch: String = "",
    val year: Int = 1,
    val section: String = "",
    
    // Hostel info
    val hostelBlock: String = "",
    val roomNumber: String = "",
    val floorNumber: Int = 0,
    
    // Contact
    val phoneNumber: String = "",
    val emergencyContact: String = "",
    
    // Metadata
    val isProfileComplete: Boolean = false,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

enum class UserRole {
    STUDENT,
    ADMIN,
    SUPER_ADMIN
}

// ==========================================
// SNACKCART
// ==========================================

@Serializable
data class Snack(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: SnackCategory = SnackCategory.OTHER,
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val stockQuantity: Int = 0,
    val preparationTime: Int = 5, // minutes
    val isVegetarian: Boolean = true,
    val tags: List<String> = emptyList(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

@Serializable
enum class SnackCategory {
    BEVERAGES,
    SNACKS,
    QUICK_BITES,
    MEALS,
    DESSERTS,
    OTHER
}

@Serializable
data class CartItem(
    val snackId: String = "",
    val snackName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val imageUrl: String = "",
    val specialInstructions: String = ""
) {
    val totalPrice: Double get() = price * quantity
}

@Serializable
data class Cart(
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) {
    val itemCount: Int get() = items.sumOf { it.quantity }
    val isEmpty: Boolean get() = items.isEmpty()
}

@Serializable
data class SnackOrder(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userName: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val deliveryLocation: String = "",
    val notes: String = "",
    val estimatedDelivery: Long = 0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val completedAt: Long = 0
)

@Serializable
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    DELIVERED,
    CANCELLED
}

@Serializable
enum class PaymentMethod {
    CASH,
    UPI,
    CARD
}

// ==========================================
// ROOMIE MATCHER
// ==========================================

@Serializable
data class Student(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val rollNumber: String = "",
    val branch: String = "",
    val year: Int = 1,
    val gender: String = "",
    val photoUrl: String = "",
    val createdAt: Long = 0
)

@Serializable
data class RoommateSurvey(
    val id: String = "",
    val studentId: String = "",
    val semester: String = "",
    
    // Preferences
    val lifestyle: LifestylePreferences = LifestylePreferences(),
    val studyHabits: StudyHabits = StudyHabits(),
    val cleanliness: CleanlinessPreferences = CleanlinessPreferences(),
    val socialPreferences: SocialPreferences = SocialPreferences(),
    val sleepSchedule: SleepSchedule = SleepSchedule(),
    val personalityTraits: PersonalityTraits = PersonalityTraits(),
    
    // Additional
    val additionalInfo: String = "",
    val dealBreakers: List<String> = emptyList(),
    
    // Metadata
    val isComplete: Boolean = false,
    val submittedAt: Long = 0,
    val updatedAt: Long = 0
)

@Serializable
data class LifestylePreferences(
    val sleepTime: String = "11:00 PM",
    val wakeTime: String = "7:00 AM",
    val foodPreference: FoodPreference = FoodPreference.VEGETARIAN,
    val smokingHabit: Boolean = false,
    val drinkingHabit: Boolean = false,
    val acTemperaturePreference: String = "moderate",
    val musicPreference: String = "headphones"
)

@Serializable
enum class FoodPreference {
    VEGETARIAN,
    NON_VEGETARIAN,
    VEGAN,
    EGGETARIAN
}

@Serializable
data class StudyHabits(
    val studyStyle: StudyStyle = StudyStyle.FOCUSED,
    val preferredStudyTime: String = "evening",
    val needsQuietEnvironment: Boolean = true,
    val groupStudyPreference: Boolean = false,
    val musicWhileStudying: Boolean = false
)

@Serializable
enum class StudyStyle {
    FOCUSED,
    RELAXED,
    INTENSE,
    FLEXIBLE
}

@Serializable
data class CleanlinessPreferences(
    val cleaningFrequency: String = "weekly",
    val organizationLevel: Int = 3, // 1-5 scale
    val sharedItemsComfort: Int = 3,
    val bathroomHabits: String = "normal"
)

@Serializable
data class SocialPreferences(
    val visitorFrequency: String = "sometimes",
    val partyAttitude: String = "occasional",
    val conversationStyle: String = "balanced",
    val privacyNeeds: Int = 3 // 1-5 scale
)

@Serializable
data class SleepSchedule(
    val typicalBedtime: String = "11:00 PM",
    val typicalWakeTime: String = "7:00 AM",
    val sleepSensitivity: String = "moderate",
    val napHabits: Boolean = false,
    val weekendScheduleDiffers: Boolean = true
)

@Serializable
data class PersonalityTraits(
    val introvertExtrovert: Int = 3, // 1=introvert, 5=extrovert
    val conflictResolution: String = "discuss",
    val communicationStyle: String = "direct",
    val adaptability: Int = 3
)

@Serializable
data class CompatibilityScore(
    val id: String = "",
    val studentId1: String = "",
    val studentId2: String = "",
    val overallScore: Int = 0,
    val lifestyleScore: Int = 0,
    val studyScore: Int = 0,
    val cleanlinessScore: Int = 0,
    val socialScore: Int = 0,
    val sleepScore: Int = 0,
    val personalityScore: Int = 0,
    val matchReasons: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val calculatedAt: Long = 0
)

@Serializable
data class Room(
    val id: String = "",
    val roomNumber: String = "",
    val hostelBlock: String = "",
    val floor: Int = 0,
    val capacity: Int = 2,
    val currentOccupants: Int = 0,
    val occupantIds: List<String> = emptyList(),
    val status: RoomStatus = RoomStatus.AVAILABLE,
    val amenities: List<String> = emptyList(),
    val createdAt: Long = 0
)

@Serializable
enum class RoomStatus {
    AVAILABLE,
    PARTIAL,
    FULL,
    MAINTENANCE,
    RESERVED
}

@Serializable
data class RoomAssignment(
    val id: String = "",
    val roomId: String = "",
    val studentIds: List<String> = emptyList(),
    val semester: String = "",
    val status: AssignmentStatus = AssignmentStatus.PENDING,
    val compatibilityScore: Int = 0,
    val notes: String = "",
    val createdAt: Long = 0,
    val approvedAt: Long = 0,
    val approvedBy: String = ""
)

@Serializable
enum class AssignmentStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED,
    COMPLETED
}

// ==========================================
// FUTURE MODULES
// ==========================================

// Laundry
@Serializable
data class LaundrySlot(
    val id: String = "",
    val machineId: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val bookedBy: String? = null,
    val status: SlotStatus = SlotStatus.AVAILABLE
)

@Serializable
enum class SlotStatus {
    AVAILABLE,
    BOOKED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

// Maintenance
@Serializable
data class MaintenanceRequest(
    val id: String = "",
    val userId: String = "",
    val roomNumber: String = "",
    val category: MaintenanceCategory = MaintenanceCategory.OTHER,
    val description: String = "",
    val priority: Int = 1, // 1=low, 5=urgent
    val status: MaintenanceStatus = MaintenanceStatus.OPEN,
    val imageUrls: List<String> = emptyList(),
    val assignedTo: String? = null,
    val createdAt: Long = 0,
    val resolvedAt: Long = 0
)

@Serializable
enum class MaintenanceCategory {
    ELECTRICAL,
    PLUMBING,
    FURNITURE,
    AC_COOLING,
    CLEANING,
    OTHER
}

@Serializable
enum class MaintenanceStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    ESCALATED
}

// Mess
@Serializable
data class MessMenu(
    val id: String = "",
    val date: String = "",
    val dayOfWeek: String = "",
    val breakfast: MealInfo = MealInfo(),
    val lunch: MealInfo = MealInfo(),
    val snacks: MealInfo = MealInfo(),
    val dinner: MealInfo = MealInfo()
)

@Serializable
data class MealInfo(
    val items: List<String> = emptyList(),
    val timing: String = "",
    val specialDiet: List<String> = emptyList()
)

@Serializable
data class MessFeedback(
    val id: String = "",
    val userId: String = "",
    val menuId: String = "",
    val mealType: MealType = MealType.LUNCH,
    val rating: Int = 3,
    val comment: String = "",
    val createdAt: Long = 0
)

@Serializable
enum class MealType {
    BREAKFAST,
    LUNCH,
    SNACKS,
    DINNER
}
