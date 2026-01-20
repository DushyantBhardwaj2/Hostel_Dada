package com.hosteldada.feature.auth.presentation

import com.hosteldada.core.common.DispatcherProvider
import com.hosteldada.core.common.result.Result
import com.hosteldada.feature.auth.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ============================================
 * LOGIN VIEWMODEL
 * ============================================
 * 
 * MVVM ViewModel with unidirectional data flow.
 * Uses StateFlow for reactive state updates.
 */

class LoginViewModel(
    private val signInWithEmail: SignInWithEmailUseCase,
    private val signUpWithEmail: SignUpWithEmailUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val sendPasswordReset: SendPasswordResetUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val observeCurrentUser: ObserveCurrentUserUseCase,
    private val signOut: SignOutUseCase,
    private val dispatcher: DispatcherProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher.main)
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        observeAuthState()
    }
    
    /**
     * Process user intents
     */
    fun onIntent(intent: AuthIntent) {
        when (intent) {
            // Form updates
            is AuthIntent.UpdateEmail -> updateEmail(intent.email)
            is AuthIntent.UpdatePassword -> updatePassword(intent.password)
            is AuthIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.password)
            is AuthIntent.UpdateDisplayName -> updateDisplayName(intent.name)
            
            // Auth actions
            is AuthIntent.SignInWithEmail -> signInWithEmailAction()
            is AuthIntent.SignUpWithEmail -> signUpWithEmailAction()
            is AuthIntent.SignInWithGoogle -> signInWithGoogleAction()
            is AuthIntent.SignOut -> signOutAction()
            
            // Mode toggles
            is AuthIntent.ToggleAuthMode -> toggleAuthMode()
            is AuthIntent.ShowPasswordReset -> showPasswordReset()
            is AuthIntent.HidePasswordReset -> hidePasswordReset()
            is AuthIntent.SendPasswordReset -> sendPasswordResetAction(intent.email)
            
            // Error handling
            is AuthIntent.ClearError -> clearError()
            is AuthIntent.ClearSuccess -> clearSuccess()
        }
    }
    
    // ==========================================
    // FORM UPDATES
    // ==========================================
    
    private fun updateEmail(email: String) {
        val error = if (email.isNotBlank() && !validateEmail(email)) {
            "Please enter a valid email"
        } else null
        
        _uiState.update { it.copy(email = email, emailError = error) }
    }
    
    private fun updatePassword(password: String) {
        val error = if (password.isNotBlank() && password.length < 6) {
            "Password must be at least 6 characters"
        } else null
        
        _uiState.update { it.copy(password = password, passwordError = error) }
    }
    
    private fun updateConfirmPassword(password: String) {
        val error = if (_uiState.value.password != password) {
            "Passwords don't match"
        } else null
        
        _uiState.update { it.copy(confirmPassword = password, passwordError = error) }
    }
    
    private fun updateDisplayName(name: String) {
        val error = if (name.isNotBlank() && name.length < 2) {
            "Name must be at least 2 characters"
        } else null
        
        _uiState.update { it.copy(displayName = name, nameError = error) }
    }
    
    // ==========================================
    // AUTH ACTIONS
    // ==========================================
    
    private fun signInWithEmailAction() {
        val state = _uiState.value
        
        // Validation
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Please fill all fields") }
            return
        }
        
        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = signInWithEmail(state.email, state.password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = result.data,
                        successMessage = "Welcome back, ${result.data.displayName}!"
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Login failed"
                    )}
                }
                is Result.Loading -> {}
            }
        }
    }
    
    private fun signUpWithEmailAction() {
        val state = _uiState.value
        
        // Validation
        if (state.email.isBlank() || state.password.isBlank() || state.displayName.isBlank()) {
            _uiState.update { it.copy(error = "Please fill all fields") }
            return
        }
        
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords don't match") }
            return
        }
        
        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = signUpWithEmail(state.email, state.password, state.displayName)) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = result.data,
                        successMessage = "Account created successfully!"
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Sign up failed"
                    )}
                }
                is Result.Loading -> {}
            }
        }
    }
    
    private fun signInWithGoogleAction() {
        // Note: Google Sign-In requires platform-specific implementation
        // The idToken would be obtained from platform code and passed here
        _uiState.update { it.copy(error = "Google Sign-In requires platform setup") }
    }
    
    private fun signOutAction() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (signOut()) {
                is Result.Success -> {
                    _uiState.update { AuthUiState(successMessage = "Signed out successfully") }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = "Sign out failed") }
                }
                is Result.Loading -> {}
            }
        }
    }
    
    // ==========================================
    // MODE TOGGLES
    // ==========================================
    
    private fun toggleAuthMode() {
        _uiState.update { state ->
            state.copy(
                authMode = if (state.authMode == AuthMode.LOGIN) AuthMode.SIGNUP else AuthMode.LOGIN,
                error = null,
                emailError = null,
                passwordError = null,
                nameError = null
            )
        }
    }
    
    private fun showPasswordReset() {
        _uiState.update { it.copy(showPasswordReset = true) }
    }
    
    private fun hidePasswordReset() {
        _uiState.update { it.copy(showPasswordReset = false, passwordResetSent = false) }
    }
    
    private fun sendPasswordResetAction(email: String) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (sendPasswordReset(email)) {
                is Result.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        passwordResetSent = true,
                        successMessage = "Password reset email sent!"
                    )}
                }
                is Result.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = "Failed to send reset email"
                    )}
                }
                is Result.Loading -> {}
            }
        }
    }
    
    // ==========================================
    // ERROR HANDLING
    // ==========================================
    
    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    private fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }
    
    // ==========================================
    // OBSERVERS
    // ==========================================
    
    private fun observeAuthState() {
        scope.launch {
            observeCurrentUser().collect { user ->
                _uiState.update { it.copy(
                    isLoggedIn = user != null,
                    currentUser = user
                )}
            }
        }
    }
}
