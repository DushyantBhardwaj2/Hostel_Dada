// RoomieMatcher - Advanced DSA-based roommate matching system
// Implements graph algorithms, compatibility scoring, and optimization

export interface Student {
  id: string
  userId: string
  email: string
  name: string
  branch: string
  year: number
  gender: 'male' | 'female' | 'other'
  hometown: string
  phoneNumber: string
  emergencyContact: string
  createdAt: Date
  updatedAt: Date
}

export interface RoommateSurvey {
  id: string
  studentId: string
  semester: string
  
  // Sleep Preferences
  sleepSchedule: 'early_bird' | 'night_owl' | 'flexible'
  sleepSensitivity: 'light_sleeper' | 'heavy_sleeper' | 'moderate'
  
  // Cleanliness & Organization
  cleanliness: 'very_clean' | 'moderately_clean' | 'casual' | 'messy'
  organizationLevel: 'very_organized' | 'organized' | 'somewhat_organized' | 'disorganized'
  
  // Social Preferences
  socialLevel: 'very_social' | 'social' | 'selective' | 'private'
  guestPolicy: 'frequent_guests' | 'occasional_guests' | 'rare_guests' | 'no_guests'
  
  // Study Habits
  studyEnvironment: 'complete_silence' | 'quiet' | 'background_noise_ok' | 'music_ok'
  studySchedule: 'morning' | 'afternoon' | 'evening' | 'late_night' | 'irregular'
  
  // Lifestyle
  smoking: boolean
  drinking: boolean
  diet: 'vegetarian' | 'non_vegetarian' | 'vegan' | 'no_preference'
  
  // Technology & Entertainment
  musicGenres: string[]
  hobbies: string[]
  languages: string[]
  
  // Room Environment
  temperaturePreference: 'cold' | 'moderate' | 'warm'
  lightPreference: 'bright' | 'moderate' | 'dim'
  
  // Personal Traits
  personality: ('introvert' | 'extrovert' | 'ambivert')[]
  conflictResolution: 'direct' | 'diplomatic' | 'avoidant' | 'mediator'
  
  // Academic Information
  gpa: number
  studyIntensive: boolean
  
  // Dating & Relationships
  relationshipStatus: 'single' | 'dating' | 'committed' | 'prefer_not_to_say'
  datingInRoom: boolean
  
  // Special Requirements
  medicalConditions: string[]
  specialRequests: string
  
  submittedAt: Date
}

export interface CompatibilityScore {
  studentId1: string
  studentId2: string
  totalScore: number
  categoryScores: {
    lifestyle: number
    study: number
    social: number
    cleanliness: number
    sleep: number
    personality: number
  }
  dealbreakers: string[]
  strongMatches: string[]
}

export interface Room {
  id: string
  number: string
  floor: number
  building: string
  capacity: number
  currentOccupants: string[] // Student IDs
  amenities: string[]
  roomType: 'single' | 'double' | 'triple' | 'suite'
  isAvailable: boolean
  createdAt: Date
}

export interface RoomAssignment {
  id: string
  roomId: string
  studentIds: string[]
  semester: string
  assignedBy: string // Admin ID
  compatibilityScore: number
  status: 'pending' | 'confirmed' | 'rejected' | 'completed'
  assignedAt: Date
  confirmedAt?: Date
}

// DSA Implementation: Graph for compatibility matching
export class CompatibilityGraph {
  private adjacencyList: Map<string, Map<string, CompatibilityScore>>
  private students: Map<string, Student>
  private surveys: Map<string, RoommateSurvey>

  constructor() {
    this.adjacencyList = new Map()
    this.students = new Map()
    this.surveys = new Map()
  }

  // Add student to the graph
  addStudent(student: Student, survey: RoommateSurvey): void {
    this.students.set(student.id, student)
    this.surveys.set(student.id, survey)
    this.adjacencyList.set(student.id, new Map())
  }

  // Calculate compatibility score between two students
  calculateCompatibility(studentId1: string, studentId2: string): CompatibilityScore {
    const survey1 = this.surveys.get(studentId1)
    const survey2 = this.surveys.get(studentId2)
    const student1 = this.students.get(studentId1)
    const student2 = this.students.get(studentId2)

    if (!survey1 || !survey2 || !student1 || !student2) {
      throw new Error('Students or surveys not found')
    }

    // Gender compatibility (assuming same-gender housing)
    if (student1.gender !== student2.gender) {
      return {
        studentId1,
        studentId2,
        totalScore: 0,
        categoryScores: {
          lifestyle: 0, study: 0, social: 0, cleanliness: 0, sleep: 0, personality: 0
        },
        dealbreakers: ['Different gender'],
        strongMatches: []
      }
    }

    const categoryScores = {
      lifestyle: this.calculateLifestyleCompatibility(survey1, survey2),
      study: this.calculateStudyCompatibility(survey1, survey2),
      social: this.calculateSocialCompatibility(survey1, survey2),
      cleanliness: this.calculateCleanlinessCompatibility(survey1, survey2),
      sleep: this.calculateSleepCompatibility(survey1, survey2),
      personality: this.calculatePersonalityCompatibility(survey1, survey2)
    }

    const dealbreakers: string[] = []
    const strongMatches: string[] = []

    // Check for dealbreakers
    if (survey1.smoking && !survey2.smoking) dealbreakers.push('Smoking conflict')
    if (Math.abs(this.getNumericValue(survey1.cleanliness, ['messy', 'casual', 'moderately_clean', 'very_clean']) - 
                 this.getNumericValue(survey2.cleanliness, ['messy', 'casual', 'moderately_clean', 'very_clean'])) > 2) {
      dealbreakers.push('Cleanliness mismatch')
    }

    // Check for strong matches
    if (categoryScores.study > 80) strongMatches.push('Study habits')
    if (categoryScores.lifestyle > 85) strongMatches.push('Lifestyle')
    if (student1.branch === student2.branch) strongMatches.push('Same academic branch')

    // Calculate weighted total score
    const weights = {
      lifestyle: 0.2,
      study: 0.25,
      social: 0.15,
      cleanliness: 0.2,
      sleep: 0.15,
      personality: 0.05
    }

    const totalScore = Object.entries(categoryScores).reduce((sum, [category, score]) => {
      return sum + (score * weights[category as keyof typeof weights])
    }, 0)

    // Apply dealbreaker penalty
    const finalScore = dealbreakers.length > 0 ? Math.max(0, totalScore - 30) : totalScore

    return {
      studentId1,
      studentId2,
      totalScore: Math.round(finalScore),
      categoryScores,
      dealbreakers,
      strongMatches
    }
  }

  private calculateLifestyleCompatibility(survey1: RoommateSurvey, survey2: RoommateSurvey): number {
    let score = 100

    // Smoking compatibility
    if (survey1.smoking !== survey2.smoking) score -= 40

    // Drinking compatibility
    if (survey1.drinking !== survey2.drinking) score -= 20

    // Diet compatibility
    if (survey1.diet !== survey2.diet && survey1.diet !== 'no_preference' && survey2.diet !== 'no_preference') {
      score -= 15
    }

    // Temperature preference
    const tempDiff = Math.abs(
      this.getNumericValue(survey1.temperaturePreference, ['cold', 'moderate', 'warm']) -
      this.getNumericValue(survey2.temperaturePreference, ['cold', 'moderate', 'warm'])
    )
    score -= tempDiff * 10

    return Math.max(0, score)
  }

  private calculateStudyCompatibility(survey1: RoommateSurvey, survey2: RoommateSurvey): number {
    let score = 100

    // Study environment compatibility
    const envDiff = Math.abs(
      this.getNumericValue(survey1.studyEnvironment, ['complete_silence', 'quiet', 'background_noise_ok', 'music_ok']) -
      this.getNumericValue(survey2.studyEnvironment, ['complete_silence', 'quiet', 'background_noise_ok', 'music_ok'])
    )
    score -= envDiff * 15

    // Study schedule compatibility
    if (survey1.studySchedule === survey2.studySchedule) {
      score += 10
    } else if (this.getOverlap(survey1.studySchedule, survey2.studySchedule)) {
      score -= 10
    } else {
      score -= 25
    }

    // Academic intensity compatibility
    if (survey1.studyIntensive === survey2.studyIntensive) {
      score += 15
    } else {
      score -= 20
    }

    // GPA similarity (closer GPAs might indicate similar study habits)
    const gpaDiff = Math.abs(survey1.gpa - survey2.gpa)
    if (gpaDiff < 0.5) score += 10
    else if (gpaDiff > 1.5) score -= 15

    return Math.max(0, score)
  }

  private calculateSocialCompatibility(survey1: RoommateSurvey, survey2: RoommateSurvey): number {
    let score = 100

    // Social level compatibility
    const socialDiff = Math.abs(
      this.getNumericValue(survey1.socialLevel, ['private', 'selective', 'social', 'very_social']) -
      this.getNumericValue(survey2.socialLevel, ['private', 'selective', 'social', 'very_social'])
    )
    score -= socialDiff * 12

    // Guest policy compatibility
    const guestDiff = Math.abs(
      this.getNumericValue(survey1.guestPolicy, ['no_guests', 'rare_guests', 'occasional_guests', 'frequent_guests']) -
      this.getNumericValue(survey2.guestPolicy, ['no_guests', 'rare_guests', 'occasional_guests', 'frequent_guests'])
    )
    score -= guestDiff * 15

    // Dating in room compatibility
    if (survey1.datingInRoom !== survey2.datingInRoom) {
      score -= 25
    }

    // Common languages boost
    const commonLanguages = survey1.languages.filter(lang => survey2.languages.includes(lang))
    score += commonLanguages.length * 5

    // Common hobbies boost
    const commonHobbies = survey1.hobbies.filter(hobby => survey2.hobbies.includes(hobby))
    score += commonHobbies.length * 3

    return Math.max(0, score)
  }

  private calculateCleanlinessCompatibility(survey1: RoommateSurvey, survey2: RoommateSurvey): number {
    let score = 100

    // Cleanliness level compatibility
    const cleanDiff = Math.abs(
      this.getNumericValue(survey1.cleanliness, ['messy', 'casual', 'moderately_clean', 'very_clean']) -
      this.getNumericValue(survey2.cleanliness, ['messy', 'casual', 'moderately_clean', 'very_clean'])
    )
    score -= cleanDiff * 20

    // Organization level compatibility
    const orgDiff = Math.abs(
      this.getNumericValue(survey1.organizationLevel, ['disorganized', 'somewhat_organized', 'organized', 'very_organized']) -
      this.getNumericValue(survey2.organizationLevel, ['disorganized', 'somewhat_organized', 'organized', 'very_organized'])
    )
    score -= orgDiff * 15

    return Math.max(0, score)
  }

  private calculateSleepCompatibility(survey1: RoommateSurvey, survey2: RoommateSurvey): number {
    let score = 100

    // Sleep schedule compatibility
    if (survey1.sleepSchedule === survey2.sleepSchedule) {
      score += 20
    } else if (survey1.sleepSchedule === 'flexible' || survey2.sleepSchedule === 'flexible') {
      score += 10
    } else {
      score -= 30
    }

    // Sleep sensitivity compatibility
    const sensitivityDiff = Math.abs(
      this.getNumericValue(survey1.sleepSensitivity, ['heavy_sleeper', 'moderate', 'light_sleeper']) -
      this.getNumericValue(survey2.sleepSensitivity, ['heavy_sleeper', 'moderate', 'light_sleeper'])
    )
    score -= sensitivityDiff * 15

    // Light preference compatibility
    const lightDiff = Math.abs(
      this.getNumericValue(survey1.lightPreference, ['dim', 'moderate', 'bright']) -
      this.getNumericValue(survey2.lightPreference, ['dim', 'moderate', 'bright'])
    )
    score -= lightDiff * 10

    return Math.max(0, score)
  }

  private calculatePersonalityCompatibility(survey1: RoommateSurvey, survey2: RoommateSurvey): number {
    let score = 100

    // Conflict resolution style compatibility
    if (survey1.conflictResolution === survey2.conflictResolution) {
      score += 15
    } else if (survey1.conflictResolution === 'mediator' || survey2.conflictResolution === 'mediator') {
      score += 10
    } else if (survey1.conflictResolution === 'avoidant' && survey2.conflictResolution === 'direct') {
      score -= 20
    }

    // Personality type compatibility
    const personality1 = survey1.personality[0]
    const personality2 = survey2.personality[0]
    
    if (personality1 === personality2) {
      score += 10
    } else if (personality1 === 'ambivert' || personality2 === 'ambivert') {
      score += 5
    } else {
      score -= 5 // Opposites can sometimes work
    }

    return Math.max(0, score)
  }

  private getNumericValue(value: string, scale: string[]): number {
    return scale.indexOf(value)
  }

  private getOverlap(schedule1: string, schedule2: string): boolean {
    const overlaps: Record<string, string[]> = {
      'morning': ['afternoon'],
      'afternoon': ['morning', 'evening'],
      'evening': ['afternoon', 'late_night'],
      'late_night': ['evening'],
      'irregular': ['morning', 'afternoon', 'evening', 'late_night']
    }
    
    return overlaps[schedule1]?.includes(schedule2) || false
  }

  // Build compatibility graph for all students
  buildCompatibilityGraph(): void {
    const studentIds = Array.from(this.students.keys())
    
    for (let i = 0; i < studentIds.length; i++) {
      for (let j = i + 1; j < studentIds.length; j++) {
        const compatibility = this.calculateCompatibility(studentIds[i], studentIds[j])
        
        // Add edge if compatibility score is above threshold
        if (compatibility.totalScore > 30) {
          this.adjacencyList.get(studentIds[i])!.set(studentIds[j], compatibility)
          this.adjacencyList.get(studentIds[j])!.set(studentIds[i], compatibility)
        }
      }
    }
  }

  // Find optimal roommate pairs using Maximum Weight Matching
  findOptimalMatching(): Map<string, string> {
    const studentIds = Array.from(this.students.keys())
    const matched = new Set<string>()
    const pairs = new Map<string, string>()

    // Sort all possible pairs by compatibility score
    const allPairs: { student1: string; student2: string; score: number }[] = []
    
    for (let i = 0; i < studentIds.length; i++) {
      for (let j = i + 1; j < studentIds.length; j++) {
        const compatibility = this.adjacencyList.get(studentIds[i])?.get(studentIds[j])
        if (compatibility) {
          allPairs.push({
            student1: studentIds[i],
            student2: studentIds[j],
            score: compatibility.totalScore
          })
        }
      }
    }

    // Sort by score in descending order
    allPairs.sort((a, b) => b.score - a.score)

    // Greedy matching (can be improved with Hungarian algorithm)
    for (const pair of allPairs) {
      if (!matched.has(pair.student1) && !matched.has(pair.student2)) {
        pairs.set(pair.student1, pair.student2)
        pairs.set(pair.student2, pair.student1)
        matched.add(pair.student1)
        matched.add(pair.student2)
      }
    }

    return pairs
  }

  // Get compatibility details between two students
  getCompatibilityDetails(studentId1: string, studentId2: string): CompatibilityScore | undefined {
    return this.adjacencyList.get(studentId1)?.get(studentId2)
  }

  // Get all potential matches for a student sorted by compatibility
  getPotentialMatches(studentId: string): Array<{ studentId: string; compatibility: CompatibilityScore }> {
    const matches = this.adjacencyList.get(studentId)
    if (!matches) return []

    return Array.from(matches.entries())
      .map(([id, compatibility]) => ({ studentId: id, compatibility }))
      .sort((a, b) => b.compatibility.totalScore - a.compatibility.totalScore)
  }
}

// Room allocation optimization using DSA
export class RoomAllocationOptimizer {
  private rooms: Map<string, Room>
  private assignments: Map<string, RoomAssignment>

  constructor() {
    this.rooms = new Map()
    this.assignments = new Map()
  }

  addRoom(room: Room): void {
    this.rooms.set(room.id, room)
  }

  // Optimal room assignment considering distance, seniority, and preferences
  optimizeRoomAssignment(
    studentPairs: Map<string, string>,
    students: Map<string, Student>,
    preferences: Map<string, string[]> // student preferences for building/floor
  ): Map<string, string> {
    const assignments = new Map<string, string>()
    const availableRooms = Array.from(this.rooms.values()).filter(room => room.isAvailable)
    
    // Sort students by seniority (year) and hometown distance
    const sortedPairs = Array.from(studentPairs.entries())
      .map(([student1Id, student2Id]) => {
        const student1 = students.get(student1Id)!
        const student2 = students.get(student2Id)!
        
        const avgYear = (student1.year + student2.year) / 2
        const sameHometown = student1.hometown === student2.hometown
        
        return {
          pair: [student1Id, student2Id],
          priority: avgYear + (sameHometown ? 0.5 : 0)
        }
      })
      .sort((a, b) => b.priority - a.priority)

    // Assign rooms based on priority
    for (const { pair } of sortedPairs) {
      const [student1Id, student2Id] = pair
      const bestRoom = this.findBestRoom(
        student1Id, 
        student2Id, 
        availableRooms, 
        preferences.get(student1Id) || [],
        preferences.get(student2Id) || []
      )
      
      if (bestRoom) {
        assignments.set(student1Id, bestRoom.id)
        assignments.set(student2Id, bestRoom.id)
        bestRoom.isAvailable = false
        bestRoom.currentOccupants = [student1Id, student2Id]
      }
    }

    return assignments
  }

  private findBestRoom(
    student1Id: string,
    student2Id: string,
    availableRooms: Room[],
    preferences1: string[],
    preferences2: string[]
  ): Room | null {
    let bestRoom: Room | null = null
    let bestScore = -1

    for (const room of availableRooms) {
      if (room.capacity < 2) continue

      let score = 0

      // Preference matching
      const commonPreferences = preferences1.filter(pref => preferences2.includes(pref))
      if (commonPreferences.some(pref => room.building.includes(pref) || room.floor.toString() === pref)) {
        score += 20
      }

      // Room amenities
      score += room.amenities.length * 2

      // Floor preference (lower floors might be preferred)
      score += Math.max(0, 10 - room.floor)

      if (score > bestScore) {
        bestScore = score
        bestRoom = room
      }
    }

    return bestRoom
  }
}
