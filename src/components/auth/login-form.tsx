'use client'

import { useState } from 'react'
import { signInWithEmailAndPassword, createUserWithEmailAndPassword } from 'firebase/auth'
import { auth } from '@/lib/firebase'

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
      if (isLogin) {
        await signInWithEmailAndPassword(auth, email, password)
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
              Email
            </label>
            <input
              type="email"
              required
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="your@email.com"
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
