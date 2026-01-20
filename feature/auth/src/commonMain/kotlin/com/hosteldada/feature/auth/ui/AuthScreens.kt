package com.hosteldada.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hosteldada.feature.auth.presentation.*
import com.hosteldada.shared.ui.components.*
import com.hosteldada.shared.ui.theme.*

/**
 * Login Screen - Composable UI
 * Follows Material Design 3 guidelines
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToSignUp: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    
    // Handle navigation on success
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onNavigateToDashboard()
        }
    }
    
    LoadingOverlay(isLoading = state.isLoading) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HostelDadaSpacing.XL.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(HostelDadaSpacing.XXXXL.dp))
            
            // Logo/Title
            Text(
                text = "Hostel Dada",
                style = MaterialTheme.typography.headlineLarge,
                color = HostelDadaColors.Primary
            )
            
            Text(
                text = "Welcome back! Sign in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = HostelDadaColors.OnSurfaceVariant,
                modifier = Modifier.padding(top = HostelDadaSpacing.S.dp)
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.XXXL.dp))
            
            // Error Banner
            ErrorBanner(
                message = state.errorMessage,
                onDismiss = { viewModel.handleIntent(LoginIntent.ClearError) },
                modifier = Modifier.padding(bottom = HostelDadaSpacing.L.dp)
            )
            
            // Email Field
            ValidatedTextField(
                value = state.email,
                onValueChange = { viewModel.handleIntent(LoginIntent.UpdateEmail(it)) },
                label = "Email",
                placeholder = "yourname@nsut.ac.in",
                error = state.emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.L.dp))
            
            // Password Field
            PasswordTextField(
                value = state.password,
                onValueChange = { viewModel.handleIntent(LoginIntent.UpdatePassword(it)) },
                label = "Password",
                error = state.passwordError,
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibility = { viewModel.handleIntent(LoginIntent.TogglePasswordVisibility) },
                imeAction = ImeAction.Done,
                onImeAction = { viewModel.handleIntent(LoginIntent.SignIn) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextLinkButton(
                    text = "Forgot Password?",
                    onClick = { viewModel.handleIntent(LoginIntent.ForgotPassword) }
                )
            }
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.XL.dp))
            
            // Sign In Button
            PrimaryButton(
                text = "Sign In",
                onClick = { viewModel.handleIntent(LoginIntent.SignIn) },
                isLoading = state.isLoading,
                enabled = state.isFormValid,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.L.dp))
            
            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = HostelDadaColors.Divider
                )
                Text(
                    text = "  or  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = HostelDadaColors.OnSurfaceVariant
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = HostelDadaColors.Divider
                )
            }
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.L.dp))
            
            // Google Sign In
            SecondaryButton(
                text = "Continue with Google",
                onClick = { viewModel.handleIntent(LoginIntent.SignInWithGoogle) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Sign Up Link
            Row(
                modifier = Modifier.padding(vertical = HostelDadaSpacing.XL.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HostelDadaColors.OnSurfaceVariant
                )
                TextLinkButton(
                    text = "Sign Up",
                    onClick = onNavigateToSignUp
                )
            }
        }
    }
}

/**
 * Sign Up Screen
 */
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    
    // Handle navigation on success
    LaunchedEffect(state.isSignUpSuccess) {
        if (state.isSignUpSuccess) {
            onNavigateToProfileSetup()
        }
    }
    
    LoadingOverlay(isLoading = state.isLoading) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = HostelDadaSpacing.XL.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(HostelDadaSpacing.XXXL.dp))
            
            // Title
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                color = HostelDadaColors.Primary
            )
            
            Text(
                text = "Sign up to get started with Hostel Dada",
                style = MaterialTheme.typography.bodyMedium,
                color = HostelDadaColors.OnSurfaceVariant,
                modifier = Modifier.padding(top = HostelDadaSpacing.S.dp)
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.XXL.dp))
            
            // Error Banner
            ErrorBanner(
                message = state.errorMessage,
                onDismiss = { viewModel.handleIntent(SignUpIntent.ClearError) },
                modifier = Modifier.padding(bottom = HostelDadaSpacing.L.dp)
            )
            
            // Name Field
            ValidatedTextField(
                value = state.displayName,
                onValueChange = { viewModel.handleIntent(SignUpIntent.UpdateDisplayName(it)) },
                label = "Full Name",
                placeholder = "Enter your full name",
                error = state.nameError,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.L.dp))
            
            // Email Field
            ValidatedTextField(
                value = state.email,
                onValueChange = { viewModel.handleIntent(SignUpIntent.UpdateEmail(it)) },
                label = "NSUT Email",
                placeholder = "yourname@nsut.ac.in",
                error = state.emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.L.dp))
            
            // Password Field
            PasswordTextField(
                value = state.password,
                onValueChange = { viewModel.handleIntent(SignUpIntent.UpdatePassword(it)) },
                label = "Password",
                error = state.passwordError,
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibility = { viewModel.handleIntent(SignUpIntent.TogglePasswordVisibility) },
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.L.dp))
            
            // Confirm Password Field
            PasswordTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.handleIntent(SignUpIntent.UpdateConfirmPassword(it)) },
                label = "Confirm Password",
                error = state.confirmPasswordError,
                isPasswordVisible = state.isPasswordVisible,
                onToggleVisibility = { viewModel.handleIntent(SignUpIntent.TogglePasswordVisibility) },
                imeAction = ImeAction.Done,
                onImeAction = { viewModel.handleIntent(SignUpIntent.SignUp) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.XL.dp))
            
            // Terms notice
            Text(
                text = "By signing up, you agree to our Terms of Service and Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = HostelDadaColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = HostelDadaSpacing.L.dp)
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.L.dp))
            
            // Sign Up Button
            PrimaryButton(
                text = "Create Account",
                onClick = { viewModel.handleIntent(SignUpIntent.SignUp) },
                isLoading = state.isLoading,
                enabled = state.isFormValid,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Login Link
            Row(
                modifier = Modifier.padding(vertical = HostelDadaSpacing.XL.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HostelDadaColors.OnSurfaceVariant
                )
                TextLinkButton(
                    text = "Sign In",
                    onClick = onNavigateToLogin
                )
            }
        }
    }
}

/**
 * Forgot Password Screen
 */
@Composable
fun ForgotPasswordScreen(
    viewModel: LoginViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = HostelDadaSpacing.XL.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(HostelDadaSpacing.XXXL.dp))
        
        // Title
        Text(
            text = "Reset Password",
            style = MaterialTheme.typography.headlineLarge,
            color = HostelDadaColors.Primary
        )
        
        Text(
            text = "Enter your email to receive a password reset link",
            style = MaterialTheme.typography.bodyMedium,
            color = HostelDadaColors.OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = HostelDadaSpacing.S.dp)
        )
        
        Spacer(modifier = Modifier.height(HostelDadaSpacing.XXXL.dp))
        
        if (isSubmitted) {
            // Success state
            SuccessBanner(
                message = "Password reset email sent! Check your inbox.",
                onDismiss = { onNavigateBack() }
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.XL.dp))
            
            SecondaryButton(
                text = "Back to Login",
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Email input
            ValidatedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "yourname@nsut.ac.in",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.XL.dp))
            
            PrimaryButton(
                text = "Send Reset Link",
                onClick = { 
                    // TODO: Call reset password use case
                    isSubmitted = true
                },
                enabled = email.isNotBlank() && email.contains("@"),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(HostelDadaSpacing.L.dp))
            
            TextLinkButton(
                text = "Back to Login",
                onClick = onNavigateBack
            )
        }
    }
}
