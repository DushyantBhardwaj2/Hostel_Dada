package com.hosteldada.feature.roomie.presentation

import com.hosteldada.core.domain.model.*

/**
 * UI State for the Roommate Survey screen
 */
data class SurveyUiState(
    // Survey progress
    val currentStep: Int = 0,
    val totalSteps: Int = 6,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    
    // Student info
    val studentId: String = "",
    val semester: String = "",
    
    // Step 1: Lifestyle Preferences
    val lifestyle: LifestyleFormState = LifestyleFormState(),
    
    // Step 2: Study Habits
    val study: StudyFormState = StudyFormState(),
    
    // Step 3: Cleanliness
    val cleanliness: CleanlinessFormState = CleanlinessFormState(),
    
    // Step 4: Social Preferences
    val social: SocialFormState = SocialFormState(),
    
    // Step 5: Sleep Schedule
    val sleep: SleepFormState = SleepFormState(),
    
    // Step 6: Personality
    val personality: PersonalityFormState = PersonalityFormState(),
    
    // Validation & Errors
    val validationErrors: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    
    // Existing survey (for editing)
    val existingSurveyId: String? = null
) {
    val canGoBack: Boolean get() = currentStep > 0
    val canGoNext: Boolean get() = currentStep < totalSteps - 1
    val canSubmit: Boolean get() = currentStep == totalSteps - 1 && isCurrentStepValid
    
    val isCurrentStepValid: Boolean get() = when (currentStep) {
        0 -> lifestyle.isValid
        1 -> study.isValid
        2 -> cleanliness.isValid
        3 -> social.isValid
        4 -> sleep.isValid
        5 -> personality.isValid
        else -> false
    }
    
    val progress: Float get() = (currentStep + 1).toFloat() / totalSteps
    
    val stepTitle: String get() = when (currentStep) {
        0 -> "Lifestyle Preferences"
        1 -> "Study Habits"
        2 -> "Cleanliness"
        3 -> "Social Preferences"
        4 -> "Sleep Schedule"
        5 -> "Personality"
        else -> ""
    }
}

/**
 * Form state for lifestyle preferences
 */
data class LifestyleFormState(
    val sleepTime: String = "",
    val wakeTime: String = "",
    val foodPreference: FoodPreference? = null,
    val smokingHabit: SmokingHabit? = null,
    val drinkingHabit: DrinkingHabit? = null,
    val acTemperature: Int? = null,
    val musicPreference: String = ""
) {
    val isValid: Boolean get() = sleepTime.isNotBlank() && 
        wakeTime.isNotBlank() &&
        foodPreference != null &&
        smokingHabit != null &&
        drinkingHabit != null
}

/**
 * Form state for study habits
 */
data class StudyFormState(
    val studyStyle: StudyStyle? = null,
    val preferredStudyTime: String = "",
    val needsQuietEnvironment: Boolean = true,
    val groupStudyPreference: Boolean = false,
    val musicWhileStudying: Boolean = false
) {
    val isValid: Boolean get() = studyStyle != null && preferredStudyTime.isNotBlank()
}

/**
 * Form state for cleanliness preferences
 */
data class CleanlinessFormState(
    val cleaningFrequency: CleaningFrequency? = null,
    val organizationLevel: Int = 3,
    val sharedItemsComfort: Int = 3,
    val bathroomHabits: String = ""
) {
    val isValid: Boolean get() = cleaningFrequency != null
}

/**
 * Form state for social preferences
 */
data class SocialFormState(
    val visitorFrequency: VisitorFrequency? = null,
    val partyAttitude: PartyAttitude? = null,
    val conversationStyle: ConversationStyle? = null,
    val privacyNeeds: Int = 3
) {
    val isValid: Boolean get() = visitorFrequency != null && 
        partyAttitude != null && 
        conversationStyle != null
}

/**
 * Form state for sleep schedule
 */
data class SleepFormState(
    val typicalBedTime: String = "",
    val typicalWakeTime: String = "",
    val sleepSensitivity: SleepSensitivity? = null,
    val napHabits: Boolean = false,
    val weekendScheduleDiffers: Boolean = false
) {
    val isValid: Boolean get() = typicalBedTime.isNotBlank() && 
        typicalWakeTime.isNotBlank() &&
        sleepSensitivity != null
}

/**
 * Form state for personality traits
 */
data class PersonalityFormState(
    val introvertExtrovertScale: Int = 3,
    val conflictResolution: ConflictResolution? = null,
    val communicationStyle: CommunicationStyle? = null,
    val adaptability: Int = 3
) {
    val isValid: Boolean get() = conflictResolution != null && communicationStyle != null
}

/**
 * UI State for the Matches screen
 */
data class MatchesUiState(
    val isLoading: Boolean = false,
    val matches: List<MatchCardData> = emptyList(),
    val selectedMatch: MatchDetailData? = null,
    val errorMessage: String? = null,
    val filterMinScore: Int = 0,
    val sortOrder: MatchSortOrder = MatchSortOrder.COMPATIBILITY_DESC
)

/**
 * Data for match card display
 */
data class MatchCardData(
    val studentId: String,
    val studentName: String,
    val studentBranch: String,
    val studentYear: Int,
    val photoUrl: String?,
    val compatibilityScore: Int,
    val matchReasons: List<String>,
    val warnings: List<String>
) {
    val scoreColor: ScoreColor get() = when {
        compatibilityScore >= 80 -> ScoreColor.GREEN
        compatibilityScore >= 60 -> ScoreColor.YELLOW
        compatibilityScore >= 40 -> ScoreColor.ORANGE
        else -> ScoreColor.RED
    }
}

enum class ScoreColor { GREEN, YELLOW, ORANGE, RED }
enum class MatchSortOrder { COMPATIBILITY_DESC, COMPATIBILITY_ASC, NAME_ASC, NAME_DESC }

/**
 * Detailed match data
 */
data class MatchDetailData(
    val studentId: String,
    val studentName: String,
    val studentEmail: String,
    val studentBranch: String,
    val studentYear: Int,
    val photoUrl: String?,
    val compatibilityScore: CompatibilityScore,
    val surveyComparison: SurveyComparisonData
)

/**
 * Side-by-side survey comparison
 */
data class SurveyComparisonData(
    val yourAnswers: Map<String, String>,
    val theirAnswers: Map<String, String>,
    val matchingAnswers: List<String>,
    val conflictingAnswers: List<String>
)

/**
 * UI State for Admin Dashboard
 */
data class RoomieAdminUiState(
    val isLoading: Boolean = false,
    val activeTab: RoomieAdminTab = RoomieAdminTab.SURVEYS,
    
    // Survey management
    val surveys: List<SurveyListItem> = emptyList(),
    val selectedSemester: String = "",
    val availableSemesters: List<String> = emptyList(),
    
    // Room management
    val rooms: List<Room> = emptyList(),
    val selectedRoom: Room? = null,
    val isRoomDialogVisible: Boolean = false,
    
    // Assignment management
    val assignments: List<AssignmentListItem> = emptyList(),
    val isAutoAssigning: Boolean = false,
    
    // Statistics
    val stats: RoomieStats = RoomieStats(),
    
    // Messages
    val errorMessage: String? = null,
    val successMessage: String? = null
)

enum class RoomieAdminTab { SURVEYS, ROOMS, ASSIGNMENTS, STATISTICS }

data class SurveyListItem(
    val id: String,
    val studentId: String,
    val studentName: String,
    val studentEmail: String,
    val semester: String,
    val submittedAt: Long,
    val isComplete: Boolean
)

data class AssignmentListItem(
    val id: String,
    val roomNumber: String,
    val blockName: String,
    val studentNames: List<String>,
    val compatibilityScore: Int,
    val status: AssignmentStatus,
    val createdAt: Long
)

data class RoomieStats(
    val totalSurveys: Int = 0,
    val completedSurveys: Int = 0,
    val totalRooms: Int = 0,
    val availableRooms: Int = 0,
    val totalAssignments: Int = 0,
    val pendingAssignments: Int = 0,
    val approvedAssignments: Int = 0,
    val averageCompatibility: Float = 0f
)

/**
 * Survey intents
 */
sealed class SurveyIntent {
    // Navigation
    object NextStep : SurveyIntent()
    object PreviousStep : SurveyIntent()
    data class GoToStep(val step: Int) : SurveyIntent()
    
    // Lifestyle updates
    data class UpdateSleepTime(val time: String) : SurveyIntent()
    data class UpdateWakeTime(val time: String) : SurveyIntent()
    data class UpdateFoodPreference(val preference: FoodPreference) : SurveyIntent()
    data class UpdateSmokingHabit(val habit: SmokingHabit) : SurveyIntent()
    data class UpdateDrinkingHabit(val habit: DrinkingHabit) : SurveyIntent()
    data class UpdateAcTemperature(val temp: Int) : SurveyIntent()
    data class UpdateMusicPreference(val preference: String) : SurveyIntent()
    
    // Study updates
    data class UpdateStudyStyle(val style: StudyStyle) : SurveyIntent()
    data class UpdatePreferredStudyTime(val time: String) : SurveyIntent()
    data class UpdateNeedsQuiet(val needs: Boolean) : SurveyIntent()
    data class UpdateGroupStudy(val preference: Boolean) : SurveyIntent()
    data class UpdateMusicWhileStudying(val preference: Boolean) : SurveyIntent()
    
    // Cleanliness updates
    data class UpdateCleaningFrequency(val frequency: CleaningFrequency) : SurveyIntent()
    data class UpdateOrganizationLevel(val level: Int) : SurveyIntent()
    data class UpdateSharedItemsComfort(val comfort: Int) : SurveyIntent()
    data class UpdateBathroomHabits(val habits: String) : SurveyIntent()
    
    // Social updates
    data class UpdateVisitorFrequency(val frequency: VisitorFrequency) : SurveyIntent()
    data class UpdatePartyAttitude(val attitude: PartyAttitude) : SurveyIntent()
    data class UpdateConversationStyle(val style: ConversationStyle) : SurveyIntent()
    data class UpdatePrivacyNeeds(val needs: Int) : SurveyIntent()
    
    // Sleep updates
    data class UpdateBedTime(val time: String) : SurveyIntent()
    data class UpdateWakeUpTime(val time: String) : SurveyIntent()
    data class UpdateSleepSensitivity(val sensitivity: SleepSensitivity) : SurveyIntent()
    data class UpdateNapHabits(val naps: Boolean) : SurveyIntent()
    data class UpdateWeekendSchedule(val differs: Boolean) : SurveyIntent()
    
    // Personality updates
    data class UpdateIntrovertExtrovertScale(val scale: Int) : SurveyIntent()
    data class UpdateConflictResolution(val style: ConflictResolution) : SurveyIntent()
    data class UpdateCommunicationStyle(val style: CommunicationStyle) : SurveyIntent()
    data class UpdateAdaptability(val level: Int) : SurveyIntent()
    
    // Actions
    object Submit : SurveyIntent()
    object LoadExistingSurvey : SurveyIntent()
    object ClearError : SurveyIntent()
}

/**
 * Match screen intents
 */
sealed class MatchesIntent {
    object LoadMatches : MatchesIntent()
    object RefreshMatches : MatchesIntent()
    data class SelectMatch(val studentId: String) : MatchesIntent()
    object ClearSelection : MatchesIntent()
    data class UpdateMinScoreFilter(val score: Int) : MatchesIntent()
    data class UpdateSortOrder(val order: MatchSortOrder) : MatchesIntent()
    object ClearError : MatchesIntent()
}

/**
 * Admin screen intents
 */
sealed class RoomieAdminIntent {
    // Tab navigation
    data class SwitchTab(val tab: RoomieAdminTab) : RoomieAdminIntent()
    
    // Survey management
    object LoadSurveys : RoomieAdminIntent()
    data class SelectSemester(val semester: String) : RoomieAdminIntent()
    data class DeleteSurvey(val surveyId: String) : RoomieAdminIntent()
    object ExportSurveys : RoomieAdminIntent()
    
    // Room management
    object LoadRooms : RoomieAdminIntent()
    data class SelectRoom(val room: Room) : RoomieAdminIntent()
    object ShowAddRoomDialog : RoomieAdminIntent()
    object HideRoomDialog : RoomieAdminIntent()
    data class SaveRoom(val room: Room) : RoomieAdminIntent()
    data class DeleteRoom(val roomId: String) : RoomieAdminIntent()
    
    // Assignment management
    object LoadAssignments : RoomieAdminIntent()
    object TriggerAutoAssignment : RoomieAdminIntent()
    data class ApproveAssignment(val assignmentId: String) : RoomieAdminIntent()
    data class RejectAssignment(val assignmentId: String) : RoomieAdminIntent()
    data class CreateManualAssignment(
        val roomId: String, 
        val studentIds: List<String>
    ) : RoomieAdminIntent()
    
    // Statistics
    object RefreshStats : RoomieAdminIntent()
    object GenerateAllCompatibilities : RoomieAdminIntent()
    
    // Messages
    object ClearError : RoomieAdminIntent()
    object ClearSuccess : RoomieAdminIntent()
}
