package com.hosteldada.feature.roomie.domain

import com.hosteldada.core.common.Result
import com.hosteldada.core.domain.model.*
import com.hosteldada.core.domain.repository.*
import com.hosteldada.core.domain.algorithm.CompatibilityGraph

/**
 * Use case for submitting roommate survey
 */
class SubmitSurveyUseCase(
    private val surveyRepository: SurveyRepository,
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(survey: RoommateSurvey): Result<String> {
        // Validate survey completion
        if (!survey.isComplete) {
            return Result.Error("Survey is incomplete")
        }
        
        // Verify student exists
        val profile = profileRepository.getProfile(survey.studentId)
        if (profile == null) {
            return Result.Error("Student profile not found")
        }
        
        // Check for existing survey this semester
        val existing = surveyRepository.getSurveyByStudentAndSemester(
            survey.studentId, 
            survey.semester
        )
        
        return if (existing != null) {
            // Update existing survey
            surveyRepository.updateSurvey(survey.copy(id = existing.id))
            Result.Success(existing.id)
        } else {
            // Create new survey
            surveyRepository.createSurvey(survey)
        }
    }
}

/**
 * Use case for getting survey by student and semester
 */
class GetSurveyUseCase(
    private val surveyRepository: SurveyRepository
) {
    suspend operator fun invoke(studentId: String, semester: String): Result<RoommateSurvey?> {
        return try {
            val survey = surveyRepository.getSurveyByStudentAndSemester(studentId, semester)
            Result.Success(survey)
        } catch (e: Exception) {
            Result.Error("Failed to fetch survey: ${e.message}", e)
        }
    }
}

/**
 * Use case for calculating compatibility between two students
 * Uses weighted graph algorithm
 */
class CalculateCompatibilityUseCase(
    private val compatibilityRepository: CompatibilityRepository,
    private val surveyRepository: SurveyRepository
) {
    private val graph = CompatibilityGraph()
    
    suspend operator fun invoke(
        studentId1: String, 
        studentId2: String,
        semester: String
    ): Result<CompatibilityScore> {
        // Check if already calculated
        val existing = compatibilityRepository.getCompatibility(studentId1, studentId2)
        if (existing != null) {
            return Result.Success(existing)
        }
        
        // Get surveys
        val survey1 = surveyRepository.getSurveyByStudentAndSemester(studentId1, semester)
            ?: return Result.Error("Survey not found for student $studentId1")
        val survey2 = surveyRepository.getSurveyByStudentAndSemester(studentId2, semester)
            ?: return Result.Error("Survey not found for student $studentId2")
        
        // Calculate using graph algorithm
        val score = graph.calculateCompatibility(survey1, survey2)
        
        // Save and return
        compatibilityRepository.saveCompatibility(score)
        return Result.Success(score)
    }
}

/**
 * Use case for getting top matches for a student
 * Time complexity: O(n log n) where n is number of surveys
 */
class GetTopMatchesUseCase(
    private val compatibilityRepository: CompatibilityRepository,
    private val surveyRepository: SurveyRepository
) {
    private val graph = CompatibilityGraph()
    
    suspend operator fun invoke(
        studentId: String,
        semester: String,
        limit: Int = 10
    ): Result<List<CompatibilityScore>> {
        return try {
            // Get all surveys for semester
            val allSurveys = surveyRepository.getSurveysBySemester(semester)
            val studentSurvey = allSurveys.find { it.studentId == studentId }
                ?: return Result.Error("Survey not found for student")
            
            // Build graph with all students
            allSurveys.forEach { survey ->
                graph.addStudent(survey.studentId)
            }
            
            // Calculate all compatibilities for this student
            val scores = mutableListOf<CompatibilityScore>()
            allSurveys.filter { it.studentId != studentId }.forEach { otherSurvey ->
                val score = graph.calculateCompatibility(studentSurvey, otherSurvey)
                scores.add(score)
            }
            
            // Sort by overall score and take top N
            val topMatches = scores
                .sortedByDescending { it.overallScore }
                .take(limit)
            
            Result.Success(topMatches)
        } catch (e: Exception) {
            Result.Error("Failed to calculate matches: ${e.message}", e)
        }
    }
}

/**
 * Use case for generating all compatibilities for a semester
 * Used by admin for batch processing
 * Time complexity: O(n²) where n is number of surveys
 */
class GenerateAllCompatibilitiesUseCase(
    private val compatibilityRepository: CompatibilityRepository,
    private val surveyRepository: SurveyRepository
) {
    private val graph = CompatibilityGraph()
    
    suspend operator fun invoke(semester: String): Result<Int> {
        return try {
            val surveys = surveyRepository.getSurveysBySemester(semester)
            
            if (surveys.size < 2) {
                return Result.Error("Need at least 2 surveys to calculate compatibility")
            }
            
            var count = 0
            
            // Generate all pairs O(n²)
            for (i in surveys.indices) {
                for (j in i + 1 until surveys.size) {
                    val score = graph.calculateCompatibility(surveys[i], surveys[j])
                    compatibilityRepository.saveCompatibility(score)
                    count++
                }
            }
            
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error("Failed to generate compatibilities: ${e.message}", e)
        }
    }
}

/**
 * Use case for getting available rooms
 */
class GetAvailableRoomsUseCase(
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(): Result<List<Room>> {
        return try {
            val rooms = roomRepository.getAllRooms()
                .filter { it.isAvailable && it.currentOccupancy < it.capacity }
            Result.Success(rooms)
        } catch (e: Exception) {
            Result.Error("Failed to fetch rooms: ${e.message}", e)
        }
    }
}

/**
 * Use case for creating room assignment
 */
class CreateAssignmentUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val roomRepository: RoomRepository,
    private val compatibilityRepository: CompatibilityRepository
) {
    suspend operator fun invoke(
        roomId: String,
        studentIds: List<String>,
        semester: String
    ): Result<RoomAssignment> {
        // Validate room
        val room = roomRepository.getRoomById(roomId)
            ?: return Result.Error("Room not found")
        
        if (!room.isAvailable) {
            return Result.Error("Room is not available")
        }
        
        if (room.currentOccupancy + studentIds.size > room.capacity) {
            return Result.Error("Room does not have enough capacity")
        }
        
        // Get compatibility score if 2 students
        val compatibilityScore = if (studentIds.size == 2) {
            compatibilityRepository.getCompatibility(studentIds[0], studentIds[1])
                ?.overallScore ?: 0
        } else 0
        
        // Create assignment
        val assignment = RoomAssignment(
            id = "", // Will be generated by repository
            roomId = roomId,
            studentIds = studentIds,
            compatibilityScore = compatibilityScore,
            semester = semester,
            status = AssignmentStatus.PENDING_APPROVAL,
            createdAt = System.currentTimeMillis()
        )
        
        return assignmentRepository.createAssignment(assignment)
    }
}

/**
 * Use case for auto-assigning students using greedy algorithm
 * Time complexity: O(n² log n) where n is number of students
 */
class AutoAssignStudentsUseCase(
    private val surveyRepository: SurveyRepository,
    private val roomRepository: RoomRepository,
    private val assignmentRepository: AssignmentRepository,
    private val compatibilityRepository: CompatibilityRepository
) {
    private val graph = CompatibilityGraph()
    
    suspend operator fun invoke(semester: String): Result<List<RoomAssignment>> {
        return try {
            // Step 1: Get all surveys for semester
            val surveys = surveyRepository.getSurveysBySemester(semester)
            if (surveys.size < 2) {
                return Result.Error("Need at least 2 surveys for assignment")
            }
            
            // Step 2: Get available rooms
            val rooms = roomRepository.getAllRooms()
                .filter { it.isAvailable && it.capacity >= 2 }
                .toMutableList()
            
            if (rooms.isEmpty()) {
                return Result.Error("No available rooms")
            }
            
            // Step 3: Generate all compatibility scores O(n²)
            val pairs = mutableListOf<Triple<String, String, CompatibilityScore>>()
            for (i in surveys.indices) {
                for (j in i + 1 until surveys.size) {
                    val score = graph.calculateCompatibility(surveys[i], surveys[j])
                    pairs.add(Triple(surveys[i].studentId, surveys[j].studentId, score))
                }
            }
            
            // Step 4: Sort by compatibility descending O(n² log n)
            pairs.sortByDescending { it.third.overallScore }
            
            // Step 5: Greedy assignment
            val assigned = mutableSetOf<String>()
            val assignments = mutableListOf<RoomAssignment>()
            var roomIndex = 0
            
            for ((student1, student2, score) in pairs) {
                // Skip if either student already assigned
                if (student1 in assigned || student2 in assigned) continue
                
                // Check if rooms available
                if (roomIndex >= rooms.size) break
                
                val room = rooms[roomIndex]
                
                // Create assignment
                val assignment = RoomAssignment(
                    id = generateAssignmentId(),
                    roomId = room.id,
                    studentIds = listOf(student1, student2),
                    compatibilityScore = score.overallScore,
                    semester = semester,
                    status = AssignmentStatus.PENDING_APPROVAL,
                    createdAt = System.currentTimeMillis()
                )
                
                // Save assignment
                val result = assignmentRepository.createAssignment(assignment)
                if (result is Result.Success) {
                    assignments.add(result.data)
                    assigned.add(student1)
                    assigned.add(student2)
                    roomIndex++
                }
            }
            
            Result.Success(assignments)
        } catch (e: Exception) {
            Result.Error("Auto-assignment failed: ${e.message}", e)
        }
    }
    
    private fun generateAssignmentId(): String {
        return "ASSIGN_${System.currentTimeMillis()}"
    }
}

/**
 * Use case for approving/rejecting assignments
 */
class UpdateAssignmentStatusUseCase(
    private val assignmentRepository: AssignmentRepository,
    private val roomRepository: RoomRepository
) {
    suspend operator fun invoke(
        assignmentId: String,
        newStatus: AssignmentStatus
    ): Result<RoomAssignment> {
        val assignment = assignmentRepository.getAssignmentById(assignmentId)
            ?: return Result.Error("Assignment not found")
        
        // Validate state transition
        val validTransitions = mapOf(
            AssignmentStatus.PENDING_APPROVAL to setOf(
                AssignmentStatus.APPROVED, 
                AssignmentStatus.REJECTED
            ),
            AssignmentStatus.APPROVED to setOf(AssignmentStatus.CANCELLED),
            AssignmentStatus.REJECTED to emptySet<AssignmentStatus>(),
            AssignmentStatus.CANCELLED to emptySet()
        )
        
        if (newStatus !in (validTransitions[assignment.status] ?: emptySet())) {
            return Result.Error("Invalid status transition from ${assignment.status} to $newStatus")
        }
        
        // Update room occupancy if approved
        if (newStatus == AssignmentStatus.APPROVED) {
            roomRepository.updateRoomOccupancy(
                assignment.roomId,
                assignment.studentIds.size
            )
        }
        
        return assignmentRepository.updateAssignmentStatus(assignmentId, newStatus)
    }
}

/**
 * Use case for admin to view all surveys
 */
class GetAllSurveysUseCase(
    private val surveyRepository: SurveyRepository
) {
    suspend operator fun invoke(semester: String? = null): Result<List<RoommateSurvey>> {
        return try {
            val surveys = if (semester != null) {
                surveyRepository.getSurveysBySemester(semester)
            } else {
                surveyRepository.getAllSurveys()
            }
            Result.Success(surveys)
        } catch (e: Exception) {
            Result.Error("Failed to fetch surveys: ${e.message}", e)
        }
    }
}

/**
 * Use case for admin to manage rooms
 */
class ManageRoomUseCase(
    private val roomRepository: RoomRepository
) {
    suspend fun addRoom(room: Room): Result<Room> {
        return try {
            roomRepository.createRoom(room)
        } catch (e: Exception) {
            Result.Error("Failed to add room: ${e.message}", e)
        }
    }
    
    suspend fun updateRoom(room: Room): Result<Room> {
        return try {
            roomRepository.updateRoom(room)
        } catch (e: Exception) {
            Result.Error("Failed to update room: ${e.message}", e)
        }
    }
    
    suspend fun deleteRoom(roomId: String): Result<Unit> {
        return try {
            roomRepository.deleteRoom(roomId)
        } catch (e: Exception) {
            Result.Error("Failed to delete room: ${e.message}", e)
        }
    }
}
