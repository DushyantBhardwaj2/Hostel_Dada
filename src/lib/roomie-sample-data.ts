// Sample data generator for RoomieMatcher development and testing
import { roomieMatcherService } from '@/lib/roomie-service'

// Sample rooms data
const sampleRooms = [
  {
    building: 'Block A',
    number: '101',
    floor: 1,
    roomType: 'double' as const,
    amenities: ['AC', 'WiFi', 'Study Table', 'Wardrobe'],
    isOccupied: false,
    capacity: 2,
    currentOccupants: [],
    isAvailable: true
  },
  {
    building: 'Block A',
    number: '102',
    floor: 1,
    roomType: 'double' as const,
    amenities: ['AC', 'WiFi', 'Study Table', 'Wardrobe', 'Balcony'],
    isOccupied: false,
    capacity: 2,
    currentOccupants: [],
    isAvailable: true
  },
  {
    building: 'Block A',
    number: '201',
    floor: 2,
    roomType: 'triple' as const,
    amenities: ['AC', 'WiFi', 'Study Table', 'Wardrobe'],
    isOccupied: false,
    capacity: 3,
    currentOccupants: [],
    isAvailable: true
  },
  {
    building: 'Block B',
    number: '101',
    floor: 1,
    roomType: 'double' as const,
    amenities: ['Fan', 'WiFi', 'Study Table', 'Wardrobe'],
    isOccupied: false,
    capacity: 2,
    currentOccupants: [],
    isAvailable: true
  },
  {
    building: 'Block B',
    number: '102',
    floor: 1,
    roomType: 'double' as const,
    amenities: ['AC', 'WiFi', 'Study Table', 'Wardrobe', 'Attached Bathroom'],
    isOccupied: false,
    capacity: 2,
    currentOccupants: [],
    isAvailable: true
  },
  {
    building: 'Block B',
    number: '201',
    floor: 2,
    roomType: 'single' as const,
    amenities: ['AC', 'WiFi', 'Study Table', 'Wardrobe', 'Balcony'],
    isOccupied: false,
    capacity: 1,
    currentOccupants: [],
    isAvailable: true
  },
  {
    building: 'Block C',
    number: '101',
    floor: 1,
    roomType: 'triple' as const,
    amenities: ['AC', 'WiFi', 'Study Table', 'Wardrobe'],
    isOccupied: false,
    capacity: 3,
    currentOccupants: [],
    isAvailable: true
  },
  {
    building: 'Block C',
    number: '102',
    floor: 1,
    roomType: 'double' as const,
    amenities: ['AC', 'WiFi', 'Study Table', 'Wardrobe', 'Mini Fridge'],
    isOccupied: false,
    capacity: 2,
    currentOccupants: [],
    isAvailable: true
  }
]

// Sample students data
const sampleStudents = [
  {
    userId: 'user_arjun',
    name: 'Arjun Sharma',
    email: 'arjun.sharma@university.edu',
    branch: 'Computer Science',
    year: 2,
    hometown: 'Delhi',
    phoneNumber: '+91-9876543210',
    gender: 'male' as const,
    emergencyContact: 'Mr. Rajesh Sharma (Father) - +91-9876543200'
  },
  {
    userId: 'user_priya',
    name: 'Priya Patel',
    email: 'priya.patel@university.edu',
    branch: 'Electronics',
    year: 2,
    hometown: 'Mumbai',
    phoneNumber: '+91-9876543211',
    gender: 'female' as const,
    emergencyContact: 'Mrs. Sunita Patel (Mother) - +91-9876543201'
  },
  {
    userId: 'user_rohit',
    name: 'Rohit Gupta',
    email: 'rohit.gupta@university.edu',
    branch: 'Mechanical',
    year: 1,
    hometown: 'Bangalore',
    phoneNumber: '+91-9876543212',
    gender: 'male' as const,
    emergencyContact: 'Mr. Suresh Gupta (Father) - +91-9876543202'
  },
  {
    userId: 'user_sneha',
    name: 'Sneha Singh',
    email: 'sneha.singh@university.edu',
    branch: 'Civil',
    year: 2,
    hometown: 'Pune',
    phoneNumber: '+91-9876543213',
    gender: 'female' as const,
    emergencyContact: 'Mr. Amit Singh (Father) - +91-9876543203'
  },
  {
    userId: 'user_vikram',
    name: 'Vikram Reddy',
    email: 'vikram.reddy@university.edu',
    branch: 'Computer Science',
    year: 1,
    hometown: 'Hyderabad',
    phoneNumber: '+91-9876543214',
    gender: 'male' as const,
    emergencyContact: 'Mrs. Lakshmi Reddy (Mother) - +91-9876543204'
  },
  {
    userId: 'user_ananya',
    name: 'Ananya Krishnan',
    email: 'ananya.krishnan@university.edu',
    branch: 'Biotechnology',
    year: 2,
    hometown: 'Chennai',
    phoneNumber: '+91-9876543215',
    gender: 'female' as const,
    emergencyContact: 'Dr. Ravi Krishnan (Father) - +91-9876543205'
  }
]

// Sample survey responses
const sampleSurveyResponses = [
  {
    studentId: 'student1',
    semester: 'Fall_2025',
    
    // Sleep Preferences
    sleepSchedule: 'early_bird' as const,
    sleepSensitivity: 'light_sleeper' as const,
    
    // Cleanliness & Organization
    cleanliness: 'very_clean' as const,
    organizationLevel: 'very_organized' as const,
    
    // Social Preferences
    socialLevel: 'selective' as const,
    guestPolicy: 'occasional_guests' as const,
    
    // Study Habits
    studyEnvironment: 'quiet' as const,
    studySchedule: 'evening' as const,
    
    // Lifestyle
    smoking: false,
    drinking: false,
    diet: 'vegetarian' as const,
    
    // Technology & Entertainment
    musicGenres: ['classical', 'lo-fi'],
    hobbies: ['reading', 'coding', 'chess'],
    languages: ['english', 'hindi'],
    
    // Room Environment
    temperaturePreference: 'moderate' as const,
    lightPreference: 'moderate' as const,
    
    // Personal Traits
    personality: ['introvert'] as ('introvert' | 'extrovert' | 'ambivert')[],
    conflictResolution: 'diplomatic' as const,
    
    // Academic Information
    gpa: 8.5,
    studyIntensive: true,
    
    // Dating & Relationships
    relationshipStatus: 'single' as const,
    datingInRoom: false,
    
    // Special Requirements
    medicalConditions: [],
    specialRequests: ''
  },
  {
    studentId: 'student2',
    semester: 'Fall_2025',
    
    // Sleep Preferences
    sleepSchedule: 'night_owl' as const,
    sleepSensitivity: 'heavy_sleeper' as const,
    
    // Cleanliness & Organization
    cleanliness: 'moderately_clean' as const,
    organizationLevel: 'organized' as const,
    
    // Social Preferences
    socialLevel: 'very_social' as const,
    guestPolicy: 'frequent_guests' as const,
    
    // Study Habits
    studyEnvironment: 'background_noise_ok' as const,
    studySchedule: 'late_night' as const,
    
    // Lifestyle
    smoking: false,
    drinking: true,
    diet: 'non_vegetarian' as const,
    
    // Technology & Entertainment
    musicGenres: ['pop', 'rock', 'electronic'],
    hobbies: ['sports', 'gaming', 'socializing'],
    languages: ['english', 'tamil'],
    
    // Room Environment
    temperaturePreference: 'cold' as const,
    lightPreference: 'bright' as const,
    
    // Personal Traits
    personality: ['extrovert'] as ('introvert' | 'extrovert' | 'ambivert')[],
    conflictResolution: 'direct' as const,
    
    // Academic Information
    gpa: 7.8,
    studyIntensive: false,
    
    // Dating & Relationships
    relationshipStatus: 'dating' as const,
    datingInRoom: false,
    
    // Special Requirements
    medicalConditions: [],
    specialRequests: ''
  },
  {
    studentId: 'student3',
    semester: 'Fall_2025',
    
    // Sleep Preferences
    sleepSchedule: 'flexible' as const,
    sleepSensitivity: 'moderate' as const,
    
    // Cleanliness & Organization
    cleanliness: 'casual' as const,
    organizationLevel: 'somewhat_organized' as const,
    
    // Social Preferences
    socialLevel: 'social' as const,
    guestPolicy: 'rare_guests' as const,
    
    // Study Habits
    studyEnvironment: 'music_ok' as const,
    studySchedule: 'afternoon' as const,
    
    // Lifestyle
    smoking: false,
    drinking: true,
    diet: 'no_preference' as const,
    
    // Technology & Entertainment
    musicGenres: ['hip-hop', 'jazz', 'indie'],
    hobbies: ['photography', 'traveling', 'music'],
    languages: ['english', 'bengali'],
    
    // Room Environment
    temperaturePreference: 'warm' as const,
    lightPreference: 'dim' as const,
    
    // Personal Traits
    personality: ['ambivert'] as ('introvert' | 'extrovert' | 'ambivert')[],
    conflictResolution: 'mediator' as const,
    
    // Academic Information
    gpa: 8.0,
    studyIntensive: true,
    
    // Dating & Relationships
    relationshipStatus: 'committed' as const,
    datingInRoom: true,
    
    // Special Requirements
    medicalConditions: ['asthma'],
    specialRequests: 'Room with good ventilation'
  }
]

// Function to initialize sample data
export async function initializeSampleData() {
  try {
    console.log('🚀 Initializing RoomieMatcher sample data...')
    
    // Add sample rooms
    for (const room of sampleRooms) {
      await roomieMatcherService.addRoom(room)
    }
    console.log('✅ Sample rooms added')
    
    // Add sample students
    for (const student of sampleStudents) {
      await roomieMatcherService.addStudent(student)
    }
    console.log('✅ Sample students added')
    
    // Add sample surveys
    for (const survey of sampleSurveyResponses) {
      await roomieMatcherService.submitSurvey(survey)
    }
    console.log('✅ Sample surveys added')
    
    console.log('🎉 RoomieMatcher sample data initialization complete!')
    
  } catch (error) {
    console.error('❌ Error initializing sample data:', error)
  }
}

// Export sample data for reference
export { sampleRooms, sampleStudents, sampleSurveyResponses }
