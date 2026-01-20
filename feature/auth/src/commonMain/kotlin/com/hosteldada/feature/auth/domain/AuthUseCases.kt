package com.hosteldada.feature.auth.domain

import com.hosteldada.core.common.result.Result
import com.hosteldada.core.domain.model.User
import com.hosteldada.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * ============================================
 * AUTHENTICATION USE CASES
 * ============================================
 * 
 * Single Responsibility - each use case does one thing.
 */

class SignInWithEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Validate input
        if (email.isBlank()) {
            return Result.Error(IllegalArgumentException("Email cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.Error(IllegalArgumentException("Password cannot be empty"))
        }
        if (!email.contains("@")) {
            return Result.Error(IllegalArgumentException("Invalid email format"))
        }
        
        return authRepository.signInWithEmail(email, password)
    }
}

class SignUpWithEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        // Validations
        if (email.isBlank()) {
            return Result.Error(IllegalArgumentException("Email cannot be empty"))
        }
        if (password.length < 6) {
            return Result.Error(IllegalArgumentException("Password must be at least 6 characters"))
        }
        if (displayName.isBlank()) {
            return Result.Error(IllegalArgumentException("Name cannot be empty"))
        }
        
        // NSUT domain validation
        if (!authRepository.validateNsutDomain(email)) {
            return Result.Error(IllegalArgumentException("Please use your NSUT email (@nsut.ac.in)"))
        }
        
        return authRepository.signUpWithEmail(email, password, displayName)
    }
}

class SignInWithGoogleUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        if (idToken.isBlank()) {
            return Result.Error(IllegalArgumentException("Invalid Google token"))
        }
        return authRepository.signInWithGoogle(idToken)
    }
}

class SendPasswordResetUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank() || !email.contains("@")) {
            return Result.Error(IllegalArgumentException("Please enter a valid email"))
        }
        return authRepository.sendPasswordResetEmail(email)
    }
}

class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signOut()
    }
}

class ObserveCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.observeCurrentUser()
    }
}

class ValidateEmailUseCase {
    operator fun invoke(email: String): Boolean {
        if (email.isBlank()) return false
        if (!email.contains("@")) return false
        
        // NSUT email validation
        return email.endsWith("@nsut.ac.in") || 
               email.endsWith("@gmail.com") // For testing
    }
}

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}

class IsLoggedInUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.isLoggedIn()
    }
}
