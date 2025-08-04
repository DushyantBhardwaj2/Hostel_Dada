'use client'

import { useState, useEffect } from 'react'
import { useAuth } from '@/lib/firebase-context'
import { roomieMatcherService } from '@/lib/roomie-service'
import { Student, RoommateSurvey, CompatibilityScore, Room, RoomAssignment } from '@/lib/roomie-models'
import { 
  Users, 
  Brain, 
  Home, 
  TrendingUp, 
  Filter, 
  Eye, 
  CheckCircle, 
  XCircle,
  Clock,
  AlertTriangle,
  BarChart3,
  UserCheck
} from 'lucide-react'

interface MatchResult {
  student1: Student
  student2: Student
  compatibility: CompatibilityScore
  recommendedRoom?: Room
}

function CompatibilityDetails({ compatibility }: { compatibility: CompatibilityScore }) {
  const getScoreColor = (score: number) => {
    if (score >= 80) return 'text-green-600 bg-green-100'
    if (score >= 60) return 'text-yellow-600 bg-yellow-100'
    return 'text-red-600 bg-red-100'
  }

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
        {Object.entries(compatibility.categoryScores).map(([category, score]) => (
          <div key={category} className="text-center">
            <div className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${getScoreColor(score)}`}>
              {score}%
            </div>
            <p className="text-xs text-gray-600 mt-1 capitalize">
              {category.replace(/([A-Z])/g, ' $1').trim()}
            </p>
          </div>
        ))}
      </div>

      {compatibility.strongMatches.length > 0 && (
        <div>
          <h4 className="text-sm font-medium text-green-700 mb-2">Strong Matches:</h4>
          <div className="flex flex-wrap gap-1">
            {compatibility.strongMatches.map((match, index) => (
              <span key={index} className="inline-flex items-center px-2 py-1 rounded-md bg-green-100 text-green-800 text-xs">
                ✓ {match}
              </span>
            ))}
          </div>
        </div>
      )}

      {compatibility.dealbreakers.length > 0 && (
        <div>
          <h4 className="text-sm font-medium text-red-700 mb-2">Potential Issues:</h4>
          <div className="flex flex-wrap gap-1">
            {compatibility.dealbreakers.map((dealbreaker, index) => (
              <span key={index} className="inline-flex items-center px-2 py-1 rounded-md bg-red-100 text-red-800 text-xs">
                ⚠ {dealbreaker}
              </span>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

function MatchCard({ 
  match, 
  onAssignRoom, 
  isAssigning 
}: { 
  match: MatchResult
  onAssignRoom: (match: MatchResult) => void
  isAssigning: boolean
}) {
  const [showDetails, setShowDetails] = useState(false)

  const getCompatibilityColor = (score: number) => {
    if (score >= 80) return 'border-green-500 bg-green-50'
    if (score >= 60) return 'border-yellow-500 bg-yellow-50'
    return 'border-red-500 bg-red-50'
  }

  const getCompatibilityBadge = (score: number) => {
    if (score >= 80) return 'bg-green-100 text-green-800'
    if (score >= 60) return 'bg-yellow-100 text-yellow-800'
    return 'bg-red-100 text-red-800'
  }

  return (
    <div className={`border-2 rounded-lg p-4 ${getCompatibilityColor(match.compatibility.totalScore)}`}>
      <div className="flex justify-between items-start mb-3">
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-2">
            <h3 className="font-semibold text-gray-900">
              {match.student1.name} & {match.student2.name}
            </h3>
            <span className={`px-2 py-1 rounded-full text-xs font-medium ${getCompatibilityBadge(match.compatibility.totalScore)}`}>
              {match.compatibility.totalScore}% Compatible
            </span>
          </div>
          
          <div className="text-sm text-gray-600 space-y-1">
            <p>📚 {match.student1.branch} & {match.student2.branch}</p>
            <p>🏠 {match.student1.hometown} & {match.student2.hometown}</p>
            <p>📧 {match.student1.email} & {match.student2.email}</p>
          </div>
        </div>

        <div className="flex gap-2">
          <button
            onClick={() => setShowDetails(!showDetails)}
            className="px-3 py-1 text-sm bg-blue-100 text-blue-800 rounded-md hover:bg-blue-200 flex items-center gap-1"
          >
            <Eye className="w-4 h-4" />
            {showDetails ? 'Hide' : 'Details'}
          </button>
          
          <button
            onClick={() => onAssignRoom(match)}
            disabled={isAssigning || !match.recommendedRoom}
            className="px-3 py-1 text-sm bg-green-100 text-green-800 rounded-md hover:bg-green-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-1"
          >
            <Home className="w-4 h-4" />
            {isAssigning ? 'Assigning...' : 'Assign Room'}
          </button>
        </div>
      </div>

      {match.recommendedRoom && (
        <div className="bg-white bg-opacity-50 rounded-md p-3 mb-3">
          <p className="text-sm font-medium text-gray-900">
            Recommended Room: {match.recommendedRoom.building} {match.recommendedRoom.number}
          </p>
          <p className="text-xs text-gray-600">
            Floor {match.recommendedRoom.floor} • {match.recommendedRoom.roomType} • 
            {match.recommendedRoom.amenities.length} amenities
          </p>
        </div>
      )}

      {showDetails && (
        <div className="bg-white bg-opacity-75 rounded-md p-3">
          <CompatibilityDetails compatibility={match.compatibility} />
        </div>
      )}
    </div>
  )
}

function AnalyticsDashboard({ semester }: { semester: string }) {
  const [analytics, setAnalytics] = useState<any>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadAnalytics()
  }, [semester])

  const loadAnalytics = async () => {
    setLoading(true)
    try {
      const data = await roomieMatcherService.getMatchingStats(semester)
      setAnalytics(data)
    } catch (error) {
      console.error('Error loading analytics:', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="animate-pulse space-y-4">
          <div className="h-4 bg-gray-200 rounded w-1/4"></div>
          <div className="space-y-2">
            <div className="h-3 bg-gray-200 rounded"></div>
            <div className="h-3 bg-gray-200 rounded w-5/6"></div>
          </div>
        </div>
      </div>
    )
  }

  if (!analytics) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6 text-center">
        <p className="text-gray-500">No analytics data available</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Compatibility Distribution */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
          <BarChart3 className="w-5 h-5 mr-2" />
          Compatibility Score Distribution
        </h3>
        <div className="space-y-3">
          {analytics.compatibilityDistribution.map((item: any, index: number) => (
            <div key={index} className="flex items-center">
              <span className="w-16 text-sm text-gray-600">{item.range}%</span>
              <div className="flex-1 bg-gray-200 rounded-full h-6 mx-3">
                <div 
                  className="bg-blue-600 h-6 rounded-full"
                  style={{ 
                    width: `${Math.max(5, (item.count / Math.max(...analytics.compatibilityDistribution.map((d: any) => d.count))) * 100)}%` 
                  }}
                ></div>
              </div>
              <span className="w-8 text-sm font-medium text-gray-900">{item.count}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Category Averages */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Average Compatibility by Category</h3>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
          {Object.entries(analytics.categoryScoreAverages).map(([category, score]: [string, any]) => (
            <div key={category} className="text-center">
              <div className={`inline-flex items-center px-3 py-2 rounded-lg text-lg font-bold ${
                score >= 70 ? 'bg-green-100 text-green-800' :
                score >= 50 ? 'bg-yellow-100 text-yellow-800' :
                'bg-red-100 text-red-800'
              }`}>
                {score}%
              </div>
              <p className="text-sm text-gray-600 mt-1 capitalize">
                {category.replace(/([A-Z])/g, ' $1').trim()}
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* Top Dealbreakers */}
      {analytics.dealbreakersFrequency.length > 0 && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
            <AlertTriangle className="w-5 h-5 mr-2 text-red-600" />
            Common Compatibility Issues
          </h3>
          <div className="space-y-2">
            {analytics.dealbreakersFrequency.slice(0, 5).map((item: any, index: number) => (
              <div key={index} className="flex justify-between items-center p-2 bg-red-50 rounded">
                <span className="text-sm text-red-800">{item.dealbreaker}</span>
                <span className="text-sm font-medium text-red-600">{item.count} pairs</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Strong Matches */}
      {analytics.strongMatchesFrequency.length > 0 && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
            <UserCheck className="w-5 h-5 mr-2 text-green-600" />
            Common Strong Matches
          </h3>
          <div className="space-y-2">
            {analytics.strongMatchesFrequency.slice(0, 5).map((item: any, index: number) => (
              <div key={index} className="flex justify-between items-center p-2 bg-green-50 rounded">
                <span className="text-sm text-green-800">{item.match}</span>
                <span className="text-sm font-medium text-green-600">{item.count} pairs</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export function RoomieMatcherAdmin() {
  const { user } = useAuth()
  const [activeTab, setActiveTab] = useState<'matching' | 'analytics' | 'assignments'>('matching')
  const [matches, setMatches] = useState<MatchResult[]>([])
  const [loading, setLoading] = useState(false)
  const [assigning, setAssigning] = useState(false)
  const [message, setMessage] = useState('')
  const [assignments, setAssignments] = useState<RoomAssignment[]>([])
  
  const currentSemester = 'Fall_2025'

  useEffect(() => {
    if (activeTab === 'matching') {
      generateMatches()
    } else if (activeTab === 'assignments') {
      loadAssignments()
    }
  }, [activeTab])

  const generateMatches = async () => {
    setLoading(true)
    try {
      const compatibilityScores = await roomieMatcherService.generateOptimalMatches(currentSemester)
      
      // Transform CompatibilityScore[] to MatchResult[]
      const matchResults: MatchResult[] = []
      for (const score of compatibilityScores) {
        const student1 = await roomieMatcherService.getStudentById(score.studentId1)
        const student2 = await roomieMatcherService.getStudentById(score.studentId2)
        
        if (student1 && student2) {
          matchResults.push({
            student1,
            student2,
            compatibility: score
          })
        }
      }
      
      setMatches(matchResults)
      setMessage(`Generated ${matchResults.length} matches.`)
    } catch (error) {
      console.error('Error generating matches:', error)
      setMessage('Error generating matches')
    } finally {
      setLoading(false)
    }
  }

  const loadAssignments = async () => {
    setLoading(true)
    try {
      const result = await roomieMatcherService.getAllAssignments(currentSemester)
      setAssignments(result)
    } catch (error) {
      console.error('Error loading assignments:', error)
      setMessage('Error loading room assignments')
    } finally {
      setLoading(false)
    }
  }

  const handleAssignRoom = async (match: MatchResult) => {
    if (!match.recommendedRoom || !user) return

    setAssigning(true)
    try {
      await roomieMatcherService.createRoomAssignment({
        roomId: match.recommendedRoom.id,
        studentIds: [match.student1.id, match.student2.id],
        semester: currentSemester,
        assignedBy: user.uid,
        compatibilityScore: match.compatibility.totalScore,
        status: 'pending',
        assignedAt: new Date()
      })
      
      setMessage(`Room assigned successfully for ${match.student1.name} & ${match.student2.name}`)
      
      // Remove the assigned match from the list
      setMatches(prev => prev.filter(m => 
        m.student1.id !== match.student1.id && m.student2.id !== match.student2.id
      ))
    } catch (error) {
      console.error('Error assigning room:', error)
      setMessage('Error assigning room')
    } finally {
      setAssigning(false)
    }
  }

  const handleConfirmAssignment = async (assignmentId: string) => {
    try {
      await roomieMatcherService.updateRoomAssignment(assignmentId, {
        status: 'confirmed',
        confirmedAt: new Date()
      })
      setMessage('Room assignment confirmed')
      loadAssignments()
    } catch (error) {
      console.error('Error confirming assignment:', error)
      setMessage('Error confirming assignment')
    }
  }

  const handleRejectAssignment = async (assignmentId: string) => {
    try {
      await roomieMatcherService.updateRoomAssignment(assignmentId, {
        status: 'rejected'
      })
      setMessage('Room assignment rejected')
      loadAssignments()
    } catch (error) {
      console.error('Error rejecting assignment:', error)
      setMessage('Error rejecting assignment')
    }
  }

  return (
    <div className="max-w-7xl mx-auto p-4">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">RoomieMatcher Admin</h1>
        <p className="text-gray-600">Manage roommate matching and room assignments for {currentSemester}</p>
      </div>

      {/* Message */}
      {message && (
        <div className={`mb-6 p-4 rounded-lg ${
          message.includes('Error') 
            ? 'bg-red-50 border border-red-200 text-red-800' 
            : 'bg-green-50 border border-green-200 text-green-800'
        }`}>
          {message}
        </div>
      )}

      {/* Tabs */}
      <div className="border-b border-gray-200 mb-6">
        <nav className="flex space-x-8">
          {[
            { id: 'matching', label: 'Generate Matches', icon: Brain },
            { id: 'analytics', label: 'Analytics', icon: TrendingUp },
            { id: 'assignments', label: 'Room Assignments', icon: Home }
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`flex items-center py-2 px-1 border-b-2 font-medium text-sm ${
                activeTab === tab.id
                  ? 'border-indigo-500 text-indigo-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              <tab.icon className="w-4 h-4 mr-2" />
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Tab Content */}
      {activeTab === 'matching' && (
        <div>
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-xl font-semibold text-gray-900">Compatibility Matches</h2>
            <button
              onClick={generateMatches}
              disabled={loading}
              className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 disabled:opacity-50 flex items-center gap-2"
            >
              <Brain className="w-4 h-4" />
              {loading ? 'Generating...' : 'Regenerate Matches'}
            </button>
          </div>

          {loading ? (
            <div className="text-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
              <p className="mt-4 text-gray-600">Analyzing compatibility...</p>
            </div>
          ) : (
            <div className="space-y-6">
              {matches.map((match, index) => (
                <MatchCard
                  key={`${match.student1.id}-${match.student2.id}`}
                  match={match}
                  onAssignRoom={handleAssignRoom}
                  isAssigning={assigning}
                />
              ))}
              
              {matches.length === 0 && (
                <div className="text-center py-12 bg-gray-50 rounded-lg">
                  <Users className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-600">No matches available. Generate matches to see results.</p>
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {activeTab === 'analytics' && (
        <div>
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Matching Analytics</h2>
          <AnalyticsDashboard semester={currentSemester} />
        </div>
      )}

      {activeTab === 'assignments' && (
        <div>
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Room Assignments</h2>
          
          {loading ? (
            <div className="text-center py-12">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600 mx-auto"></div>
              <p className="mt-4 text-gray-600">Loading assignments...</p>
            </div>
          ) : (
            <div className="space-y-4">
              {assignments.map((assignment) => (
                <div key={assignment.id} className="bg-white border rounded-lg p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-medium text-gray-900">
                        Room Assignment #{assignment.id.slice(0, 8)}
                      </p>
                      <p className="text-sm text-gray-600 mt-1">
                        Students: {assignment.studentIds.length} • 
                        Compatibility: {assignment.compatibilityScore}% •
                        Assigned: {assignment.assignedAt.toLocaleDateString()}
                      </p>
                    </div>
                    
                    <div className="flex items-center gap-2">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                        assignment.status === 'confirmed' ? 'bg-green-100 text-green-800' :
                        assignment.status === 'pending' ? 'bg-yellow-100 text-yellow-800' :
                        assignment.status === 'rejected' ? 'bg-red-100 text-red-800' :
                        'bg-gray-100 text-gray-800'
                      }`}>
                        {assignment.status}
                      </span>
                      
                      {assignment.status === 'pending' && (
                        <div className="flex gap-1">
                          <button
                            onClick={() => handleConfirmAssignment(assignment.id)}
                            className="p-1 text-green-600 hover:bg-green-100 rounded"
                            title="Confirm Assignment"
                          >
                            <CheckCircle className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => handleRejectAssignment(assignment.id)}
                            className="p-1 text-red-600 hover:bg-red-100 rounded"
                            title="Reject Assignment"
                          >
                            <XCircle className="w-4 h-4" />
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
              
              {assignments.length === 0 && (
                <div className="text-center py-12 bg-gray-50 rounded-lg">
                  <Home className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-600">No room assignments yet.</p>
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  )
}
