'use client'

import { useState, useEffect } from 'react'
import { useAuth } from '@/lib/firebase-context'
import { Users, UserCog, Heart, Brain, Home, Settings } from 'lucide-react'
import { RoomieMatcherAdmin } from './roomie-admin'
import { RoomieMatcherAdminEnhanced } from './roomie-admin-enhanced'
import RoomieUser from './roomie-user'
import { roomieMatcherService } from '@/lib/roomie-service'

type ViewMode = 'user' | 'admin'

export function RoomieMatcher() {
  const { user, isAdmin } = useAuth()
  const [viewMode, setViewMode] = useState<ViewMode>('user')
  const [hasSubmittedSurvey, setHasSubmittedSurvey] = useState(false)
  const [loading, setLoading] = useState(true)
  const [surveyCount, setSurveyCount] = useState(0)
  const [pendingMatches, setPendingMatches] = useState(0)

  useEffect(() => {
    if (user) {
      checkUserStatus()
      loadDashboardStats()
    }
  }, [user])

  const checkUserStatus = async () => {
    if (!user) return
    
    try {
      const survey = await roomieMatcherService.getSurveyByStudent(user.uid, 'Fall_2025')
      setHasSubmittedSurvey(survey !== null)
    } catch (error) {
      console.error('Error checking survey status:', error)
    } finally {
      setLoading(false)
    }
  }

  const loadDashboardStats = async () => {
    try {
      const surveys = await roomieMatcherService.getAllSurveys('Fall_2025')
      setSurveyCount(surveys.length)
      
      if (isAdmin) {
        // Load pending matches count for admin
        const assignments = await roomieMatcherService.getAllAssignments('Fall_2025')
        setPendingMatches(assignments.filter(a => a.status === 'pending').length)
      }
    } catch (error) {
      console.error('Error loading dashboard stats:', error)
    }
  }

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-96">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading RoomieMatcher...</p>
        </div>
      </div>
    )
  }

  if (!user) {
    return (
      <div className="text-center py-12">
        <Heart className="w-16 h-16 text-gray-400 mx-auto mb-4" />
        <h2 className="text-2xl font-bold text-gray-900 mb-2">Welcome to RoomieMatcher</h2>
        <p className="text-gray-600 mb-4">Please sign in to find your perfect roommate match</p>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto p-4">
      {/* Header with Navigation */}
      <div className="mb-8">
        <div className="flex justify-between items-center mb-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-3">
              <Heart className="w-8 h-8 text-pink-500" />
              RoomieMatcher
            </h1>
            <p className="text-gray-600 mt-2">
              Find your perfect roommate with AI-powered compatibility matching
            </p>
          </div>

          {/* View Toggle */}
          {isAdmin && (
            <div className="flex items-center gap-2">
              <button
                onClick={() => setViewMode('user')}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
                  viewMode === 'user'
                    ? 'bg-indigo-600 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                <Users className="w-4 h-4" />
                User View
              </button>
              <button
                onClick={() => setViewMode('admin')}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
                  viewMode === 'admin'
                    ? 'bg-indigo-600 text-white'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                }`}
              >
                <UserCog className="w-4 h-4" />
                Admin Panel
              </button>
            </div>
          )}
        </div>

        {/* Quick Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div className="bg-gradient-to-r from-blue-500 to-blue-600 text-white p-4 rounded-lg">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-100 text-sm">Total Surveys</p>
                <p className="text-2xl font-bold">{surveyCount}</p>
              </div>
              <Users className="w-8 h-8 text-blue-200" />
            </div>
          </div>

          <div className="bg-gradient-to-r from-green-500 to-green-600 text-white p-4 rounded-lg">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-green-100 text-sm">Your Status</p>
                <p className="text-lg font-semibold">
                  {hasSubmittedSurvey ? 'Survey Complete' : 'Pending Survey'}
                </p>
              </div>
              <Heart className="w-8 h-8 text-green-200" />
            </div>
          </div>

          {isAdmin && (
            <div className="bg-gradient-to-r from-purple-500 to-purple-600 text-white p-4 rounded-lg">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-purple-100 text-sm">Pending Matches</p>
                  <p className="text-2xl font-bold">{pendingMatches}</p>
                </div>
                <Brain className="w-8 h-8 text-purple-200" />
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Feature Overview (shown only when user hasn't submitted survey) */}
      {viewMode === 'user' && !hasSubmittedSurvey && (
        <div className="bg-gradient-to-r from-indigo-50 to-purple-50 border border-indigo-200 rounded-lg p-6 mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">How RoomieMatcher Works</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="text-center">
              <div className="bg-indigo-100 rounded-full w-12 h-12 flex items-center justify-center mx-auto mb-3">
                <Users className="w-6 h-6 text-indigo-600" />
              </div>
              <h3 className="font-medium text-gray-900 mb-2">1. Complete Survey</h3>
              <p className="text-sm text-gray-600">
                Share your lifestyle preferences, study habits, and roommate expectations
              </p>
            </div>
            
            <div className="text-center">
              <div className="bg-purple-100 rounded-full w-12 h-12 flex items-center justify-center mx-auto mb-3">
                <Brain className="w-6 h-6 text-purple-600" />
              </div>
              <h3 className="font-medium text-gray-900 mb-2">2. AI Matching</h3>
              <p className="text-sm text-gray-600">
                Our advanced algorithm analyzes compatibility across multiple dimensions
              </p>
            </div>
            
            <div className="text-center">
              <div className="bg-green-100 rounded-full w-12 h-12 flex items-center justify-center mx-auto mb-3">
                <Home className="w-6 h-6 text-green-600" />
              </div>
              <h3 className="font-medium text-gray-900 mb-2">3. Room Assignment</h3>
              <p className="text-sm text-gray-600">
                Get matched with compatible roommates and assigned to suitable rooms
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Main Content */}
      <div className="bg-white rounded-lg shadow-sm">
        {viewMode === 'admin' ? (
          <RoomieMatcherAdminEnhanced />
        ) : (
          <RoomieUser />
        )}
      </div>

      {/* Footer */}
      <div className="mt-8 text-center text-sm text-gray-500">
        <p>
          RoomieMatcher uses advanced compatibility algorithms to ensure the best possible roommate matches.
          {isAdmin && ' Admin features include real-time analytics and room assignment management.'}
        </p>
      </div>
    </div>
  )
}
