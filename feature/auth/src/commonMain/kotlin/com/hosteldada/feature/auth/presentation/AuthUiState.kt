package com.hosteldada.feature.auth.presentation

import com.hosteldada.core.domain.model.User

/**
 * ============================================
 * AUTHENTICATION UI STATE
 * ============================================
 * 
 * Immutable state for the auth screens.
 */

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val authMode: AuthMode = AuthMode.LOGIN,
    
    // Form fields
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    
    // Validation
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    
    // Status
    val error: String? = null,
    val successMessage: String? = null,
    
    // Password reset
    val showPasswordReset: Boolean = false,
    val passwordResetSent: Boolean = false
)

enum class AuthMode {
    LOGIN,
    SIGNUP
}

/**
 * User intents (actions)
 */
sealed interface AuthIntent {
    // Form updates
    data class UpdateEmail(val email: String) : AuthIntent
    data class UpdatePassword(val password: String) : AuthIntent
    data class UpdateConfirmPassword(val password: String) : AuthIntent
    data class UpdateDisplayName(val name: String) : AuthIntent
    
    // Auth actions
    object SignInWithEmail : AuthIntent
    object SignUpWithEmail : AuthIntent
    object SignInWithGoogle : AuthIntent
    object SignOut : AuthIntent
    
    // Mode toggles
    object ToggleAuthMode : AuthIntent
    object ShowPasswordReset : AuthIntent
    object HidePasswordReset : AuthIntent
    data class SendPasswordReset(val email: String) : AuthIntent
    
    // Error handling
    object ClearError : AuthIntent
    object ClearSuccess : AuthIntent
}
