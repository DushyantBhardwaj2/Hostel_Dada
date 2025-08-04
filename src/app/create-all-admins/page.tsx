'use client'

import { useState } from 'react'
import { createUserWithEmailAndPassword, signOut } from 'firebase/auth'
import { ref, set } from 'firebase/database'
import { auth, realtimeDb } from '@/lib/firebase'

const COMPLETE_ADMIN_LIST = [
  // Global Admins (2)
  { email: 'admin1@hosteldada.com', password: 'admin123', role: 'admin', type: 'global' },
  { email: 'admin2@hosteldada.com', password: 'admin456', role: 'admin', type: 'global' },
  
  // SnackCart Admins (2)
  { email: 'snackcart.admin1@hosteldada.com', password: 'snack123', role: 'admin', type: 'module' },
  { email: 'snackcart.admin2@hosteldada.com', password: 'snack456', role: 'admin', type: 'module' },
  
  // RoomieMatcher Admins (2)
  { email: 'roomie.admin1@hosteldada.com', password: 'roomie123', role: 'admin', type: 'module' },
  { email: 'roomie.admin2@hosteldada.com', password: 'roomie456', role: 'admin', type: 'module' },
  
  // LaundryBalancer Admins (2)
  { email: 'laundry.admin1@hosteldada.com', password: 'laundry123', role: 'admin', type: 'module' },
  { email: 'laundry.admin2@hosteldada.com', password: 'laundry456', role: 'admin', type: 'module' },
  
  // MessyMess Admins (2)
  { email: 'mess.admin1@hosteldada.com', password: 'mess123', role: 'admin', type: 'module' },
  { email: 'mess.admin2@hosteldada.com', password: 'mess456', role: 'admin', type: 'module' },
  
  // HostelFixer Admins (2)
  { email: 'maintenance.admin1@hosteldada.com', password: 'maintenance123', role: 'admin', type: 'module' },
  { email: 'maintenance.admin2@hosteldada.com', password: 'maintenance456', role: 'admin', type: 'module' },

  // Test Users (2)
  { email: 'student@university.edu', password: 'student123', role: 'user', type: 'student' },
  { email: 'demo@hosteldada.com', password: 'demo123', role: 'user', type: 'demo' }
]

export default function CreateAllAdminsPage() {
  const [isCreating, setIsCreating] = useState(false)
  const [results, setResults] = useState<any[]>([])
  const [currentlyCreating, setCurrentlyCreating] = useState('')

  const createSingleAccount = async (account: any) => {
    try {
      console.log(`Creating: ${account.email}`)
      setCurrentlyCreating(account.email)
      
      // Create Firebase Auth account
      const userCredential = await createUserWithEmailAndPassword(
        auth, 
        account.email, 
        account.password
      )
      
      // Create Realtime Database profile
      const userProfileRef = ref(realtimeDb, `userProfiles/${userCredential.user.uid}`)
      await set(userProfileRef, {
        email: account.email,
        displayName: account.email.split('@')[0],
        role: account.role,
        type: account.type,
        isActive: true,
        joinedAt: new Date().toISOString(),
        totalOrders: 0,
        totalSpent: 0,
        hostelRoom: account.role === 'admin' ? 'Admin Office' : null,
        phoneNumber: ''
      })

      // Sign out after creating to avoid conflicts
      await signOut(auth)
      
      return { success: true, email: account.email }
      
    } catch (error: any) {
      console.error(`Failed to create ${account.email}:`, error)
      return { 
        success: false, 
        email: account.email, 
        error: error.code === 'auth/email-already-in-use' ? 'Already exists' : error.message 
      }
    }
  }

  const createAllAccounts = async () => {
    setIsCreating(true)
    setResults([])
    setCurrentlyCreating('')

    const accountResults = []

    for (let i = 0; i < COMPLETE_ADMIN_LIST.length; i++) {
      const account = COMPLETE_ADMIN_LIST[i]
      const result = await createSingleAccount(account)
      accountResults.push(result)
      setResults([...accountResults])
      
      // Small delay between accounts to avoid rate limiting
      await new Promise(resolve => setTimeout(resolve, 1000))
    }

    setIsCreating(false)
    setCurrentlyCreating('')
  }

  const successCount = results.filter(r => r.success).length
  const failedCount = results.filter(r => !r.success).length
  const alreadyExistsCount = results.filter(r => !r.success && r.error === 'Already exists').length

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-4xl mx-auto px-4">
        <div className="bg-white rounded-lg shadow-lg p-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            🔥 Create All Admin Accounts
          </h1>
          <p className="text-gray-600 mb-8">
            This will create all {COMPLETE_ADMIN_LIST.length} accounts (12 admins + 2 test users)
          </p>

          {/* Account List */}
          <div className="mb-8">
            <h2 className="text-xl font-semibold mb-4">📋 Accounts to Create:</h2>
            <div className="bg-gray-50 rounded-lg p-4 max-h-60 overflow-y-auto">
              {COMPLETE_ADMIN_LIST.map((account, index) => (
                <div key={index} className="flex justify-between items-center py-2 border-b border-gray-200 last:border-b-0">
                  <div>
                    <span className="font-medium">{account.email}</span>
                    <span className="ml-2 text-sm text-gray-500">({account.type} {account.role})</span>
                  </div>
                  <span className="text-sm text-gray-400">{account.password}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Create Button */}
          <div className="mb-8">
            <button
              onClick={createAllAccounts}
              disabled={isCreating}
              className="w-full bg-red-600 text-white py-4 px-6 rounded-lg text-lg font-semibold hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {isCreating ? (
                <span>Creating... {currentlyCreating}</span>
              ) : (
                <span>🚀 Create All {COMPLETE_ADMIN_LIST.length} Accounts</span>
              )}
            </button>
          </div>

          {/* Progress */}
          {results.length > 0 && (
            <div className="mb-8">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-semibold">📊 Progress</h2>
                <div className="text-sm text-gray-600">
                  {results.length} / {COMPLETE_ADMIN_LIST.length} processed
                </div>
              </div>
              
              <div className="grid grid-cols-3 gap-4 mb-4">
                <div className="bg-green-50 rounded-lg p-4 text-center">
                  <div className="text-2xl font-bold text-green-600">{successCount}</div>
                  <div className="text-sm text-green-700">Created</div>
                </div>
                <div className="bg-yellow-50 rounded-lg p-4 text-center">
                  <div className="text-2xl font-bold text-yellow-600">{alreadyExistsCount}</div>
                  <div className="text-sm text-yellow-700">Already Exist</div>
                </div>
                <div className="bg-red-50 rounded-lg p-4 text-center">
                  <div className="text-2xl font-bold text-red-600">{failedCount - alreadyExistsCount}</div>
                  <div className="text-sm text-red-700">Failed</div>
                </div>
              </div>

              {/* Detailed Results */}
              <div className="space-y-2 max-h-60 overflow-y-auto">
                {results.map((result, index) => (
                  <div key={index} className={`flex justify-between items-center p-3 rounded ${
                    result.success ? 'bg-green-50 border border-green-200' : 
                    result.error === 'Already exists' ? 'bg-yellow-50 border border-yellow-200' :
                    'bg-red-50 border border-red-200'
                  }`}>
                    <span className="font-medium">{result.email}</span>
                    <span className={`text-sm ${
                      result.success ? 'text-green-600' : 
                      result.error === 'Already exists' ? 'text-yellow-600' :
                      'text-red-600'
                    }`}>
                      {result.success ? '✅ Created' : 
                       result.error === 'Already exists' ? '⚠️ Already exists' :
                       `❌ ${result.error}`}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Navigation */}
          <div className="flex gap-4">
            <a
              href="/login"
              className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
            >
              → Test Login
            </a>
            <a
              href="/admin-config-test"
              className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700"
            >
              → Test Admin Config
            </a>
            <a
              href="/"
              className="bg-gray-600 text-white px-6 py-2 rounded-lg hover:bg-gray-700"
            >
              → Dashboard
            </a>
          </div>
        </div>
      </div>
    </div>
  )
}
