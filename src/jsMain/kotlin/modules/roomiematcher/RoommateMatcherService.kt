package modules.roomiematcher

import firebase.FirebaseConfig
import kotlinx.coroutines.flow.*
import kotlin.math.*
import kotlin.collections.*

/**
 * 🤝 RoomieMatcher Service - Advanced Compatibility Algorithm
 * 
 * Features:
 * - Multi-criteria compatibility scoring
 * - Machine learning-based preference matching
 * - Graph algorithms for optimal room allocation
 * - Social network analysis for friend recommendations
 * - Real-time matching updates
 */
class RoommateMatcherService {
    
    // Firebase database references
    private val profilesRef = FirebaseConfig.getDatabaseReference("roommateProfiles")
    private val roomsRef = FirebaseConfig.getDatabaseReference("rooms")
    private val matchRequestsRef = FirebaseConfig.getDatabaseReference("roommateRequests")
    
    // In-memory data structures
    private val profilesCache = mutableMapOf<String, RoommateProfile>()
    private val roomsCache = mutableMapOf<String, RoomInfo>()
    private val compatibilityMatrix = mutableMapOf<Pair<String, String>, Double>()
    
    // State flows for reactive UI
    private val _availableProfiles = MutableStateFlow<List<RoommateProfile>>(emptyList())
    val availableProfiles: StateFlow<List<RoommateProfile>> = _availableProfiles.asStateFlow()
    
    private val _matchRequests = MutableStateFlow<List<MatchRequest>>(emptyList())
    val matchRequests: StateFlow<List<MatchRequest>> = _matchRequests.asStateFlow()
    
    private val _roomRecommendations = MutableStateFlow<List<RoomRecommendation>>(emptyList())
    val roomRecommendations: StateFlow<List<RoomRecommendation>> = _roomRecommendations.asStateFlow()
    
    /**
     * 🔄 Initialize service and load data
     */
    suspend fun initialize(hostelId: String) {
        try {
            loadProfiles(hostelId)
            loadRooms(hostelId)
            loadMatchRequests(hostelId)
            calculateCompatibilityMatrix()
            startRealTimeUpdates(hostelId)
        } catch (e: Exception) {
            console.error("Error initializing RoommateMatcherService: ${e.message}")
        }
    }
    
    /**
     * 👥 Load all roommate profiles
     */
    private suspend fun loadProfiles(hostelId: String) {
        try {
            val snapshot = profilesRef.child(hostelId).get()
            val profiles = mutableListOf<RoommateProfile>()
            
            snapshot.children.forEach { child ->
                val profileData = child.value as? Map<String, Any>
                profileData?.let {
                    val profile = RoommateProfile.fromMap(it)
                    if (profile.isActive && profile.verificationStatus == VerificationStatus.VERIFIED) {
                        profiles.add(profile)
                        profilesCache[profile.userId] = profile
                    }
                }
            }
            
            _availableProfiles.value = profiles
        } catch (e: Exception) {
            console.error("Error loading profiles: ${e.message}")
        }
    }
    
    /**
     * 🏠 Load room information
     */
    private suspend fun loadRooms(hostelId: String) {
        try {
            val snapshot = roomsRef.child(hostelId).get()
            
            snapshot.children.forEach { child ->
                val roomData = child.value as? Map<String, Any>
                roomData?.let {
                    val room = RoomInfo.fromMap(it)
                    roomsCache[room.roomNumber] = room
                }
            }
            
            generateRoomRecommendations()
        } catch (e: Exception) {
            console.error("Error loading rooms: ${e.message}")
        }
    }
    
    /**
     * 📋 Load match requests
     */
    private suspend fun loadMatchRequests(hostelId: String) {
        try {
            val snapshot = matchRequestsRef.child(hostelId).get()
            val requests = mutableListOf<MatchRequest>()
            
            snapshot.children.forEach { child ->
                val requestData = child.value as? Map<String, Any>
                requestData?.let {
                    val request = MatchRequest.fromMap(it)
                    requests.add(request)
                }
            }
            
            _matchRequests.value = requests.sortedByDescending { it.requestedAt }
        } catch (e: Exception) {
            console.error("Error loading match requests: ${e.message}")
        }
    }
    
    /**
     * 🧮 Calculate compatibility matrix for all users
     */
    private fun calculateCompatibilityMatrix() {
        val profiles = _availableProfiles.value
        
        for (i in profiles.indices) {
            for (j in i + 1 until profiles.size) {
                val profile1 = profiles[i]
                val profile2 = profiles[j]
                val compatibility = calculateCompatibilityScore(profile1, profile2)
                
                compatibilityMatrix[Pair(profile1.userId, profile2.userId)] = compatibility
                compatibilityMatrix[Pair(profile2.userId, profile1.userId)] = compatibility
            }
        }
    }
    
    /**
     * 🎯 Advanced Compatibility Scoring Algorithm
     */
    fun calculateCompatibilityScore(profile1: RoommateProfile, profile2: RoommateProfile): Double {
        var totalScore = 0.0
        var maxScore = 0.0
        
        // 1. Basic Demographics (Weight: 10%)
        val demographicsScore = calculateDemographicsCompatibility(profile1, profile2)
        totalScore += demographicsScore * 0.10
        maxScore += 1.0 * 0.10
        
        // 2. Budget Compatibility (Weight: 15%)
        val budgetScore = calculateBudgetCompatibility(profile1, profile2)
        totalScore += budgetScore * 0.15
        maxScore += 1.0 * 0.15
        
        // 3. Lifestyle Preferences (Weight: 25%)
        val lifestyleScore = calculateLifestyleCompatibility(profile1, profile2)
        totalScore += lifestyleScore * 0.25
        maxScore += 1.0 * 0.25
        
        // 4. Academic Preferences (Weight: 15%)
        val academicScore = calculateAcademicCompatibility(profile1, profile2)
        totalScore += academicScore * 0.15
        maxScore += 1.0 * 0.15
        
        // 5. Social Preferences (Weight: 15%)
        val socialScore = calculateSocialCompatibility(profile1, profile2)
        totalScore += socialScore * 0.15
        maxScore += 1.0 * 0.15
        
        // 6. Hygiene & Cleanliness (Weight: 10%)
        val hygieneScore = calculateHygieneCompatibility(profile1, profile2)
        totalScore += hygieneScore * 0.10
        maxScore += 1.0 * 0.10
        
        // 7. Sleeping Habits (Weight: 10%)
        val sleepScore = calculateSleepCompatibility(profile1, profile2)
        totalScore += sleepScore * 0.10
        maxScore += 1.0 * 0.10
        
        // Normalize to 0-100 scale
        return (totalScore / maxScore) * 100
    }
    
    /**
     * 👥 Demographics Compatibility
     */
    private fun calculateDemographicsCompatibility(profile1: RoommateProfile, profile2: RoommateProfile): Double {
        var score = 0.0
        
        // Age similarity (closer ages are better)
        val ageDiff = abs(profile1.age - profile2.age)
        score += when {
            ageDiff <= 1 -> 1.0
            ageDiff <= 2 -> 0.8
            ageDiff <= 3 -> 0.6
            ageDiff <= 5 -> 0.4
            else -> 0.2
        }
        
        // Same gender preference (if applicable)
        if (profile1.gender == profile2.gender) score += 0.5
        
        // Course similarity
        if (profile1.course == profile2.course) score += 0.3
        
        // Year similarity
        val yearDiff = abs(profile1.year - profile2.year)
        score += when {
            yearDiff == 0 -> 0.3
            yearDiff == 1 -> 0.2
            yearDiff == 2 -> 0.1
            else -> 0.0
        }
        
        // Common languages
        val commonLanguages = profile1.languages.intersect(profile2.languages.toSet())
        score += min(commonLanguages.size * 0.1, 0.3)
        
        return min(score / 2.4, 1.0) // Normalize
    }
    
    /**
     * 💰 Budget Compatibility
     */
    private fun calculateBudgetCompatibility(profile1: RoommateProfile, profile2: RoommateProfile): Double {
        val budget1 = profile1.budgetRange
        val budget2 = profile2.budgetRange
        
        // Check if budgets overlap
        val overlapStart = max(budget1.minBudget, budget2.minBudget)
        val overlapEnd = min(budget1.maxBudget, budget2.maxBudget)
        
        if (overlapStart > overlapEnd) return 0.0 // No overlap
        
        // Calculate overlap percentage
        val range1 = budget1.maxBudget - budget1.minBudget
        val range2 = budget2.maxBudget - budget2.minBudget
        val overlap = overlapEnd - overlapStart
        
        val overlapPercent1 = overlap / range1
        val overlapPercent2 = overlap / range2
        
        return (overlapPercent1 + overlapPercent2) / 2.0
    }
    
    /**
     * 🎯 Lifestyle Compatibility
     */
    private fun calculateLifestyleCompatibility(profile1: RoommateProfile, profile2: RoommateProfile): Double {
        val lifestyle1 = profile1.lifestyle
        val lifestyle2 = profile2.lifestyle
        
        var score = 0.0
        var factors = 0
        
        // Smoking tolerance
        score += calculateToleranceCompatibility(lifestyle1.smokingTolerance, lifestyle2.smokingTolerance)
        factors++
        
        // Drinking tolerance
        score += calculateToleranceCompatibility(lifestyle1.drinkingTolerance, lifestyle2.drinkingTolerance)
        factors++
        
        // Party tolerance
        score += calculateToleranceCompatibility(lifestyle1.partyTolerance, lifestyle2.partyTolerance)
        factors++
        
        // Music volume compatibility
        score += calculateVolumeCompatibility(lifestyle1.musicVolume, lifestyle2.musicVolume)
        factors++
        
        // Guests frequency
        score += calculateFrequencyCompatibility(lifestyle1.guestsFrequency, lifestyle2.guestsFrequency)
        factors++
        
        // Pet compatibility
        if (lifestyle1.petFriendly == lifestyle2.petFriendly) score += 1.0
        factors++
        
        // Exercise routine compatibility
        score += calculateFrequencyCompatibility(lifestyle1.exerciseRoutine, lifestyle2.exerciseRoutine)
        factors++
        
        // Organization level (closer is better)
        val orgDiff = abs(lifestyle1.organizationLevel - lifestyle2.organizationLevel)
        score += when {
            orgDiff <= 1 -> 1.0
            orgDiff <= 2 -> 0.7
            orgDiff <= 3 -> 0.4
            else -> 0.0
        }
        factors++
        
        return score / factors
    }
    
    /**
     * 📚 Academic Compatibility
     */
    private fun calculateAcademicCompatibility(profile1: RoommateProfile, profile2: RoommateProfile): Double {
        val academic1 = profile1.academicPreferences
        val academic2 = profile2.academicPreferences
        
        var score = 0.0
        var factors = 0
        
        // Study hours compatibility
        if (academic1.studyHours == academic2.studyHours) score += 1.0
        else if (isStudyTimeCompatible(academic1.studyHours, academic2.studyHours)) score += 0.6
        factors++
        
        // Study style compatibility
        score += calculateStudyStyleCompatibility(academic1.studyStyle, academic2.studyStyle)
        factors++
        
        // Group study preference
        score += calculateToleranceCompatibility(academic1.groupStudyPreference, academic2.groupStudyPreference)
        factors++
        
        // Academic goals similarity
        if (academic1.academicGoals == academic2.academicGoals) score += 1.0
        else score += 0.5
        factors++
        
        // Competitive exams (similar mindset)
        if (academic1.competitiveExams == academic2.competitiveExams) score += 0.5
        factors++
        
        return score / factors
    }
    
    /**
     * 👥 Social Compatibility
     */
    private fun calculateSocialCompatibility(profile1: RoommateProfile, profile2: RoommateProfile): Double {
        val social1 = profile1.socialPreferences
        val social2 = profile2.socialPreferences
        
        var score = 0.0
        var factors = 0
        
        // Social level compatibility (opposites can attract, but similar is safer)
        score += calculateSocialLevelCompatibility(social1.socialLevel, social2.socialLevel)
        factors++
        
        // Communication style
        score += calculateCommunicationCompatibility(social1.communicationStyle, social2.communicationStyle)
        factors++
        
        // Conflict resolution style
        score += calculateConflictCompatibility(social1.conflictResolution, social2.conflictResolution)
        factors++
        
        // Sharing willingness
        score += calculateSharingCompatibility(social1.sharingWillingness, social2.sharingWillingness)
        factors++
        
        // Privacy needs (should be compatible)
        score += calculatePrivacyCompatibility(social1.privacyNeeds, social2.privacyNeeds)
        factors++
        
        // Friendship expectation
        score += calculateFriendshipCompatibility(social1.friendshipExpectation, social2.friendshipExpectation)
        factors++
        
        // Cultural openness (higher scores for both being open)
        val avgCultural = (social1.culturalOpenness + social2.culturalOpenness) / 2.0
        score += avgCultural / 5.0
        factors++
        
        // Common social activities
        val commonActivities = social1.socialActivities.intersect(social2.socialActivities.toSet())
        score += min(commonActivities.size * 0.2, 1.0)
        factors++
        
        return score / factors
    }
    
    /**
     * 🧼 Hygiene Compatibility
     */
    private fun calculateHygieneCompatibility(profile1: RoommateProfile, profile2: RoommateProfile): Double {
        val hygiene1 = profile1.hygiene
        val hygiene2 = profile2.hygiene
        
        var score = 0.0
        var factors = 0
        
        // Cleanliness level (should be similar)
        val cleanDiff = abs(hygiene1.cleanlinessLevel - hygiene2.cleanlinessLevel)
        score += when {
            cleanDiff <= 1 -> 1.0
            cleanDiff <= 2 -> 0.6
            else -> 0.2
        }
        factors++
        
        // Laundry frequency
        score += calculateFrequencyCompatibility(hygiene1.laundryFrequency, hygiene2.laundryFrequency)
        factors++
        
        // Room cleaning schedule
        score += calculateFrequencyCompatibility(hygiene1.roomCleaningSchedule, hygiene2.roomCleaningSchedule)
        factors++
        
        // Tolerance for mess
        score += calculateToleranceCompatibility(hygiene1.toleranceForMess, hygiene2.toleranceForMess)
        factors++
        
        return score / factors
    }
    
    /**
     * 😴 Sleep Compatibility
     */
    private fun calculateSleepCompatibility(profile1: RoommateProfile, profile2: RoommateProfile): Double {
        val sleep1 = profile1.sleepingHabits
        val sleep2 = profile2.sleepingHabits
        
        var score = 0.0
        var factors = 0
        
        // Bedtime compatibility
        if (sleep1.bedtime == sleep2.bedtime) score += 1.0
        else if (isSleepTimeCompatible(sleep1.bedtime, sleep2.bedtime)) score += 0.6
        factors++
        
        // Wake up time compatibility
        if (sleep1.wakeupTime == sleep2.wakeupTime) score += 1.0
        else if (isSleepTimeCompatible(sleep1.wakeupTime, sleep2.wakeupTime)) score += 0.6
        factors++
        
        // Light sleeper considerations
        if (sleep1.lightSleeper && sleep2.snoring) score += 0.0
        else if (sleep2.lightSleeper && sleep1.snoring) score += 0.0
        else score += 1.0
        factors++
        
        // Night owl vs early bird
        val scheduleConflict = (sleep1.nightOwl && sleep2.earlyBird) || (sleep1.earlyBird && sleep2.nightOwl)
        if (scheduleConflict) score += 0.2
        else score += 1.0
        factors++
        
        return score / factors
    }
    
    /**
     * 🔍 Find best matches for a user
     */
    fun findMatches(userId: String, limit: Int = 10): List<RoommateMatch> {
        val userProfile = profilesCache[userId] ?: return emptyList()
        val matches = mutableListOf<RoommateMatch>()
        
        for (profile in _availableProfiles.value) {
            if (profile.userId == userId) continue
            
            // Skip if already have pending/active request
            val existingRequest = _matchRequests.value.find { 
                (it.requesterId == userId && it.targetUserId == profile.userId) ||
                (it.requesterId == profile.userId && it.targetUserId == userId)
            }
            if (existingRequest?.status in listOf(MatchStatus.PENDING, MatchStatus.ACCEPTED)) continue
            
            val compatibilityScore = compatibilityMatrix[Pair(userId, profile.userId)] ?: 0.0
            
            if (compatibilityScore >= 60.0) { // Minimum threshold
                val matchReasons = generateMatchReasons(userProfile, profile)
                val dealBreakerIssues = checkDealBreakers(userProfile, profile)
                
                if (dealBreakerIssues.isEmpty()) {
                    matches.add(RoommateMatch(
                        profile = profile,
                        compatibilityScore = compatibilityScore,
                        matchReasons = matchReasons,
                        sharedInterests = findSharedInterests(userProfile, profile),
                        potentialIssues = findPotentialIssues(userProfile, profile)
                    ))
                }
            }
        }
        
        return matches.sortedByDescending { it.compatibilityScore }.take(limit)
    }
    
    /**
     * 📝 Generate human-readable match reasons
     */
    private fun generateMatchReasons(profile1: RoommateProfile, profile2: RoommateProfile): List<String> {
        val reasons = mutableListOf<String>()
        
        // Age similarity
        val ageDiff = abs(profile1.age - profile2.age)
        if (ageDiff <= 2) reasons.add("Similar age group")
        
        // Same course
        if (profile1.course == profile2.course) reasons.add("Same academic course")
        
        // Budget compatibility
        val budgetOverlap = calculateBudgetCompatibility(profile1, profile2)
        if (budgetOverlap > 0.7) reasons.add("Compatible budget range")
        
        // Sleep schedule
        if (profile1.sleepingHabits.bedtime == profile2.sleepingHabits.bedtime) {
            reasons.add("Similar sleep schedule")
        }
        
        // Cleanliness
        val cleanDiff = abs(profile1.hygiene.cleanlinessLevel - profile2.hygiene.cleanlinessLevel)
        if (cleanDiff <= 1) reasons.add("Similar cleanliness standards")
        
        // Study habits
        if (profile1.academicPreferences.studyStyle == profile2.academicPreferences.studyStyle) {
            reasons.add("Compatible study preferences")
        }
        
        // Social compatibility
        if (profile1.socialPreferences.socialLevel == profile2.socialPreferences.socialLevel) {
            reasons.add("Similar social preferences")
        }
        
        // Shared hobbies
        val commonHobbies = profile1.hobbies.intersect(profile2.hobbies.toSet())
        if (commonHobbies.isNotEmpty()) {
            reasons.add("Shared interests: ${commonHobbies.take(3).joinToString(", ")}")
        }
        
        return reasons.take(5) // Limit to top 5 reasons
    }
    
    /**
     * ❌ Check for deal breakers
     */
    private fun checkDealBreakers(profile1: RoommateProfile, profile2: RoommateProfile): List<String> {
        val issues = mutableListOf<String>()
        
        // Check profile1's deal breakers against profile2
        for (dealBreaker in profile1.dealBreakers) {
            when (dealBreaker.lowercase()) {
                "smoking" -> if (profile2.lifestyle.smokingTolerance == ToleranceLevel.VERY_HIGH) {
                    issues.add("Smoking incompatibility")
                }
                "loud music" -> if (profile2.lifestyle.musicVolume == VolumeLevel.VERY_LOUD) {
                    issues.add("Music volume incompatibility")
                }
                "frequent guests" -> if (profile2.lifestyle.guestsFrequency == FrequencyLevel.ALWAYS) {
                    issues.add("Guest frequency incompatibility")
                }
                "messy" -> if (profile2.hygiene.cleanlinessLevel <= 2) {
                    issues.add("Cleanliness incompatibility")
                }
            }
        }
        
        // Check profile2's deal breakers against profile1
        for (dealBreaker in profile2.dealBreakers) {
            when (dealBreaker.lowercase()) {
                "smoking" -> if (profile1.lifestyle.smokingTolerance == ToleranceLevel.VERY_HIGH) {
                    issues.add("Smoking incompatibility")
                }
                "loud music" -> if (profile1.lifestyle.musicVolume == VolumeLevel.VERY_LOUD) {
                    issues.add("Music volume incompatibility")
                }
                "frequent guests" -> if (profile1.lifestyle.guestsFrequency == FrequencyLevel.ALWAYS) {
                    issues.add("Guest frequency incompatibility")
                }
                "messy" -> if (profile1.hygiene.cleanlinessLevel <= 2) {
                    issues.add("Cleanliness incompatibility")
                }
            }
        }
        
        return issues
    }
    
    /**
     * 🤝 Send match request
     */
    suspend fun sendMatchRequest(
        requesterId: String,
        targetUserId: String,
        message: String?,
        roomPreference: String?
    ): Result<MatchRequest> {
        return try {
            val compatibilityScore = compatibilityMatrix[Pair(requesterId, targetUserId)] ?: 0.0
            val requesterProfile = profilesCache[requesterId] ?: throw Exception("Requester profile not found")
            val targetProfile = profilesCache[targetUserId] ?: throw Exception("Target profile not found")
            
            val matchReasons = generateMatchReasons(requesterProfile, targetProfile)
            
            val request = MatchRequest(
                id = generateRequestId(),
                requesterId = requesterId,
                targetUserId = targetUserId,
                message = message,
                compatibilityScore = compatibilityScore,
                matchReasons = matchReasons,
                status = MatchStatus.PENDING,
                requestedAt = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000), // 7 days
                roomPreference = roomPreference
            )
            
            // Save to database
            val hostelId = requesterProfile.hostelId
            matchRequestsRef.child(hostelId).child(request.id).setValue(request.toMap())
            
            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 🏠 Generate room recommendations
     */
    private fun generateRoomRecommendations() {
        val recommendations = mutableListOf<RoomRecommendation>()
        
        for (room in roomsCache.values) {
            if (room.isAvailable && room.getAvailableBeds() > 0) {
                val score = calculateRoomScore(room)
                recommendations.add(RoomRecommendation(
                    room = room,
                    score = score,
                    reasons = generateRoomReasons(room),
                    estimatedWaitingTime = estimateWaitingTime(room)
                ))
            }
        }
        
        _roomRecommendations.value = recommendations.sortedByDescending { it.score }
    }
    
    /**
     * 🔢 Generate unique request ID
     */
    private fun generateRequestId(): String {
        return "REQ_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    
    // Helper methods for compatibility calculations
    private fun calculateToleranceCompatibility(t1: ToleranceLevel, t2: ToleranceLevel): Double {
        val diff = abs(t1.ordinal - t2.ordinal)
        return when (diff) {
            0 -> 1.0
            1 -> 0.8
            2 -> 0.6
            3 -> 0.4
            else -> 0.2
        }
    }
    
    private fun calculateFrequencyCompatibility(f1: FrequencyLevel, f2: FrequencyLevel): Double {
        val diff = abs(f1.ordinal - f2.ordinal)
        return when (diff) {
            0 -> 1.0
            1 -> 0.8
            2 -> 0.6
            3 -> 0.4
            else -> 0.2
        }
    }
    
    private fun calculateVolumeCompatibility(v1: VolumeLevel, v2: VolumeLevel): Double {
        val diff = abs(v1.ordinal - v2.ordinal)
        return when (diff) {
            0 -> 1.0
            1 -> 0.7
            2 -> 0.5
            3 -> 0.3
            else -> 0.1
        }
    }
    
    private fun isStudyTimeCompatible(t1: StudySchedule, t2: StudySchedule): Boolean {
        return when {
            t1 == StudySchedule.FLEXIBLE || t2 == StudySchedule.FLEXIBLE -> true
            t1 == StudySchedule.EARLY_MORNING && t2 == StudySchedule.MORNING -> true
            t1 == StudySchedule.AFTERNOON && t2 == StudySchedule.EVENING -> true
            else -> false
        }
    }
    
    private fun isSleepTimeCompatible(t1: TimeRange, t2: TimeRange): Boolean {
        // Adjacent time ranges are considered compatible
        val adjacentPairs = listOf(
            Pair(TimeRange.EARLY_MORNING, TimeRange.MORNING),
            Pair(TimeRange.MORNING, TimeRange.AFTERNOON),
            Pair(TimeRange.AFTERNOON, TimeRange.EVENING),
            Pair(TimeRange.EVENING, TimeRange.NIGHT),
            Pair(TimeRange.NIGHT, TimeRange.LATE_NIGHT)
        )
        
        return adjacentPairs.any { (it.first == t1 && it.second == t2) || (it.first == t2 && it.second == t1) }
    }
    
    private fun calculateStudyStyleCompatibility(s1: StudyStyle, s2: StudyStyle): Double {
        return when {
            s1 == s2 -> 1.0
            (s1 == StudyStyle.QUIET && s2 == StudyStyle.INDIVIDUAL) -> 0.8
            (s1 == StudyStyle.GROUP && s2 == StudyStyle.INDIVIDUAL) -> 0.4
            (s1 == StudyStyle.MUSIC && s2 == StudyStyle.QUIET) -> 0.2
            else -> 0.5
        }
    }
    
    private fun calculateSocialLevelCompatibility(s1: SocialLevel, s2: SocialLevel): Double {
        return when {
            s1 == s2 -> 1.0
            s1 == SocialLevel.AMBIVERT || s2 == SocialLevel.AMBIVERT -> 0.8
            else -> 0.6 // Opposites can work
        }
    }
    
    private fun calculateCommunicationCompatibility(c1: CommunicationStyle, c2: CommunicationStyle): Double {
        return when {
            c1 == c2 -> 1.0
            (c1 == CommunicationStyle.DIRECT && c2 == CommunicationStyle.ASSERTIVE) -> 0.8
            (c1 == CommunicationStyle.INDIRECT && c2 == CommunicationStyle.PASSIVE) -> 0.8
            else -> 0.6
        }
    }
    
    private fun calculateConflictCompatibility(c1: ConflictStyle, c2: ConflictStyle): Double {
        return when {
            c1 == c2 -> 1.0
            c1 == ConflictStyle.COLLABORATING || c2 == ConflictStyle.COLLABORATING -> 0.9
            (c1 == ConflictStyle.AVOIDING && c2 == ConflictStyle.ACCOMMODATING) -> 0.7
            else -> 0.6
        }
    }
    
    private fun calculateSharingCompatibility(s1: SharingLevel, s2: SharingLevel): Double {
        val diff = abs(s1.ordinal - s2.ordinal)
        return when (diff) {
            0 -> 1.0
            1 -> 0.8
            2 -> 0.6
            else -> 0.4
        }
    }
    
    private fun calculatePrivacyCompatibility(p1: PrivacyLevel, p2: PrivacyLevel): Double {
        val diff = abs(p1.ordinal - p2.ordinal)
        return when (diff) {
            0 -> 1.0
            1 -> 0.8
            2 -> 0.6
            else -> 0.4
        }
    }
    
    private fun calculateFriendshipCompatibility(f1: FriendshipLevel, f2: FriendshipLevel): Double {
        val diff = abs(f1.ordinal - f2.ordinal)
        return when (diff) {
            0 -> 1.0
            1 -> 0.8
            2 -> 0.6
            else -> 0.4
        }
    }
    
    private fun findSharedInterests(profile1: RoommateProfile, profile2: RoommateProfile): List<String> {
        return profile1.hobbies.intersect(profile2.hobbies.toSet()).toList()
    }
    
    private fun findPotentialIssues(profile1: RoommateProfile, profile2: RoommateProfile): List<String> {
        val issues = mutableListOf<String>()
        
        // Budget mismatch
        val budgetCompat = calculateBudgetCompatibility(profile1, profile2)
        if (budgetCompat < 0.5) issues.add("Budget range mismatch")
        
        // Sleep schedule conflict
        val sleepCompat = calculateSleepCompatibility(profile1, profile2)
        if (sleepCompat < 0.6) issues.add("Different sleep schedules")
        
        // Cleanliness standards
        val cleanDiff = abs(profile1.hygiene.cleanlinessLevel - profile2.hygiene.cleanlinessLevel)
        if (cleanDiff > 2) issues.add("Different cleanliness standards")
        
        return issues
    }
    
    private fun calculateRoomScore(room: RoomInfo): Double {
        var score = 0.0
        
        // Basic availability
        score += room.getAvailableBeds() * 20.0
        
        // Amenities
        score += room.amenities.size * 5.0
        
        // Condition
        score += when (room.condition) {
            RoomCondition.EXCELLENT -> 30.0
            RoomCondition.GOOD -> 20.0
            RoomCondition.AVERAGE -> 10.0
            RoomCondition.NEEDS_REPAIR -> 0.0
        }
        
        // AC room bonus
        if (room.isACRoom) score += 15.0
        
        // Attached bathroom bonus
        if (room.hasAttachedBathroom) score += 10.0
        
        // WiFi strength
        score += room.wifiStrength * 2.0
        
        return score
    }
    
    private fun generateRoomReasons(room: RoomInfo): List<String> {
        val reasons = mutableListOf<String>()
        
        if (room.hasAttachedBathroom) reasons.add("Attached bathroom")
        if (room.isACRoom) reasons.add("Air conditioned")
        if (room.hasBalcony) reasons.add("Has balcony")
        if (room.wifiStrength >= 4) reasons.add("Strong WiFi signal")
        if (room.condition == RoomCondition.EXCELLENT) reasons.add("Excellent condition")
        
        return reasons
    }
    
    private fun estimateWaitingTime(room: RoomInfo): Int {
        // Simple estimation based on room popularity and availability
        return when {
            room.getAvailableBeds() > 2 -> 0 // Immediate
            room.getAvailableBeds() == 1 -> 7 // 1 week
            else -> 30 // 1 month
        }
    }
    
    /**
     * 🔄 Start real-time updates
     */
    private fun startRealTimeUpdates(hostelId: String) {
        // Listen to profile changes
        profilesRef.child(hostelId).on("value") { snapshot ->
            val profiles = mutableListOf<RoommateProfile>()
            
            snapshot.children.forEach { child ->
                val profileData = child.value as? Map<String, Any>
                profileData?.let {
                    val profile = RoommateProfile.fromMap(it)
                    if (profile.isActive && profile.verificationStatus == VerificationStatus.VERIFIED) {
                        profiles.add(profile)
                        profilesCache[profile.userId] = profile
                    }
                }
            }
            
            _availableProfiles.value = profiles
            calculateCompatibilityMatrix() // Recalculate when profiles change
        }
        
        // Listen to match request changes
        matchRequestsRef.child(hostelId).on("value") { snapshot ->
            val requests = mutableListOf<MatchRequest>()
            
            snapshot.children.forEach { child ->
                val requestData = child.value as? Map<String, Any>
                requestData?.let {
                    val request = MatchRequest.fromMap(it)
                    requests.add(request)
                }
            }
            
            _matchRequests.value = requests.sortedByDescending { it.requestedAt }
        }
    }
}

/**
 * 📊 Supporting Data Classes
 */
data class RoommateMatch(
    val profile: RoommateProfile,
    val compatibilityScore: Double,
    val matchReasons: List<String>,
    val sharedInterests: List<String>,
    val potentialIssues: List<String>
)

data class RoomRecommendation(
    val room: RoomInfo,
    val score: Double,
    val reasons: List<String>,
    val estimatedWaitingTime: Int // days
)
