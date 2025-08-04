// Enhanced Admin Models for Roomie Matcher Batch System

export interface SurveyBatch {
  id: string
  name: string
  semester: string
  totalStudentsExpected: number // 0 means flexible/no limit
  studentsCompleted: number
  startDate: Date
  deadline: Date
  status: 'open' | 'closed' | 'processing' | 'completed'
  isResultsReleased: boolean
  createdBy: string
  createdAt: Date
}

export interface AdminRoommateControl {
  batchId: string
  canViewResults: boolean
  canReleaseResults: boolean
  canProcessMatches: boolean
  manualOverrides: string[]
  lastProcessedAt?: Date
  processedBy?: string
}

export interface SurveyStats {
  totalStudents: number // Actual count of registered/expected students (can be 0)
  completedSurveys: number
  pendingSurveys: number // Calculated or manual count
  averageCompatibility: number
  highCompatibilityPairs: number
  lowCompatibilityPairs: number
  roomsAvailable: number
  roomsAssigned: number
}

export interface StudentSurveyStatus {
  studentId: string
  studentName: string
  email: string
  hasSubmitted: boolean
  submittedAt?: Date
  preferredRooms: string[]
  compatibility?: {
    bestMatch: string
    worstMatch: string
    averageScore: number
  }
}

export interface RoommateMatchAdmin {
  id: string
  student1Id: string
  student2Id: string
  student1Name: string
  student2Name: string
  compatibilityScore: number
  isApproved: boolean
  isRejected: boolean
  assignedRoom?: string
  adminNotes: string
  createdAt: Date
  approvedBy?: string
  approvedAt?: Date
}

// Admin Action Types
export type AdminAction = 
  | 'create_batch'
  | 'close_batch'
  | 'process_matches'
  | 'release_results'
  | 'approve_match'
  | 'reject_match'
  | 'manual_assign'
  | 'send_reminder'
  | 'export_data'

export interface AdminActionLog {
  id: string
  action: AdminAction
  adminId: string
  adminName: string
  batchId: string
  details: string
  timestamp: Date
  affectedStudents?: string[]
}
