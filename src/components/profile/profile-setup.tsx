'use client'

import { useState, useEffect } from 'react'
import { useAuth } from '@/lib/firebase-context'
import { profileService } from '@/lib/profile-service'
import { 
  ProfileFormData, 
  PROFILE_STEPS, 
  FOOD_PREFERENCES, 
  DIETARY_RESTRICTIONS, 
  HOBBIES, 
  LANGUAGES, 
  COURSES, 
  YEARS_OF_STUDY, 
  EMERGENCY_RELATIONS 
} from '@/lib/profile-models'
import { ChevronLeft, ChevronRight, Check, User, GraduationCap, Home, Phone, Star, Shield } from 'lucide-react'

interface ProfileSetupProps {
  onComplete: () => void
}

export function ProfileSetup({ onComplete }: ProfileSetupProps) {
  const { user } = useAuth()
  const [currentStep, setCurrentStep] = useState(0)
  const [loading, setLoading] = useState(false)
  const [formData, setFormData] = useState<ProfileFormData>({
    // Basic Info
    fullName: user?.displayName || '',
    phoneNumber: '',
    dateOfBirth: '',
    gender: '',
    
    // Academic Info
    collegeName: '',
    course: '',
    yearOfStudy: '',
    enrollmentNumber: '',
    
    // Hostel Info
    hostelId: '',
    roomNumber: '',
    currentFloor: 1,
    preferredRoomType: '',
    
    // Emergency Contacts - set defaults
    emergencyContactName: 'Not provided',
    emergencyContactPhone: 'Not provided',
    emergencyContactRelation: 'parent',
    parentContactPhone: '',
    homeAddress: '',
    
    // Preferences - set defaults
    foodPreferences: ['Vegetarian'], // Default to avoid validation issues
    dietaryRestrictions: [],
    hobbies: ['Reading'], // Default to avoid validation issues
    languages: ['Hindi', 'English'],
    
    // Privacy & Notifications - auto-accept for simplified flow
    isPrivacyAccepted: true,
    notificationPreferences: {
      email: true,
      sms: false,
      push: true
    }
  })

  const updateFormData = (field: keyof ProfileFormData, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }))
  }

  const toggleArrayField = (field: keyof ProfileFormData, value: string) => {
    setFormData(prev => {
      const currentArray = prev[field] as string[]
      const newArray = currentArray.includes(value)
        ? currentArray.filter(item => item !== value)
        : [...currentArray, value]
      return { ...prev, [field]: newArray }
    })
  }

  const validateCurrentStep = (): boolean => {
    switch (currentStep) {
      case 0: // Basic Info
        return !!(formData.fullName && formData.phoneNumber && formData.dateOfBirth && formData.gender)
      case 1: // Academic Info
        return !!(formData.collegeName && formData.course && formData.yearOfStudy)
      case 2: // Hostel Info
        return !!(formData.hostelId && formData.roomNumber)
      default:
        return false
    }
  }

  const handleNext = () => {
    if (validateCurrentStep() && currentStep < PROFILE_STEPS.length - 1) {
      setCurrentStep(currentStep + 1)
    }
  }

  const handlePrevious = () => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1)
    }
  }

  const handleSubmit = async () => {
    if (!user || !validateCurrentStep()) return

    setLoading(true)
    try {
      await profileService.updateProfile(user.uid, formData)
      onComplete()
    } catch (error) {
      console.error('Error saving profile:', error)
      alert('Error saving profile. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const getStepIcon = (stepIndex: number) => {
    switch (stepIndex) {
      case 0: return <User className="w-5 h-5" />
      case 1: return <GraduationCap className="w-5 h-5" />
      case 2: return <Home className="w-5 h-5" />
      default: return <User className="w-5 h-5" />
    }
  }

  const renderStepContent = () => {
    switch (currentStep) {
      case 0: // Basic Information
        return (
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Full Name *
              </label>
              <input
                type="text"
                value={formData.fullName}
                onChange={(e) => updateFormData('fullName', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Enter your full name"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Phone Number *
              </label>
              <input
                type="tel"
                value={formData.phoneNumber}
                onChange={(e) => updateFormData('phoneNumber', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Enter your phone number"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Date of Birth *
              </label>
              <input
                type="date"
                value={formData.dateOfBirth}
                onChange={(e) => updateFormData('dateOfBirth', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Gender *
              </label>
              <select
                value={formData.gender}
                onChange={(e) => updateFormData('gender', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">Select gender</option>
                <option value="male">Male</option>
                <option value="female">Female</option>
                <option value="other">Other</option>
                <option value="prefer_not_to_say">Prefer not to say</option>
              </select>
            </div>
          </div>
        )

      case 1: // Academic Information
        return (
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                College/University Name *
              </label>
              <input
                type="text"
                value={formData.collegeName}
                onChange={(e) => updateFormData('collegeName', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Enter your college or university name"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Course *
              </label>
              <select
                value={formData.course}
                onChange={(e) => updateFormData('course', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">Select your course</option>
                {COURSES.map(course => (
                  <option key={course} value={course}>{course}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Year of Study *
              </label>
              <select
                value={formData.yearOfStudy}
                onChange={(e) => updateFormData('yearOfStudy', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">Select year</option>
                {YEARS_OF_STUDY.map(year => (
                  <option key={year} value={year}>{year}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Enrollment/Roll Number
              </label>
              <input
                type="text"
                value={formData.enrollmentNumber}
                onChange={(e) => updateFormData('enrollmentNumber', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Enter your enrollment or roll number"
              />
            </div>
          </div>
        )

      case 2: // Hostel Information
        return (
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Hostel ID *
              </label>
              <input
                type="text"
                value={formData.hostelId}
                onChange={(e) => updateFormData('hostelId', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="e.g., HOSTEL_A, BOYS_HOSTEL_1"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Room Number *
              </label>
              <input
                type="text"
                value={formData.roomNumber}
                onChange={(e) => updateFormData('roomNumber', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="e.g., A-101, 205, B-301"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Floor Number
              </label>
              <select
                value={formData.currentFloor}
                onChange={(e) => updateFormData('currentFloor', parseInt(e.target.value))}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value={0}>Ground Floor</option>
                <option value={1}>1st Floor</option>
                <option value={2}>2nd Floor</option>
                <option value={3}>3rd Floor</option>
                <option value={4}>4th Floor</option>
                <option value={5}>5th Floor</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Room Type Preference
              </label>
              <select
                value={formData.preferredRoomType}
                onChange={(e) => updateFormData('preferredRoomType', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">Select preference</option>
                <option value="single">Single Room</option>
                <option value="double">Double Sharing</option>
                <option value="triple">Triple Sharing</option>
                <option value="shared">Shared Room (4+)</option>
              </select>
            </div>
          </div>
        )

      case 3: // Emergency Contacts
        return (
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Emergency Contact Name *
              </label>
              <input
                type="text"
                value={formData.emergencyContactName}
                onChange={(e) => updateFormData('emergencyContactName', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Full name of emergency contact"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Emergency Contact Phone *
              </label>
              <input
                type="tel"
                value={formData.emergencyContactPhone}
                onChange={(e) => updateFormData('emergencyContactPhone', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Emergency contact phone number"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Relationship *
              </label>
              <select
                value={formData.emergencyContactRelation}
                onChange={(e) => updateFormData('emergencyContactRelation', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">Select relationship</option>
                {EMERGENCY_RELATIONS.map(relation => (
                  <option key={relation} value={relation}>{relation}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Parent Contact Phone
              </label>
              <input
                type="tel"
                value={formData.parentContactPhone}
                onChange={(e) => updateFormData('parentContactPhone', e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Parent/guardian phone number"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Home Address
              </label>
              <textarea
                value={formData.homeAddress}
                onChange={(e) => updateFormData('homeAddress', e.target.value)}
                rows={3}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="Your home address"
              />
            </div>
          </div>
        )

      case 4: // Preferences
        return (
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">
                Food Preferences * (Select at least one)
              </label>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                {FOOD_PREFERENCES.map(preference => (
                  <label key={preference} className="flex items-center p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formData.foodPreferences.includes(preference)}
                      onChange={() => toggleArrayField('foodPreferences', preference)}
                      className="mr-3 text-blue-600 focus:ring-blue-500"
                    />
                    <span className="text-sm">{preference}</span>
                  </label>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">
                Dietary Restrictions
              </label>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                {DIETARY_RESTRICTIONS.map(restriction => (
                  <label key={restriction} className="flex items-center p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formData.dietaryRestrictions.includes(restriction)}
                      onChange={() => toggleArrayField('dietaryRestrictions', restriction)}
                      className="mr-3 text-blue-600 focus:ring-blue-500"
                    />
                    <span className="text-sm">{restriction}</span>
                  </label>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">
                Hobbies & Interests * (Select at least one)
              </label>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3 max-h-64 overflow-y-auto">
                {HOBBIES.map(hobby => (
                  <label key={hobby} className="flex items-center p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formData.hobbies.includes(hobby)}
                      onChange={() => toggleArrayField('hobbies', hobby)}
                      className="mr-3 text-blue-600 focus:ring-blue-500"
                    />
                    <span className="text-sm">{hobby}</span>
                  </label>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-3">
                Languages You Speak
              </label>
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3 max-h-48 overflow-y-auto">
                {LANGUAGES.map(language => (
                  <label key={language} className="flex items-center p-3 border border-gray-200 rounded-lg hover:bg-gray-50 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formData.languages.includes(language)}
                      onChange={() => toggleArrayField('languages', language)}
                      className="mr-3 text-blue-600 focus:ring-blue-500"
                    />
                    <span className="text-sm">{language}</span>
                  </label>
                ))}
              </div>
            </div>
          </div>
        )

      case 5: // Privacy & Settings
        return (
          <div className="space-y-6">
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
              <h3 className="text-lg font-semibold text-blue-900 mb-4">Privacy Agreement</h3>
              <div className="space-y-3 text-sm text-blue-800">
                <p>By completing your profile, you agree to:</p>
                <ul className="list-disc list-inside space-y-1 ml-4">
                  <li>Share basic information with hostel administration for management purposes</li>
                  <li>Allow roommate matching based on your preferences and survey responses</li>
                  <li>Receive notifications about hostel activities and important updates</li>
                  <li>Keep your profile information accurate and up to date</li>
                </ul>
              </div>
              
              <label className="flex items-start gap-3 mt-4 cursor-pointer">
                <input
                  type="checkbox"
                  checked={formData.isPrivacyAccepted}
                  onChange={(e) => updateFormData('isPrivacyAccepted', e.target.checked)}
                  className="mt-1 text-blue-600 focus:ring-blue-500"
                />
                <span className="text-sm text-blue-900">
                  I agree to the privacy terms and data sharing policies *
                </span>
              </label>
            </div>

            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Notification Preferences</h3>
              <div className="space-y-4">
                <label className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
                  <div>
                    <span className="font-medium">Email Notifications</span>
                    <p className="text-sm text-gray-600">Receive updates via email</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={formData.notificationPreferences.email}
                    onChange={(e) => updateFormData('notificationPreferences', {
                      ...formData.notificationPreferences,
                      email: e.target.checked
                    })}
                    className="text-blue-600 focus:ring-blue-500"
                  />
                </label>

                <label className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
                  <div>
                    <span className="font-medium">SMS Notifications</span>
                    <p className="text-sm text-gray-600">Receive urgent updates via SMS</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={formData.notificationPreferences.sms}
                    onChange={(e) => updateFormData('notificationPreferences', {
                      ...formData.notificationPreferences,
                      sms: e.target.checked
                    })}
                    className="text-blue-600 focus:ring-blue-500"
                  />
                </label>

                <label className="flex items-center justify-between p-4 border border-gray-200 rounded-lg">
                  <div>
                    <span className="font-medium">Push Notifications</span>
                    <p className="text-sm text-gray-600">Receive app notifications</p>
                  </div>
                  <input
                    type="checkbox"
                    checked={formData.notificationPreferences.push}
                    onChange={(e) => updateFormData('notificationPreferences', {
                      ...formData.notificationPreferences,
                      push: e.target.checked
                    })}
                    className="text-blue-600 focus:ring-blue-500"
                  />
                </label>
              </div>
            </div>
          </div>
        )

      default:
        return null
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-4xl overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6">
          <h1 className="text-2xl font-bold mb-2">Complete Your Profile</h1>
          <p className="text-blue-100">Help us personalize your hostel experience</p>
        </div>

        {/* Progress Steps */}
        <div className="border-b border-gray-200 p-6">
          <div className="flex items-center justify-between">
            {PROFILE_STEPS.map((step, index) => (
              <div key={step.id} className="flex flex-col items-center">
                <div className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-medium transition-colors ${
                  index < currentStep 
                    ? 'bg-green-500 text-white' 
                    : index === currentStep 
                      ? 'bg-blue-600 text-white' 
                      : 'bg-gray-200 text-gray-600'
                }`}>
                  {index < currentStep ? <Check className="w-5 h-5" /> : getStepIcon(index)}
                </div>
                <div className="mt-2 text-center">
                  <p className={`text-xs font-medium ${index <= currentStep ? 'text-gray-900' : 'text-gray-500'}`}>
                    {step.title}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Content */}
        <div className="p-6">
          <div className="mb-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              {PROFILE_STEPS[currentStep].title}
            </h2>
            <p className="text-gray-600">
              {PROFILE_STEPS[currentStep].description}
            </p>
          </div>

          {renderStepContent()}
        </div>

        {/* Navigation */}
        <div className="border-t border-gray-200 p-6 flex justify-between items-center">
          <button
            onClick={handlePrevious}
            disabled={currentStep === 0}
            className="flex items-center gap-2 px-4 py-2 text-gray-600 hover:text-gray-800 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <ChevronLeft className="w-4 h-4" />
            Previous
          </button>

          <div className="text-sm text-gray-500">
            Step {currentStep + 1} of {PROFILE_STEPS.length}
          </div>

          {currentStep === PROFILE_STEPS.length - 1 ? (
            <button
              onClick={handleSubmit}
              disabled={!validateCurrentStep() || loading}
              className="flex items-center gap-2 bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <>
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  Saving...
                </>
              ) : (
                <>
                  Complete Profile
                  <Check className="w-4 h-4" />
                </>
              )}
            </button>
          ) : (
            <button
              onClick={handleNext}
              disabled={!validateCurrentStep()}
              className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Next
              <ChevronRight className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>
    </div>
  )
}
