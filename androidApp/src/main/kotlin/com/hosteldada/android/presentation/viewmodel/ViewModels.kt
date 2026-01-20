package com.hosteldada.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.hosteldada.android.di.*
import com.hosteldada.android.data.firebase.*

/**
 * ViewModels for Hostel Dada App
 * 
 * Implements MVVM pattern with:
 * - Unidirectional data flow
 * - StateFlow for reactive UI updates
 * - Use cases for business logic
 */

// ============================================================================
// LOGIN VIEW MODEL
// ============================================================================

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()
    
    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }
    
    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }
    
    fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            // Simulated login - replace with actual use case
            try {
                // val result = loginUseCase(state.value.email, state.value.password)
                kotlinx.coroutines.delay(1500)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(isLoading = false, error = e.message ?: "Login failed") 
                }
            }
        }
    }
    
    fun googleSignIn(idToken: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            // Implement Google Sign-In flow
            kotlinx.coroutines.delay(1000)
            _state.update { it.copy(isLoading = false) }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

// ============================================================================
// REGISTER VIEW MODEL
// ============================================================================

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()
    
    fun onNameChange(name: String) {
        _state.update { it.copy(name = name, error = null) }
    }
    
    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }
    
    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
        validatePasswords()
    }
    
    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword, error = null) }
        validatePasswords()
    }
    
    private fun validatePasswords() {
        val current = _state.value
        if (current.confirmPassword.isNotEmpty() && current.password != current.confirmPassword) {
            _state.update { it.copy(passwordMismatch = true) }
        } else {
            _state.update { it.copy(passwordMismatch = false) }
        }
    }
    
    fun register() {
        viewModelScope.launch {
            val current = _state.value
            
            if (current.passwordMismatch) {
                _state.update { it.copy(error = "Passwords do not match") }
                return@launch
            }
            
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                // val result = registerUseCase(current.email, current.password, current.name)
                kotlinx.coroutines.delay(1500)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(isLoading = false, error = e.message ?: "Registration failed") 
                }
            }
        }
    }
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordMismatch: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

// ============================================================================
// DASHBOARD VIEW MODEL
// ============================================================================

class DashboardViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()
    
    init {
        loadUserData()
    }
    
    private fun loadUserData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Load user data from repository
            kotlinx.coroutines.delay(500)
            _state.update { 
                it.copy(
                    isLoading = false,
                    userName = "Dushy",
                    userEmail = "dushy@hosteldada.com"
                ) 
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // logoutUseCase()
            _state.update { it.copy(isLoading = false) }
        }
    }
}

data class DashboardUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isLoading: Boolean = false
)

// ============================================================================
// SNACKCART VIEW MODEL
// ============================================================================

class SnackCartViewModel(
    private val getSnacksUseCase: GetSnacksUseCase,
    private val searchSnacksUseCase: SearchSnacksUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(SnackCartUiState())
    val state: StateFlow<SnackCartUiState> = _state.asStateFlow()
    
    init {
        loadSnacks()
    }
    
    private fun loadSnacks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Sample data
            val sampleSnacks = listOf(
                SnackItem("1", "Maggi", "Instant noodles", 25.0, "Noodles", ""),
                SnackItem("2", "Kurkure", "Crunchy snack", 20.0, "Chips", ""),
                SnackItem("3", "Parle-G", "Biscuits", 10.0, "Biscuits", ""),
                SnackItem("4", "Coca-Cola", "Cold drink", 40.0, "Beverages", ""),
                SnackItem("5", "Lays", "Potato chips", 20.0, "Chips", "")
            )
            
            _state.update { 
                it.copy(isLoading = false, snacks = sampleSnacks) 
            }
        }
    }
    
    fun search(query: String) {
        _state.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            val filtered = _state.value.snacks.filter { 
                it.name.contains(query, ignoreCase = true) 
            }
            _state.update { it.copy(filteredSnacks = filtered) }
        }
    }
    
    fun addToCart(snackId: String, quantity: Int) {
        viewModelScope.launch {
            val snack = _state.value.snacks.find { it.id == snackId } ?: return@launch
            val cartItem = CartItemUi(snack, quantity)
            
            val currentCart = _state.value.cartItems.toMutableList()
            val existingIndex = currentCart.indexOfFirst { it.snack.id == snackId }
            
            if (existingIndex >= 0) {
                currentCart[existingIndex] = cartItem.copy(
                    quantity = currentCart[existingIndex].quantity + quantity
                )
            } else {
                currentCart.add(cartItem)
            }
            
            val total = currentCart.sumOf { it.snack.price * it.quantity }
            _state.update { 
                it.copy(cartItems = currentCart, totalAmount = total) 
            }
        }
    }
    
    fun placeOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1500)
            _state.update { 
                it.copy(
                    isLoading = false, 
                    orderPlaced = true,
                    cartItems = emptyList(),
                    totalAmount = 0.0
                ) 
            }
        }
    }
}

data class SnackCartUiState(
    val snacks: List<SnackItem> = emptyList(),
    val filteredSnacks: List<SnackItem> = emptyList(),
    val cartItems: List<CartItemUi> = emptyList(),
    val searchQuery: String = "",
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val orderPlaced: Boolean = false,
    val error: String? = null
)

data class SnackItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String
)

data class CartItemUi(
    val snack: SnackItem,
    val quantity: Int
)

// ============================================================================
// ROOMIE VIEW MODEL
// ============================================================================

class RoomieViewModel(
    private val getProfileUseCase: GetRoomieProfileUseCase,
    private val saveProfileUseCase: SaveRoomieProfileUseCase,
    private val findMatchesUseCase: FindMatchesUseCase,
    private val getCompatibilityUseCase: GetCompatibilityScoreUseCase,
    private val sendRequestUseCase: SendMatchRequestUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(RoomieUiState())
    val state: StateFlow<RoomieUiState> = _state.asStateFlow()
    
    init {
        loadProfile()
    }
    
    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // Sample profile
            _state.update { 
                it.copy(
                    isLoading = false,
                    hasProfile = false,
                    currentTab = RoomieTab.PROFILE
                ) 
            }
        }
    }
    
    fun saveProfile(profile: RoomieProfileUi) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000)
            _state.update { 
                it.copy(
                    isLoading = false,
                    hasProfile = true,
                    profile = profile
                ) 
            }
        }
    }
    
    fun findMatches() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, currentTab = RoomieTab.MATCHES) }
            
            // Sample matches
            val sampleMatches = listOf(
                RoomieMatchUi(
                    userId = "u1",
                    name = "Rahul Sharma",
                    compatibilityScore = 92,
                    bio = "CS student, night owl, loves gaming"
                ),
                RoomieMatchUi(
                    userId = "u2",
                    name = "Amit Patel",
                    compatibilityScore = 85,
                    bio = "ECE student, early riser, fitness enthusiast"
                ),
                RoomieMatchUi(
                    userId = "u3",
                    name = "Vikram Singh",
                    compatibilityScore = 78,
                    bio = "ME student, moderate sleeper, movie buff"
                )
            )
            
            kotlinx.coroutines.delay(1500)
            _state.update { 
                it.copy(isLoading = false, matches = sampleMatches) 
            }
        }
    }
    
    fun sendRequest(toUserId: String, message: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            kotlinx.coroutines.delay(1000)
            _state.update { 
                it.copy(isLoading = false, requestSent = true) 
            }
        }
    }
}

data class RoomieUiState(
    val isLoading: Boolean = false,
    val hasProfile: Boolean = false,
    val profile: RoomieProfileUi? = null,
    val matches: List<RoomieMatchUi> = emptyList(),
    val currentTab: RoomieTab = RoomieTab.PROFILE,
    val requestSent: Boolean = false,
    val error: String? = null
)

data class RoomieProfileUi(
    val name: String = "",
    val bio: String = "",
    val sleepSchedule: String = "Flexible",
    val cleanlinessLevel: Int = 3,
    val studyHabits: String = "Moderate",
    val interests: List<String> = emptyList()
)

data class RoomieMatchUi(
    val userId: String,
    val name: String,
    val compatibilityScore: Int,
    val bio: String
)

enum class RoomieTab {
    PROFILE, MATCHES, REQUESTS
}
