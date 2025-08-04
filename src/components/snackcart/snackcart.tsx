'use client'

import { useState, useEffect } from 'react'
import { Users, Settings, Crown } from 'lucide-react'
import { SnackCartUserOptimized } from './snackcart-user-optimized'
import { SnackCartAdmin } from './snackcart-admin'
import { useAuth } from '@/lib/firebase-context'
import { ref, get } from 'firebase/database'
import { realtimeDb } from '@/lib/firebase'
import { getAdminRole } from '@/lib/admin-config'

interface UserProfile {
  role: 'user' | 'admin' | 'moderator'
  displayName?: string
  hostelRoom?: string
}

export function SnackCart() {
  const { user } = useAuth()
  const [view, setView] = useState<'user' | 'admin'>('user')
  const [userProfile, setUserProfile] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (user) {
      loadUserProfile()
    } else {
      setLoading(false)
    }
  }, [user])

  const loadUserProfile = async () => {
    if (!user) return
    
    try {
      const profileRef = ref(realtimeDb, `userProfiles/${user.uid}`)
      const snapshot = await get(profileRef)
      if (snapshot.exists()) {
        setUserProfile(snapshot.val() as UserProfile)
      } else {
        // Use predefined admin list as per README specifications
        const adminRole = getAdminRole(user.email || '')
        setUserProfile({ 
          role: adminRole.isAdmin ? 'admin' : 'user'
        })
      }
    } catch (error) {
      console.error('Error loading user profile:', error)
      // Use predefined admin list as fallback
      const adminRole = getAdminRole(user.email || '')
      setUserProfile({ 
        role: adminRole.isAdmin ? 'admin' : 'user'
      })
    } finally {
      setLoading(false)
    }
  }

  const isAdmin = userProfile?.role === 'admin' || userProfile?.role === 'moderator'

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <span className="ml-2 text-gray-600">Loading SnackCart...</span>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header with view toggle for admins */}
      {isAdmin && (
        <div className="bg-white shadow-sm border-b">
          <div className="max-w-7xl mx-auto px-4">
            <div className="flex justify-between items-center py-4">
              <div className="flex items-center">
                <h1 className="text-xl font-semibold text-gray-900">SnackCart Dashboard</h1>
                <div className="ml-3 flex items-center">
                  <Crown className="w-4 h-4 text-yellow-500 mr-1" />
                  <span className="text-sm text-yellow-700 font-medium">
                    {userProfile?.role === 'admin' ? 'Administrator' : 'Moderator'}
                  </span>
                </div>
              </div>
              <div className="flex bg-gray-100 rounded-lg p-1">
                <button
                  onClick={() => setView('user')}
                  className={`px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 ${
                    view === 'user'
                      ? 'bg-white text-blue-600 shadow-sm'
                      : 'text-gray-600 hover:text-gray-900'
                  }`}
                >
                  <Users className="w-4 h-4" />
                  Customer View
                </button>
                <button
                  onClick={() => setView('admin')}
                  className={`px-4 py-2 rounded-md text-sm font-medium transition-colors flex items-center gap-2 ${
                    view === 'admin'
                      ? 'bg-white text-blue-600 shadow-sm'
                      : 'text-gray-600 hover:text-gray-900'
                  }`}
                >
                  <Settings className="w-4 h-4" />
                  Admin Panel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Content */}
      <div className="py-6">
        {view === 'user' ? <SnackCartUserOptimized /> : <SnackCartAdmin />}
      </div>

      {/* Module Info Footer */}
      <div className="bg-white border-t mt-12">
        <div className="max-w-7xl mx-auto px-4 py-8">
          <div className="text-center">
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              🍿 SnackCart Module
            </h3>
            <p className="text-gray-600 mb-4">
              Campus snack ordering system with Cash on Delivery (COD) payment
            </p>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
              <div className="text-center">
                <div className="bg-blue-100 w-12 h-12 rounded-lg flex items-center justify-center mx-auto mb-2">
                  🔍
                </div>
                <h4 className="font-medium text-gray-900">Smart Search</h4>
                <p className="text-sm text-gray-600">Trie-based prefix matching with O(prefix length) complexity</p>
              </div>
              
              <div className="text-center">
                <div className="bg-green-100 w-12 h-12 rounded-lg flex items-center justify-center mx-auto mb-2">
                  📦
                </div>
                <h4 className="font-medium text-gray-900">Inventory Management</h4>
                <p className="text-sm text-gray-600">Hash Map indexing for O(1) lookup and category filtering</p>
              </div>
              
              <div className="text-center">
                <div className="bg-purple-100 w-12 h-12 rounded-lg flex items-center justify-center mx-auto mb-2">
                  📊
                </div>
                <h4 className="font-medium text-gray-900">Analytics</h4>
                <p className="text-sm text-gray-600">Merge Sort algorithms for popularity rankings and revenue analysis</p>
              </div>
            </div>
            
            <div className="mt-6 text-xs text-gray-500">
              Part of the DSA-Based Hostel Management System • Educational Project
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
