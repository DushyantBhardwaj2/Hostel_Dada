// Profile Service for managing user profiles using Realtime Database

import { UserProfile, ProfileFormData } from './profile-models'
import { realtimeDb } from './firebase'
import { ref, get, set, update } from 'firebase/database'

export class ProfileService {
  private static instance: ProfileService
  
  static getInstance(): ProfileService {
    if (!ProfileService.instance) {
      ProfileService.instance = new ProfileService()
    }
    return ProfileService.instance
  }

  /**
   * Check if user has completed their profile
   */
  async isProfileComplete(uid: string): Promise<boolean> {
    try {
      const profileRef = ref(realtimeDb, `userProfiles/${uid}`)
      const snapshot = await get(profileRef)
      
      if (!snapshot.exists()) {
        return false
      }
      
      const profile = snapshot.val() as UserProfile
      return profile.isProfileComplete === true
    } catch (error) {
      console.error('Error checking profile completion:', error)
      return false
    }
  }

  /**
   * Get user profile
   */
  async getUserProfile(uid: string): Promise<UserProfile | null> {
    try {
      const profileRef = ref(realtimeDb, `userProfiles/${uid}`)
      const snapshot = await get(profileRef)
      
      if (!snapshot.exists()) {
        return null
      }
      
      const data = snapshot.val()
      return {
        ...data,
        createdAt: data.createdAt ? new Date(data.createdAt) : new Date(),
        updatedAt: data.updatedAt ? new Date(data.updatedAt) : new Date(),
        lastLoginAt: data.lastLoginAt ? new Date(data.lastLoginAt) : undefined
      } as UserProfile
    } catch (error) {
      console.error('Error getting user profile:', error)
      return null
    }
  }

  /**
   * Create initial user profile
   */
  async createUserProfile(uid: string, email: string, displayName?: string): Promise<void> {
    try {
      const existingProfile = await this.getUserProfile(uid)
      if (existingProfile) {
        return // Profile already exists
      }

      const initialProfile: Partial<UserProfile> = {
        uid,
        email,
        fullName: displayName || '',
        isProfileComplete: false,
        createdAt: new Date(),
        updatedAt: new Date(),
        notificationPreferences: {
          email: true,
          sms: false,
          push: true
        }
      }

      const profileRef = ref(realtimeDb, `userProfiles/${uid}`)
      await set(profileRef, {
        ...initialProfile,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      })
    } catch (error) {
      console.error('Error creating user profile:', error)
      throw error
    }
  }

  /**
   * Update user profile with form data
   */
  async updateProfile(uid: string, formData: ProfileFormData): Promise<void> {
    try {
      const profileData: any = {
        ...formData,
        uid,
        isProfileComplete: true,
        updatedAt: new Date().toISOString()
      }

      // Remove any undefined values
      Object.keys(profileData).forEach(key => {
        if (profileData[key] === undefined) {
          delete profileData[key]
        }
      })

      const profileRef = ref(realtimeDb, `userProfiles/${uid}`)
      await update(profileRef, profileData)
    } catch (error) {
      console.error('Error updating profile:', error)
      throw error
    }
  }

  /**
   * Update specific profile fields
   */
  async updateProfileFields(uid: string, fields: Partial<UserProfile>): Promise<void> {
    try {
      const updates: any = {
        ...fields,
        updatedAt: new Date().toISOString()
      }

      // Remove any undefined values
      Object.keys(updates).forEach(key => {
        if (updates[key] === undefined) {
          delete updates[key]
        }
      })

      const profileRef = ref(realtimeDb, `userProfiles/${uid}`)
      await update(profileRef, updates)
    } catch (error) {
      console.error('Error updating profile fields:', error)
      throw error
    }
  }

  /**
   * Update last login timestamp
   */
  async updateLastLogin(uid: string): Promise<void> {
    try {
      const profileRef = ref(realtimeDb, `userProfiles/${uid}`)
      await update(profileRef, {
        lastLoginAt: new Date().toISOString()
      })
    } catch (error) {
      console.error('Error updating last login:', error)
      // Don't throw error for login timestamp update
    }
  }

  /**
   * Delete user profile
   */
  async deleteProfile(uid: string): Promise<void> {
    try {
      const profileRef = ref(realtimeDb, `userProfiles/${uid}`)
      await set(profileRef, null)
    } catch (error) {
      console.error('Error deleting profile:', error)
      throw error
    }
  }

  /**
   * Get all profiles (admin function)
   */
  async getAllProfiles(): Promise<UserProfile[]> {
    try {
      const profilesRef = ref(realtimeDb, 'userProfiles')
      const snapshot = await get(profilesRef)
      
      if (!snapshot.exists()) {
        return []
      }
      
      const data = snapshot.val()
      return Object.values(data).map((profile: any) => ({
        ...profile,
        createdAt: profile.createdAt ? new Date(profile.createdAt) : new Date(),
        updatedAt: profile.updatedAt ? new Date(profile.updatedAt) : new Date(),
        lastLoginAt: profile.lastLoginAt ? new Date(profile.lastLoginAt) : undefined
      })) as UserProfile[]
    } catch (error) {
      console.error('Error getting all profiles:', error)
      return []
    }
  }

  /**
   * Search profiles by criteria
   */
  async searchProfiles(criteria: Partial<UserProfile>): Promise<UserProfile[]> {
    try {
      const allProfiles = await this.getAllProfiles()
      
      return allProfiles.filter(profile => {
        return Object.entries(criteria).every(([key, value]) => {
          const profileValue = profile[key as keyof UserProfile]
          if (typeof value === 'string' && typeof profileValue === 'string') {
            return profileValue.toLowerCase().includes(value.toLowerCase())
          }
          return profileValue === value
        })
      })
    } catch (error) {
      console.error('Error searching profiles:', error)
      return []
    }
  }

  /**
   * Get profiles by hostel
   */
  async getProfilesByHostel(hostelId: string): Promise<UserProfile[]> {
    try {
      const allProfiles = await this.getAllProfiles()
      return allProfiles.filter(profile => profile.hostelId === hostelId)
    } catch (error) {
      console.error('Error getting profiles by hostel:', error)
      return []
    }
  }
}

// Export singleton instance
export const profileService = ProfileService.getInstance()
