import { createUserWithEmailAndPassword, signInWithEmailAndPassword } from 'firebase/auth'
import { ref, set } from 'firebase/database'
import { auth, realtimeDb } from './firebase'

// Admin accounts to create
const ADMIN_ACCOUNTS = [
  // Global Admins
  { email: 'admin1@hosteldada.com', password: 'admin123', role: 'admin', type: 'global', module: undefined },
  { email: 'admin2@hosteldada.com', password: 'admin456', role: 'admin', type: 'global', module: undefined },
  
  // SnackCart Admins
  { email: 'snackcart.admin1@hosteldada.com', password: 'snack123', role: 'admin', type: 'module', module: 'snackCart' },
  { email: 'snackcart.admin2@hosteldada.com', password: 'snack456', role: 'admin', type: 'module', module: 'snackCart' },
  
  // RoomieMatcher Admins
  { email: 'roomie.admin1@hosteldada.com', password: 'roomie123', role: 'admin', type: 'module', module: 'roomieMatcher' },
  { email: 'roomie.admin2@hosteldada.com', password: 'roomie456', role: 'admin', type: 'module', module: 'roomieMatcher' },
  
  // LaundryBalancer Admins
  { email: 'laundry.admin1@hosteldada.com', password: 'laundry123', role: 'admin', type: 'module', module: 'laundryBalancer' },
  { email: 'laundry.admin2@hosteldada.com', password: 'laundry456', role: 'admin', type: 'module', module: 'laundryBalancer' },
  
  // MessyMess Admins
  { email: 'mess.admin1@hosteldada.com', password: 'mess123', role: 'admin', type: 'module', module: 'messyMess' },
  { email: 'mess.admin2@hosteldada.com', password: 'mess456', role: 'admin', type: 'module', module: 'messyMess' },
  
  // HostelFixer Admins
  { email: 'maintenance.admin1@hosteldada.com', password: 'maintenance123', role: 'admin', type: 'module', module: 'hostelFixer' },
  { email: 'maintenance.admin2@hosteldada.com', password: 'maintenance456', role: 'admin', type: 'module', module: 'hostelFixer' }
]

// Test user accounts
const TEST_USERS = [
  { email: 'student@university.edu', password: 'student123', role: 'user', type: 'student', module: undefined },
  { email: 'demo@hosteldada.com', password: 'demo123', role: 'user', type: 'demo', module: undefined }
]

export async function createAdminAccounts() {
  const results = {
    created: [] as string[],
    failed: [] as { email: string, error: string }[],
    alreadyExists: [] as string[]
  }

  console.log('🚀 Starting admin account creation...')

  for (const account of [...ADMIN_ACCOUNTS, ...TEST_USERS]) {
    try {
      console.log(`Creating account: ${account.email}`)
      
      // Create the user account
      const userCredential = await createUserWithEmailAndPassword(
        auth, 
        account.email, 
        account.password
      )
      
      // Create user profile in Realtime Database
      const userProfileRef = ref(realtimeDb, `userProfiles/${userCredential.user.uid}`)
      await set(userProfileRef, {
        email: account.email,
        displayName: account.email.split('@')[0],
        role: account.role,
        type: account.type,
        module: account.module || null,
        isActive: true,
        joinedAt: new Date().toISOString(),
        totalOrders: 0,
        totalSpent: 0,
        hostelRoom: account.role === 'admin' ? 'Admin Office' : null,
        phoneNumber: ''
      })

      results.created.push(account.email)
      console.log(`✅ Created: ${account.email}`)
      
    } catch (error: any) {
      if (error.code === 'auth/email-already-in-use') {
        results.alreadyExists.push(account.email)
        console.log(`⚠️  Already exists: ${account.email}`)
      } else {
        results.failed.push({ 
          email: account.email, 
          error: error.message 
        })
        console.log(`❌ Failed: ${account.email} - ${error.message}`)
      }
    }
  }

  console.log('\n📊 Account Creation Summary:')
  console.log(`✅ Created: ${results.created.length}`)
  console.log(`⚠️  Already existed: ${results.alreadyExists.length}`)
  console.log(`❌ Failed: ${results.failed.length}`)

  return results
}

export async function testAdminLogin(email: string, password: string) {
  try {
    const userCredential = await signInWithEmailAndPassword(auth, email, password)
    console.log(`✅ Login successful for: ${email}`)
    return { success: true, user: userCredential.user }
  } catch (error: any) {
    console.log(`❌ Login failed for: ${email} - ${error.message}`)
    return { success: false, error: error.message }
  }
}
