package firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database

/**
 * 🔥 Firebase Configuration for Hostel Dada Web Application
 * 
 * Central configuration for all Firebase services:
 * - Authentication (Email/Password)
 * - Realtime Database
 * - Security Rules
 */
object FirebaseConfig {
    
    // Firebase configuration object - REAL PRODUCTION CONFIG
    private val firebaseConfig = js("""
        {
            apiKey: "AIzaSyAe9Bl1We4bxGIg3Avdfk5sfc3CbXZnfWI",
            authDomain: "hostel-dada.firebaseapp.com",
            databaseURL: "https://hostel-dada-default-rtdb.firebaseio.com",
            projectId: "hostel-dada",
            storageBucket: "hostel-dada.firebasestorage.app",
            messagingSenderId: "394184379850",
            appId: "1:394184379850:web:1f791f278c83507bea5333",
            measurementId: "G-CVPNW6CESZ"
        }
    """)
    
    // Firebase app instance
    private lateinit var app: FirebaseApp
    
    // Firebase services
    val auth: FirebaseAuth by lazy { Firebase.auth }
    val database: FirebaseDatabase by lazy { Firebase.database }
    
    /**
     * Initialize Firebase services with real configuration
     * Call this once at application startup
     */
    fun initialize() {
        try {
            // Initialize Firebase with the real config
            app = Firebase.initializeApp(firebaseConfig)
            console.log("🔥 Firebase initialized successfully with real configuration")
            console.log("📊 Project ID: hostel-dada")
            console.log("🌐 Database URL: https://hostel-dada-default-rtdb.firebaseio.com")
            console.log("🔐 Auth Domain: hostel-dada.firebaseapp.com")
            
            // Test database connection
            testDatabaseConnection()
            
        } catch (e: Exception) {
            console.error("❌ Firebase initialization failed: ${e.message}")
            console.error("🔧 Please check your Firebase project configuration")
            throw e
        }
    }
    
    /**
     * Test Firebase database connection
     */
    private fun testDatabaseConnection() {
        try {
            val testRef = database.reference("test/connection")
            testRef.set(js("""{ 
                "timestamp": "${js("Date.now()")}",
                "status": "connected",
                "message": "Hostel Dada Firebase connection successful"
            }"""))
            console.log("✅ Database connection test successful")
        } catch (e: Exception) {
            console.warn("⚠️ Database connection test failed: ${e.message}")
        }
    }
    
    /**
     * Check if Firebase is properly initialized
     */
    fun isInitialized(): Boolean {
        return ::app.isInitialized
    }
    
    /**
     * Get database reference for specific path
     */
    fun getDatabaseReference(path: String) = database.reference(path)
    
    /**
     * Common database paths for the application
     * Enhanced for all Phase 2 modules
     */
    object DatabasePaths {
        // Core paths
        const val USERS = "users"
        const val HOSTELS = "hostels"
        const val ADMIN_SETTINGS = "adminSettings"
        const val NOTIFICATIONS = "notifications"
        const val ANALYTICS = "analytics"
        
        // SnackCart Module
        const val SNACK_ORDERS = "snackOrders"
        const val SNACK_INVENTORY = "snackInventory"
        const val SNACK_ITEMS = "snackItems"
        const val SNACK_VENDORS = "snackVendors"
        
        // RoomieMatcher Module
        const val ROOMMATE_REQUESTS = "roommateRequests"
        const val ROOMMATE_PROFILES = "roommateProfiles"
        const val ROOM_ALLOCATIONS = "roomAllocations"
        const val COMPATIBILITY_SCORES = "compatibilityScores"
        
        // LaundryBalancer Module
        const val LAUNDRY_BOOKINGS = "laundryBookings"
        const val LAUNDRY_FACILITIES = "laundryFacilities"
        const val LAUNDRY_MACHINES = "laundryMachines"
        const val LAUNDRY_ANALYTICS = "laundryAnalytics"
        
        // MessyMess Module
        const val MESS_FEEDBACK = "messFeedback"
        const val MESS_FACILITIES = "messFacilities"
        const val MENU_ITEMS = "menuItems"
        const val MESS_REVIEWS = "messReviews"
        const val NUTRITIONAL_DATA = "nutritionalData"
        
        // HostelFixer Module
        const val MAINTENANCE_REQUESTS = "maintenanceRequests"
        const val MAINTENANCE_STAFF = "maintenanceStaff"
        const val WORK_ORDERS = "workOrders"
        const val ISSUE_ANALYTICS = "issueAnalytics"
        const val SLA_CONFIG = "slaConfig"
        
        // Dashboard & Shared Data
        const val DASHBOARD_METRICS = "dashboardMetrics"
        const val REAL_TIME_UPDATES = "realTimeUpdates"
        const val USER_PREFERENCES = "userPreferences"
        const val MODULE_CONFIGS = "moduleConfigs"
    }
}

/**
 * 🔒 Firebase Security Rules (for reference)
 * These should be applied in Firebase Console
 */
object SecurityRules {
    val rules = """
    {
      "rules": {
        "users": {
          "${'$'}uid": {
            ".read": "${'$'}uid === auth.uid",
            ".write": "${'$'}uid === auth.uid"
          }
        },
        "hostels": {
          ".read": "auth != null",
          ".write": false
        },
        "snackOrders": {
          "${'$'}hostelId": {
            "${'$'}orderId": {
              ".read": "auth != null && data.child('userId').val() === auth.uid",
              ".write": "auth != null && (!data.exists() || data.child('userId').val() === auth.uid)"
            }
          }
        },
        "roommateRequests": {
          "${'$'}hostelId": {
            "${'$'}requestId": {
              ".read": "auth != null",
              ".write": "auth != null && (!data.exists() || data.child('createdBy').val() === auth.uid)"
            }
          }
        },
        "laundryBookings": {
          "${'$'}hostelId": {
            "${'$'}bookingId": {
              ".read": "auth != null",
              ".write": "auth != null && (!data.exists() || data.child('userId').val() === auth.uid)"
            }
          }
        },
        "messFeedback": {
          "${'$'}hostelId": {
            "${'$'}feedbackId": {
              ".read": "auth != null",
              ".write": "auth != null && (!data.exists() || data.child('userId').val() === auth.uid)"
            }
          }
        },
        "maintenanceRequests": {
          "${'$'}hostelId": {
            "${'$'}requestId": {
              ".read": "auth != null",
              ".write": "auth != null && (!data.exists() || data.child('reportedBy').val() === auth.uid)"
            }
          }
        },
        "adminSettings": {
          ".read": false,
          ".write": false
        },
        "notifications": {
          "${'$'}userId": {
            ".read": "${'$'}userId === auth.uid",
            ".write": false
          }
        }
      }
    }
    """.trimIndent()
}
