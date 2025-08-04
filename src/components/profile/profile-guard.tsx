'use client'

import { useState, useEffect } from 'react'
import { useAuth } from '@/lib/firebase-context'
import { profileService } from '@/lib/profile-service'
import { ProfileSetup } from './profile-setup'
import { LoadingSpinner } from '@/components/ui/loading-spinner'

interface ProfileGuardProps {
  children: React.ReactNode
}

export function ProfileGuard({ children }: ProfileGuardProps) {
  const { user, loading: authLoading } = useAuth()
  const [profileComplete, setProfileComplete] = useState(false)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    checkProfileStatus()
  }, [user])

  const checkProfileStatus = async () => {
    if (!user) {
      setLoading(false)
      return
    }

    try {
      const isComplete = await profileService.isProfileComplete(user.uid)
      setProfileComplete(isComplete)
    } catch (error) {
      console.error('Error checking profile status:', error)
      setProfileComplete(false)
    } finally {
      setLoading(false)
    }
  }

  const handleProfileComplete = () => {
    setProfileComplete(true)
  }

  // Show loading while checking auth and profile status
  if (authLoading || loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <LoadingSpinner size="lg" />
          <p className="mt-4 text-gray-600">Loading your profile...</p>
        </div>
      </div>
    )
  }

  // If not authenticated, let children handle it (they will show login)
  if (!user) {
    return <>{children}</>
  }

  // If profile is not complete, show profile setup
  if (!profileComplete) {
    return <ProfileSetup onComplete={handleProfileComplete} />
  }

  // Profile is complete, show the main app
  return <>{children}</>
}
