'use client'

import { useState } from 'react'
import { createAdminAccounts, testAdminLogin } from '@/lib/setup-admin-accounts'
import { signOut } from 'firebase/auth'
import { auth } from '@/lib/firebase'

export default function AdminSetupPage() {
  const [isLoading, setIsLoading] = useState(false)
  const [results, setResults] = useState<any>(null)
  const [testResults, setTestResults] = useState<any[]>([])

  const handleCreateAccounts = async () => {
    setIsLoading(true)
    try {
      const result = await createAdminAccounts()
      setResults(result)
    } catch (error) {
      console.error('Error creating accounts:', error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleTestLogin = async (email: string, password: string) => {
    const result = await testAdminLogin(email, password)
    setTestResults(prev => [...prev, { email, ...result }])
    
    // Sign out after testing to avoid conflicts
    if (result.success) {
      await signOut(auth)
    }
  }

  const adminCredentials = [
    { email: 'admin1@hosteldada.com', password: 'admin123', type: 'Global Admin' },
    { email: 'snackcart.admin1@hosteldada.com', password: 'snack123', type: 'SnackCart Admin' },
    { email: 'student@university.edu', password: 'student123', type: 'Test User' }
  ]

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-4xl mx-auto px-4">
        <div className="bg-white rounded-lg shadow-lg p-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            🔧 Admin Account Setup
          </h1>
          <p className="text-gray-600 mb-8">
            Create all admin accounts for Hostel Dada system
          </p>

          {/* Create Accounts Section */}
          <div className="mb-8">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold text-gray-900">
                Step 1: Create Admin Accounts
              </h2>
              <button
                onClick={handleCreateAccounts}
                disabled={isLoading}
                className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isLoading ? 'Creating...' : 'Create All Accounts'}
              </button>
            </div>
            
            <div className="bg-blue-50 rounded-lg p-4 mb-4">
              <p className="text-blue-800 text-sm">
                This will create 12 admin accounts + 2 test user accounts (14 total)
              </p>
            </div>

            {results && (
              <div className="space-y-4">
                {results.created.length > 0 && (
                  <div className="bg-green-50 rounded-lg p-4">
                    <h3 className="font-medium text-green-800 mb-2">
                      ✅ Successfully Created ({results.created.length})
                    </h3>
                    <div className="text-sm text-green-700 space-y-1">
                      {results.created.map((email: string) => (
                        <div key={email}>• {email}</div>
                      ))}
                    </div>
                  </div>
                )}

                {results.alreadyExists.length > 0 && (
                  <div className="bg-yellow-50 rounded-lg p-4">
                    <h3 className="font-medium text-yellow-800 mb-2">
                      ⚠️ Already Existed ({results.alreadyExists.length})
                    </h3>
                    <div className="text-sm text-yellow-700 space-y-1">
                      {results.alreadyExists.map((email: string) => (
                        <div key={email}>• {email}</div>
                      ))}
                    </div>
                  </div>
                )}

                {results.failed.length > 0 && (
                  <div className="bg-red-50 rounded-lg p-4">
                    <h3 className="font-medium text-red-800 mb-2">
                      ❌ Failed ({results.failed.length})
                    </h3>
                    <div className="text-sm text-red-700 space-y-1">
                      {results.failed.map((item: any) => (
                        <div key={item.email}>• {item.email}: {item.error}</div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Test Login Section */}
          <div className="mb-8">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              Step 2: Test Admin Login
            </h2>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
              {adminCredentials.map((cred) => (
                <div key={cred.email} className="border rounded-lg p-4">
                  <h3 className="font-medium text-gray-900 mb-2">{cred.type}</h3>
                  <p className="text-sm text-gray-600 mb-2">{cred.email}</p>
                  <button
                    onClick={() => handleTestLogin(cred.email, cred.password)}
                    className="w-full bg-green-600 text-white py-2 px-4 rounded hover:bg-green-700 text-sm"
                  >
                    Test Login
                  </button>
                </div>
              ))}
            </div>

            {testResults.length > 0 && (
              <div className="bg-gray-50 rounded-lg p-4">
                <h3 className="font-medium text-gray-900 mb-2">Test Results:</h3>
                <div className="space-y-2">
                  {testResults.map((result, index) => (
                    <div key={index} className={`text-sm p-2 rounded ${
                      result.success 
                        ? 'bg-green-100 text-green-800' 
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {result.success ? '✅' : '❌'} {result.email}
                      {!result.success && ` - ${result.error}`}
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Instructions */}
          <div className="bg-gray-50 rounded-lg p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">
              📋 What This Does
            </h2>
            <div className="space-y-3 text-sm text-gray-700">
              <div>
                <strong>Creates 12 Admin Accounts:</strong>
                <ul className="ml-4 mt-1 space-y-1">
                  <li>• 2 Global Admins (full access)</li>
                  <li>• 2 SnackCart Admins</li>
                  <li>• 2 RoomieMatcher Admins</li>
                  <li>• 2 LaundryBalancer Admins</li>
                  <li>• 2 MessyMess Admins</li>
                  <li>• 2 HostelFixer Admins</li>
                </ul>
              </div>
              <div>
                <strong>Creates 2 Test Users:</strong>
                <ul className="ml-4 mt-1 space-y-1">
                  <li>• student@university.edu (regular user)</li>
                  <li>• demo@hosteldada.com (demo user)</li>
                </ul>
              </div>
              <div>
                <strong>Sets Up Firestore Profiles:</strong>
                <ul className="ml-4 mt-1 space-y-1">
                  <li>• Role-based access control</li>
                  <li>• User profiles with metadata</li>
                  <li>• Admin permissions</li>
                </ul>
              </div>
            </div>
          </div>

          {/* Next Steps */}
          <div className="mt-6 p-4 bg-blue-50 rounded-lg">
            <h3 className="font-medium text-blue-900 mb-2">🚀 Next Steps:</h3>
            <ol className="text-sm text-blue-800 space-y-1">
              <li>1. Click "Create All Accounts" above</li>
              <li>2. Test login with admin credentials</li>
              <li>3. Go to main app and login with admin1@hosteldada.com</li>
              <li>4. Check admin features in SnackCart module</li>
            </ol>
          </div>
        </div>
      </div>
    </div>
  )
}
