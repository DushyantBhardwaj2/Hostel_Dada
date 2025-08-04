import { initializeApp } from 'firebase/app'
import { getAuth, GoogleAuthProvider } from 'firebase/auth'
import { getDatabase } from 'firebase/database'
import { getAnalytics } from 'firebase/analytics'

const firebaseConfig = {
  apiKey: process.env.NEXT_PUBLIC_FIREBASE_API_KEY || "AIzaSyAe9Bl1We4bxGIg3Avdfk5sfc3CbXZnfWI",
  authDomain: process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN || "hostel-dada.firebaseapp.com",
  databaseURL: process.env.NEXT_PUBLIC_FIREBASE_DATABASE_URL || "https://hostel-dada-default-rtdb.firebaseio.com",
  projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID || "hostel-dada",
  storageBucket: process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET || "hostel-dada.firebasestorage.app",
  messagingSenderId: process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID || "394184379850",
  appId: process.env.NEXT_PUBLIC_FIREBASE_APP_ID || "1:394184379850:web:1f791f278c83507bea5333",
  measurementId: process.env.NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID || "G-CVPNW6CESZ"
}

// Debug logging for development
if (typeof window !== 'undefined' && process.env.NODE_ENV === 'development') {
  console.log('Firebase Config:', firebaseConfig)
}

// Initialize Firebase
const app = initializeApp(firebaseConfig)

// Initialize Firebase services
export const auth = getAuth(app)
export const realtimeDb = getDatabase(app)
export const analytics = typeof window !== 'undefined' ? getAnalytics(app) : null

// Configure Google Auth Provider with domain restriction
export const googleProvider = new GoogleAuthProvider()
googleProvider.setCustomParameters({
  hd: 'nsut.ac.in' // Restrict to NSUT domain
})

export default app
