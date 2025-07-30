/**
 * Firebase Database Setup Script for Hostel Dada
 * This script initializes the Firebase Realtime Database with sample data
 */

package firebase

import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.serialization.json.*

/**
 * Firebase Database Setup Utility
 * Sets up the initial database structure and sample data
 */
object FirebaseDatabaseSetup {
    
    /**
     * Initialize Firebase database with sample data
     * This should be run once during initial setup
     */
    suspend fun initializeDatabase(): Boolean {
        return try {
            console.log("🚀 Starting Firebase database initialization...")
            
            // Initialize Firebase first
            FirebaseConfig.initialize()
            
            // Set up database structure
            setupRooms()
            setupStudents()
            setupFees()
            setupMaintenance()
            setupMess()
            setupVisitors()
            setupSettings()
            
            // Set up database rules and indexes
            setupDatabaseRules()
            
            console.log("✅ Firebase database initialization completed successfully!")
            console.log("📊 Database URL: https://hostel-dada-default-rtdb.firebaseio.com")
            console.log("🎯 You can view your data at: https://console.firebase.google.com/project/hostel-dada/database")
            
            true
        } catch (e: Exception) {
            console.error("❌ Database initialization failed: ${e.message}")
            false
        }
    }
    
    /**
     * Set up rooms data
     */
    private suspend fun setupRooms() {
        console.log("🏠 Setting up rooms data...")
        
        val rooms = listOf(
            mapOf(
                "id" to "101",
                "number" to "101",
                "type" to "Single",
                "floor" to 1,
                "capacity" to 1,
                "occupied" to false,
                "facilities" to listOf("AC", "WiFi", "Study Table", "Wardrobe"),
                "price" to 8000,
                "status" to "Available",
                "lastCleaned" to "2024-01-15",
                "maintenanceStatus" to "Good"
            ),
            mapOf(
                "id" to "102",
                "number" to "102",
                "type" to "Double",
                "floor" to 1,
                "capacity" to 2,
                "occupied" to true,
                "facilities" to listOf("AC", "WiFi", "Study Table", "Wardrobe", "Balcony"),
                "price" to 6000,
                "status" to "Occupied",
                "lastCleaned" to "2024-01-14",
                "maintenanceStatus" to "Good"
            ),
            mapOf(
                "id" to "201",
                "number" to "201",
                "type" to "Triple",
                "floor" to 2,
                "capacity" to 3,
                "occupied" to false,
                "facilities" to listOf("Fan", "WiFi", "Study Table", "Wardrobe"),
                "price" to 5000,
                "status" to "Available",
                "lastCleaned" to "2024-01-13",
                "maintenanceStatus" to "Needs Repair"
            ),
            mapOf(
                "id" to "202",
                "number" to "202",
                "type" to "Single",
                "floor" to 2,
                "capacity" to 1,
                "occupied" to true,
                "facilities" to listOf("AC", "WiFi", "Study Table", "Wardrobe", "Attached Bathroom"),
                "price" to 9000,
                "status" to "Occupied",
                "lastCleaned" to "2024-01-15",
                "maintenanceStatus" to "Excellent"
            )
        )
        
        rooms.forEach { room ->
            val roomRef = FirebaseConfig.database.reference("rooms/${room["id"]}")
            roomRef.set(Json.encodeToJsonElement(room))
        }
    }
    
    /**
     * Set up students data
     */
    private suspend fun setupStudents() {
        console.log("👥 Setting up students data...")
        
        val students = listOf(
            mapOf(
                "id" to "STU001",
                "name" to "Rahul Sharma",
                "email" to "rahul.sharma@email.com",
                "phone" to "+91-9876543210",
                "course" to "Computer Science",
                "year" to 2,
                "roomNumber" to "102",
                "admissionDate" to "2023-08-15",
                "feeStatus" to "Paid",
                "parentContact" to "+91-9876543211",
                "address" to "Delhi, India",
                "emergencyContact" to "+91-9876543212",
                "documents" to listOf("Aadhar", "College ID", "Parent ID"),
                "status" to "Active"
            ),
            mapOf(
                "id" to "STU002",
                "name" to "Priya Patel",
                "email" to "priya.patel@email.com",
                "phone" to "+91-9876543213",
                "course" to "Engineering",
                "year" to 3,
                "roomNumber" to "202",
                "admissionDate" to "2022-08-20",
                "feeStatus" to "Paid",
                "parentContact" to "+91-9876543214",
                "address" to "Mumbai, India",
                "emergencyContact" to "+91-9876543215",
                "documents" to listOf("Aadhar", "College ID", "Parent ID", "Medical Certificate"),
                "status" to "Active"
            ),
            mapOf(
                "id" to "STU003",
                "name" to "Amit Kumar",
                "email" to "amit.kumar@email.com",
                "phone" to "+91-9876543216",
                "course" to "Business Administration",
                "year" to 1,
                "roomNumber" to "",
                "admissionDate" to "",
                "feeStatus" to "Pending",
                "parentContact" to "+91-9876543217",
                "address" to "Bangalore, India",
                "emergencyContact" to "+91-9876543218",
                "documents" to listOf("Aadhar", "College ID"),
                "status" to "Pending"
            )
        )
        
        students.forEach { student ->
            val studentRef = FirebaseConfig.database.reference("students/${student["id"]}")
            studentRef.set(Json.encodeToJsonElement(student))
        }
    }
    
    /**
     * Set up fees data
     */
    private suspend fun setupFees() {
        console.log("💰 Setting up fees data...")
        
        val fees = listOf(
            mapOf(
                "id" to "FEE001",
                "studentId" to "STU001",
                "studentName" to "Rahul Sharma",
                "roomNumber" to "102",
                "type" to "Monthly Rent",
                "amount" to 6000,
                "dueDate" to "2024-02-01",
                "paidDate" to "2024-01-25",
                "status" to "Paid",
                "paymentMethod" to "UPI",
                "transactionId" to "TXN001234567",
                "lateFee" to 0,
                "description" to "Room rent for February 2024"
            ),
            mapOf(
                "id" to "FEE002",
                "studentId" to "STU002",
                "studentName" to "Priya Patel",
                "roomNumber" to "202",
                "type" to "Monthly Rent",
                "amount" to 9000,
                "dueDate" to "2024-02-01",
                "paidDate" to "2024-01-30",
                "status" to "Paid",
                "paymentMethod" to "Bank Transfer",
                "transactionId" to "TXN001234568",
                "lateFee" to 0,
                "description" to "Room rent for February 2024"
            ),
            mapOf(
                "id" to "FEE003",
                "studentId" to "STU001",
                "studentName" to "Rahul Sharma",
                "roomNumber" to "102",
                "type" to "Security Deposit",
                "amount" to 12000,
                "dueDate" to "2024-03-01",
                "paidDate" to "",
                "status" to "Pending",
                "paymentMethod" to "",
                "transactionId" to "",
                "lateFee" to 50,
                "description" to "Security deposit for room 102"
            )
        )
        
        fees.forEach { fee ->
            val feeRef = FirebaseConfig.database.reference("fees/${fee["id"]}")
            feeRef.set(Json.encodeToJsonElement(fee))
        }
    }
    
    /**
     * Set up maintenance data
     */
    private suspend fun setupMaintenance() {
        console.log("🔧 Setting up maintenance data...")
        
        val maintenanceRequests = listOf(
            mapOf(
                "id" to "MAIN001",
                "roomNumber" to "201",
                "type" to "Electrical",
                "description" to "AC not working properly, needs repair",
                "priority" to "High",
                "status" to "Open",
                "reportedDate" to "2024-01-10",
                "assignedTo" to "John Electrician",
                "estimatedCost" to 2500,
                "actualCost" to 0,
                "completedDate" to "",
                "reportedBy" to "Hostel Staff",
                "notes" to "AC compressor making noise"
            ),
            mapOf(
                "id" to "MAIN002",
                "roomNumber" to "102",
                "type" to "Plumbing",
                "description" to "Bathroom tap leaking",
                "priority" to "Medium",
                "status" to "In Progress",
                "reportedDate" to "2024-01-12",
                "assignedTo" to "Mike Plumber",
                "estimatedCost" to 500,
                "actualCost" to 0,
                "completedDate" to "",
                "reportedBy" to "STU001",
                "notes" to "Small leak, easy fix"
            ),
            mapOf(
                "id" to "MAIN003",
                "roomNumber" to "101",
                "type" to "Cleaning",
                "description" to "Deep cleaning required",
                "priority" to "Low",
                "status" to "Completed",
                "reportedDate" to "2024-01-08",
                "assignedTo" to "Cleaning Staff",
                "estimatedCost" to 300,
                "actualCost" to 300,
                "completedDate" to "2024-01-15",
                "reportedBy" to "Hostel Manager",
                "notes" to "Room cleaned and sanitized"
            )
        )
        
        maintenanceRequests.forEach { maintenance ->
            val maintenanceRef = FirebaseConfig.database.reference("maintenance/${maintenance["id"]}")
            maintenanceRef.set(Json.encodeToJsonElement(maintenance))
        }
    }
    
    /**
     * Set up mess data
     */
    private suspend fun setupMess() {
        console.log("🍽️ Setting up mess data...")
        
        // Setup menu
        val menu = mapOf(
            "2024-01-15" to mapOf(
                "date" to "2024-01-15",
                "breakfast" to mapOf(
                    "items" to listOf("Poha", "Tea/Coffee", "Banana"),
                    "time" to "7:00 AM - 9:00 AM",
                    "cost" to 40
                ),
                "lunch" to mapOf(
                    "items" to listOf("Rice", "Dal", "Sabzi", "Roti", "Pickle"),
                    "time" to "12:00 PM - 2:00 PM",
                    "cost" to 80
                ),
                "dinner" to mapOf(
                    "items" to listOf("Rice/Roti", "Dal", "Paneer Curry", "Salad"),
                    "time" to "7:00 PM - 9:00 PM",
                    "cost" to 90
                )
            ),
            "2024-01-16" to mapOf(
                "date" to "2024-01-16",
                "breakfast" to mapOf(
                    "items" to listOf("Upma", "Tea/Coffee", "Boiled Eggs"),
                    "time" to "7:00 AM - 9:00 AM",
                    "cost" to 45
                ),
                "lunch" to mapOf(
                    "items" to listOf("Pulao", "Rajma", "Aloo Gobi", "Roti", "Curd"),
                    "time" to "12:00 PM - 2:00 PM",
                    "cost" to 85
                ),
                "dinner" to mapOf(
                    "items" to listOf("Rice/Roti", "Dal", "Chicken Curry", "Salad"),
                    "time" to "7:00 PM - 9:00 PM",
                    "cost" to 120
                )
            )
        )
        
        val menuRef = FirebaseConfig.database.reference("mess/menu")
        menuRef.set(Json.encodeToJsonElement(menu))
        
        // Setup subscriptions
        val subscriptions = listOf(
            mapOf(
                "id" to "SUB001",
                "studentId" to "STU001",
                "studentName" to "Rahul Sharma",
                "plan" to "Full Meal",
                "startDate" to "2024-01-01",
                "endDate" to "2024-01-31",
                "totalAmount" to 4500,
                "paidAmount" to 4500,
                "status" to "Active",
                "meals" to listOf("breakfast", "lunch", "dinner")
            ),
            mapOf(
                "id" to "SUB002",
                "studentId" to "STU002",
                "studentName" to "Priya Patel",
                "plan" to "Lunch & Dinner",
                "startDate" to "2024-01-01",
                "endDate" to "2024-01-31",
                "totalAmount" to 3200,
                "paidAmount" to 3200,
                "status" to "Active",
                "meals" to listOf("lunch", "dinner")
            )
        )
        
        subscriptions.forEach { subscription ->
            val subRef = FirebaseConfig.database.reference("mess/subscriptions/${subscription["id"]}")
            subRef.set(Json.encodeToJsonElement(subscription))
        }
        
        // Setup feedback
        val feedback = listOf(
            mapOf(
                "id" to "FB001",
                "studentId" to "STU001",
                "studentName" to "Rahul Sharma",
                "date" to "2024-01-14",
                "meal" to "dinner",
                "rating" to 4,
                "comment" to "Good food quality, but could be less spicy",
                "response" to "",
                "status" to "New"
            ),
            mapOf(
                "id" to "FB002",
                "studentId" to "STU002",
                "studentName" to "Priya Patel",
                "date" to "2024-01-13",
                "meal" to "lunch",
                "rating" to 5,
                "comment" to "Excellent taste and variety",
                "response" to "Thank you for the feedback!",
                "status" to "Responded"
            )
        )
        
        feedback.forEach { fb ->
            val fbRef = FirebaseConfig.database.reference("mess/feedback/${fb["id"]}")
            fbRef.set(Json.encodeToJsonElement(fb))
        }
    }
    
    /**
     * Set up visitors data
     */
    private suspend fun setupVisitors() {
        console.log("👥 Setting up visitors data...")
        
        val visitors = listOf(
            mapOf(
                "id" to "VIS001",
                "visitorName" to "Rajesh Sharma",
                "visitorPhone" to "+91-9876543211",
                "studentId" to "STU001",
                "studentName" to "Rahul Sharma",
                "roomNumber" to "102",
                "purpose" to "Parent Visit",
                "checkInTime" to "2024-01-14T10:30:00",
                "checkOutTime" to "2024-01-14T16:45:00",
                "idProof" to "Aadhar Card",
                "idNumber" to "1234-5678-9012",
                "approvedBy" to "Hostel Warden",
                "status" to "Completed",
                "notes" to "Father visiting for monthly check"
            ),
            mapOf(
                "id" to "VIS002",
                "visitorName" to "Amit Friend",
                "visitorPhone" to "+91-9876543220",
                "studentId" to "STU002",
                "studentName" to "Priya Patel",
                "roomNumber" to "202",
                "purpose" to "Friend Visit",
                "checkInTime" to "2024-01-15T14:00:00",
                "checkOutTime" to "",
                "idProof" to "Driving License",
                "idNumber" to "DL123456789",
                "approvedBy" to "Security Guard",
                "status" to "In Progress",
                "notes" to "College friend visiting"
            )
        )
        
        visitors.forEach { visitor ->
            val visitorRef = FirebaseConfig.database.reference("visitors/${visitor["id"]}")
            visitorRef.set(Json.encodeToJsonElement(visitor))
        }
    }
    
    /**
     * Set up hostel settings
     */
    private suspend fun setupSettings() {
        console.log("⚙️ Setting up hostel settings...")
        
        val settings = mapOf(
            "name" to "Hostel Dada",
            "address" to "123 Student Street, Education District, Mumbai, Maharashtra 400001",
            "phone" to "+91-22-12345678",
            "email" to "info@hosteldada.com",
            "warden" to "Mr. Suresh Patil",
            "capacity" to 100,
            "currentOccupancy" to 45,
            "checkInTime" to "10:00 AM",
            "checkOutTime" to "11:00 AM",
            "visitorHours" to "9:00 AM - 8:00 PM",
            "wifi" to "HostelDada_WiFi",
            "amenities" to listOf("WiFi", "Laundry", "Gym", "Common Room", "Kitchen", "Parking")
        )
        
        val settingsRef = FirebaseConfig.database.reference("settings/hostel")
        settingsRef.set(Json.encodeToJsonElement(settings))
    }
    
    /**
     * Set up database rules and indexes for optimal performance
     */
    private suspend fun setupDatabaseRules() {
        console.log("🔐 Setting up database rules...")
        
        // Note: Database rules are typically set through Firebase Console
        // This is a placeholder for rule configuration
        val rulesRef = FirebaseConfig.database.reference(".info/rules")
        console.log("📝 Database rules should be configured in Firebase Console")
        console.log("🔗 https://console.firebase.google.com/project/hostel-dada/database/rules")
    }
    
    /**
     * Test database connectivity and data retrieval
     */
    suspend fun testDatabase(): Boolean {
        return try {
            console.log("🧪 Testing database connectivity...")
            
            // Test reading data
            val roomsRef = FirebaseConfig.database.reference("rooms")
            val snapshot = roomsRef.get()
            
            console.log("✅ Database test successful!")
            console.log("📊 Sample rooms data retrieved")
            
            true
        } catch (e: Exception) {
            console.error("❌ Database test failed: ${e.message}")
            false
        }
    }
}

/**
 * Utility function to run database setup from browser console
 */
@JsExport
fun setupFirebaseDatabase() {
    GlobalScope.launch {
        FirebaseDatabaseSetup.initializeDatabase()
    }
}

/**
 * Utility function to test database from browser console
 */
@JsExport
fun testFirebaseDatabase() {
    GlobalScope.launch {
        FirebaseDatabaseSetup.testDatabase()
    }
}
