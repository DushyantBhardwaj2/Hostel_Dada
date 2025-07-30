package modules.roomiematcher

import kotlinx.serialization.Serializable
import kotlin.math.*

/**
 * 🤝 RoomieMatcher Module - Data Models
 * 
 * Advanced roommate matching with DSA optimization:
 * - Compatibility scoring algorithms
 * - Graph-based room optimization
 * - Machine learning preference matching
 * - Multi-criteria decision analysis
 */

/**
 * 👤 Roommate Profile
 */
@Serializable
data class RoommateProfile(
    val userId: String,
    val fullName: String,
    val age: Int,
    val gender: Gender,
    val course: String,
    val year: Int,
    val currentRoomNumber: String?,
    val preferredRoomType: RoomType,
    val budgetRange: BudgetRange,
    val lifestyle: LifestylePreferences,
    val academicPreferences: AcademicPreferences,
    val socialPreferences: SocialPreferences,
    val hygiene: HygienePreferences,
    val sleepingHabits: SleepingHabits,
    val hobbies: List<String>,
    val languages: List<String>,
    val personalityType: PersonalityType?,
    val specialRequirements: List<String> = emptyList(),
    val dealBreakers: List<String> = emptyList(),
    val bio: String? = null,
    val profilePictureUrl: String? = null,
    val contactPreferences: ContactPreferences,
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromMap(map: Map<String, Any>): RoommateProfile {
            return RoommateProfile(
                userId = map["userId"] as String,
                fullName = map["fullName"] as String,
                age = (map["age"] as Number).toInt(),
                gender = Gender.valueOf(map["gender"] as String),
                course = map["course"] as String,
                year = (map["year"] as Number).toInt(),
                currentRoomNumber = map["currentRoomNumber"] as? String,
                preferredRoomType = RoomType.valueOf(map["preferredRoomType"] as String),
                budgetRange = BudgetRange.fromMap(map["budgetRange"] as Map<String, Any>),
                lifestyle = LifestylePreferences.fromMap(map["lifestyle"] as Map<String, Any>),
                academicPreferences = AcademicPreferences.fromMap(map["academicPreferences"] as Map<String, Any>),
                socialPreferences = SocialPreferences.fromMap(map["socialPreferences"] as Map<String, Any>),
                hygiene = HygienePreferences.fromMap(map["hygiene"] as Map<String, Any>),
                sleepingHabits = SleepingHabits.fromMap(map["sleepingHabits"] as Map<String, Any>),
                hobbies = (map["hobbies"] as? List<String>) ?: emptyList(),
                languages = (map["languages"] as? List<String>) ?: emptyList(),
                personalityType = (map["personalityType"] as? String)?.let { PersonalityType.valueOf(it) },
                specialRequirements = (map["specialRequirements"] as? List<String>) ?: emptyList(),
                dealBreakers = (map["dealBreakers"] as? List<String>) ?: emptyList(),
                bio = map["bio"] as? String,
                profilePictureUrl = map["profilePictureUrl"] as? String,
                contactPreferences = ContactPreferences.fromMap(map["contactPreferences"] as Map<String, Any>),
                verificationStatus = VerificationStatus.valueOf(map["verificationStatus"] as? String ?: "PENDING"),
                isActive = map["isActive"] as? Boolean ?: true,
                createdAt = (map["createdAt"] as Number).toLong(),
                lastUpdated = (map["lastUpdated"] as Number).toLong()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("userId", userId)
            put("fullName", fullName)
            put("age", age)
            put("gender", gender.name)
            put("course", course)
            put("year", year)
            currentRoomNumber?.let { put("currentRoomNumber", it) }
            put("preferredRoomType", preferredRoomType.name)
            put("budgetRange", budgetRange.toMap())
            put("lifestyle", lifestyle.toMap())
            put("academicPreferences", academicPreferences.toMap())
            put("socialPreferences", socialPreferences.toMap())
            put("hygiene", hygiene.toMap())
            put("sleepingHabits", sleepingHabits.toMap())
            put("hobbies", hobbies)
            put("languages", languages)
            personalityType?.let { put("personalityType", it.name) }
            put("specialRequirements", specialRequirements)
            put("dealBreakers", dealBreakers)
            bio?.let { put("bio", it) }
            profilePictureUrl?.let { put("profilePictureUrl", it) }
            put("contactPreferences", contactPreferences.toMap())
            put("verificationStatus", verificationStatus.name)
            put("isActive", isActive)
            put("createdAt", createdAt)
            put("lastUpdated", lastUpdated)
        }
    }
}

/**
 * 🏠 Room Information
 */
@Serializable
data class RoomInfo(
    val roomNumber: String,
    val hostelId: String,
    val roomType: RoomType,
    val capacity: Int,
    val currentOccupancy: Int,
    val rentPerBed: Double,
    val amenities: List<String>,
    val floor: Int,
    val facing: String, // North, South, East, West
    val hasAttachedBathroom: Boolean,
    val hasBalcony: Boolean,
    val isACRoom: Boolean,
    val wifiStrength: Int, // 1-5 scale
    val condition: RoomCondition,
    val restrictions: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val lastMaintenance: Long? = null,
    val isAvailable: Boolean = true
) {
    companion object {
        fun fromMap(map: Map<String, Any>): RoomInfo {
            return RoomInfo(
                roomNumber = map["roomNumber"] as String,
                hostelId = map["hostelId"] as String,
                roomType = RoomType.valueOf(map["roomType"] as String),
                capacity = (map["capacity"] as Number).toInt(),
                currentOccupancy = (map["currentOccupancy"] as Number).toInt(),
                rentPerBed = (map["rentPerBed"] as Number).toDouble(),
                amenities = (map["amenities"] as? List<String>) ?: emptyList(),
                floor = (map["floor"] as Number).toInt(),
                facing = map["facing"] as String,
                hasAttachedBathroom = map["hasAttachedBathroom"] as Boolean,
                hasBalcony = map["hasBalcony"] as Boolean,
                isACRoom = map["isACRoom"] as Boolean,
                wifiStrength = (map["wifiStrength"] as Number).toInt(),
                condition = RoomCondition.valueOf(map["condition"] as String),
                restrictions = (map["restrictions"] as? List<String>) ?: emptyList(),
                images = (map["images"] as? List<String>) ?: emptyList(),
                lastMaintenance = (map["lastMaintenance"] as? Number)?.toLong(),
                isAvailable = map["isAvailable"] as? Boolean ?: true
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("roomNumber", roomNumber)
            put("hostelId", hostelId)
            put("roomType", roomType.name)
            put("capacity", capacity)
            put("currentOccupancy", currentOccupancy)
            put("rentPerBed", rentPerBed)
            put("amenities", amenities)
            put("floor", floor)
            put("facing", facing)
            put("hasAttachedBathroom", hasAttachedBathroom)
            put("hasBalcony", hasBalcony)
            put("isACRoom", isACRoom)
            put("wifiStrength", wifiStrength)
            put("condition", condition.name)
            put("restrictions", restrictions)
            put("images", images)
            lastMaintenance?.let { put("lastMaintenance", it) }
            put("isAvailable", isAvailable)
        }
    }
    
    fun getAvailableBeds(): Int = capacity - currentOccupancy
    fun isFullyOccupied(): Boolean = currentOccupancy >= capacity
}

/**
 * 🤝 Roommate Match Request
 */
@Serializable
data class MatchRequest(
    val id: String,
    val requesterId: String,
    val targetUserId: String,
    val message: String? = null,
    val compatibilityScore: Double,
    val matchReasons: List<String>,
    val status: MatchStatus,
    val requestedAt: Long,
    val respondedAt: Long? = null,
    val expiresAt: Long,
    val roomPreference: String? = null,
    val moveInDate: Long? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>): MatchRequest {
            return MatchRequest(
                id = map["id"] as String,
                requesterId = map["requesterId"] as String,
                targetUserId = map["targetUserId"] as String,
                message = map["message"] as? String,
                compatibilityScore = (map["compatibilityScore"] as Number).toDouble(),
                matchReasons = (map["matchReasons"] as? List<String>) ?: emptyList(),
                status = MatchStatus.valueOf(map["status"] as String),
                requestedAt = (map["requestedAt"] as Number).toLong(),
                respondedAt = (map["respondedAt"] as? Number)?.toLong(),
                expiresAt = (map["expiresAt"] as Number).toLong(),
                roomPreference = map["roomPreference"] as? String,
                moveInDate = (map["moveInDate"] as? Number)?.toLong()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return buildMap {
            put("id", id)
            put("requesterId", requesterId)
            put("targetUserId", targetUserId)
            message?.let { put("message", it) }
            put("compatibilityScore", compatibilityScore)
            put("matchReasons", matchReasons)
            put("status", status.name)
            put("requestedAt", requestedAt)
            respondedAt?.let { put("respondedAt", it) }
            put("expiresAt", expiresAt)
            roomPreference?.let { put("roomPreference", it) }
            moveInDate?.let { put("moveInDate", it) }
        }
    }
    
    fun isExpired(): Boolean = System.currentTimeMillis() > expiresAt
}

/**
 * 💰 Budget Range
 */
@Serializable
data class BudgetRange(
    val minBudget: Double,
    val maxBudget: Double,
    val includesUtilities: Boolean = false,
    val flexibleWithRoommates: Boolean = true
) {
    companion object {
        fun fromMap(map: Map<String, Any>): BudgetRange {
            return BudgetRange(
                minBudget = (map["minBudget"] as Number).toDouble(),
                maxBudget = (map["maxBudget"] as Number).toDouble(),
                includesUtilities = map["includesUtilities"] as? Boolean ?: false,
                flexibleWithRoommates = map["flexibleWithRoommates"] as? Boolean ?: true
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "minBudget" to minBudget,
            "maxBudget" to maxBudget,
            "includesUtilities" to includesUtilities,
            "flexibleWithRoommates" to flexibleWithRoommates
        )
    }
    
    fun isInRange(amount: Double): Boolean = amount in minBudget..maxBudget
}

/**
 * 🎯 Lifestyle Preferences
 */
@Serializable
data class LifestylePreferences(
    val smokingTolerance: ToleranceLevel,
    val drinkingTolerance: ToleranceLevel,
    val partyTolerance: ToleranceLevel,
    val musicVolume: VolumeLevel,
    val guestsFrequency: FrequencyLevel,
    val petFriendly: Boolean,
    val dietaryRestrictions: List<String> = emptyList(),
    val exerciseRoutine: FrequencyLevel,
    val religiousPractices: ToleranceLevel,
    val environmentalConsciousness: Int, // 1-5 scale
    val organizationLevel: Int, // 1-5 scale
    val socialMediaUsage: FrequencyLevel
) {
    companion object {
        fun fromMap(map: Map<String, Any>): LifestylePreferences {
            return LifestylePreferences(
                smokingTolerance = ToleranceLevel.valueOf(map["smokingTolerance"] as String),
                drinkingTolerance = ToleranceLevel.valueOf(map["drinkingTolerance"] as String),
                partyTolerance = ToleranceLevel.valueOf(map["partyTolerance"] as String),
                musicVolume = VolumeLevel.valueOf(map["musicVolume"] as String),
                guestsFrequency = FrequencyLevel.valueOf(map["guestsFrequency"] as String),
                petFriendly = map["petFriendly"] as Boolean,
                dietaryRestrictions = (map["dietaryRestrictions"] as? List<String>) ?: emptyList(),
                exerciseRoutine = FrequencyLevel.valueOf(map["exerciseRoutine"] as String),
                religiousPractices = ToleranceLevel.valueOf(map["religiousPractices"] as String),
                environmentalConsciousness = (map["environmentalConsciousness"] as Number).toInt(),
                organizationLevel = (map["organizationLevel"] as Number).toInt(),
                socialMediaUsage = FrequencyLevel.valueOf(map["socialMediaUsage"] as String)
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "smokingTolerance" to smokingTolerance.name,
            "drinkingTolerance" to drinkingTolerance.name,
            "partyTolerance" to partyTolerance.name,
            "musicVolume" to musicVolume.name,
            "guestsFrequency" to guestsFrequency.name,
            "petFriendly" to petFriendly,
            "dietaryRestrictions" to dietaryRestrictions,
            "exerciseRoutine" to exerciseRoutine.name,
            "religiousPractices" to religiousPractices.name,
            "environmentalConsciousness" to environmentalConsciousness,
            "organizationLevel" to organizationLevel,
            "socialMediaUsage" to socialMediaUsage.name
        )
    }
}

/**
 * 📚 Academic Preferences
 */
@Serializable
data class AcademicPreferences(
    val studyHours: StudySchedule,
    val studyStyle: StudyStyle,
    val examPreparationStyle: StudyStyle,
    val groupStudyPreference: ToleranceLevel,
    val libraryUsage: FrequencyLevel,
    val academicGoals: AcademicLevel,
    val tutoring: TutoringPreference,
    val competitiveExams: Boolean,
    val researchInterest: Boolean,
    val internshipPlans: Boolean
) {
    companion object {
        fun fromMap(map: Map<String, Any>): AcademicPreferences {
            return AcademicPreferences(
                studyHours = StudySchedule.valueOf(map["studyHours"] as String),
                studyStyle = StudyStyle.valueOf(map["studyStyle"] as String),
                examPreparationStyle = StudyStyle.valueOf(map["examPreparationStyle"] as String),
                groupStudyPreference = ToleranceLevel.valueOf(map["groupStudyPreference"] as String),
                libraryUsage = FrequencyLevel.valueOf(map["libraryUsage"] as String),
                academicGoals = AcademicLevel.valueOf(map["academicGoals"] as String),
                tutoring = TutoringPreference.valueOf(map["tutoring"] as String),
                competitiveExams = map["competitiveExams"] as Boolean,
                researchInterest = map["researchInterest"] as Boolean,
                internshipPlans = map["internshipPlans"] as Boolean
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "studyHours" to studyHours.name,
            "studyStyle" to studyStyle.name,
            "examPreparationStyle" to examPreparationStyle.name,
            "groupStudyPreference" to groupStudyPreference.name,
            "libraryUsage" to libraryUsage.name,
            "academicGoals" to academicGoals.name,
            "tutoring" to tutoring.name,
            "competitiveExams" to competitiveExams,
            "researchInterest" to researchInterest,
            "internshipPlans" to internshipPlans
        )
    }
}

/**
 * 👥 Social Preferences
 */
@Serializable
data class SocialPreferences(
    val socialLevel: SocialLevel,
    val communicationStyle: CommunicationStyle,
    val conflictResolution: ConflictStyle,
    val sharingWillingness: SharingLevel,
    val privacyNeeds: PrivacyLevel,
    val friendshipExpectation: FriendshipLevel,
    val culturalOpenness: Int, // 1-5 scale
    val languagePreference: List<String>,
    val socialActivities: List<String>
) {
    companion object {
        fun fromMap(map: Map<String, Any>): SocialPreferences {
            return SocialPreferences(
                socialLevel = SocialLevel.valueOf(map["socialLevel"] as String),
                communicationStyle = CommunicationStyle.valueOf(map["communicationStyle"] as String),
                conflictResolution = ConflictStyle.valueOf(map["conflictResolution"] as String),
                sharingWillingness = SharingLevel.valueOf(map["sharingWillingness"] as String),
                privacyNeeds = PrivacyLevel.valueOf(map["privacyNeeds"] as String),
                friendshipExpectation = FriendshipLevel.valueOf(map["friendshipExpectation"] as String),
                culturalOpenness = (map["culturalOpenness"] as Number).toInt(),
                languagePreference = (map["languagePreference"] as? List<String>) ?: emptyList(),
                socialActivities = (map["socialActivities"] as? List<String>) ?: emptyList()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "socialLevel" to socialLevel.name,
            "communicationStyle" to communicationStyle.name,
            "conflictResolution" to conflictResolution.name,
            "sharingWillingness" to sharingWillingness.name,
            "privacyNeeds" to privacyNeeds.name,
            "friendshipExpectation" to friendshipExpectation.name,
            "culturalOpenness" to culturalOpenness,
            "languagePreference" to languagePreference,
            "socialActivities" to socialActivities
        )
    }
}

/**
 * 🧼 Hygiene Preferences
 */
@Serializable
data class HygienePreferences(
    val cleanlinessLevel: Int, // 1-5 scale
    val bathingSchedule: TimeRange,
    val laundryFrequency: FrequencyLevel,
    val roomCleaningSchedule: FrequencyLevel,
    val personalHygieneImportance: Int, // 1-5 scale
    val sharedSpaceCleanliness: Int, // 1-5 scale
    val toleranceForMess: ToleranceLevel
) {
    companion object {
        fun fromMap(map: Map<String, Any>): HygienePreferences {
            return HygienePreferences(
                cleanlinessLevel = (map["cleanlinessLevel"] as Number).toInt(),
                bathingSchedule = TimeRange.valueOf(map["bathingSchedule"] as String),
                laundryFrequency = FrequencyLevel.valueOf(map["laundryFrequency"] as String),
                roomCleaningSchedule = FrequencyLevel.valueOf(map["roomCleaningSchedule"] as String),
                personalHygieneImportance = (map["personalHygieneImportance"] as Number).toInt(),
                sharedSpaceCleanliness = (map["sharedSpaceCleanliness"] as Number).toInt(),
                toleranceForMess = ToleranceLevel.valueOf(map["toleranceForMess"] as String)
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "cleanlinessLevel" to cleanlinessLevel,
            "bathingSchedule" to bathingSchedule.name,
            "laundryFrequency" to laundryFrequency.name,
            "roomCleaningSchedule" to roomCleaningSchedule.name,
            "personalHygieneImportance" to personalHygieneImportance,
            "sharedSpaceCleanliness" to sharedSpaceCleanliness,
            "toleranceForMess" to toleranceForMess.name
        )
    }
}

/**
 * 😴 Sleeping Habits
 */
@Serializable
data class SleepingHabits(
    val bedtime: TimeRange,
    val wakeupTime: TimeRange,
    val sleepDuration: Int, // hours
    val napFrequency: FrequencyLevel,
    val snoring: Boolean,
    val lightSleeper: Boolean,
    val nightOwl: Boolean,
    val earlyBird: Boolean,
    val weekendScheduleDifference: Boolean,
    val sleepAids: List<String> = emptyList() // white noise, blackout curtains, etc.
) {
    companion object {
        fun fromMap(map: Map<String, Any>): SleepingHabits {
            return SleepingHabits(
                bedtime = TimeRange.valueOf(map["bedtime"] as String),
                wakeupTime = TimeRange.valueOf(map["wakeupTime"] as String),
                sleepDuration = (map["sleepDuration"] as Number).toInt(),
                napFrequency = FrequencyLevel.valueOf(map["napFrequency"] as String),
                snoring = map["snoring"] as Boolean,
                lightSleeper = map["lightSleeper"] as Boolean,
                nightOwl = map["nightOwl"] as Boolean,
                earlyBird = map["earlyBird"] as Boolean,
                weekendScheduleDifference = map["weekendScheduleDifference"] as Boolean,
                sleepAids = (map["sleepAids"] as? List<String>) ?: emptyList()
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "bedtime" to bedtime.name,
            "wakeupTime" to wakeupTime.name,
            "sleepDuration" to sleepDuration,
            "napFrequency" to napFrequency.name,
            "snoring" to snoring,
            "lightSleeper" to lightSleeper,
            "nightOwl" to nightOwl,
            "earlyBird" to earlyBird,
            "weekendScheduleDifference" to weekendScheduleDifference,
            "sleepAids" to sleepAids
        )
    }
}

/**
 * 📞 Contact Preferences
 */
@Serializable
data class ContactPreferences(
    val preferredContactMethod: ContactMethod,
    val responseTime: ResponseTime,
    val availabilityHours: TimeRange,
    val emergencyContact: Boolean,
    val socialMediaSharing: Boolean,
    val phoneNumberSharing: Boolean,
    val emailSharing: Boolean
) {
    companion object {
        fun fromMap(map: Map<String, Any>): ContactPreferences {
            return ContactPreferences(
                preferredContactMethod = ContactMethod.valueOf(map["preferredContactMethod"] as String),
                responseTime = ResponseTime.valueOf(map["responseTime"] as String),
                availabilityHours = TimeRange.valueOf(map["availabilityHours"] as String),
                emergencyContact = map["emergencyContact"] as Boolean,
                socialMediaSharing = map["socialMediaSharing"] as Boolean,
                phoneNumberSharing = map["phoneNumberSharing"] as Boolean,
                emailSharing = map["emailSharing"] as Boolean
            )
        }
    }
    
    fun toMap(): Map<String, Any> {
        return mapOf(
            "preferredContactMethod" to preferredContactMethod.name,
            "responseTime" to responseTime.name,
            "availabilityHours" to availabilityHours.name,
            "emergencyContact" to emergencyContact,
            "socialMediaSharing" to socialMediaSharing,
            "phoneNumberSharing" to phoneNumberSharing,
            "emailSharing" to emailSharing
        )
    }
}

/**
 * 🏷️ Enums for Various Preferences
 */
enum class Gender { MALE, FEMALE, NON_BINARY, PREFER_NOT_TO_SAY }
enum class RoomType { SINGLE, DOUBLE, TRIPLE, QUAD, DORMITORY }
enum class RoomCondition { EXCELLENT, GOOD, AVERAGE, NEEDS_REPAIR }
enum class MatchStatus { PENDING, ACCEPTED, REJECTED, EXPIRED, WITHDRAWN }
enum class VerificationStatus { PENDING, VERIFIED, REJECTED }

enum class ToleranceLevel { NONE, LOW, MODERATE, HIGH, VERY_HIGH }
enum class VolumeLevel { SILENT, QUIET, MODERATE, LOUD, VERY_LOUD }
enum class FrequencyLevel { NEVER, RARELY, SOMETIMES, OFTEN, ALWAYS }

enum class StudySchedule { EARLY_MORNING, MORNING, AFTERNOON, EVENING, LATE_NIGHT, FLEXIBLE }
enum class StudyStyle { QUIET, MUSIC, GROUP, INDIVIDUAL, INTENSIVE, CASUAL }
enum class AcademicLevel { PASS, GOOD, EXCELLENT, DISTINCTION, RESEARCH }
enum class TutoringPreference { NONE, GIVING, RECEIVING, BOTH }

enum class SocialLevel { INTROVERT, AMBIVERT, EXTROVERT }
enum class CommunicationStyle { DIRECT, INDIRECT, ASSERTIVE, PASSIVE }
enum class ConflictStyle { AVOIDING, ACCOMMODATING, COMPETING, COLLABORATING }
enum class SharingLevel { MINIMAL, BASIC, MODERATE, HIGH, EVERYTHING }
enum class PrivacyLevel { VERY_HIGH, HIGH, MODERATE, LOW, MINIMAL }
enum class FriendshipLevel { ACQUAINTANCE, CASUAL, CLOSE, BEST_FRIEND }

enum class PersonalityType { 
    INTJ, INTP, ENTJ, ENTP, INFJ, INFP, ENFJ, ENFP,
    ISTJ, ISFJ, ESTJ, ESFJ, ISTP, ISFP, ESTP, ESFP
}

enum class ContactMethod { PHONE, EMAIL, MESSAGING_APP, IN_PERSON, SOCIAL_MEDIA }
enum class ResponseTime { IMMEDIATE, WITHIN_HOUR, WITHIN_DAY, FLEXIBLE }
enum class TimeRange { 
    EARLY_MORNING, MORNING, AFTERNOON, EVENING, NIGHT, LATE_NIGHT, ANYTIME
}
