'use client'

import { useState } from 'react'
import { signOut } from 'firebase/auth'
import { auth } from '@/lib/firebase'
import { useAuth } from '@/lib/firebase-context'
import { SnackCart } from '@/components/snackcart/snackcart'
import { RoomieMatcher } from '@/components/roomie/roomie-matcher'
import { getAdminRole } from '@/lib/admin-config'

const modules = [
  {
    id: 'snackcart',
    name: 'SnackCart',
    description: 'Order snacks and food from hostel canteen with COD payment',
    icon: '🛒',
    color: 'bg-green-500',
    status: 'active',
    features: ['Smart Search', 'Real-time Orders', 'Inventory Management']
  },
  {
    id: 'roomies',
    name: 'RoomieMatcher',
    description: 'Find perfect roommates based on compatibility algorithms',
    icon: '👥',
    color: 'bg-blue-500',
    status: 'active',
    features: ['Compatibility Matching', 'Room Assignment', 'Analytics Dashboard']
  },
  {
    id: 'laundry',
    name: 'LaundryBalancer',
    description: 'Book laundry slots and track status with queue management',
    icon: '👕',
    color: 'bg-purple-500',
    status: 'coming-soon',
    features: ['Slot Booking', 'Status Tracking', 'Queue Management']
  },
  {
    id: 'mess',
    name: 'MessyMess',
    description: 'Mess menu, feedback and meal management system',
    icon: '🍽️',
    color: 'bg-orange-500',
    status: 'coming-soon',
    features: ['Daily Menu', 'Feedback System', 'Meal Planning']
  },
  {
    id: 'maintenance',
    name: 'HostelFixer',
    description: 'Report and track maintenance issues with priority queues',
    icon: '🔧',
    color: 'bg-red-500',
    status: 'coming-soon',
    features: ['Issue Reporting', 'Status Tracking', 'Priority Management']
  }
]

export function Dashboard() {
  const { user } = useAuth()
  const [activeModule, setActiveModule] = useState('home')

  // Proper admin detection as per README specifications
  const adminRole = user?.email ? getAdminRole(user.email) : { isAdmin: false, isGlobal: false, modules: [] }
  const isAdmin = adminRole.isAdmin

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
              {isAdmin && (
                <div className="flex items-center space-x-2">
                  <span className="bg-red-100 text-red-800 text-xs font-medium px-2 py-1 rounded-full">
                    {adminRole.isGlobal ? 'Global Admin' : 'Module Admin'}
                  </span>
                  {!adminRole.isGlobal && (
                    <span className="text-xs text-gray-600">
                      Access: {adminRole.modules.join(', ')}
                    </span>
                  )}
                </div>
              )}
            </div>
            <div className="flex items-center space-x-4">
              <div className="hidden sm:flex items-center space-x-2">
                <span className="text-sm text-gray-600">Welcome,</span>
                <span className="text-sm font-medium text-gray-900">
                  {user?.email?.split('@')[0] || 'User'}
                </span>
              </div>
              <button className="p-2 text-gray-600 hover:text-gray-900 relative">
                🔔
                <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                  3
                </span>
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
                  className={`bg-white rounded-lg shadow hover:shadow-md transition-shadow cursor-pointer p-6 relative ${
                    module.status === 'coming-soon' ? 'opacity-75' : ''
                  }`}
                  onClick={() => module.status === 'active' && setActiveModule(module.id)}
                >
                  {module.status === 'coming-soon' && (
                    <div className="absolute top-2 right-2 bg-yellow-100 text-yellow-800 text-xs font-medium px-2 py-1 rounded-full">
                      Coming Soon
                    </div>
                  )}
                  {module.status === 'active' && (
                    <div className="absolute top-2 right-2 bg-green-100 text-green-800 text-xs font-medium px-2 py-1 rounded-full">
                      Active
                    </div>
                  )}
                  
                  <div className="flex items-center mb-4">
                    <div className={`p-3 rounded-lg ${module.color} text-white text-2xl mr-4`}>
                      {module.icon}
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">{module.name}</h3>
                    </div>
                  </div>
                  
                  <p className="text-gray-600 mb-3">{module.description}</p>
                  
                  {/* Key Features */}
                  <div className="mb-4">
                    <p className="text-xs text-gray-500 mb-2">Key Features:</p>
                    <div className="flex flex-wrap gap-1">
                      {module.features?.map((feature) => (
                        <span
                          key={feature}
                          className="bg-blue-50 text-blue-700 text-xs px-2 py-1 rounded"
                        >
                          {feature}
                        </span>
                      ))}
                    </div>
                  </div>
                  
                  <div className="mt-4">
                    <span className={`font-medium ${
                      module.status === 'active' 
                        ? 'text-blue-600 hover:text-blue-800' 
                        : 'text-gray-400'
                    }`}>
                      {module.status === 'active' ? 'Explore →' : 'In Development...'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : activeModule === 'snackcart' ? (
          // SnackCart Module - Fully Integrated
          <SnackCart />
        ) : activeModule === 'roomies' ? (
          // RoomieMatcher Module - Fully Integrated
          <RoomieMatcher />
        ) : (
          // Other Modules - Coming Soon
          <div className="bg-white rounded-lg shadow p-8">
            <div className="text-center">
              <div className="text-6xl mb-4">
                {modules.find(m => m.id === activeModule)?.icon}
              </div>
              <h2 className="text-2xl font-bold text-gray-900 mb-2">
                {modules.find(m => m.id === activeModule)?.name}
              </h2>
              <p className="text-gray-600 mb-6 max-w-md mx-auto">
                {modules.find(m => m.id === activeModule)?.description}
              </p>
              
              {/* Features Preview */}
              <div className="bg-blue-50 rounded-lg p-6 mb-8 max-w-lg mx-auto">
                <h3 className="font-semibold text-blue-900 mb-3">
                  ✨ Key Features
                </h3>
                <div className="flex flex-wrap justify-center gap-2">
                  {modules.find(m => m.id === activeModule)?.features?.map((feature) => (
                    <span
                      key={feature}
                      className="bg-blue-100 text-blue-800 text-sm px-3 py-1 rounded-full"
                    >
                      {feature}
                    </span>
                  ))}
                </div>
              </div>
              
              <div className="space-y-4">
                <p className="text-lg text-gray-800">
                  🚀 This module is in development!
                </p>
                <p className="text-sm text-gray-600">
                  Educational DSA implementations will demonstrate real-world algorithms
                </p>
                <button 
                  className="bg-gray-400 text-white px-6 py-3 rounded-lg cursor-not-allowed"
                  disabled
                >
                  Coming Soon - {modules.find(m => m.id === activeModule)?.name}
                </button>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Footer with Project Info */}
      <footer className="bg-white border-t mt-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="text-center">
            <p className="text-sm text-gray-600">
              🎓 DSA-Based Hostel Management System • Educational Project
            </p>
            <p className="text-xs text-gray-500 mt-1">
              Demonstrating Data Structures & Algorithms in practical campus scenarios
            </p>
          </div>
        </div>
      </footer>
    </div>
  )
}
