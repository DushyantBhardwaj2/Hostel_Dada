'use client'

import { useState } from 'react'
import { signInWithEmailAndPassword, createUserWithEmailAndPassword, signInWithPopup, signOut } from 'firebase/auth'
import { auth, googleProvider } from '@/lib/firebase'
import { validateEmailDomain, getDomainValidationError, validateEmailDomainBeforeSignup } from '@/lib/auth-utils'

export function LoginForm() {
  const [isLogin, setIsLogin] = useState(true)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const testFirebaseConfig = () => {
    console.log('Firebase Auth object:', auth)
    console.log('Firebase Auth config:', auth.config)
    console.log('Environment check:', {
      NODE_ENV: process.env.NODE_ENV,
      API_KEY: process.env.NEXT_PUBLIC_FIREBASE_API_KEY ? 'Present' : 'Missing'
    })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')

    try {
      // Validate email domain for regular sign-up
      if (!isLogin && !validateEmailDomainBeforeSignup(email)) {
        setError(getDomainValidationError())
        setLoading(false)
        return
      }

      if (isLogin) {
        const result = await signInWithEmailAndPassword(auth, email, password)
        // For existing users, we'll allow login but show a warning if not NSUT domain
        if (!validateEmailDomain(result.user)) {
          console.warn('User logged in with non-NSUT email')
        }
      } else {
        await createUserWithEmailAndPassword(auth, email, password)
      }
    } catch (error: any) {
      console.error('Firebase Auth Error:', error)
      console.error('Error code:', error.code)
      console.error('Error message:', error.message)
      
      // Provide user-friendly error messages
      if (error.code === 'auth/api-key-not-valid') {
        setError('Firebase configuration error. Please check your API key.')
      } else if (error.code === 'auth/user-not-found') {
        setError('No account found with this email.')
      } else if (error.code === 'auth/wrong-password') {
        setError('Incorrect password.')
      } else if (error.code === 'auth/email-already-in-use') {
        setError('An account with this email already exists.')
      } else if (error.code === 'auth/weak-password') {
        setError('Password is too weak. Please use at least 6 characters.')
      } else {
        setError(error.message)
      }
    }
    setLoading(false)
  }

  const handleGoogleSignIn = async () => {
    setLoading(true)
    setError('')

    try {
      const result = await signInWithPopup(auth, googleProvider)
      
      // Validate domain after successful Google sign-in
      if (!validateEmailDomain(result.user)) {
        // Sign out the user if domain doesn't match
        await signOut(auth)
        setError(getDomainValidationError())
        setLoading(false)
        return
      }

      // If domain is valid, login is successful and Firebase context will handle the rest
    } catch (error: any) {
      console.error('Google Sign-in Error:', error)
      
      if (error.code === 'auth/popup-closed-by-user') {
        setError('Sign-in was cancelled. Please try again.')
      } else if (error.code === 'auth/popup-blocked') {
        setError('Popup was blocked. Please allow popups and try again.')
      } else {
        setError('Google sign-in failed. Please try again.')
      }
    }
    setLoading(false)
  }

  return (
    <div className="min-h-screen flex items-center justify-center gradient-bg p-4">
      <div className="bg-white rounded-lg shadow-xl p-8 w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">🏠 Hostel Dada</h1>
          <p className="text-gray-600">Smart hostel management for modern students</p>
        </div>

        <div className="flex rounded-lg bg-gray-100 p-1 mb-6">
          <button
            className={`flex-1 py-2 px-4 rounded-md transition-colors ${
              isLogin 
                ? 'bg-white text-blue-600 shadow-sm' 
                : 'text-gray-600 hover:text-gray-900'
            }`}
            onClick={() => setIsLogin(true)}
          >
            Login
          </button>
          <button
            className={`flex-1 py-2 px-4 rounded-md transition-colors ${
              !isLogin 
                ? 'bg-white text-blue-600 shadow-sm' 
                : 'text-gray-600 hover:text-gray-900'
            }`}
            onClick={() => setIsLogin(false)}
          >
            Sign Up
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Email {!isLogin && <span className="text-red-500 text-xs">(must be @nsut.ac.in)</span>}
            </label>
            <input
              type="email"
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder={isLogin ? "your@email.com" : "yourname@nsut.ac.in"}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Password
            </label>
            <input
              type="password"
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          {error && (
            <div className="text-red-600 text-sm bg-red-50 p-3 rounded-md">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {loading ? 'Please wait...' : (isLogin ? 'Login' : 'Sign Up')}
          </button>
        </form>

        {/* Divider */}
        <div className="my-6 flex items-center">
          <div className="flex-1 border-t border-gray-300"></div>
          <span className="px-4 text-sm text-gray-600">or</span>
          <div className="flex-1 border-t border-gray-300"></div>
        </div>

        {/* Google Sign-in Button */}
        <button
          onClick={handleGoogleSignIn}
          disabled={loading}
          className="w-full bg-white text-gray-700 py-2 px-4 rounded-md border border-gray-300 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center justify-center space-x-2"
        >
          <svg className="w-5 h-5" viewBox="0 0 24 24">
            <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
            <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
            <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
            <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
          </svg>
          <span>{loading ? 'Please wait...' : `${isLogin ? 'Sign in' : 'Sign up'} with Google (NSUT)`}</span>
        </button>

        <div className="mt-4 text-center text-xs text-gray-500">
          <p>🎓 Only NSUT (@nsut.ac.in) email addresses are allowed</p>
          <p>for college hostel management system</p>
        </div>

        {process.env.NODE_ENV === 'development' && (
          <button
            onClick={testFirebaseConfig}
            className="w-full mt-4 bg-gray-600 text-white py-2 px-4 rounded-md hover:bg-gray-700 transition-colors text-sm"
          >
            🔧 Test Firebase Config (Dev Only)
          </button>
        )}

        <div className="mt-6 text-center text-sm text-gray-600">
          <p>Demo credentials:</p>
          <p>Email: demo@hosteldada.com</p>
          <p>Password: demo123</p>
        </div>
      </div>
    </div>
  )
}
