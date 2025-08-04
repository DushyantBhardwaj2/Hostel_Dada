// RoomieMatcher Service - Advanced roommate matching with Firebase integration

import { 
  ref,
  get,
  set,
  update,
  remove,
  push,
  query,
  orderByChild,
  equalTo,
  onValue,
  off
} from 'firebase/database'
import { realtimeDb } from './firebase'
import { 
  Student,
  RoommateSurvey,
  CompatibilityScore,
  Room,
  RoomAssignment,
  CompatibilityGraph,
  RoomAllocationOptimizer
} from './roomie-models'

export class RoomieMatcherService {
  private compatibilityGraph: CompatibilityGraph
  private roomOptimizer: RoomAllocationOptimizer
  
  // Firebase references
  private studentsRef = ref(realtimeDb, 'students')
  private surveysRef = ref(realtimeDb, 'roommateSurveys')
  private roomsRef = ref(realtimeDb, 'rooms')
  private assignmentsRef = ref(realtimeDb, 'roomAssignments')

  constructor() {
    this.compatibilityGraph = new CompatibilityGraph()
    this.roomOptimizer = new RoomAllocationOptimizer()
  }

  // Student Management
  async addStudent(studentData: Omit<Student, 'id' | 'createdAt' | 'updatedAt'>): Promise<string> {
    try {
      const newStudent = {
        ...studentData,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }

      const newStudentRef = push(this.studentsRef)
      await set(newStudentRef, newStudent)
      return newStudentRef.key!
    } catch (error) {
      console.error('Error adding student:', error)
      throw error
    }
  }

  async getStudentById(id: string): Promise<Student | null> {
    try {
      const studentRef = ref(realtimeDb, `students/${id}`)
      const snapshot = await get(studentRef)
      if (!snapshot.exists()) return null
      
      const data = snapshot.val()
      return {
        id,
        ...data,
        createdAt: data.createdAt ? new Date(data.createdAt) : new Date(),
        updatedAt: data.updatedAt ? new Date(data.updatedAt) : new Date()
      } as Student
    } catch (error) {
      console.error('Error getting student:', error)
      throw error
    }
  }

  async getAllStudents(): Promise<Student[]> {
    try {
      const snapshot = await get(this.studentsRef)
      if (!snapshot.exists()) return []
      
      const studentsData = snapshot.val()
      const students: Student[] = []
      
      Object.keys(studentsData).forEach(studentId => {
        const data = studentsData[studentId]
        students.push({
          id: studentId,
          ...data,
          createdAt: data.createdAt ? new Date(data.createdAt) : new Date(),
          updatedAt: data.updatedAt ? new Date(data.updatedAt) : new Date()
        } as Student)
      })
      
      return students
    } catch (error) {
      console.error('Error getting students:', error)
      throw error
    }
  }

  // Survey Management
  async submitSurvey(surveyData: Omit<RoommateSurvey, 'id' | 'submittedAt'>): Promise<string> {
    try {
      // Check if student already has a survey for this semester
      const snapshot = await get(this.surveysRef)
      if (snapshot.exists()) {
        const surveysData = snapshot.val()
        const existingSurvey = Object.values(surveysData).find((survey: any) => 
          survey.studentId === surveyData.studentId && survey.semester === surveyData.semester
        )
        
        if (existingSurvey) {
          throw new Error('Survey already submitted for this semester')
        }
      }

      const newSurvey = {
        ...surveyData,
        submittedAt: new Date().toISOString()
      }

      const newSurveyRef = push(this.surveysRef)
      await set(newSurveyRef, newSurvey)
      return newSurveyRef.key!
    } catch (error) {
      console.error('Error submitting survey:', error)
      throw error
    }
  }

  async updateSurvey(surveyId: string, updates: Partial<RoommateSurvey>): Promise<void> {
    try {
      const surveyRef = ref(realtimeDb, `roommateSurveys/${surveyId}`)
      await update(surveyRef, {
        ...updates,
        submittedAt: new Date().toISOString()
      })
    } catch (error) {
      console.error('Error updating survey:', error)
      throw error
    }
  }

  async getSurveyByStudent(studentId: string, semester: string): Promise<RoommateSurvey | null> {
    try {
      const snapshot = await get(this.surveysRef)
      if (!snapshot.exists()) return null
      
      const surveysData = snapshot.val()
      const surveyEntry = Object.entries(surveysData).find(([_, survey]: [string, any]) => 
        survey.studentId === studentId && survey.semester === semester
      )
      
      if (!surveyEntry) return null
      
      const [surveyId, data] = surveyEntry
      const surveyData = data as any // Type assertion for spread operation
      return {
        id: surveyId,
        ...surveyData,
        submittedAt: surveyData.submittedAt ? new Date(surveyData.submittedAt) : new Date()
      } as RoommateSurvey
    } catch (error) {
      console.error('Error getting survey:', error)
      throw error
    }
  }

  async getAllSurveys(semester: string): Promise<RoommateSurvey[]> {
    try {
      const snapshot = await get(this.surveysRef)
      if (!snapshot.exists()) return []
      
      const surveysData = snapshot.val()
      const surveys: RoommateSurvey[] = []
      
      Object.keys(surveysData).forEach(surveyId => {
        const data = surveysData[surveyId]
        if (data.semester === semester) {
          surveys.push({
            id: surveyId,
            ...data,
            submittedAt: data.submittedAt ? new Date(data.submittedAt) : new Date()
          } as RoommateSurvey)
        }
      })
      
      return surveys
    } catch (error) {
      console.error('Error getting surveys:', error)
      throw error
    }
  }

  // Room Management
  async addRoom(roomData: Omit<Room, 'id' | 'createdAt'>): Promise<string> {
    try {
      const newRoom = {
        ...roomData,
        createdAt: new Date().toISOString()
      }

      const newRoomRef = push(this.roomsRef)
      await set(newRoomRef, newRoom)
      return newRoomRef.key!
    } catch (error) {
      console.error('Error adding room:', error)
      throw error
    }
  }

  async getAllRooms(): Promise<Room[]> {
    try {
      const snapshot = await get(this.roomsRef)
      if (!snapshot.exists()) return []
      
      const roomsData = snapshot.val()
      const rooms: Room[] = []
      
      Object.keys(roomsData).forEach(roomId => {
        const data = roomsData[roomId]
        rooms.push({
          id: roomId,
          ...data,
          createdAt: data.createdAt ? new Date(data.createdAt) : new Date()
        } as Room)
      })
      
      return rooms
    } catch (error) {
      console.error('Error getting rooms:', error)
      throw error
    }
  }

  async updateRoomStatus(roomId: string, status: string, occupied: number): Promise<void> {
    try {
      const roomRef = ref(realtimeDb, `rooms/${roomId}`)
      await update(roomRef, {
        status,
        occupied
      })
    } catch (error) {
      console.error('Error updating room status:', error)
      throw error
    }
  }

  // Compatibility Calculation
  // Simplified Compatibility Calculation matching actual interface
  calculateCompatibility(survey1: RoommateSurvey, survey2: RoommateSurvey): CompatibilityScore {
    try {
      let totalScore = 0

      // Sleep compatibility (25 points)
      const sleepMatch = survey1.sleepSchedule === survey2.sleepSchedule ? 25 : 
                        (survey1.sleepSensitivity === survey2.sleepSensitivity ? 15 : 5)
      totalScore += sleepMatch

      // Study compatibility (20 points)
      const studyMatch = survey1.studyEnvironment === survey2.studyEnvironment ? 20 : 
                        (survey1.studySchedule === survey2.studySchedule ? 10 : 5)
      totalScore += studyMatch

      // Cleanliness compatibility (20 points)
      const cleanMatch = survey1.cleanliness === survey2.cleanliness ? 20 : 10
      totalScore += cleanMatch

      // Social compatibility (15 points)
      const socialMatch = survey1.socialLevel === survey2.socialLevel ? 15 : 
                         (survey1.guestPolicy === survey2.guestPolicy ? 10 : 5)
      totalScore += socialMatch

      // Lifestyle compatibility (20 points)
      let lifestyleScore = 0
      if (survey1.smoking === survey2.smoking) lifestyleScore += 5
      if (survey1.drinking === survey2.drinking) lifestyleScore += 5
      if (survey1.diet === survey2.diet) lifestyleScore += 5
      if (survey1.temperaturePreference === survey2.temperaturePreference) lifestyleScore += 5
      totalScore += lifestyleScore

      // Calculate category scores
      const categoryScores = {
        sleep: sleepMatch,
        study: studyMatch,
        cleanliness: cleanMatch,
        social: socialMatch,
        lifestyle: lifestyleScore,
        personality: 0 // Placeholder
      }

      return {
        studentId1: survey1.studentId,
        studentId2: survey2.studentId,
        totalScore,
        categoryScores,
        dealbreakers: [],
        strongMatches: []
      }
    } catch (error) {
      console.error('Error calculating compatibility:', error)
      throw error
    }
  }

  private parseTime(timeString: string): number {
    const [hours, minutes] = timeString.split(':').map(Number)
    return hours + (minutes / 60)
  }

  private calculateLifestyleCompatibility(survey1: RoommateSurvey, survey2: RoommateSurvey): number {
    let compatibilityScore = 0
    let factors = 0

    // Compare hobbies and interests (using the actual interface)
    const hobbies1 = survey1.hobbies || []
    const hobbies2 = survey2.hobbies || []
    const commonHobbies = hobbies1.filter((hobby: string) => hobbies2.includes(hobby))
    compatibilityScore += (commonHobbies.length / Math.max(hobbies1.length, hobbies2.length, 1)) * 100
    factors++

    return factors > 0 ? compatibilityScore / factors : 50
  }

  // Matching Algorithm
  async generateOptimalMatches(semester: string): Promise<CompatibilityScore[]> {
    try {
      console.log(`Generating optimal matches for semester: ${semester}`)
      
      const surveys = await this.getAllSurveys(semester)
      console.log(`Found ${surveys.length} surveys`)
      
      if (surveys.length < 2) {
        throw new Error('Not enough surveys to generate matches')
      }

      // Clear existing compatibility graph
      this.compatibilityGraph = new CompatibilityGraph()

      // Calculate all pairwise compatibility scores
      const compatibilityScores: CompatibilityScore[] = []
      
      for (let i = 0; i < surveys.length; i++) {
        for (let j = i + 1; j < surveys.length; j++) {
          const score = this.calculateCompatibility(surveys[i], surveys[j])
          compatibilityScores.push(score)
          
          // Note: compatibilityGraph.addEdge is not implemented, skip for now
          // this.compatibilityGraph.addEdge(
          //   surveys[i].studentId, 
          //   surveys[j].studentId, 
          //   score.totalScore
          // )
        }
      }

      // Sort by compatibility score (highest first)
      compatibilityScores.sort((a, b) => b.totalScore - a.totalScore)
      
      console.log(`Generated ${compatibilityScores.length} compatibility scores`)
      
      return compatibilityScores
    } catch (error) {
      console.error('Error generating optimal matches:', error)
      throw error
    }
  }

  // Room Assignment
  async createRoomAssignment(assignment: Omit<RoomAssignment, 'id' | 'createdAt'>): Promise<string> {
    try {
      const newAssignment = {
        ...assignment,
        createdAt: new Date().toISOString()
      }

      const newAssignmentRef = push(this.assignmentsRef)
      await set(newAssignmentRef, newAssignment)
      return newAssignmentRef.key!
    } catch (error) {
      console.error('Error creating room assignment:', error)
      throw error
    }
  }

  async updateRoomAssignment(assignmentId: string, updates: Partial<RoomAssignment>): Promise<void> {
    try {
      const assignmentRef = ref(realtimeDb, `roomAssignments/${assignmentId}`)
      await update(assignmentRef, updates)
    } catch (error) {
      console.error('Error updating room assignment:', error)
      throw error
    }
  }

  async deleteRoomAssignment(assignmentId: string): Promise<void> {
    try {
      const assignmentRef = ref(realtimeDb, `roomAssignments/${assignmentId}`)
      await remove(assignmentRef)
    } catch (error) {
      console.error('Error deleting room assignment:', error)
      throw error
    }
  }

  async getAllAssignments(semester: string): Promise<RoomAssignment[]> {
    try {
      const snapshot = await get(this.assignmentsRef)
      if (!snapshot.exists()) return []
      
      const assignmentsData = snapshot.val()
      const assignments: RoomAssignment[] = []
      
      Object.keys(assignmentsData).forEach(assignmentId => {
        const data = assignmentsData[assignmentId]
        if (data.semester === semester) {
          assignments.push({
            id: assignmentId,
            ...data,
            createdAt: data.createdAt ? new Date(data.createdAt) : new Date()
          } as RoomAssignment)
        }
      })
      
      return assignments
    } catch (error) {
      console.error('Error getting assignments:', error)
      throw error
    }
  }

  // Utility Methods
  async getStudentMatches(studentId: string): Promise<CompatibilityScore[]> {
    try {
      // This would typically be cached or stored separately
      // For now, we'll regenerate matches for the current semester
      const currentSemester = this.getCurrentSemester()
      const allMatches = await this.generateOptimalMatches(currentSemester)
      
      return allMatches.filter(match => 
        match.studentId1 === studentId || match.studentId2 === studentId
      )
    } catch (error) {
      console.error('Error getting student matches:', error)
      throw error
    }
  }

  private getCurrentSemester(): string {
    const now = new Date()
    const year = now.getFullYear()
    const month = now.getMonth() + 1
    
    // Simple semester logic: Jan-May = Spring, Aug-Dec = Fall
    if (month >= 1 && month <= 5) {
      return `Spring ${year}`
    } else {
      return `Fall ${year}`
    }
  }

  // Statistics and Analytics
  async getMatchingStats(semester: string): Promise<any> {
    try {
      const surveys = await this.getAllSurveys(semester)
      const assignments = await this.getAllAssignments(semester)
      
      const totalStudents = surveys.length
      const assignedStudents = assignments.reduce((count, assignment) => 
        count + assignment.studentIds.length, 0
      )
      const unassignedStudents = totalStudents - assignedStudents
      
      // Calculate average compatibility scores
      if (surveys.length >= 2) {
        const matches = await this.generateOptimalMatches(semester)
        const avgCompatibility = matches.length > 0 
          ? matches.reduce((sum, match) => sum + match.totalScore, 0) / matches.length 
          : 0
          
        return {
          totalStudents,
          assignedStudents,
          unassignedStudents,
          totalRooms: (await this.getAllRooms()).length,
          avgCompatibility: Math.round(avgCompatibility),
          matchingEfficiency: totalStudents > 0 ? (assignedStudents / totalStudents) * 100 : 0
        }
      }
      
      return {
        totalStudents,
        assignedStudents,
        unassignedStudents,
        totalRooms: (await this.getAllRooms()).length,
        avgCompatibility: 0,
        matchingEfficiency: 0
      }
    } catch (error) {
      console.error('Error getting matching stats:', error)
      throw error
    }
  }

  // Real-time subscriptions
  subscribeToSurveys(semester: string, callback: (surveys: RoommateSurvey[]) => void): () => void {
    return onValue(this.surveysRef, (snapshot) => {
      const surveys: RoommateSurvey[] = []
      if (snapshot.exists()) {
        const surveysData = snapshot.val()
        Object.keys(surveysData).forEach(surveyId => {
          const data = surveysData[surveyId]
          if (data.semester === semester) {
            surveys.push({
              id: surveyId,
              ...data,
              submittedAt: data.submittedAt ? new Date(data.submittedAt) : new Date()
            } as RoommateSurvey)
          }
        })
      }
      callback(surveys)
    })
  }

  subscribeToAssignments(semester: string, callback: (assignments: RoomAssignment[]) => void): () => void {
    return onValue(this.assignmentsRef, (snapshot) => {
      const assignments: RoomAssignment[] = []
      if (snapshot.exists()) {
        const assignmentsData = snapshot.val()
        Object.keys(assignmentsData).forEach(assignmentId => {
          const data = assignmentsData[assignmentId]
          if (data.semester === semester) {
            assignments.push({
              id: assignmentId,
              ...data,
              createdAt: data.createdAt ? new Date(data.createdAt) : new Date()
            } as RoomAssignment)
          }
        })
      }
      callback(assignments)
    })
  }

  // User-specific methods
  async getUserSurveys(userId: string): Promise<RoommateSurvey[]> {
    try {
      const snapshot = await get(this.surveysRef)
      if (!snapshot.exists()) return []
      
      const surveysData = snapshot.val()
      const userSurveys: RoommateSurvey[] = []
      
      Object.keys(surveysData).forEach(surveyId => {
        const data = surveysData[surveyId]
        if (data.userId === userId) {
          userSurveys.push({
            id: surveyId,
            ...data,
            submittedAt: data.submittedAt ? new Date(data.submittedAt) : new Date()
          } as RoommateSurvey)
        }
      })
      
      return userSurveys
    } catch (error) {
      console.error('Error getting user surveys:', error)
      throw error
    }
  }
}

// Export singleton instance
export const roomieMatcherService = new RoomieMatcherService()
