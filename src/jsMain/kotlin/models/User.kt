package models

import auth.AdminRole
import auth.UserRole
import kotlinx.serialization.Serializable

/**
 * 👤 User Data Model for Hostel Dada
 * 
 * Supports both regular users and admins with role-based access control
 * - Users: Can access all modules for personal use
 * - Admins: Have management access to specific modules only
 */
@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val adminRole: AdminRole? = null,
    val accessibleModules: Set<String> = emptySet(),
    
    // Optional profile fields
    val hostelId: String? = null,
    val roomNumber: String? = null,
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    
    // Extended user information
    val emergencyContact: String? = null,
    val course: String? = null,
    val year: String? = null,
    val preferences: UserPreferences? = null
) {
    
    /**
     * Check if user is admin
     */
    fun isAdmin(): Boolean = role == UserRole.ADMIN
    
    /**
     * Check if user is global admin
     */
    fun isGlobalAdmin(): Boolean = adminRole == AdminRole.GLOBAL_ADMIN
    
    /**
     * Check if user has access to specific module
     */
    fun hasModuleAccess(module: String): Boolean {
        return when (role) {
            UserRole.USER -> true  // Regular users can access all modules
            UserRole.ADMIN -> accessibleModules.contains(module)
        }
    }
    
    /**
     * Get display name
     */
    fun getDisplayName(): String = name.ifEmpty { email.substringBefore("@") }
    
    /**
     * Get admin type description
     */
    fun getAdminTypeDescription(): String? {
        return when (adminRole) {
            AdminRole.GLOBAL_ADMIN -> "Global Administrator"
            AdminRole.SNACKCART_ADMIN -> "SnackCart Administrator"
            AdminRole.ROOMIEMATCHER_ADMIN -> "RoomieMatcher Administrator"
            AdminRole.LAUNDRYBALANCER_ADMIN -> "LaundryBalancer Administrator"
            AdminRole.MESSYMESS_ADMIN -> "MessyMess Administrator"
            AdminRole.HOSTELFIXER_ADMIN -> "HostelFixer Administrator"
            null -> null
        }
    }
}
) {
    companion object {
        /**
         * Create User from Firebase database map
         */
        fun fromMap(map: Map<String, Any>): User {
            return User(
                uid = map["uid"] as String,
                email = map["email"] as String,
                fullName = map["fullName"] as String,
                hostelId = map["hostelId"] as String,
                roomNumber = map["roomNumber"] as String,
                phoneNumber = map["phoneNumber"] as String,
                role = UserRole.valueOf(map["role"] as? String ?: "STUDENT"),
                profilePictureUrl = map["profilePictureUrl"] as? String,
                isActive = map["isActive"] as? Boolean ?: true,
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: 0L,
                lastLoginAt = (map["lastLoginAt"] as? Number)?.toLong() ?: 0L,
                emergencyContact = map["emergencyContact"] as? String,
                parentContact = map["parentContact"] as? String,
                course = map["course"] as? String,
                year = map["year"] as? String,
                preferences = (map["preferences"] as? Map<String, Any>)?.let { 
                    UserPreferences.fromMap(it) 
                }
            )
        }
    }
    
    /**
     * Convert User to Firebase database map
     */
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("uid", uid)
            put("email", email)
            put("fullName", fullName)
            put("hostelId", hostelId)
            put("roomNumber", roomNumber)
            put("phoneNumber", phoneNumber)
            put("role", role.name)
            profilePictureUrl?.let { put("profilePictureUrl", it) }
            put("isActive", isActive)
            put("createdAt", createdAt)
            put("lastLoginAt", lastLoginAt)
            emergencyContact?.let { put("emergencyContact", it) }
            parentContact?.let { put("parentContact", it) }
            course?.let { put("course", it) }
            year?.let { put("year", it) }
            preferences?.let { put("preferences", it.toMap()) }
        }
    }
    
    /**
     * Get display name for UI
     */
    fun getDisplayName(): String = fullName
    
    /**
     * Get user initials for avatar
     */
    fun getInitials(): String {
        return fullName.split(" ")
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
    }
    
    /**
     * Check if user is admin
     */
    fun isAdmin(): Boolean = role == UserRole.ADMIN
    
    /**
     * Check if user is warden
     */
    fun isWarden(): Boolean = role == UserRole.WARDEN
    
    /**
     * Check if user has elevated privileges
     */
    fun hasElevatedPrivileges(): Boolean = role in listOf(UserRole.ADMIN, UserRole.WARDEN)
}

/**
 * 🎭 User Roles Enum
 */
enum class UserRole {
    STUDENT,    // Regular student
    WARDEN,     // Hostel warden
    ADMIN,      // System administrator
    MESS_STAFF, // Mess management staff
    MAINTENANCE // Maintenance staff
}

/**
 * ⚙️ User Preferences
 */
@Serializable
data class UserPreferences(
    val notifications: NotificationPreferences = NotificationPreferences(),
    val privacy: PrivacyPreferences = PrivacyPreferences(),
    val appearance: AppearancePreferences = AppearancePreferences()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): UserPreferences {
            return UserPreferences(
                notifications = (map["notifications"] as? Map<String, Any>)?.let { 
                    NotificationPreferences.fromMap(it) 
                } ?: NotificationPreferences(),
                privacy = (map["privacy"] as? Map<String, Any>)?.let { 
                    PrivacyPreferences.fromMap(it) 
                } ?: PrivacyPreferences(),
                appearance = (map["appearance"] as? Map<String, Any>)?.let { 
                    AppearancePreferences.fromMap(it) 
                } ?: AppearancePreferences()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "notifications" to notifications.toMap(),
            "privacy" to privacy.toMap(),
            "appearance" to appearance.toMap()
        )
    }
}

/**
 * 🔔 Notification Preferences
 */
@Serializable
data class NotificationPreferences(
    val snackOrderUpdates: Boolean = true,
    val roommateMatches: Boolean = true,
    val laundryReminders: Boolean = true,
    val messMenuUpdates: Boolean = true,
    val maintenanceUpdates: Boolean = true,
    val emergencyAlerts: Boolean = true,
    val emailNotifications: Boolean = false,
    val pushNotifications: Boolean = true
) {
    companion object {
        fun fromMap(map: Map<String, Any>): NotificationPreferences {
            return NotificationPreferences(
                snackOrderUpdates = map["snackOrderUpdates"] as? Boolean ?: true,
                roommateMatches = map["roommateMatches"] as? Boolean ?: true,
                laundryReminders = map["laundryReminders"] as? Boolean ?: true,
                messMenuUpdates = map["messMenuUpdates"] as? Boolean ?: true,
                maintenanceUpdates = map["maintenanceUpdates"] as? Boolean ?: true,
                emergencyAlerts = map["emergencyAlerts"] as? Boolean ?: true,
                emailNotifications = map["emailNotifications"] as? Boolean ?: false,
                pushNotifications = map["pushNotifications"] as? Boolean ?: true
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "snackOrderUpdates" to snackOrderUpdates,
            "roommateMatches" to roommateMatches,
            "laundryReminders" to laundryReminders,
            "messMenuUpdates" to messMenuUpdates,
            "maintenanceUpdates" to maintenanceUpdates,
            "emergencyAlerts" to emergencyAlerts,
            "emailNotifications" to emailNotifications,
            "pushNotifications" to pushNotifications
        )
    }
}

/**
 * 🔒 Privacy Preferences
 */
@Serializable
data class PrivacyPreferences(
    val showRoomNumber: Boolean = true,
    val showPhoneNumber: Boolean = false,
    val allowRoommateRequests: Boolean = true,
    val showOnlineStatus: Boolean = true,
    val shareActivityData: Boolean = false
) {
    companion object {
        fun fromMap(map: Map<String, Any>): PrivacyPreferences {
            return PrivacyPreferences(
                showRoomNumber = map["showRoomNumber"] as? Boolean ?: true,
                showPhoneNumber = map["showPhoneNumber"] as? Boolean ?: false,
                allowRoommateRequests = map["allowRoommateRequests"] as? Boolean ?: true,
                showOnlineStatus = map["showOnlineStatus"] as? Boolean ?: true,
                shareActivityData = map["shareActivityData"] as? Boolean ?: false
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "showRoomNumber" to showRoomNumber,
            "showPhoneNumber" to showPhoneNumber,
            "allowRoommateRequests" to allowRoommateRequests,
            "showOnlineStatus" to showOnlineStatus,
            "shareActivityData" to shareActivityData
        )
    }
}

/**
 * 🎨 Appearance Preferences
 */
@Serializable
data class AppearancePreferences(
    val theme: String = "system", // "light", "dark", "system"
    val accentColor: String = "#2196F3",
    val compactMode: Boolean = false,
    val animationsEnabled: Boolean = true
) {
    companion object {
        fun fromMap(map: Map<String, Any>): AppearancePreferences {
            return AppearancePreferences(
                theme = map["theme"] as? String ?: "system",
                accentColor = map["accentColor"] as? String ?: "#2196F3",
                compactMode = map["compactMode"] as? Boolean ?: false,
                animationsEnabled = map["animationsEnabled"] as? Boolean ?: true
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "theme" to theme,
            "accentColor" to accentColor,
            "compactMode" to compactMode,
            "animationsEnabled" to animationsEnabled
        )
    }
}
