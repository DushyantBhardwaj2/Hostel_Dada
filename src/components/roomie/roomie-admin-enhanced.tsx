'use client'

import { useState, useEffect } from 'react'
import { useAuth } from '@/lib/firebase-context'
import { 
  SurveyBatch, 
  AdminRoommateControl, 
  SurveyStats, 
  StudentSurveyStatus, 
  RoommateMatchAdmin 
} from '@/lib/roomie-admin-models'
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
  UserCheck,
  Lock,
  Unlock,
  Play,
  Pause,
  Download,
  Mail,
  Settings,
  RefreshCw,
  Award
} from 'lucide-react'

export function RoomieMatcherAdminEnhanced() {
  const { user, isAdmin } = useAuth()
  const [currentBatch, setCurrentBatch] = useState<SurveyBatch | null>(null)
  const [stats, setStats] = useState<SurveyStats | null>(null)
  const [studentStatuses, setStudentStatuses] = useState<StudentSurveyStatus[]>([])
  const [matches, setMatches] = useState<RoommateMatchAdmin[]>([])
  const [loading, setLoading] = useState(true)
  const [activeTab, setActiveTab] = useState<'overview' | 'students' | 'matches' | 'settings'>('overview')

  useEffect(() => {
    if (isAdmin) {
      loadAdminData()
    }
  }, [isAdmin])

  const loadAdminData = async () => {
    setLoading(true)
    try {
      // Load current batch
      const batch: SurveyBatch = {
        id: 'fall_2025_batch',
        name: 'Fall 2025 Roommate Matching',
        semester: 'Fall_2025',
        totalStudentsExpected: 0, // Admin can set this or process when ready
        studentsCompleted: 87,
        startDate: new Date('2025-08-01'),
        deadline: new Date('2025-08-15'),
        status: 'open',
        isResultsReleased: false,
        createdBy: 'admin',
        createdAt: new Date()
      }
      setCurrentBatch(batch)

      // Load stats
      const statsData: SurveyStats = {
        totalStudents: 0, // Dynamic based on actual submissions
        completedSurveys: 87,
        pendingSurveys: 0, // Admin controlled
        averageCompatibility: 78.5,
        highCompatibilityPairs: 32,
        lowCompatibilityPairs: 8,
        roomsAvailable: 75,
        roomsAssigned: 0
      }
      setStats(statsData)

      // Load student statuses (mock data)
      const mockStudents: StudentSurveyStatus[] = Array.from({ length: 20 }, (_, i) => ({
        studentId: `student_${i + 1}`,
        studentName: `Student ${i + 1}`,
        email: `student${i + 1}@hostel.com`,
        hasSubmitted: Math.random() > 0.4,
        submittedAt: Math.random() > 0.4 ? new Date() : undefined,
        preferredRooms: [`${Math.floor(Math.random() * 3) + 1}0${Math.floor(Math.random() * 10) + 1}`],
        compatibility: Math.random() > 0.4 ? {
          bestMatch: `student_${Math.floor(Math.random() * 20) + 1}`,
          worstMatch: `student_${Math.floor(Math.random() * 20) + 1}`,
          averageScore: Math.floor(Math.random() * 40) + 60
        } : undefined
      }))
      setStudentStatuses(mockStudents)

    } catch (error) {
      console.error('Error loading admin data:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleProcessMatches = async () => {
    if (!currentBatch) return
    
    setLoading(true)
    try {
      // Simulate processing matches
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      // Generate mock matches
      const mockMatches: RoommateMatchAdmin[] = Array.from({ length: 40 }, (_, i) => ({
        id: `match_${i + 1}`,
        student1Id: `student_${i * 2 + 1}`,
        student2Id: `student_${i * 2 + 2}`,
        student1Name: `Student ${i * 2 + 1}`,
        student2Name: `Student ${i * 2 + 2}`,
        compatibilityScore: Math.floor(Math.random() * 40) + 60,
        isApproved: false,
        isRejected: false,
        adminNotes: '',
        createdAt: new Date()
      }))
      
      setMatches(mockMatches)
      
      // Update batch status
      setCurrentBatch(prev => prev ? { ...prev, status: 'processing' } : null)
    } catch (error) {
      console.error('Error processing matches:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleReleaseResults = async () => {
    if (!currentBatch) return
    
    setLoading(true)
    try {
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      setCurrentBatch(prev => prev ? { ...prev, isResultsReleased: true, status: 'completed' } : null)
      
      alert('🎉 Results released! Students can now see their roommate matches.')
    } catch (error) {
      console.error('Error releasing results:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSendReminders = async () => {
    const pendingStudents = studentStatuses.filter(s => !s.hasSubmitted)
    alert(`📧 Reminders sent to ${pendingStudents.length} students who haven't completed the survey.`)
  }

  // Admin can decide when to process matches (not based on hard number)
  const canProcessMatches = (currentBatch?.studentsCompleted ?? 0) > 0 && !currentBatch?.isResultsReleased
  const canReleaseResults = matches.length > 0 && !currentBatch?.isResultsReleased

  if (loading && !currentBatch) {
    return (
      <div className="max-w-7xl mx-auto p-6">
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading admin dashboard...</p>
        </div>
      </div>
    )
  }

  if (!isAdmin) {
    return (
      <div className="max-w-4xl mx-auto p-6">
        <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
          <AlertTriangle className="w-12 h-12 text-red-600 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-red-800 mb-2">Access Denied</h3>
          <p className="text-red-700">You need admin privileges to access this panel.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="max-w-7xl mx-auto p-6 space-y-6">
      {/* Header */}
      <div className="bg-gradient-to-r from-purple-50 to-blue-50 border border-purple-200 rounded-xl p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">
              Roommate Matcher Admin Panel
            </h1>
            <p className="text-gray-600">
              {currentBatch?.name} - Batch Control & Management
            </p>
          </div>
          <div className="text-right">
            <div className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${
              currentBatch?.status === 'open' ? 'bg-green-100 text-green-800' :
              currentBatch?.status === 'processing' ? 'bg-yellow-100 text-yellow-800' :
              'bg-blue-100 text-blue-800'
            }`}>
              {currentBatch?.status === 'open' && <Clock className="w-4 h-4 mr-1" />}
              {currentBatch?.status === 'processing' && <RefreshCw className="w-4 h-4 mr-1 animate-spin" />}
              {currentBatch?.status === 'completed' && <CheckCircle className="w-4 h-4 mr-1" />}
              {currentBatch?.status?.toUpperCase()}
            </div>
          </div>
        </div>
      </div>

      {/* Quick Stats */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="bg-white border border-gray-200 rounded-xl p-6">
            <div className="flex items-center gap-4">
              <div className="bg-blue-100 p-3 rounded-lg">
                <Users className="h-6 w-6 text-blue-600" />
              </div>
              <div>
                <p className="text-2xl font-bold text-gray-900">{stats.completedSurveys}/{stats.totalStudents}</p>
                <p className="text-gray-600">Surveys Completed</p>
              </div>
            </div>
            <div className="mt-4 bg-gray-200 rounded-full h-2">
              <div 
                className="bg-blue-500 h-2 rounded-full transition-all"
                style={{ width: `${(stats.completedSurveys / stats.totalStudents) * 100}%` }}
              ></div>
            </div>
          </div>

          <div className="bg-white border border-gray-200 rounded-xl p-6">
            <div className="flex items-center gap-4">
              <div className="bg-green-100 p-3 rounded-lg">
                <Award className="h-6 w-6 text-green-600" />
              </div>
              <div>
                <p className="text-2xl font-bold text-gray-900">{stats.averageCompatibility}%</p>
                <p className="text-gray-600">Avg Compatibility</p>
              </div>
            </div>
          </div>

          <div className="bg-white border border-gray-200 rounded-xl p-6">
            <div className="flex items-center gap-4">
              <div className="bg-purple-100 p-3 rounded-lg">
                <Home className="h-6 w-6 text-purple-600" />
              </div>
              <div>
                <p className="text-2xl font-bold text-gray-900">{stats.roomsAssigned}/{stats.roomsAvailable}</p>
                <p className="text-gray-600">Rooms Assigned</p>
              </div>
            </div>
          </div>

          <div className="bg-white border border-gray-200 rounded-xl p-6">
            <div className="flex items-center gap-4">
              <div className="bg-orange-100 p-3 rounded-lg">
                <Brain className="h-6 w-6 text-orange-600" />
              </div>
              <div>
                <p className="text-2xl font-bold text-gray-900">{matches.length}</p>
                <p className="text-gray-600">Potential Matches</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Navigation Tabs */}
      <div className="bg-white border border-gray-200 rounded-xl">
        <div className="border-b border-gray-200">
          <nav className="flex space-x-8 px-6">
            {[
              { id: 'overview', label: 'Overview', icon: BarChart3 },
              { id: 'students', label: 'Student Status', icon: Users },
              { id: 'matches', label: 'Matches', icon: Brain },
              { id: 'settings', label: 'Settings', icon: Settings }
            ].map((tab) => {
              const Icon = tab.icon
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`flex items-center gap-2 py-4 px-1 border-b-2 font-medium text-sm transition-colors ${
                    activeTab === tab.id
                      ? 'border-blue-500 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                  }`}
                >
                  <Icon className="w-4 h-4" />
                  {tab.label}
                </button>
              )
            })}
          </nav>
        </div>

        <div className="p-6">
          {activeTab === 'overview' && (
            <div className="space-y-6">
              {/* Batch Control Actions */}
              <div className="bg-gradient-to-r from-gray-50 to-blue-50 border border-gray-200 rounded-xl p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Batch Control Actions</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                  
                  <button
                    onClick={handleSendReminders}
                    className="flex items-center gap-3 p-4 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                  >
                    <Mail className="w-5 h-5 text-blue-600" />
                    <div className="text-left">
                      <p className="font-medium text-gray-900">Send Reminders</p>
                      <p className="text-sm text-gray-600">{stats?.pendingSurveys} pending</p>
                    </div>
                  </button>

                  <button
                    onClick={handleProcessMatches}
                    disabled={!canProcessMatches || loading}
                    className={`flex items-center gap-3 p-4 border rounded-lg transition-colors ${
                      canProcessMatches && !loading
                        ? 'bg-green-500 text-white border-green-500 hover:bg-green-600'
                        : 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                    }`}
                  >
                    <Brain className="w-5 h-5" />
                    <div className="text-left">
                      <p className="font-medium">Process Matches</p>
                      <p className="text-sm opacity-75">
                        {canProcessMatches ? 'Ready to process current submissions' : 'No submissions to process'}
                      </p>
                    </div>
                  </button>

                  <button
                    onClick={handleReleaseResults}
                    disabled={!canReleaseResults || loading}
                    className={`flex items-center gap-3 p-4 border rounded-lg transition-colors ${
                      canReleaseResults && !loading
                        ? 'bg-purple-500 text-white border-purple-500 hover:bg-purple-600'
                        : 'bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed'
                    }`}
                  >
                    {currentBatch?.isResultsReleased ? <Unlock className="w-5 h-5" /> : <Lock className="w-5 h-5" />}
                    <div className="text-left">
                      <p className="font-medium">
                        {currentBatch?.isResultsReleased ? 'Results Released' : 'Release Results'}
                      </p>
                      <p className="text-sm opacity-75">
                        {currentBatch?.isResultsReleased ? 'Students can see matches' : 'Make results visible'}
                      </p>
                    </div>
                  </button>

                  <button className="flex items-center gap-3 p-4 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                    <Download className="w-5 h-5 text-gray-600" />
                    <div className="text-left">
                      <p className="font-medium text-gray-900">Export Data</p>
                      <p className="text-sm text-gray-600">Download CSV</p>
                    </div>
                  </button>
                </div>
              </div>

              {/* Progress Indicator */}
              <div className="bg-white border border-gray-200 rounded-xl p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Batch Progress</h3>
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-medium text-gray-700">Survey Collection</span>
                    <span className="text-sm text-gray-500">{stats?.completedSurveys}/{stats?.totalStudents}</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      className={`h-2 rounded-full transition-all ${
                        (stats?.completedSurveys || 0) >= (stats?.totalStudents || 1) ? 'bg-green-500' : 'bg-blue-500'
                      }`}
                      style={{ width: `${((stats?.completedSurveys || 0) / (stats?.totalStudents || 1)) * 100}%` }}
                    ></div>
                  </div>
                  
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-medium text-gray-700">Match Processing</span>
                    <span className="text-sm text-gray-500">{matches.length > 0 ? 'Complete' : 'Pending'}</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      className={`h-2 rounded-full transition-all ${matches.length > 0 ? 'bg-green-500' : 'bg-gray-300'}`}
                      style={{ width: matches.length > 0 ? '100%' : '0%' }}
                    ></div>
                  </div>
                  
                  <div className="flex items-center justify-between">
                    <span className="text-sm font-medium text-gray-700">Results Release</span>
                    <span className="text-sm text-gray-500">{currentBatch?.isResultsReleased ? 'Released' : 'Locked'}</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      className={`h-2 rounded-full transition-all ${currentBatch?.isResultsReleased ? 'bg-green-500' : 'bg-gray-300'}`}
                      style={{ width: currentBatch?.isResultsReleased ? '100%' : '0%' }}
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'students' && (
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <h3 className="text-lg font-semibold text-gray-900">Student Survey Status</h3>
                <div className="flex gap-2">
                  <button className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors">
                    Filter Pending
                  </button>
                  <button className="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors">
                    Filter Completed
                  </button>
                </div>
              </div>
              
              <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Student</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Submitted</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Compatibility</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {studentStatuses.slice(0, 10).map((student) => (
                      <tr key={student.studentId} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div>
                            <div className="text-sm font-medium text-gray-900">{student.studentName}</div>
                            <div className="text-sm text-gray-500">{student.email}</div>
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                            student.hasSubmitted
                              ? 'bg-green-100 text-green-800'
                              : 'bg-red-100 text-red-800'
                          }`}>
                            {student.hasSubmitted ? (
                              <>
                                <CheckCircle className="w-3 h-3 mr-1" />
                                Completed
                              </>
                            ) : (
                              <>
                                <XCircle className="w-3 h-3 mr-1" />
                                Pending
                              </>
                            )}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {student.submittedAt ? student.submittedAt.toLocaleDateString() : '-'}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {student.compatibility ? `${student.compatibility.averageScore}%` : '-'}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <button className="text-blue-600 hover:text-blue-900 mr-3">View</button>
                          {!student.hasSubmitted && (
                            <button className="text-green-600 hover:text-green-900">Remind</button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {activeTab === 'matches' && (
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <h3 className="text-lg font-semibold text-gray-900">Generated Matches</h3>
                <div className="text-sm text-gray-500">
                  {matches.length} potential matches found
                </div>
              </div>
              
              {matches.length === 0 ? (
                <div className="text-center py-12 bg-gray-50 rounded-lg">
                  <Brain className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-600">No matches processed yet. Complete survey collection first.</p>
                </div>
              ) : (
                <div className="grid gap-4">
                  {matches.slice(0, 10).map((match) => (
                    <div key={match.id} className="bg-white border border-gray-200 rounded-lg p-6">
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-4">
                          <div className="bg-purple-100 p-2 rounded-lg">
                            <Users className="w-5 h-5 text-purple-600" />
                          </div>
                          <div>
                            <h4 className="font-medium text-gray-900">
                              {match.student1Name} & {match.student2Name}
                            </h4>
                            <p className="text-sm text-gray-600">
                              Compatibility: {match.compatibilityScore}%
                            </p>
                          </div>
                        </div>
                        <div className="flex items-center gap-2">
                          <button className="px-3 py-1 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors">
                            Approve
                          </button>
                          <button className="px-3 py-1 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors">
                            Reject
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {activeTab === 'settings' && (
            <div className="space-y-6">
              <h3 className="text-lg font-semibold text-gray-900">Batch Settings</h3>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="bg-white border border-gray-200 rounded-lg p-6">
                  <h4 className="font-medium text-gray-900 mb-4">Survey Settings</h4>
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Expected Students (Optional)
                      </label>
                      <input
                        type="number"
                        value={currentBatch?.totalStudentsExpected || ''}
                        placeholder="Leave empty for flexible processing"
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                      <p className="text-xs text-gray-500 mt-1">
                        You can process matches anytime regardless of this number
                      </p>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Survey Deadline
                      </label>
                      <input
                        type="date"
                        value={currentBatch?.deadline.toISOString().split('T')[0]}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                    </div>
                  </div>
                </div>

                <div className="bg-white border border-gray-200 rounded-lg p-6">
                  <h4 className="font-medium text-gray-900 mb-4">Notification Settings</h4>
                  <div className="space-y-4">
                    <label className="flex items-center">
                      <input type="checkbox" className="mr-2" defaultChecked />
                      <span className="text-sm text-gray-700">Auto-send reminders</span>
                    </label>
                    <label className="flex items-center">
                      <input type="checkbox" className="mr-2" defaultChecked />
                      <span className="text-sm text-gray-700">Email notifications</span>
                    </label>
                    <label className="flex items-center">
                      <input type="checkbox" className="mr-2" />
                      <span className="text-sm text-gray-700">SMS notifications</span>
                    </label>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
