package com.hosteldada.feature.roomie.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.hosteldada.core.common.DispatcherProvider
import com.hosteldada.core.common.Result
import com.hosteldada.core.domain.model.*
import com.hosteldada.feature.roomie.domain.*

/**
 * ViewModel for the Survey screen
 * Handles multi-step form with validation
 */
class SurveyViewModel(
    private val submitSurveyUseCase: SubmitSurveyUseCase,
    private val getSurveyUseCase: GetSurveyUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val coroutineScope: CoroutineScope
) {
    private val _state = MutableStateFlow(SurveyUiState())
    val state: StateFlow<SurveyUiState> = _state.asStateFlow()
    
    fun initialize(studentId: String, semester: String) {
        _state.update { it.copy(studentId = studentId, semester = semester) }
        loadExistingSurvey()
    }
    
    fun handleIntent(intent: SurveyIntent) {
        when (intent) {
            // Navigation
            is SurveyIntent.NextStep -> nextStep()
            is SurveyIntent.PreviousStep -> previousStep()
            is SurveyIntent.GoToStep -> goToStep(intent.step)
            
            // Lifestyle
            is SurveyIntent.UpdateSleepTime -> updateLifestyle { it.copy(sleepTime = intent.time) }
            is SurveyIntent.UpdateWakeTime -> updateLifestyle { it.copy(wakeTime = intent.time) }
            is SurveyIntent.UpdateFoodPreference -> updateLifestyle { it.copy(foodPreference = intent.preference) }
            is SurveyIntent.UpdateSmokingHabit -> updateLifestyle { it.copy(smokingHabit = intent.habit) }
            is SurveyIntent.UpdateDrinkingHabit -> updateLifestyle { it.copy(drinkingHabit = intent.habit) }
            is SurveyIntent.UpdateAcTemperature -> updateLifestyle { it.copy(acTemperature = intent.temp) }
            is SurveyIntent.UpdateMusicPreference -> updateLifestyle { it.copy(musicPreference = intent.preference) }
            
            // Study
            is SurveyIntent.UpdateStudyStyle -> updateStudy { it.copy(studyStyle = intent.style) }
            is SurveyIntent.UpdatePreferredStudyTime -> updateStudy { it.copy(preferredStudyTime = intent.time) }
            is SurveyIntent.UpdateNeedsQuiet -> updateStudy { it.copy(needsQuietEnvironment = intent.needs) }
            is SurveyIntent.UpdateGroupStudy -> updateStudy { it.copy(groupStudyPreference = intent.preference) }
            is SurveyIntent.UpdateMusicWhileStudying -> updateStudy { it.copy(musicWhileStudying = intent.preference) }
            
            // Cleanliness
            is SurveyIntent.UpdateCleaningFrequency -> updateCleanliness { it.copy(cleaningFrequency = intent.frequency) }
            is SurveyIntent.UpdateOrganizationLevel -> updateCleanliness { it.copy(organizationLevel = intent.level) }
            is SurveyIntent.UpdateSharedItemsComfort -> updateCleanliness { it.copy(sharedItemsComfort = intent.comfort) }
            is SurveyIntent.UpdateBathroomHabits -> updateCleanliness { it.copy(bathroomHabits = intent.habits) }
            
            // Social
            is SurveyIntent.UpdateVisitorFrequency -> updateSocial { it.copy(visitorFrequency = intent.frequency) }
            is SurveyIntent.UpdatePartyAttitude -> updateSocial { it.copy(partyAttitude = intent.attitude) }
            is SurveyIntent.UpdateConversationStyle -> updateSocial { it.copy(conversationStyle = intent.style) }
            is SurveyIntent.UpdatePrivacyNeeds -> updateSocial { it.copy(privacyNeeds = intent.needs) }
            
            // Sleep
            is SurveyIntent.UpdateBedTime -> updateSleep { it.copy(typicalBedTime = intent.time) }
            is SurveyIntent.UpdateWakeUpTime -> updateSleep { it.copy(typicalWakeTime = intent.time) }
            is SurveyIntent.UpdateSleepSensitivity -> updateSleep { it.copy(sleepSensitivity = intent.sensitivity) }
            is SurveyIntent.UpdateNapHabits -> updateSleep { it.copy(napHabits = intent.naps) }
            is SurveyIntent.UpdateWeekendSchedule -> updateSleep { it.copy(weekendScheduleDiffers = intent.differs) }
            
            // Personality
            is SurveyIntent.UpdateIntrovertExtrovertScale -> updatePersonality { it.copy(introvertExtrovertScale = intent.scale) }
            is SurveyIntent.UpdateConflictResolution -> updatePersonality { it.copy(conflictResolution = intent.style) }
            is SurveyIntent.UpdateCommunicationStyle -> updatePersonality { it.copy(communicationStyle = intent.style) }
            is SurveyIntent.UpdateAdaptability -> updatePersonality { it.copy(adaptability = intent.level) }
            
            // Actions
            is SurveyIntent.Submit -> submitSurvey()
            is SurveyIntent.LoadExistingSurvey -> loadExistingSurvey()
            is SurveyIntent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }
    
    private fun nextStep() {
        val current = _state.value
        if (current.isCurrentStepValid && current.canGoNext) {
            _state.update { it.copy(currentStep = it.currentStep + 1) }
        }
    }
    
    private fun previousStep() {
        val current = _state.value
        if (current.canGoBack) {
            _state.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }
    
    private fun goToStep(step: Int) {
        if (step in 0 until _state.value.totalSteps) {
            _state.update { it.copy(currentStep = step) }
        }
    }
    
    private fun updateLifestyle(transform: (LifestyleFormState) -> LifestyleFormState) {
        _state.update { it.copy(lifestyle = transform(it.lifestyle)) }
    }
    
    private fun updateStudy(transform: (StudyFormState) -> StudyFormState) {
        _state.update { it.copy(study = transform(it.study)) }
    }
    
    private fun updateCleanliness(transform: (CleanlinessFormState) -> CleanlinessFormState) {
        _state.update { it.copy(cleanliness = transform(it.cleanliness)) }
    }
    
    private fun updateSocial(transform: (SocialFormState) -> SocialFormState) {
        _state.update { it.copy(social = transform(it.social)) }
    }
    
    private fun updateSleep(transform: (SleepFormState) -> SleepFormState) {
        _state.update { it.copy(sleep = transform(it.sleep)) }
    }
    
    private fun updatePersonality(transform: (PersonalityFormState) -> PersonalityFormState) {
        _state.update { it.copy(personality = transform(it.personality)) }
    }
    
    private fun loadExistingSurvey() {
        coroutineScope.launch(dispatcherProvider.io) {
            val current = _state.value
            _state.update { it.copy(isLoading = true) }
            
            when (val result = getSurveyUseCase(current.studentId, current.semester)) {
                is Result.Success -> {
                    result.data?.let { survey ->
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                existingSurveyId = survey.id,
                                lifestyle = survey.lifestyle.toFormState(),
                                study = survey.studyHabits.toFormState(),
                                cleanliness = survey.cleanliness.toFormState(),
                                social = survey.socialPreferences.toFormState(),
                                sleep = survey.sleepSchedule.toFormState(),
                                personality = survey.personalityTraits.toFormState()
                            )
                        }
                    } ?: _state.update { it.copy(isLoading = false) }
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
    
    private fun submitSurvey() {
        coroutineScope.launch(dispatcherProvider.io) {
            val current = _state.value
            _state.update { it.copy(isSaving = true) }
            
            val survey = buildSurveyFromState(current)
            
            when (val result = submitSurveyUseCase(survey)) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            isSaving = false,
                            existingSurveyId = result.data,
                            successMessage = "Survey submitted successfully!"
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isSaving = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    
    private fun buildSurveyFromState(state: SurveyUiState): RoommateSurvey {
        return RoommateSurvey(
            id = state.existingSurveyId ?: "",
            studentId = state.studentId,
            semester = state.semester,
            lifestyle = state.lifestyle.toModel(),
            studyHabits = state.study.toModel(),
            cleanliness = state.cleanliness.toModel(),
            socialPreferences = state.social.toModel(),
            sleepSchedule = state.sleep.toModel(),
            personalityTraits = state.personality.toModel(),
            isComplete = true,
            submittedAt = System.currentTimeMillis()
        )
    }
}

/**
 * ViewModel for the Matches screen
 */
class MatchesViewModel(
    private val getTopMatchesUseCase: GetTopMatchesUseCase,
    private val calculateCompatibilityUseCase: CalculateCompatibilityUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val coroutineScope: CoroutineScope
) {
    private val _state = MutableStateFlow(MatchesUiState())
    val state: StateFlow<MatchesUiState> = _state.asStateFlow()
    
    private var currentStudentId: String = ""
    private var currentSemester: String = ""
    
    fun initialize(studentId: String, semester: String) {
        currentStudentId = studentId
        currentSemester = semester
        loadMatches()
    }
    
    fun handleIntent(intent: MatchesIntent) {
        when (intent) {
            is MatchesIntent.LoadMatches -> loadMatches()
            is MatchesIntent.RefreshMatches -> loadMatches()
            is MatchesIntent.SelectMatch -> selectMatch(intent.studentId)
            is MatchesIntent.ClearSelection -> _state.update { it.copy(selectedMatch = null) }
            is MatchesIntent.UpdateMinScoreFilter -> applyFilter(intent.score)
            is MatchesIntent.UpdateSortOrder -> applySorting(intent.order)
            is MatchesIntent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }
    
    private fun loadMatches() {
        coroutineScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isLoading = true) }
            
            when (val result = getTopMatchesUseCase(currentStudentId, currentSemester, 20)) {
                is Result.Success -> {
                    val matchCards = result.data.map { score ->
                        MatchCardData(
                            studentId = score.studentId2,
                            studentName = "", // TODO: Fetch from profile
                            studentBranch = "",
                            studentYear = 0,
                            photoUrl = null,
                            compatibilityScore = score.overallScore,
                            matchReasons = score.matchReasons,
                            warnings = score.warnings
                        )
                    }
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            matches = matchCards
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    
    private fun selectMatch(studentId: String) {
        coroutineScope.launch(dispatcherProvider.io) {
            when (val result = calculateCompatibilityUseCase(currentStudentId, studentId, currentSemester)) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            selectedMatch = MatchDetailData(
                                studentId = studentId,
                                studentName = "", // TODO: Fetch
                                studentEmail = "",
                                studentBranch = "",
                                studentYear = 0,
                                photoUrl = null,
                                compatibilityScore = result.data,
                                surveyComparison = SurveyComparisonData(
                                    emptyMap(), emptyMap(), emptyList(), emptyList()
                                )
                            )
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }
    
    private fun applyFilter(minScore: Int) {
        _state.update { current ->
            val filtered = current.matches.filter { it.compatibilityScore >= minScore }
            current.copy(
                filterMinScore = minScore,
                matches = filtered
            )
        }
    }
    
    private fun applySorting(order: MatchSortOrder) {
        _state.update { current ->
            val sorted = when (order) {
                MatchSortOrder.COMPATIBILITY_DESC -> current.matches.sortedByDescending { it.compatibilityScore }
                MatchSortOrder.COMPATIBILITY_ASC -> current.matches.sortedBy { it.compatibilityScore }
                MatchSortOrder.NAME_ASC -> current.matches.sortedBy { it.studentName }
                MatchSortOrder.NAME_DESC -> current.matches.sortedByDescending { it.studentName }
            }
            current.copy(
                sortOrder = order,
                matches = sorted
            )
        }
    }
}

/**
 * ViewModel for Admin Dashboard
 */
class RoomieAdminViewModel(
    private val getAllSurveysUseCase: GetAllSurveysUseCase,
    private val getAvailableRoomsUseCase: GetAvailableRoomsUseCase,
    private val manageRoomUseCase: ManageRoomUseCase,
    private val autoAssignStudentsUseCase: AutoAssignStudentsUseCase,
    private val updateAssignmentStatusUseCase: UpdateAssignmentStatusUseCase,
    private val generateAllCompatibilitiesUseCase: GenerateAllCompatibilitiesUseCase,
    private val dispatcherProvider: DispatcherProvider,
    private val coroutineScope: CoroutineScope
) {
    private val _state = MutableStateFlow(RoomieAdminUiState())
    val state: StateFlow<RoomieAdminUiState> = _state.asStateFlow()
    
    fun handleIntent(intent: RoomieAdminIntent) {
        when (intent) {
            // Tab navigation
            is RoomieAdminIntent.SwitchTab -> switchTab(intent.tab)
            
            // Survey management
            is RoomieAdminIntent.LoadSurveys -> loadSurveys()
            is RoomieAdminIntent.SelectSemester -> selectSemester(intent.semester)
            is RoomieAdminIntent.DeleteSurvey -> deleteSurvey(intent.surveyId)
            is RoomieAdminIntent.ExportSurveys -> exportSurveys()
            
            // Room management
            is RoomieAdminIntent.LoadRooms -> loadRooms()
            is RoomieAdminIntent.SelectRoom -> _state.update { it.copy(selectedRoom = intent.room) }
            is RoomieAdminIntent.ShowAddRoomDialog -> _state.update { it.copy(isRoomDialogVisible = true) }
            is RoomieAdminIntent.HideRoomDialog -> _state.update { it.copy(isRoomDialogVisible = false, selectedRoom = null) }
            is RoomieAdminIntent.SaveRoom -> saveRoom(intent.room)
            is RoomieAdminIntent.DeleteRoom -> deleteRoom(intent.roomId)
            
            // Assignment management
            is RoomieAdminIntent.LoadAssignments -> loadAssignments()
            is RoomieAdminIntent.TriggerAutoAssignment -> triggerAutoAssignment()
            is RoomieAdminIntent.ApproveAssignment -> approveAssignment(intent.assignmentId)
            is RoomieAdminIntent.RejectAssignment -> rejectAssignment(intent.assignmentId)
            is RoomieAdminIntent.CreateManualAssignment -> createManualAssignment(intent.roomId, intent.studentIds)
            
            // Statistics
            is RoomieAdminIntent.RefreshStats -> refreshStats()
            is RoomieAdminIntent.GenerateAllCompatibilities -> generateAllCompatibilities()
            
            // Messages
            is RoomieAdminIntent.ClearError -> _state.update { it.copy(errorMessage = null) }
            is RoomieAdminIntent.ClearSuccess -> _state.update { it.copy(successMessage = null) }
        }
    }
    
    private fun switchTab(tab: RoomieAdminTab) {
        _state.update { it.copy(activeTab = tab) }
        when (tab) {
            RoomieAdminTab.SURVEYS -> loadSurveys()
            RoomieAdminTab.ROOMS -> loadRooms()
            RoomieAdminTab.ASSIGNMENTS -> loadAssignments()
            RoomieAdminTab.STATISTICS -> refreshStats()
        }
    }
    
    private fun loadSurveys() {
        coroutineScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isLoading = true) }
            
            val semester = _state.value.selectedSemester.takeIf { it.isNotBlank() }
            when (val result = getAllSurveysUseCase(semester)) {
                is Result.Success -> {
                    val items = result.data.map { survey ->
                        SurveyListItem(
                            id = survey.id,
                            studentId = survey.studentId,
                            studentName = "", // TODO: Fetch from profile
                            studentEmail = "",
                            semester = survey.semester,
                            submittedAt = survey.submittedAt,
                            isComplete = survey.isComplete
                        )
                    }
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            surveys = items
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    
    private fun selectSemester(semester: String) {
        _state.update { it.copy(selectedSemester = semester) }
        loadSurveys()
    }
    
    private fun deleteSurvey(surveyId: String) {
        // TODO: Implement delete
        _state.update { it.copy(successMessage = "Survey deleted") }
    }
    
    private fun exportSurveys() {
        // TODO: Implement export
        _state.update { it.copy(successMessage = "Export started") }
    }
    
    private fun loadRooms() {
        coroutineScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isLoading = true) }
            
            when (val result = getAvailableRoomsUseCase()) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            rooms = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    
    private fun saveRoom(room: Room) {
        coroutineScope.launch(dispatcherProvider.io) {
            val result = if (room.id.isBlank()) {
                manageRoomUseCase.addRoom(room)
            } else {
                manageRoomUseCase.updateRoom(room)
            }
            
            when (result) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            isRoomDialogVisible = false,
                            selectedRoom = null,
                            successMessage = "Room saved successfully"
                        )
                    }
                    loadRooms()
                }
                is Result.Error -> {
                    _state.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }
    
    private fun deleteRoom(roomId: String) {
        coroutineScope.launch(dispatcherProvider.io) {
            when (val result = manageRoomUseCase.deleteRoom(roomId)) {
                is Result.Success -> {
                    _state.update { it.copy(successMessage = "Room deleted") }
                    loadRooms()
                }
                is Result.Error -> {
                    _state.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }
    
    private fun loadAssignments() {
        // TODO: Implement
        _state.update { it.copy(isLoading = false) }
    }
    
    private fun triggerAutoAssignment() {
        coroutineScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isAutoAssigning = true) }
            
            val semester = _state.value.selectedSemester
            when (val result = autoAssignStudentsUseCase(semester)) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            isAutoAssigning = false,
                            successMessage = "Created ${result.data.size} assignments"
                        )
                    }
                    loadAssignments()
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isAutoAssigning = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    
    private fun approveAssignment(assignmentId: String) {
        coroutineScope.launch(dispatcherProvider.io) {
            when (val result = updateAssignmentStatusUseCase(assignmentId, AssignmentStatus.APPROVED)) {
                is Result.Success -> {
                    _state.update { it.copy(successMessage = "Assignment approved") }
                    loadAssignments()
                }
                is Result.Error -> {
                    _state.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }
    
    private fun rejectAssignment(assignmentId: String) {
        coroutineScope.launch(dispatcherProvider.io) {
            when (val result = updateAssignmentStatusUseCase(assignmentId, AssignmentStatus.REJECTED)) {
                is Result.Success -> {
                    _state.update { it.copy(successMessage = "Assignment rejected") }
                    loadAssignments()
                }
                is Result.Error -> {
                    _state.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }
    
    private fun createManualAssignment(roomId: String, studentIds: List<String>) {
        // TODO: Implement
    }
    
    private fun refreshStats() {
        // TODO: Implement statistics calculation
        _state.update { 
            it.copy(
                stats = RoomieStats(
                    totalSurveys = it.surveys.size,
                    completedSurveys = it.surveys.count { s -> s.isComplete },
                    totalRooms = it.rooms.size,
                    availableRooms = it.rooms.count { r -> r.isAvailable },
                    totalAssignments = it.assignments.size,
                    pendingAssignments = it.assignments.count { a -> a.status == AssignmentStatus.PENDING_APPROVAL },
                    approvedAssignments = it.assignments.count { a -> a.status == AssignmentStatus.APPROVED }
                )
            )
        }
    }
    
    private fun generateAllCompatibilities() {
        coroutineScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isLoading = true) }
            
            val semester = _state.value.selectedSemester
            when (val result = generateAllCompatibilitiesUseCase(semester)) {
                is Result.Success -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Generated ${result.data} compatibility scores"
                        )
                    }
                }
                is Result.Error -> {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}

// Extension functions to convert domain models to form states and vice versa
private fun LifestylePreferences.toFormState(): LifestyleFormState {
    return LifestyleFormState(
        sleepTime = sleepTime,
        wakeTime = wakeTime,
        foodPreference = foodPreference,
        smokingHabit = smokingHabit,
        drinkingHabit = drinkingHabit,
        acTemperature = acTemperature,
        musicPreference = musicPreference
    )
}

private fun StudyHabits.toFormState(): StudyFormState {
    return StudyFormState(
        studyStyle = studyStyle,
        preferredStudyTime = preferredStudyTime,
        needsQuietEnvironment = needsQuietEnvironment,
        groupStudyPreference = groupStudyPreference,
        musicWhileStudying = musicWhileStudying
    )
}

private fun CleanlinessPreferences.toFormState(): CleanlinessFormState {
    return CleanlinessFormState(
        cleaningFrequency = cleaningFrequency,
        organizationLevel = organizationLevel,
        sharedItemsComfort = sharedItemsComfort,
        bathroomHabits = bathroomHabits
    )
}

private fun SocialPreferences.toFormState(): SocialFormState {
    return SocialFormState(
        visitorFrequency = visitorFrequency,
        partyAttitude = partyAttitude,
        conversationStyle = conversationStyle,
        privacyNeeds = privacyNeeds
    )
}

private fun SleepSchedule.toFormState(): SleepFormState {
    return SleepFormState(
        typicalBedTime = typicalBedTime,
        typicalWakeTime = typicalWakeTime,
        sleepSensitivity = sleepSensitivity,
        napHabits = napHabits,
        weekendScheduleDiffers = weekendScheduleDiffers
    )
}

private fun PersonalityTraits.toFormState(): PersonalityFormState {
    return PersonalityFormState(
        introvertExtrovertScale = introvertExtrovertScale,
        conflictResolution = conflictResolution,
        communicationStyle = communicationStyle,
        adaptability = adaptability
    )
}

private fun LifestyleFormState.toModel(): LifestylePreferences {
    return LifestylePreferences(
        sleepTime = sleepTime,
        wakeTime = wakeTime,
        foodPreference = foodPreference!!,
        smokingHabit = smokingHabit!!,
        drinkingHabit = drinkingHabit!!,
        acTemperature = acTemperature ?: 24,
        musicPreference = musicPreference
    )
}

private fun StudyFormState.toModel(): StudyHabits {
    return StudyHabits(
        studyStyle = studyStyle!!,
        preferredStudyTime = preferredStudyTime,
        needsQuietEnvironment = needsQuietEnvironment,
        groupStudyPreference = groupStudyPreference,
        musicWhileStudying = musicWhileStudying
    )
}

private fun CleanlinessFormState.toModel(): CleanlinessPreferences {
    return CleanlinessPreferences(
        cleaningFrequency = cleaningFrequency!!,
        organizationLevel = organizationLevel,
        sharedItemsComfort = sharedItemsComfort,
        bathroomHabits = bathroomHabits
    )
}

private fun SocialFormState.toModel(): SocialPreferences {
    return SocialPreferences(
        visitorFrequency = visitorFrequency!!,
        partyAttitude = partyAttitude!!,
        conversationStyle = conversationStyle!!,
        privacyNeeds = privacyNeeds
    )
}

private fun SleepFormState.toModel(): SleepSchedule {
    return SleepSchedule(
        typicalBedTime = typicalBedTime,
        typicalWakeTime = typicalWakeTime,
        sleepSensitivity = sleepSensitivity!!,
        napHabits = napHabits,
        weekendScheduleDiffers = weekendScheduleDiffers
    )
}

private fun PersonalityFormState.toModel(): PersonalityTraits {
    return PersonalityTraits(
        introvertExtrovertScale = introvertExtrovertScale,
        conflictResolution = conflictResolution!!,
        communicationStyle = communicationStyle!!,
        adaptability = adaptability
    )
}
