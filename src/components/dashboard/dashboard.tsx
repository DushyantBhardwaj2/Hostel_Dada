'use client'

import { useState } from 'react'
import { signOut } from 'firebase/auth'
import { auth } from '@/lib/firebase'

const modules = [
  {
    id: 'snackcart',
    name: 'SnackCart',
    description: 'Order snacks and food from hostel canteen',
    icon: '🛒',
    color: 'bg-green-500'
  },
  {
    id: 'roomies',
    name: 'RoomieMatcher',
    description: 'Find perfect roommates based on compatibility',
    icon: '👥',
    color: 'bg-blue-500'
  },
  {
    id: 'laundry',
    name: 'LaundryBalancer',
    description: 'Book laundry slots and track status',
    icon: '👕',
    color: 'bg-purple-500'
  },
  {
    id: 'mess',
    name: 'MessyMess',
    description: 'Mess menu, feedback and meal management',
    icon: '🍽️',
    color: 'bg-orange-500'
  },
  {
    id: 'maintenance',
    name: 'HostelFixer',
    description: 'Report and track maintenance issues',
    icon: '🔧',
    color: 'bg-red-500'
  }
]

export function Dashboard() {
  const [activeModule, setActiveModule] = useState('home')

  const handleLogout = () => {
    signOut(auth)
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <span className="text-2xl mr-3">🏠</span>
              <h1 className="text-2xl font-bold text-gray-900">Hostel Dada</h1>
            </div>
            <div className="flex items-center space-x-4">
              <button className="p-2 text-gray-600 hover:text-gray-900">
                🔔
              </button>
              <button
                onClick={handleLogout}
                className="bg-red-100 text-red-700 px-4 py-2 rounded-md hover:bg-red-200 transition-colors"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Navigation Tabs */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex space-x-8 overflow-x-auto">
            <button
              className={`py-4 px-1 border-b-2 font-medium text-sm whitespace-nowrap ${
                activeModule === 'home'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700'
              }`}
              onClick={() => setActiveModule('home')}
            >
              🏠 Home
            </button>
            {modules.map((module) => (
              <button
                key={module.id}
                className={`py-4 px-1 border-b-2 font-medium text-sm whitespace-nowrap ${
                  activeModule === module.id
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700'
                }`}
                onClick={() => setActiveModule(module.id)}
              >
                {module.icon} {module.name}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {activeModule === 'home' ? (
          <div className="space-y-8">
            {/* Welcome Section */}
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">
                Welcome to Hostel Dada! 🎉
              </h2>
              <p className="text-gray-600 text-lg">
                Your all-in-one hostel management solution. Manage your hostel life 
                seamlessly with our integrated modules for food ordering, roommate 
                matching, laundry management, mess services, and maintenance requests.
              </p>
            </div>

            {/* Modules Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {modules.map((module) => (
                <div
                  key={module.id}
                  className="bg-white rounded-lg shadow hover:shadow-md transition-shadow cursor-pointer p-6"
                  onClick={() => setActiveModule(module.id)}
                >
                  <div className="flex items-center mb-4">
                    <div className={`p-3 rounded-lg ${module.color} text-white text-2xl mr-4`}>
                      {module.icon}
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">{module.name}</h3>
                    </div>
                  </div>
                  <p className="text-gray-600">{module.description}</p>
                  <div className="mt-4">
                    <span className="text-blue-600 font-medium hover:text-blue-800">
                      Explore →
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          // Module Content
          <div className="bg-white rounded-lg shadow p-8">
            <div className="text-center">
              <div className="text-6xl mb-4">
                {modules.find(m => m.id === activeModule)?.icon}
              </div>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">
                {modules.find(m => m.id === activeModule)?.name}
              </h2>
              <p className="text-gray-600 mb-8 max-w-md mx-auto">
                {modules.find(m => m.id === activeModule)?.description}
              </p>
              <div className="space-y-4">
                <p className="text-lg text-gray-800">
                  🚀 This module is ready for development!
                </p>
                <button className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors">
                  Start Using {modules.find(m => m.id === activeModule)?.name}
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}
