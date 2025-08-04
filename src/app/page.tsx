'use client'

import { useAuth } from '@/lib/firebase-context'
import { LoginForm } from '@/components/auth/login-form'
import { Dashboard } from '@/components/dashboard/dashboard'
import { LoadingSpinner } from '@/components/ui/loading-spinner'
import { ProfileGuard } from '@/components/profile/profile-guard'

export default function Home() {
  const { user, loading } = useAuth()

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center gradient-bg">
        <div className="text-center text-white">
          <LoadingSpinner size="lg" />
          <p className="mt-4 text-lg">Loading Hostel Dada...</p>
        </div>
      </div>
    )
  }

  return (
    <ProfileGuard>
      {user ? <Dashboard /> : <LoginForm />}
    </ProfileGuard>
  )
}
