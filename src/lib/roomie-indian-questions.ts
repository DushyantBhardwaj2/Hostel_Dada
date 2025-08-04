// Enhanced Indian Hostel Life Questions for Roommate Matching
// These questions are designed to capture the authentic hostel experience and personality traits

export interface IndianHostelSurvey {
  id: string
  studentId: string
  semester: string
  
  // Regional & Cultural Background
  region: 'north_india' | 'south_india' | 'east_india' | 'west_india' | 'central_northeast'
  
  // Personality & Mood
  generalMood: 'passive_chill' | 'smart_fun' | 'selective_talker' | 'direct_aggressive'
  
  // Sleep Patterns
  sleepTime: 'early_10pm' | 'moderate_12am' | 'late_2am' | 'whenever_mood'
  
  // Study Habits
  studyPattern: 'daily_topper' | 'last_minute_warrior' | 'group_study_fun' | 'attendance_only'
  
  // Cleanliness & Room Management
  roomCleanliness: 'ocd_daily_clean' | 'weekly_cleaning' | 'manageable_mess' | 'chaos_master'
  
  // Social Behavior
  socialLevel: 'hostel_hr_everyone' | 'circle_friends' | 'introvert_personal' | 'room_bed_phone'
  
  // Smoking Habits
  smokingHabits: 'daily_smoker' | 'party_smoker' | 'ex_smoker' | 'never_hate_it'
  
  // Drinking Habits
  drinkingHabits: 'regular_saturday' | 'occasional_party' | 'rare_drinker' | 'teetotaler'
  
  // Fitness & Sports
  fitnessLevel: 'daily_gym_sports' | 'weekly_2_3_times' | 'mood_dependent' | 'remote_workout'
  
  // Music in Room
  musicInRoom: 'loud_speaker_full' | 'low_volume_respectful' | 'headphones_always' | 'no_music'
  
  // Noise Tolerance
  noiseTolerance: 'full_tolerance' | 'sometimes_problem' | 'ask_first_ok' | 'need_silence'
  
  // Gaming Habits
  gamingLevel: 'full_setup_gamer' | 'mobile_casual' | 'rare_gamer' | 'no_gaming'
  
  // Weekend Activities
  weekendStyle: 'hostel_chill_sleep' | 'roam_with_friends' | 'go_home_mostly' | 'study_weekend'
  
  // Sharing Attitude
  sharingAttitude: 'everything_shareable' | 'ask_first_ok' | 'emergency_only' | 'no_sharing'
  
  // Guest Policy
  guestPolicy: 'hostel_adda_open' | 'friends_limit' | 'rarely_guests' | 'privacy_no_guests'
  
  // Light Preferences at Night
  nightLights: 'complete_darkness' | 'dim_night_bulb' | 'full_lights_on' | 'doesnt_matter'
  
  // Morning Routine
  wakeUpTime: 'early_6am_gym' | 'normal_7_9am' | 'late_9_11am' | 'when_cleaner_comes'
  
  // Food Preferences
  foodPreference: 'pure_vegetarian' | 'egg_ok_only' | 'full_non_veg' | 'anything_available'
  
  // Conflict Resolution
  conflictStyle: 'ignore_chill' | 'talk_solve' | 'direct_straight' | 'angry_outburst'
  
  // Hostel Life Goal
  hostelLifeGoal: 'cgpa_focused' | 'study_fun_both' | 'masti_chill_only' | 'networking_jugaad'
  
  // Room Number Preference (addressing user's request)
  preferredRoomNumbers: string[] // User can select multiple room numbers
  preferredFloor: 'ground_floor' | 'first_floor' | 'second_floor' | 'third_floor' | 'any_floor'
  
  submittedAt: Date
}

export const IndianHostelQuestions = [
  {
    id: 'region',
    category: 'Regional & Cultural',
    question: 'Tu kis region ka launda hai?',
    options: [
      { value: 'north_india', label: 'North India – Bhaiya zone (Delhi, Haryana, Punjab, UP)', score: 1 },
      { value: 'south_india', label: 'South India – Silent killer (TN, KA, Kerala, AP)', score: 2 },
      { value: 'east_india', label: 'East India – Chill zone (WB, Odisha, Assam)', score: 3 },
      { value: 'west_india', label: 'West India – Bindass bhai (Maharashtra, Gujarat, Rajasthan)', score: 4 },
      { value: 'central_northeast', label: 'Central/North-East – Bhai alag hi swag mein', score: 5 }
    ]
  },
  {
    id: 'generalMood',
    category: 'Personality',
    question: 'Tera mood kaise rehta hai zyada time?',
    options: [
      { value: 'passive_chill', label: 'Bhai full shaant – passive AF', score: 1 },
      { value: 'smart_fun', label: 'Masti karta hoon but samajhdar hoon', score: 2 },
      { value: 'selective_talker', label: 'Thoda bhaav khaata hoon, thoda bolta hoon', score: 3 },
      { value: 'direct_aggressive', label: 'Bhai seedha thok deta hoon (aggressive types)', score: 4 }
    ]
  },
  {
    id: 'sleepTime',
    category: 'Sleep Schedule',
    question: 'Sone ka kya scene hai?',
    options: [
      { value: 'early_10pm', label: '10 baje lights off – mummy wala routine', score: 1 },
      { value: 'moderate_12am', label: '12 tak chill, fir neend', score: 2 },
      { value: 'late_2am', label: '2 baje tak reels, Discord, Netflix', score: 3 },
      { value: 'whenever_mood', label: 'Jab neend aaye tab sota hoon', score: 4 }
    ]
  },
  {
    id: 'studyPattern',
    category: 'Academic',
    question: 'Padhai ka kya funda hai bhai?',
    options: [
      { value: 'daily_topper', label: 'Roz padhta hoon – top karna hai', score: 1 },
      { value: 'last_minute_warrior', label: 'Exam se 3 din pehle jag jaata hoon', score: 2 },
      { value: 'group_study_fun', label: 'Group study mein bakchodi zyada hoti hai', score: 3 },
      { value: 'attendance_only', label: 'Bhai attendance mil jaaye bas', score: 4 }
    ]
  },
  {
    id: 'roomCleanliness',
    category: 'Lifestyle',
    question: 'Room kitna clean rakhta hai tu?',
    options: [
      { value: 'ocd_daily_clean', label: 'Full OCD – jhaadu pocha daily', score: 1 },
      { value: 'weekly_cleaning', label: 'Ek haftay mein ek baar saaf kar leta hoon', score: 2 },
      { value: 'manageable_mess', label: 'Thoda bikhra rehta hai – manage ho jaata hai', score: 3 },
      { value: 'chaos_master', label: 'Bhai kabadi wala bhi darr jaaye', score: 4 }
    ]
  },
  {
    id: 'socialLevel',
    category: 'Social',
    question: 'Tera social scene kaisa hai?',
    options: [
      { value: 'hostel_hr_everyone', label: 'Har kisi se dosti – hostel ka HR', score: 1 },
      { value: 'circle_friends', label: 'Apne circle mein chill', score: 2 },
      { value: 'introvert_personal', label: 'Introvert hoon – apne mein mast', score: 3 },
      { value: 'room_bed_phone', label: 'Bhai sirf room, bed aur phone', score: 4 }
    ]
  },
  {
    id: 'smokingHabits',
    category: 'Lifestyle',
    question: 'Smoke karta hai kya?',
    options: [
      { value: 'daily_smoker', label: 'Haan bhai – daily do-teen', score: 4 },
      { value: 'party_smoker', label: 'Party-sharty mein chalta hai', score: 3 },
      { value: 'ex_smoker', label: 'Chhoda hoon ab', score: 2 },
      { value: 'never_hate_it', label: 'Kabhi nahi – nafrat hai', score: 1 }
    ]
  },
  {
    id: 'drinkingHabits',
    category: 'Lifestyle',
    question: 'Daaru ka kya system hai?',
    options: [
      { value: 'regular_saturday', label: 'Regular Saturday scenes', score: 4 },
      { value: 'occasional_party', label: 'Kabhi-kabhi ya hostel party mein', score: 3 },
      { value: 'rare_drinker', label: 'Rare hi', score: 2 },
      { value: 'teetotaler', label: 'Bhai pakka teetotaler hoon', score: 1 }
    ]
  },
  {
    id: 'fitnessLevel',
    category: 'Health & Fitness',
    question: 'Workout ya sports mein kitna active hai?',
    options: [
      { value: 'daily_gym_sports', label: 'Roz gym ya ground – fit rehna hai', score: 1 },
      { value: 'weekly_2_3_times', label: '2–3 baar weekly', score: 2 },
      { value: 'mood_dependent', label: 'Mood aaya to chala gaya', score: 3 },
      { value: 'remote_workout', label: 'Bhai remote uthana bhi workout hai', score: 4 }
    ]
  },
  {
    id: 'musicInRoom',
    category: 'Entertainment',
    question: 'Music chalata hai kya room mein?',
    options: [
      { value: 'loud_speaker_full', label: 'Full volume – speaker pe Lofi ya rap', score: 4 },
      { value: 'low_volume_respectful', label: 'Halka volume – respect karta hoon', score: 2 },
      { value: 'headphones_always', label: 'Headphones bhagwan hain', score: 1 },
      { value: 'no_music', label: 'Music sunta hi nahi hoon', score: 3 }
    ]
  },
  {
    id: 'noiseTolerance',
    category: 'Environment',
    question: 'Dusre ke music/noise se problem hoti hai kya?',
    options: [
      { value: 'full_tolerance', label: 'Bilkul nahi – full tolerance', score: 1 },
      { value: 'sometimes_problem', label: 'Kabhi-kabhi dikkat hoti hai', score: 2 },
      { value: 'ask_first_ok', label: 'Pehle bol de to thik hai', score: 3 },
      { value: 'need_silence', label: 'Bhai shanti chahiye mujhe', score: 4 }
    ]
  },
  {
    id: 'gamingLevel',
    category: 'Entertainment',
    question: 'Gaming ka kya chakkar hai?',
    options: [
      { value: 'full_setup_gamer', label: 'Full setup – PUBG, Valorant, FIFA sab', score: 4 },
      { value: 'mobile_casual', label: 'Mobile gamer hoon – free time mein', score: 3 },
      { value: 'rare_gamer', label: 'Kabhi kabhi hi', score: 2 },
      { value: 'no_gaming', label: 'Mujhe gaming ki bakchodi pasand nahi', score: 1 }
    ]
  },
  {
    id: 'weekendStyle',
    category: 'Lifestyle',
    question: 'Weekend scene kya rehta hai?',
    options: [
      { value: 'hostel_chill_sleep', label: 'Hostel mein hi – chill, neend aur bakchodi', score: 1 },
      { value: 'roam_with_friends', label: 'Ghoomta hoon friends ke sath', score: 2 },
      { value: 'go_home_mostly', label: 'Mostly ghar chala jaata hoon', score: 3 },
      { value: 'study_weekend', label: 'Weekend mein bhi padhai bro', score: 4 }
    ]
  },
  {
    id: 'sharingAttitude',
    category: 'Social',
    question: 'Apni cheezein share karta hai kya?',
    options: [
      { value: 'everything_shareable', label: 'Khaana, charger, soap – sab shareable', score: 1 },
      { value: 'ask_first_ok', label: 'Poochh ke le toh thik hai', score: 2 },
      { value: 'emergency_only', label: 'Sirf zarurat mein', score: 3 },
      { value: 'no_sharing', label: 'Bhai meri cheezein meri – no sharing', score: 4 }
    ]
  },
  {
    id: 'guestPolicy',
    category: 'Social',
    question: 'Room mein guests lana allowed hai?',
    options: [
      { value: 'hostel_adda_open', label: 'Haan bhai – hostel hi adda hai', score: 4 },
      { value: 'friends_limit', label: 'Dost chalega, par limit hai', score: 3 },
      { value: 'rarely_guests', label: 'Kabhi-kabhi bas', score: 2 },
      { value: 'privacy_no_guests', label: 'Nahi bhai – privacy chahiye', score: 1 }
    ]
  },
  {
    id: 'nightLights',
    category: 'Environment',
    question: 'Raat ko lights ka kya scene hai?',
    options: [
      { value: 'complete_darkness', label: 'Full lights off – darkness me comfort', score: 1 },
      { value: 'dim_night_bulb', label: 'Dim light – night bulb', score: 2 },
      { value: 'full_lights_on', label: 'Full lights on', score: 4 },
      { value: 'doesnt_matter', label: 'Mujhe fark nahi padta', score: 3 }
    ]
  },
  {
    id: 'wakeUpTime',
    category: 'Schedule',
    question: 'Uthta kab hai subah?',
    options: [
      { value: 'early_6am_gym', label: '6 baje gym ka alarm', score: 1 },
      { value: 'normal_7_9am', label: '7–9 baje tak', score: 2 },
      { value: 'late_9_11am', label: '9–11 baje – mood dependent', score: 3 },
      { value: 'when_cleaner_comes', label: 'Jab room clean karne waala aata hai', score: 4 }
    ]
  },
  {
    id: 'foodPreference',
    category: 'Lifestyle',
    question: 'Khaane ka preference kya hai?',
    options: [
      { value: 'pure_vegetarian', label: 'Full veg – no meat zone', score: 1 },
      { value: 'egg_ok_only', label: 'Egg ok hai, baaki nahi', score: 2 },
      { value: 'full_non_veg', label: 'Non-veg all the way', score: 4 },
      { value: 'anything_available', label: 'Jo mile, plate mein daalo', score: 3 }
    ]
  },
  {
    id: 'conflictStyle',
    category: 'Personality',
    question: 'Jab jhagda ya issue hota hai toh?',
    options: [
      { value: 'ignore_chill', label: 'Bhai chill rehne ka – ignore karta hoon', score: 1 },
      { value: 'talk_solve', label: 'Baatein karke solve karta hoon', score: 2 },
      { value: 'direct_straight', label: 'Bol deta hoon seedha', score: 3 },
      { value: 'angry_outburst', label: 'Bhai gussa aata hai toh kuch bhi bol deta hoon', score: 4 }
    ]
  },
  {
    id: 'hostelLifeGoal',
    category: 'Life Goals',
    question: 'Tere liye hostel life ka matlab kya hai?',
    options: [
      { value: 'cgpa_focused', label: 'Goal clear – CGPA upar leke jaana hai', score: 1 },
      { value: 'study_fun_both', label: 'Padhai aur majje – dono', score: 2 },
      { value: 'masti_chill_only', label: 'Masti, bakchodi, chill', score: 3 },
      { value: 'networking_jugaad', label: 'Networking, jugaad, life sikhi jaa rahi hai', score: 4 }
    ]
  }
]

// Graph Node Matching Algorithm
export class RoommateGraphMatcher {
  
  // Calculate compatibility score between two users
  static calculateCompatibility(user1: IndianHostelSurvey, user2: IndianHostelSurvey): number {
    let totalScore = 0
    let maxPossibleScore = 0
    
    // Weight factors for different categories
    const weights = {
      critical: 3,    // Sleep, cleanliness, noise tolerance
      important: 2,   // Social level, study patterns, lifestyle habits
      moderate: 1     // Entertainment, food preferences, regional background
    }
    
    // Critical compatibility factors (higher weight)
    const criticalFactors = [
      { field: 'sleepTime', weight: weights.critical },
      { field: 'roomCleanliness', weight: weights.critical },
      { field: 'noiseTolerance', weight: weights.critical },
      { field: 'smokingHabits', weight: weights.critical },
      { field: 'nightLights', weight: weights.critical }
    ]
    
    // Important compatibility factors
    const importantFactors = [
      { field: 'socialLevel', weight: weights.important },
      { field: 'studyPattern', weight: weights.important },
      { field: 'drinkingHabits', weight: weights.important },
      { field: 'guestPolicy', weight: weights.important },
      { field: 'conflictStyle', weight: weights.important }
    ]
    
    // Moderate compatibility factors
    const moderateFactors = [
      { field: 'region', weight: weights.moderate },
      { field: 'generalMood', weight: weights.moderate },
      { field: 'musicInRoom', weight: weights.moderate },
      { field: 'gamingLevel', weight: weights.moderate },
      { field: 'weekendStyle', weight: weights.moderate },
      { field: 'sharingAttitude', weight: weights.moderate },
      { field: 'wakeUpTime', weight: weights.moderate },
      { field: 'foodPreference', weight: weights.moderate },
      { field: 'hostelLifeGoal', weight: weights.moderate },
      { field: 'fitnessLevel', weight: weights.moderate }
    ]
    
    // Calculate scores for all factors
    const allFactors = [...criticalFactors, ...importantFactors, ...moderateFactors]
    
    allFactors.forEach(factor => {
      const user1Value = user1[factor.field as keyof IndianHostelSurvey]
      const user2Value = user2[factor.field as keyof IndianHostelSurvey]
      
      maxPossibleScore += factor.weight * 4 // Max score difference is 4
      
      if (user1Value === user2Value) {
        // Perfect match
        totalScore += factor.weight * 4
      } else {
        // Calculate similarity based on question scores
        const score1 = this.getQuestionScore(factor.field, user1Value as string)
        const score2 = this.getQuestionScore(factor.field, user2Value as string)
        const difference = Math.abs(score1 - score2)
        const similarity = Math.max(0, 4 - difference)
        totalScore += factor.weight * similarity
      }
    })
    
    // Convert to percentage
    return Math.round((totalScore / maxPossibleScore) * 100)
  }
  
  // Get score for a specific question option
  private static getQuestionScore(questionId: string, value: string): number {
    const question = IndianHostelQuestions.find(q => q.id === questionId)
    const option = question?.options.find(opt => opt.value === value)
    return option?.score || 0
  }
  
  // Find best matches for a user
  static findBestMatches(
    targetUser: IndianHostelSurvey, 
    allUsers: IndianHostelSurvey[], 
    limit: number = 10
  ): Array<{ user: IndianHostelSurvey; compatibility: number; reasons: string[] }> {
    
    const matches = allUsers
      .filter(user => user.studentId !== targetUser.studentId)
      .map(user => ({
        user,
        compatibility: this.calculateCompatibility(targetUser, user),
        reasons: this.getCompatibilityReasons(targetUser, user)
      }))
      .sort((a, b) => b.compatibility - a.compatibility)
      .slice(0, limit)
    
    return matches
  }
  
  // Generate reasons for compatibility
  private static getCompatibilityReasons(user1: IndianHostelSurvey, user2: IndianHostelSurvey): string[] {
    const reasons: string[] = []
    
    // Check for perfect matches in critical areas
    if (user1.sleepTime === user2.sleepTime) {
      const sleepLabels = {
        'early_10pm': 'Both are early sleepers (10 PM)',
        'moderate_12am': 'Both sleep around midnight',
        'late_2am': 'Both are night owls (2 AM+)',
        'whenever_mood': 'Both have flexible sleep schedules'
      }
      reasons.push(`🌙 ${sleepLabels[user1.sleepTime]}`)
    }
    
    if (user1.roomCleanliness === user2.roomCleanliness) {
      const cleanLabels = {
        'ocd_daily_clean': 'Both are super clean (daily cleaning)',
        'weekly_cleaning': 'Both do weekly room cleaning',
        'manageable_mess': 'Both manage moderate messiness',
        'chaos_master': 'Both are comfortable with chaos'
      }
      reasons.push(`🧹 ${cleanLabels[user1.roomCleanliness]}`)
    }
    
    if (user1.socialLevel === user2.socialLevel) {
      const socialLabels = {
        'hostel_hr_everyone': 'Both are super social (hostel HR types)',
        'circle_friends': 'Both prefer close friend circles',
        'introvert_personal': 'Both are introverts who enjoy personal space',
        'room_bed_phone': 'Both prefer room, bed, and phone lifestyle'
      }
      reasons.push(`👥 ${socialLabels[user1.socialLevel]}`)
    }
    
    if (user1.studyPattern === user2.studyPattern) {
      const studyLabels = {
        'daily_topper': 'Both are dedicated daily studiers',
        'last_minute_warrior': 'Both are last-minute study warriors',
        'group_study_fun': 'Both enjoy group study sessions',
        'attendance_only': 'Both focus on just getting attendance'
      }
      reasons.push(`📚 ${studyLabels[user1.studyPattern]}`)
    }
    
    // Check lifestyle compatibility
    if (user1.smokingHabits === user2.smokingHabits && user1.drinkingHabits === user2.drinkingHabits) {
      reasons.push('🍺 Compatible lifestyle choices')
    }
    
    if (user1.region === user2.region) {
      const regionLabels = {
        'north_india': 'Both from North India (Bhaiya zone)',
        'south_india': 'Both from South India (Silent killers)',
        'east_india': 'Both from East India (Chill zone)',
        'west_india': 'Both from West India (Bindass types)',
        'central_northeast': 'Both from Central/Northeast (Unique swag)'
      }
      reasons.push(`🗺️ ${regionLabels[user1.region]}`)
    }
    
    // Check entertainment compatibility
    if (user1.musicInRoom === user2.musicInRoom || user1.gamingLevel === user2.gamingLevel) {
      reasons.push('🎮 Similar entertainment preferences')
    }
    
    if (reasons.length === 0) {
      reasons.push('🤝 Good overall personality compatibility')
    }
    
    return reasons.slice(0, 4) // Limit to top 4 reasons
  }
  
  // Room assignment optimization
  static optimizeRoomAssignments(
    surveys: IndianHostelSurvey[], 
    availableRooms: string[]
  ): Array<{ roomNumber: string; students: IndianHostelSurvey[]; avgCompatibility: number }> {
    
    const assignments: Array<{ roomNumber: string; students: IndianHostelSurvey[]; avgCompatibility: number }> = []
    const unassigned = [...surveys]
    
    availableRooms.forEach(roomNumber => {
      if (unassigned.length >= 2) {
        // Find the best pair for this room
        let bestPair: { student1: IndianHostelSurvey; student2: IndianHostelSurvey; compatibility: number } | null = null
        
        for (let i = 0; i < unassigned.length; i++) {
          for (let j = i + 1; j < unassigned.length; j++) {
            const compatibility = this.calculateCompatibility(unassigned[i], unassigned[j])
            
            // Check room number preferences
            const student1WantsRoom = unassigned[i].preferredRoomNumbers.includes(roomNumber)
            const student2WantsRoom = unassigned[j].preferredRoomNumbers.includes(roomNumber)
            
            let adjustedCompatibility = compatibility
            if (student1WantsRoom && student2WantsRoom) {
              adjustedCompatibility += 15 // Bonus for both wanting the room
            } else if (student1WantsRoom || student2WantsRoom) {
              adjustedCompatibility += 5 // Bonus for one wanting the room
            }
            
            if (!bestPair || adjustedCompatibility > bestPair.compatibility) {
              bestPair = {
                student1: unassigned[i],
                student2: unassigned[j],
                compatibility: adjustedCompatibility
              }
            }
          }
        }
        
        if (bestPair) {
          assignments.push({
            roomNumber,
            students: [bestPair.student1, bestPair.student2],
            avgCompatibility: bestPair.compatibility
          })
          
          // Remove assigned students
          const index1 = unassigned.findIndex(s => s.studentId === bestPair!.student1.studentId)
          const index2 = unassigned.findIndex(s => s.studentId === bestPair!.student2.studentId)
          unassigned.splice(Math.max(index1, index2), 1)
          unassigned.splice(Math.min(index1, index2), 1)
        }
      }
    })
    
    return assignments.sort((a, b) => b.avgCompatibility - a.avgCompatibility)
  }
}

// Export scoring system for individual answers
export const getMyPersonalScores = () => {
  // This would be used to show users their own personality profile
  return {
    region: { answer: 'north_india', score: 1, label: 'North India Bhaiya' },
    generalMood: { answer: 'smart_fun', score: 2, label: 'Smart & Fun' },
    sleepTime: { answer: 'late_2am', score: 3, label: 'Night Owl' },
    studyPattern: { answer: 'last_minute_warrior', score: 2, label: 'Last Minute Warrior' },
    roomCleanliness: { answer: 'manageable_mess', score: 3, label: 'Manageable Mess' },
    socialLevel: { answer: 'circle_friends', score: 2, label: 'Circle Friends' },
    smokingHabits: { answer: 'never_hate_it', score: 1, label: 'Never Smoke' },
    drinkingHabits: { answer: 'occasional_party', score: 3, label: 'Occasional Party' },
    fitnessLevel: { answer: 'mood_dependent', score: 3, label: 'Mood Dependent' },
    musicInRoom: { answer: 'headphones_always', score: 1, label: 'Headphones Always' },
    noiseTolerance: { answer: 'ask_first_ok', score: 3, label: 'Ask First OK' },
    gamingLevel: { answer: 'mobile_casual', score: 3, label: 'Mobile Casual' },
    weekendStyle: { answer: 'roam_with_friends', score: 2, label: 'Roam with Friends' },
    sharingAttitude: { answer: 'ask_first_ok', score: 2, label: 'Ask First OK' },
    guestPolicy: { answer: 'friends_limit', score: 3, label: 'Friends with Limit' },
    nightLights: { answer: 'dim_night_bulb', score: 2, label: 'Dim Night Bulb' },
    wakeUpTime: { answer: 'normal_7_9am', score: 2, label: 'Normal 7-9 AM' },
    foodPreference: { answer: 'anything_available', score: 3, label: 'Anything Available' },
    conflictStyle: { answer: 'talk_solve', score: 2, label: 'Talk & Solve' },
    hostelLifeGoal: { answer: 'study_fun_both', score: 2, label: 'Study & Fun Both' }
  }
}
