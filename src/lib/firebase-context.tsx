'use client'

import { createContext, useContext, useEffect, useState } from 'react'
import { User, onAuthStateChanged } from 'firebase/auth'
import { auth } from '@/lib/firebase'
import { isAdminEmail, getAdminRole } from '@/lib/admin-config'
import { profileService } from '@/lib/profile-service'

interface FirebaseContextType {
  user: User | null
  loading: boolean
  isAdmin: boolean
  adminRole: {
    isAdmin: boolean
    isGlobal: boolean
    modules: string[]
  }
}

const FirebaseContext = createContext<FirebaseContextType>({
  user: null,
  loading: true,
  isAdmin: false,
  adminRole: {
    isAdmin: false,
    isGlobal: false,
    modules: []
  }
})

export const useAuth = () => useContext(FirebaseContext)

export function FirebaseProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const [isAdmin, setIsAdmin] = useState(false)
  const [adminRole, setAdminRole] = useState<{
    isAdmin: boolean
    isGlobal: boolean
    modules: string[]
  }>({
    isAdmin: false,
    isGlobal: false,
    modules: []
  })

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (user) => {
      setUser(user)
      
      if (user?.email) {
        const admin = isAdminEmail(user.email)
        const role = getAdminRole(user.email)
        setIsAdmin(admin)
        setAdminRole(role)

        // Check if this is a new user and create initial profile
        try {
          const existingProfile = await profileService.getUserProfile(user.uid)
          if (!existingProfile) {
            await profileService.createUserProfile(
              user.uid,
              user.email,
              user.displayName || undefined
            )
          }
        } catch (error) {
          console.error('Error handling user profile:', error)
        }
      } else {
        setIsAdmin(false)
        setAdminRole({
          isAdmin: false,
          isGlobal: false,
          modules: []
        })
      }
      
      setLoading(false)
    })

    return unsubscribe
  }, [])

  return (
    <FirebaseContext.Provider value={{ user, loading, isAdmin, adminRole }}>
      {children}
    </FirebaseContext.Provider>
  )
}
