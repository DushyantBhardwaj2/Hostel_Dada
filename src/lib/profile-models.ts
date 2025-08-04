// Profile Models and Types

export interface UserProfile {
  uid: string
  email: string
  
  // Basic Information
  fullName: string
  phoneNumber: string
  dateOfBirth?: string
  gender?: 'male' | 'female' | 'other' | 'prefer_not_to_say'
  
  // Academic Information
  collegeName?: string
  course?: string
  yearOfStudy?: string
  enrollmentNumber?: string
  
  // Hostel Information
  hostelId: string
  roomNumber: string
  currentFloor?: number
  preferredRoomType?: 'single' | 'double' | 'triple' | 'shared'
  
  // Contact Information
  emergencyContactName?: string
  emergencyContactPhone?: string
  emergencyContactRelation?: string
  parentContactPhone?: string
  homeAddress?: string
  
  // Preferences
  foodPreferences?: string[]
  dietaryRestrictions?: string[]
  hobbies?: string[]
  languages?: string[]
  
  // Profile Settings
  profilePictureUrl?: string
  isProfileComplete: boolean
  isPrivacyAccepted: boolean
  notificationPreferences?: {
    email: boolean
    sms: boolean
    push: boolean
  }
  
  // Metadata
  createdAt: Date
  updatedAt: Date
  lastLoginAt?: Date
}

export interface ProfileFormData {
  // Step 1: Basic Info
  fullName: string
  phoneNumber: string
  dateOfBirth: string
  gender: string
  
  // Step 2: Academic Info
  collegeName: string
  course: string
  yearOfStudy: string
  enrollmentNumber: string
  
  // Step 3: Hostel Info
  hostelId: string
  roomNumber: string
  currentFloor: number
  preferredRoomType: string
  
  // Step 4: Emergency Contacts
  emergencyContactName: string
  emergencyContactPhone: string
  emergencyContactRelation: string
  parentContactPhone: string
  homeAddress: string
  
  // Step 5: Preferences
  foodPreferences: string[]
  dietaryRestrictions: string[]
  hobbies: string[]
  languages: string[]
  
  // Step 6: Privacy & Notifications
  isPrivacyAccepted: boolean
  notificationPreferences: {
    email: boolean
    sms: boolean
    push: boolean
  }
}

export const PROFILE_STEPS = [
  {
    id: 'basic',
    title: 'Basic Information',
    description: 'Tell us about yourself',
    icon: '👤'
  },
  {
    id: 'academic',
    title: 'Academic Details',
    description: 'Your college and course information',
    icon: '🎓'
  },
  {
    id: 'hostel',
    title: 'Hostel Information',
    description: 'Your accommodation details',
    icon: '🏠'
  }
] as const

export const FOOD_PREFERENCES = [
  'Vegetarian',
  'Non-Vegetarian', 
  'Vegan',
  'Jain Food',
  'North Indian',
  'South Indian',
  'Continental',
  'Chinese',
  'Italian',
  'Fast Food',
  'Street Food',
  'Healthy Food',
  'Spicy Food',
  'Sweet Tooth'
]

export const DIETARY_RESTRICTIONS = [
  'No Restrictions',
  'Lactose Intolerant',
  'Gluten Free',
  'Nut Allergies',
  'Diabetic Friendly',
  'Low Sodium',
  'No Onion/Garlic',
  'Halal',
  'Kosher'
]

export const HOBBIES = [
  'Reading',
  'Gaming',
  'Sports',
  'Music',
  'Dance',
  'Cooking',
  'Photography',
  'Traveling',
  'Movies/TV',
  'Art/Painting',
  'Writing',
  'Coding',
  'Fitness',
  'Yoga',
  'Swimming',
  'Cricket',
  'Football',
  'Basketball',
  'Badminton',
  'Chess',
  'Singing',
  'Guitar',
  'Piano'
]

export const LANGUAGES = [
  'Hindi',
  'English',
  'Tamil',
  'Telugu',
  'Marathi',
  'Bengali',
  'Gujarati',
  'Kannada',
  'Malayalam',
  'Punjabi',
  'Odia',
  'Assamese',
  'Urdu',
  'Sanskrit',
  'French',
  'German',
  'Spanish',
  'Chinese',
  'Japanese',
  'Korean'
]

export const COURSES = [
  'B.Tech Computer Science',
  'B.Tech Electronics',
  'B.Tech Mechanical', 
  'B.Tech Civil',
  'B.Tech Chemical',
  'B.Tech Electrical',
  'B.Sc Computer Science',
  'B.Sc Physics',
  'B.Sc Chemistry',
  'B.Sc Mathematics',
  'B.Com',
  'BBA',
  'BA English',
  'BA Economics',
  'BCA',
  'M.Tech',
  'M.Sc',
  'MBA',
  'MA',
  'MCA',
  'PhD'
]

export const YEARS_OF_STUDY = [
  '1st Year',
  '2nd Year', 
  '3rd Year',
  '4th Year',
  '5th Year',
  'Final Year',
  'Post Graduate'
]

export const EMERGENCY_RELATIONS = [
  'Father',
  'Mother',
  'Guardian',
  'Brother',
  'Sister',
  'Uncle',
  'Aunt',
  'Grandparent',
  'Spouse',
  'Friend',
  'Other'
]
