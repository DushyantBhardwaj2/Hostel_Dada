package auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import models.User

/**
 * 🔐 Authentication Manager for Hostel Dada
 * 
 * Handles all authentication operations:
 * - Email/Password authentication with Firebase
 * - Role-based access control (User vs Admin)
 * - Admin role assignment based on predefined email lists
 * - Session management and user state
 */
class AuthManager {
    
    // User state management
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    /**
     * 🔑 Sign in with email and password
     * Automatically determines if user is admin based on email
     */
    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            _authState.value = AuthState.Loading
            
            // Simulate Firebase authentication
            if (password.isNotEmpty()) {
                val user = createUserFromEmail(email)
                _currentUser.value = user
                _isAuthenticated.value = true
                _authState.value = AuthState.Authenticated(user)
                
                console.log("✅ User signed in: ${user.email} (Role: ${user.role})")
                AuthResult.Success(user)
            } else {
                _authState.value = AuthState.Error("Invalid credentials")
                AuthResult.Error("Invalid credentials")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign in failed")
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }
    
    /**
     * � Register new user (only regular users can register)
     * Admins are pre-configured and cannot register
     */
    suspend fun signUp(email: String, password: String, name: String): AuthResult {
        return try {
            _authState.value = AuthState.Loading
            
            // Check if email is admin email
            if (AdminConfig.isAdmin(email)) {
                _authState.value = AuthState.Error("Admin accounts cannot be registered")
                return AuthResult.Error("Admin accounts are pre-configured. Contact system administrator.")
            }
            
            // Create regular user account
            val user = User(
                id = generateUserId(),
                email = email,
                name = name,
                role = UserRole.USER,
                adminRole = null,
                accessibleModules = emptySet()
            )
            
            _currentUser.value = user
            _isAuthenticated.value = true
            _authState.value = AuthState.Authenticated(user)
            
            console.log("✅ New user registered: ${user.email}")
            AuthResult.Success(user)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Registration failed")
            AuthResult.Error(e.message ?: "Registration failed")
        }
    }
    
    /**
     * 🚪 Sign out current user
     */
    suspend fun signOut(): Boolean {
        return try {
            _currentUser.value = null
            _isAuthenticated.value = false
            _authState.value = AuthState.Unauthenticated
            console.log("✅ User signed out")
            true
        } catch (e: Exception) {
            console.error("❌ Sign out failed: ${e.message}")
            false
        }
    }
    
    /**
     * 👤 Create user object from email with proper role assignment
     */
    private fun createUserFromEmail(email: String): User {
        val isAdmin = AdminConfig.isAdmin(email)
        val adminRole = if (isAdmin) AdminConfig.getAdminRole(email) else null
        val accessibleModules = if (isAdmin) AdminConfig.getAccessibleModules(email) else emptySet()
        
        return User(
            id = generateUserId(),
            email = email,
            name = extractNameFromEmail(email),
            role = if (isAdmin) UserRole.ADMIN else UserRole.USER,
            adminRole = adminRole,
            accessibleModules = accessibleModules
        )
    }
    
    /**
     * 🔢 Generate unique user ID
     */
    private fun generateUserId(): String {
        return "user_${(Math.random() * 1000000).toInt()}"
    }
    
    /**
     * 📧 Extract name from email for display
     */
    private fun extractNameFromEmail(email: String): String {
        val localPart = email.substringBefore("@")
        return localPart.split(".").joinToString(" ") { it.capitalize() }
    }
    
    /**
     * ✅ Check if current user has access to a specific module
     */
    fun hasModuleAccess(module: String): Boolean {
        val user = _currentUser.value ?: return false
        
        return when (user.role) {
            UserRole.USER -> true  // Users have access to all modules
            UserRole.ADMIN -> user.accessibleModules.contains(module)
        }
    }
    
    /**
     * 🔍 Check if current user is global admin
     */
    fun isGlobalAdmin(): Boolean {
        val user = _currentUser.value ?: return false
        return user.adminRole == AdminRole.GLOBAL_ADMIN
    }
}

/**
 * 🔄 Authentication state
 */
sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * 📊 Authentication result
 */
sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
                _isAuthenticated.value = false
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
    
    /**
     * 📧 Sign in with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            _authState.value = AuthState.Loading
            
            val result = auth.signInWithEmailAndPassword(email, password)
            
            if (result.user != null) {
                AuthResult.Success("Sign in successful")
            } else {
                AuthResult.Error("Sign in failed")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Unknown error")
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }
    
    /**
     * 📝 Create new account with email and password
     */
    suspend fun createAccount(
        email: String, 
        password: String,
        fullName: String,
        hostelId: String,
        roomNumber: String,
        phoneNumber: String
    ): AuthResult {
        return try {
            _authState.value = AuthState.Loading
            
            val result = auth.createUserWithEmailAndPassword(email, password)
            
            if (result.user != null) {
                // Create user profile in database
                val user = User(
                    uid = result.user!!.uid,
                    email = email,
                    fullName = fullName,
                    hostelId = hostelId,
                    roomNumber = roomNumber,
                    phoneNumber = phoneNumber,
                    role = UserRole.STUDENT,
                    profilePictureUrl = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis()
                )
                
                saveUserProfile(user)
                AuthResult.Success("Account created successfully")
            } else {
                AuthResult.Error("Account creation failed")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Unknown error")
            AuthResult.Error(e.message ?: "Account creation failed")
        }
    }
    
    /**
     * 🔓 Sign out current user
     */
    suspend fun signOut(): AuthResult {
        return try {
            auth.signOut()
            _currentUser.value = null
            _isAuthenticated.value = false
            _authState.value = AuthState.Unauthenticated
            AuthResult.Success("Signed out successfully")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign out failed")
        }
    }
    
    /**
     * 🔄 Reset password via email
     */
    suspend fun resetPassword(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email)
            AuthResult.Success("Password reset email sent")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Password reset failed")
        }
    }
    
    /**
     * 👤 Load user profile from database
     */
    private suspend fun loadUserProfile(firebaseUser: FirebaseUser) {
        try {
            val userRef = FirebaseConfig.getDatabaseReference("${FirebaseConfig.DatabasePaths.USERS}/${firebaseUser.uid}")
            val snapshot = userRef.get()
            
            if (snapshot.exists()) {
                val userData = snapshot.value as? Map<String, Any>
                if (userData != null) {
                    val user = User.fromMap(userData)
                    _currentUser.value = user
                    _isAuthenticated.value = true
                    _authState.value = AuthState.Authenticated(user)
                    
                    // Update last login time
                    updateLastLogin(user.uid)
                }
            } else {
                // User profile doesn't exist, sign out
                auth.signOut()
            }
        } catch (e: Exception) {
            console.error("Error loading user profile: ${e.message}")
            _authState.value = AuthState.Error(e.message ?: "Failed to load user profile")
        }
    }
    
    /**
     * 💾 Save user profile to database
     */
    private suspend fun saveUserProfile(user: User) {
        try {
            val userRef = FirebaseConfig.getDatabaseReference("${FirebaseConfig.DatabasePaths.USERS}/${user.uid}")
            userRef.setValue(user.toMap())
        } catch (e: Exception) {
            console.error("Error saving user profile: ${e.message}")
            throw e
        }
    }
    
    /**
     * ⏰ Update last login timestamp
     */
    private suspend fun updateLastLogin(uid: String) {
        try {
            val userRef = FirebaseConfig.getDatabaseReference("${FirebaseConfig.DatabasePaths.USERS}/$uid/lastLoginAt")
            userRef.setValue(System.currentTimeMillis())
        } catch (e: Exception) {
            console.error("Error updating last login: ${e.message}")
        }
    }
    
    /**
     * 🛡️ Check if current user has specific role
     */
    fun hasRole(role: UserRole): Boolean {
        return _currentUser.value?.role == role
    }
    
    /**
     * 🏠 Check if current user belongs to specific hostel
     */
    fun belongsToHostel(hostelId: String): Boolean {
        return _currentUser.value?.hostelId == hostelId
    }
    
    /**
     * 🔍 Get current user ID
     */
    fun getCurrentUserId(): String? {
        return _currentUser.value?.uid
    }
}

/**
 * 🎭 Authentication State Sealed Class
 */
sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * 📊 Authentication Result Sealed Class
 */
sealed class AuthResult {
    data class Success(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * 🔐 Global Auth Manager Instance
 */
object Auth {
    val manager = AuthManager()
}
