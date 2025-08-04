'use client'

import { getAdminRole, isAdminEmail, ALL_ADMIN_EMAILS } from '@/lib/admin-config'
import { useState } from 'react'

export default function AdminConfigTestPage() {
  const [testEmail, setTestEmail] = useState('admin1@hosteldada.com')
  const [testResult, setTestResult] = useState<any>(null)

  const runTest = () => {
    const adminRole = getAdminRole(testEmail)
    const isAdmin = isAdminEmail(testEmail)
    
    setTestResult({
      email: testEmail,
      isAdmin,
      adminRole
    })
  }

  const predefinedTests = [
    'admin1@hosteldada.com',
    'admin2@hosteldada.com',
    'snackcart.admin1@hosteldada.com',
    'roomie.admin1@hosteldada.com',
    'student@university.edu'
  ]

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="max-w-4xl mx-auto px-4">
        <div className="bg-white rounded-lg shadow-lg p-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-6">
            🔧 Admin Config Test
          </h1>

          {/* All Admin Emails */}
          <div className="mb-8">
            <h2 className="text-xl font-semibold mb-4">📋 All Admin Emails ({ALL_ADMIN_EMAILS.length})</h2>
            <div className="bg-gray-50 rounded-lg p-4 max-h-40 overflow-y-auto">
              {ALL_ADMIN_EMAILS.map((email, index) => (
                <div key={index} className="text-sm text-gray-600 mb-1">
                  {index + 1}. {email}
                </div>
              ))}
            </div>
          </div>

          {/* Email Test */}
          <div className="mb-8">
            <h2 className="text-xl font-semibold mb-4">🧪 Test Email</h2>
            <div className="flex gap-4 mb-4">
              <input
                type="email"
                value={testEmail}
                onChange={(e) => setTestEmail(e.target.value)}
                className="flex-1 px-3 py-2 border border-gray-300 rounded-lg"
                placeholder="Enter email to test"
              />
              <button
                onClick={runTest}
                className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
              >
                Test
              </button>
            </div>

            {testResult && (
              <div className="bg-blue-50 rounded-lg p-4">
                <h3 className="font-medium text-blue-800 mb-2">Test Results:</h3>
                <div className="text-sm text-blue-700 space-y-1">
                  <div>📧 Email: {testResult.email}</div>
                  <div>✅ Is Admin: {testResult.isAdmin ? 'YES' : 'NO'}</div>
                  <div>🌟 Is Global: {testResult.adminRole.isGlobal ? 'YES' : 'NO'}</div>
                  <div>📦 Modules: [{testResult.adminRole.modules.join(', ')}]</div>
                </div>
              </div>
            )}
          </div>

          {/* Predefined Tests */}
          <div className="mb-8">
            <h2 className="text-xl font-semibold mb-4">⚡ Quick Tests</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {predefinedTests.map((email, index) => {
                const adminRole = getAdminRole(email)
                const isAdmin = isAdminEmail(email)
                
                return (
                  <div key={index} className={`border rounded-lg p-4 ${isAdmin ? 'border-green-300 bg-green-50' : 'border-red-300 bg-red-50'}`}>
                    <div className="font-medium text-gray-900 mb-2">{email}</div>
                    <div className="text-sm space-y-1">
                      <div>✅ Admin: {isAdmin ? 'YES' : 'NO'}</div>
                      <div>🌟 Global: {adminRole.isGlobal ? 'YES' : 'NO'}</div>
                      <div>📦 Modules: {adminRole.modules.length > 0 ? adminRole.modules.join(', ') : 'None'}</div>
                    </div>
                  </div>
                )
              })}
            </div>
          </div>

          {/* Navigation */}
          <div className="flex gap-4">
            <a
              href="/admin-setup"
              className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700"
            >
              → Admin Setup Page
            </a>
            <a
              href="/login"
              className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
            >
              → Login Page
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
