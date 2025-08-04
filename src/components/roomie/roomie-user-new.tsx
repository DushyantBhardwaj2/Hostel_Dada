'use client'

import { useState, useEffect } from 'react'
import { useAuth } from '@/lib/firebase-context'
import { roomieMatcherService } from '@/lib/roomie-service'
import { Student } from '@/lib/roomie-models'
import { IndianHostelSurvey, IndianHostelQuestions, RoommateGraphMatcher } from '@/lib/roomie-indian-questions'
import { Users, Heart, Home, BookOpen, Moon, Sparkles, AlertCircle, MapPin, Clock, Home as HomeIcon } from 'lucide-react'

interface IndianSurveyFormData {
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
  
  // Room Number Preference (NEW!)
  preferredRoomNumbers: string[]
  preferredFloor: 'ground_floor' | 'first_floor' | 'second_floor' | 'third_floor' | 'any_floor'
}

function IndianSurveyForm({ 
  onSubmit, 
  existingSurvey, 
  isLoading 
}: { 
  onSubmit: (data: IndianSurveyFormData) => void
  existingSurvey?: IndianHostelSurvey | null
  isLoading: boolean 
}) {
  const [formData, setFormData] = useState<IndianSurveyFormData>({
    region: 'north_india',
    generalMood: 'smart_fun',
    sleepTime: 'moderate_12am',
    studyPattern: 'last_minute_warrior',
    roomCleanliness: 'manageable_mess',
    socialLevel: 'circle_friends',
    smokingHabits: 'never_hate_it',
    drinkingHabits: 'occasional_party',
    fitnessLevel: 'mood_dependent',
    musicInRoom: 'headphones_always',
    noiseTolerance: 'ask_first_ok',
    gamingLevel: 'mobile_casual',
    weekendStyle: 'roam_with_friends',
    sharingAttitude: 'ask_first_ok',
    guestPolicy: 'friends_limit',
    nightLights: 'dim_night_bulb',
    wakeUpTime: 'normal_7_9am',
    foodPreference: 'anything_available',
    conflictStyle: 'talk_solve',
    hostelLifeGoal: 'study_fun_both',
    preferredRoomNumbers: [],
    preferredFloor: 'any_floor'
  })

  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0)
  const [showRoomSelection, setShowRoomSelection] = useState(false)

  useEffect(() => {
    if (existingSurvey) {
      setFormData({
        region: existingSurvey.region,
        generalMood: existingSurvey.generalMood,
        sleepTime: existingSurvey.sleepTime,
        studyPattern: existingSurvey.studyPattern,
        roomCleanliness: existingSurvey.roomCleanliness,
        socialLevel: existingSurvey.socialLevel,
        smokingHabits: existingSurvey.smokingHabits,
        drinkingHabits: existingSurvey.drinkingHabits,
        fitnessLevel: existingSurvey.fitnessLevel,
        musicInRoom: existingSurvey.musicInRoom,
        noiseTolerance: existingSurvey.noiseTolerance,
        gamingLevel: existingSurvey.gamingLevel,
        weekendStyle: existingSurvey.weekendStyle,
        sharingAttitude: existingSurvey.sharingAttitude,
        guestPolicy: existingSurvey.guestPolicy,
        nightLights: existingSurvey.nightLights,
        wakeUpTime: existingSurvey.wakeUpTime,
        foodPreference: existingSurvey.foodPreference,
        conflictStyle: existingSurvey.conflictStyle,
        hostelLifeGoal: existingSurvey.hostelLifeGoal,
        preferredRoomNumbers: existingSurvey.preferredRoomNumbers,
        preferredFloor: existingSurvey.preferredFloor
      })
    }
  }, [existingSurvey])

  const updateFormData = (field: keyof IndianSurveyFormData, value: any) => {
    setFormData((prev: IndianSurveyFormData) => ({ ...prev, [field]: value }))
  }

  const handleRoomNumberToggle = (roomNumber: string) => {
    const current = formData.preferredRoomNumbers
    if (current.includes(roomNumber)) {
      updateFormData('preferredRoomNumbers', current.filter(r => r !== roomNumber))
    } else {
      updateFormData('preferredRoomNumbers', [...current, roomNumber])
    }
  }

  const currentQuestion = IndianHostelQuestions[currentQuestionIndex]
  const isLastQuestion = currentQuestionIndex === IndianHostelQuestions.length - 1

  const handleNext = () => {
    if (isLastQuestion) {
      setShowRoomSelection(true)
    } else {
      setCurrentQuestionIndex(prev => prev + 1)
    }
  }

  const handlePrevious = () => {
    if (showRoomSelection) {
      setShowRoomSelection(false)
    } else if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(prev => prev - 1)
    }
  }

  const handleSubmit = () => {
    onSubmit(formData)
  }

  const availableRooms = [
    '101', '102', '103', '104', '105', '106', '107', '108', '109', '110',
    '201', '202', '203', '204', '205', '206', '207', '208', '209', '210',
    '301', '302', '303', '304', '305', '306', '307', '308', '309', '310'
  ]

  if (showRoomSelection) {
    return (
      <div className="space-y-6">
        <div className="bg-gradient-to-r from-blue-50 to-indigo-50 border border-blue-200 rounded-xl p-6">
          <div className="flex items-center gap-3 mb-4">
            <div className="bg-blue-100 p-2 rounded-lg">
              <HomeIcon className="h-6 w-6 text-blue-600" />
            </div>
            <div>
              <h3 className="text-xl font-bold text-gray-900">Room Preference Selection</h3>
              <p className="text-gray-600">Choose your preferred rooms and floor</p>
            </div>
          </div>
        </div>

        <div className="space-y-6">
          {/* Floor Preference */}
          <div className="bg-white border border-gray-200 rounded-xl p-6">
            <h4 className="text-lg font-semibold text-gray-900 mb-4">Preferred Floor</h4>
            <div className="grid grid-cols-2 md:grid-cols-5 gap-3">
              {[
                { value: 'ground_floor', label: 'Ground Floor' },
                { value: 'first_floor', label: '1st Floor' },
                { value: 'second_floor', label: '2nd Floor' },
                { value: 'third_floor', label: '3rd Floor' },
                { value: 'any_floor', label: 'Any Floor' }
              ].map((floor) => (
                <button
                  key={floor.value}
                  onClick={() => updateFormData('preferredFloor', floor.value)}
                  className={`p-3 rounded-lg border text-center transition-all ${
                    formData.preferredFloor === floor.value
                      ? 'bg-blue-500 text-white border-blue-500'
                      : 'bg-white text-gray-700 border-gray-200 hover:border-blue-300'
                  }`}
                >
                  {floor.label}
                </button>
              ))}
            </div>
          </div>

          {/* Room Number Selection */}
          <div className="bg-white border border-gray-200 rounded-xl p-6">
            <h4 className="text-lg font-semibold text-gray-900 mb-4">
              Preferred Room Numbers 
              <span className="text-sm text-gray-500 ml-2">(Select multiple if you have preferences)</span>
            </h4>
            <div className="grid grid-cols-5 md:grid-cols-10 gap-2">
              {availableRooms.map((room) => (
                <button
                  key={room}
                  onClick={() => handleRoomNumberToggle(room)}
                  className={`p-3 rounded-lg border text-center transition-all ${
                    formData.preferredRoomNumbers.includes(room)
                      ? 'bg-green-500 text-white border-green-500'
                      : 'bg-white text-gray-700 border-gray-200 hover:border-green-300'
                  }`}
                >
                  {room}
                </button>
              ))}
            </div>
            {formData.preferredRoomNumbers.length > 0 && (
              <div className="mt-4 p-3 bg-green-50 border border-green-200 rounded-lg">
                <p className="text-green-800">
                  <span className="font-medium">Selected rooms:</span> {formData.preferredRoomNumbers.join(', ')}
                </p>
              </div>
            )}
          </div>

          {/* Navigation */}
          <div className="flex justify-between items-center pt-6">
            <button
              onClick={handlePrevious}
              className="px-6 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              ← Back to Questions
            </button>
            <button
              onClick={handleSubmit}
              disabled={isLoading}
              className="px-8 py-3 bg-gradient-to-r from-green-600 to-blue-600 text-white rounded-lg hover:from-green-700 hover:to-blue-700 transition-all disabled:opacity-50"
            >
              {isLoading ? 'Submitting...' : 'Complete Survey 🎉'}
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Progress Bar */}
      <div className="bg-white border border-gray-200 rounded-xl p-6">
        <div className="flex justify-between items-center mb-4">
          <span className="text-sm font-medium text-gray-600">
            Question {currentQuestionIndex + 1} of {IndianHostelQuestions.length}
          </span>
          <span className="text-sm font-medium text-blue-600">
            {Math.round(((currentQuestionIndex + 1) / IndianHostelQuestions.length) * 100)}% Complete
          </span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div 
            className="bg-gradient-to-r from-blue-500 to-green-500 h-2 rounded-full transition-all duration-300"
            style={{ width: `${((currentQuestionIndex + 1) / IndianHostelQuestions.length) * 100}%` }}
          ></div>
        </div>
      </div>

      {/* Current Question */}
      <div className="bg-gradient-to-r from-orange-50 to-red-50 border border-orange-200 rounded-xl p-8">
        <div className="text-center mb-6">
          <div className="bg-orange-100 p-3 rounded-full w-16 h-16 mx-auto mb-4 flex items-center justify-center">
            <span className="text-2xl">🤔</span>
          </div>
          <div className="text-sm font-medium text-orange-600 mb-2">
            {currentQuestion.category}
          </div>
          <h3 className="text-2xl font-bold text-gray-900 mb-2">
            {currentQuestion.question}
          </h3>
        </div>

        <div className="space-y-3">
          {currentQuestion.options.map((option, index) => (
            <button
              key={option.value}
              onClick={() => updateFormData(currentQuestion.id as keyof IndianSurveyFormData, option.value)}
              className={`w-full p-4 text-left rounded-xl border transition-all hover:scale-[1.02] ${
                formData[currentQuestion.id as keyof IndianSurveyFormData] === option.value
                  ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white border-blue-500 shadow-lg'
                  : 'bg-white text-gray-700 border-gray-200 hover:border-blue-300 hover:shadow-md'
              }`}
            >
              <div className="flex items-center gap-4">
                <div className={`w-8 h-8 rounded-full border-2 flex items-center justify-center text-sm font-bold ${
                  formData[currentQuestion.id as keyof IndianSurveyFormData] === option.value
                    ? 'border-white text-white'
                    : 'border-gray-300 text-gray-500'
                }`}>
                  {String.fromCharCode(97 + index)}
                </div>
                <span className="font-medium">{option.label}</span>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Navigation */}
      <div className="flex justify-between items-center">
        <button
          onClick={handlePrevious}
          disabled={currentQuestionIndex === 0}
          className="px-6 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
        >
          ← Previous
        </button>
        
        <div className="text-center">
          <div className="text-sm text-gray-500">
            {IndianHostelQuestions.length - currentQuestionIndex - 1} questions left
          </div>
        </div>

        <button
          onClick={handleNext}
          disabled={!formData[currentQuestion.id as keyof IndianSurveyFormData]}
          className="px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-lg hover:from-blue-700 hover:to-purple-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {isLastQuestion ? 'Choose Rooms →' : 'Next →'}
        </button>
      </div>
    </div>
  )
}

export default function RoomieUser() {
  const { user } = useAuth()
  const [currentStudent, setCurrentStudent] = useState<Student | null>(null)
  const [existingSurvey, setExistingSurvey] = useState<IndianHostelSurvey | null>(null)
  const [matches, setMatches] = useState<Array<{ user: IndianHostelSurvey; compatibility: number; reasons: string[] }>>([])
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [showMatches, setShowMatches] = useState(false)

  useEffect(() => {
    const loadData = async () => {
      if (!user?.uid) return

      try {
        // Load student profile
        const student = await roomieMatcherService.getStudentById(user.uid)
        if (student) {
          setCurrentStudent(student)
        }

        // Load existing survey (we'll need to update this service)
        // For now, we'll create a placeholder
        setExistingSurvey(null)
      } catch (error) {
        console.error('Error loading data:', error)
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [user])

  const handleSurveySubmit = async (formData: IndianSurveyFormData) => {
    if (!user?.uid || !currentStudent) return

    setSubmitting(true)
    try {
      // Create survey object
      const survey: IndianHostelSurvey = {
        id: `${user.uid}_${Date.now()}`,
        studentId: user.uid,
        semester: 'Fall_2025', // Default semester
        ...formData,
        submittedAt: new Date()
      }

      // TODO: Update service to handle Indian survey
      console.log('Indian Survey submitted:', survey)
      
      // For now, generate some mock matches based on the user's answers
      const mockMatches = [
        {
          user: { 
            ...survey, 
            id: 'mock1', 
            studentId: 'Student A',
            region: formData.region,
            generalMood: formData.generalMood,
            sleepTime: formData.sleepTime
          } as IndianHostelSurvey,
          compatibility: 87,
          reasons: [
            '🌙 Both are night owls (2 AM+)', 
            '👥 Both prefer close friend circles', 
            '📚 Both are last-minute study warriors',
            '🗺️ Same regional background'
          ]
        },
        {
          user: { 
            ...survey, 
            id: 'mock2', 
            studentId: 'Student B',
            generalMood: formData.generalMood === 'smart_fun' ? 'smart_fun' : 'passive_chill',
            roomCleanliness: formData.roomCleanliness,
            socialLevel: formData.socialLevel
          } as IndianHostelSurvey,
          compatibility: 79,
          reasons: [
            '🧹 Both do weekly room cleaning', 
            '💪 Similar fitness routines', 
            '🍺 Compatible lifestyle choices',
            '🎮 Similar gaming preferences'
          ]
        },
        {
          user: { 
            ...survey, 
            id: 'mock3', 
            studentId: 'Student C',
            musicInRoom: formData.musicInRoom,
            noiseTolerance: formData.noiseTolerance,
            studyPattern: formData.studyPattern
          } as IndianHostelSurvey,
          compatibility: 73,
          reasons: [
            '🎵 Compatible music preferences', 
            '📖 Similar study patterns', 
            '😴 Compatible sleep schedules',
            '🤝 Good conflict resolution match'
          ]
        }
      ]
      
      setMatches(mockMatches)
      setExistingSurvey(survey)
      setShowMatches(true)
    } catch (error) {
      console.error('Error submitting survey:', error)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto p-6">
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading your profile...</p>
        </div>
      </div>
    )
  }

  if (!user) {
    return (
      <div className="max-w-4xl mx-auto p-6">
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6 text-center">
          <AlertCircle className="w-12 h-12 text-yellow-600 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-yellow-800 mb-2">Authentication Required</h3>
          <p className="text-yellow-700">Please log in to access the Roommate Matcher.</p>
        </div>
      </div>
    )
  }

  if (showMatches && matches.length > 0) {
    return (
      <div className="max-w-6xl mx-auto p-6 space-y-6">
        <div className="bg-gradient-to-r from-green-50 to-blue-50 border border-green-200 rounded-xl p-6">
          <div className="text-center">
            <div className="bg-green-100 p-3 rounded-full w-16 h-16 mx-auto mb-4 flex items-center justify-center">
              <Heart className="h-8 w-8 text-green-600" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">Your Roommate Matches! 🎉</h2>
            <p className="text-gray-600">Based on your Indian hostel lifestyle preferences</p>
          </div>
        </div>

        <div className="grid gap-6">
          {matches.map((match, index) => (
            <div key={match.user.id} className="bg-white border border-gray-200 rounded-xl p-6 hover:shadow-lg transition-shadow">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-4">
                  <div className="bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-full w-12 h-12 flex items-center justify-center font-bold">
                    #{index + 1}
                  </div>
                  <div>
                    <h3 className="text-xl font-bold text-gray-900">{match.user.studentId}</h3>
                    <p className="text-gray-600">Compatibility Score</p>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-3xl font-bold text-green-600">{match.compatibility}%</div>
                  <div className="text-sm text-gray-500">Match Score</div>
                </div>
              </div>

              <div className="space-y-3">
                <h4 className="font-semibold text-gray-900">Why you're compatible:</h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                  {match.reasons.map((reason, idx) => (
                    <div key={idx} className="bg-blue-50 border border-blue-200 rounded-lg p-3">
                      <span className="text-blue-800 text-sm">{reason}</span>
                    </div>
                  ))}
                </div>
              </div>

              <div className="mt-4 pt-4 border-t border-gray-200">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                  <div>
                    <span className="text-gray-500">Region:</span>
                    <p className="font-medium capitalize">{match.user.region?.replace('_', ' ')}</p>
                  </div>
                  <div>
                    <span className="text-gray-500">Sleep Time:</span>
                    <p className="font-medium capitalize">{match.user.sleepTime?.replace('_', ' ')}</p>
                  </div>
                  <div>
                    <span className="text-gray-500">Social Level:</span>
                    <p className="font-medium capitalize">{match.user.socialLevel?.replace('_', ' ')}</p>
                  </div>
                  <div>
                    <span className="text-gray-500">Study Pattern:</span>
                    <p className="font-medium capitalize">{match.user.studyPattern?.replace('_', ' ')}</p>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="text-center">
          <button
            onClick={() => setShowMatches(false)}
            className="px-6 py-3 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-lg hover:from-blue-700 hover:to-purple-700 transition-all"
          >
            Take Survey Again
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto p-6 space-y-8">
      {/* Header */}
      <div className="bg-gradient-to-r from-indigo-50 to-purple-50 border border-indigo-200 rounded-xl p-8 text-center">
        <div className="bg-indigo-100 p-3 rounded-full w-16 h-16 mx-auto mb-4 flex items-center justify-center">
          <Users className="h-8 w-8 text-indigo-600" />
        </div>
        <h1 className="text-3xl font-bold text-gray-900 mb-4">
          Indian Hostel Roommate Matcher 🏠
        </h1>
        <p className="text-lg text-gray-700 mb-2">
          Find your perfect roommate using desi hostel life questions!
        </p>
        <p className="text-gray-600">
          Answer honestly about your lifestyle, habits, and preferences
        </p>
      </div>

      {/* Survey Form */}
      <IndianSurveyForm
        onSubmit={handleSurveySubmit}
        existingSurvey={existingSurvey}
        isLoading={submitting}
      />
    </div>
  )
}
