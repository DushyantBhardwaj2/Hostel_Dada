package ui.auth

import androidx.compose.runtime.*
import auth.Auth
import auth.AuthResult
import auth.AuthState
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import ui.components.LoadingSpinner
import ui.theme.AppColors
import ui.theme.AppSpacing
import ui.theme.AppTypography

/**
 * 🔐 Authentication Screen Component
 * 
 * Provides login and registration functionality
 * with responsive design and validation
 */
@Composable
fun AuthScreen() {
    var isLogin by remember { mutableStateOf(true) }
    val authState by Auth.manager.authState.collectAsState()
    
    Div({
        style {
            minHeight(100.vh)
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            background("linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            padding(AppSpacing.medium.px)
        }
    }) {
        Div({
            style {
                backgroundColor(Color.white)
                borderRadius(12.px)
                padding(AppSpacing.large.px)
                boxShadow("0 20px 40px rgba(0,0,0,0.1)")
                width(100.percent)
                maxWidth(400.px)
            }
        }) {
            // Header
            Div({
                style {
                    textAlign("center")
                    marginBottom(AppSpacing.large.px)
                }
            }) {
                H1({
                    style {
                        color(AppColors.primary)
                        fontSize(28.px)
                        fontWeight("bold")
                        marginBottom(AppSpacing.small.px)
                    }
                }) {
                    Text("🏠 Hostel Dada")
                }
                P({
                    style {
                        color(AppColors.textSecondary)
                        fontSize(16.px)
                        margin(0.px)
                    }
                }) {
                    Text(if (isLogin) "Welcome back!" else "Create your account")
                }
            }
            
            // Auth Form
            when (authState) {
                is AuthState.Loading -> {
                    LoadingSpinner()
                }
                
                is AuthState.Error -> {
                    ErrorMessage(authState.message)
                    if (isLogin) {
                        LoginForm { isLogin = false }
                    } else {
                        RegisterForm { isLogin = true }
                    }
                }
                
                else -> {
                    if (isLogin) {
                        LoginForm { isLogin = false }
                    } else {
                        RegisterForm { isLogin = true }
                    }
                }
            }
        }
    }
}

/**
 * 🔑 Login Form Component
 */
@Composable
private fun LoginForm(onSwitchToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    Form({
        onSubmit { event ->
            event.preventDefault()
            scope.launch {
                isLoading = true
                errorMessage = null
                
                when (val result = Auth.manager.signInWithEmail(email, password)) {
                    is AuthResult.Success -> {
                        // Navigation will be handled by auth state observer
                    }
                    is AuthResult.Error -> {
                        errorMessage = result.message
                    }
                }
                isLoading = false
            }
        }
    }) {
        // Email Input
        InputField(
            label = "Email Address",
            value = email,
            onValueChange = { email = it },
            type = InputType.Email,
            placeholder = "Enter your email",
            required = true
        )
        
        // Password Input
        InputField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            type = InputType.Password,
            placeholder = "Enter your password",
            required = true
        )
        
        // Error Message
        errorMessage?.let { message ->
            ErrorMessage(message)
        }
        
        // Submit Button
        Button({
            type(ButtonType.Submit)
            disabled(isLoading || email.isBlank() || password.isBlank())
            style {
                width(100.percent)
                padding(AppSpacing.medium.px)
                backgroundColor(AppColors.primary)
                color(Color.white)
                border(0.px)
                borderRadius(8.px)
                fontSize(16.px)
                fontWeight("500")
                cursor("pointer")
                marginBottom(AppSpacing.medium.px)
                
                if (isLoading) {
                    opacity(0.7)
                    cursor("not-allowed")
                }
            }
        }) {
            Text(if (isLoading) "Signing in..." else "Sign In")
        }
        
        // Switch to Register
        Div({
            style {
                textAlign("center")
            }
        }) {
            Span({
                style {
                    color(AppColors.textSecondary)
                    fontSize(14.px)
                }
            }) {
                Text("Don't have an account? ")
            }
            Span({
                style {
                    color(AppColors.primary)
                    cursor("pointer")
                    textDecoration("underline")
                }
                onClick { onSwitchToRegister() }
            }) {
                Text("Sign up")
            }
        }
    }
}

/**
 * 📝 Registration Form Component
 */
@Composable
private fun RegisterForm(onSwitchToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var hostelId by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    Form({
        onSubmit { event ->
            event.preventDefault()
            
            // Validation
            if (password != confirmPassword) {
                errorMessage = "Passwords do not match"
                return@onSubmit
            }
            
            if (password.length < 6) {
                errorMessage = "Password must be at least 6 characters"
                return@onSubmit
            }
            
            scope.launch {
                isLoading = true
                errorMessage = null
                
                when (val result = Auth.manager.createAccount(
                    email = email,
                    password = password,
                    fullName = fullName,
                    hostelId = hostelId,
                    roomNumber = roomNumber,
                    phoneNumber = phoneNumber
                )) {
                    is AuthResult.Success -> {
                        // Navigation will be handled by auth state observer
                    }
                    is AuthResult.Error -> {
                        errorMessage = result.message
                    }
                }
                isLoading = false
            }
        }
    }) {
        // Full Name
        InputField(
            label = "Full Name",
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = "Enter your full name",
            required = true
        )
        
        // Email
        InputField(
            label = "Email Address",
            value = email,
            onValueChange = { email = it },
            type = InputType.Email,
            placeholder = "Enter your email",
            required = true
        )
        
        // Hostel ID
        InputField(
            label = "Hostel ID",
            value = hostelId,
            onValueChange = { hostelId = it },
            placeholder = "e.g., hostel_001",
            required = true
        )
        
        // Room Number
        InputField(
            label = "Room Number",
            value = roomNumber,
            onValueChange = { roomNumber = it },
            placeholder = "e.g., A-101",
            required = true
        )
        
        // Phone Number
        InputField(
            label = "Phone Number",
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            type = InputType.Tel,
            placeholder = "Enter your phone number",
            required = true
        )
        
        // Password
        InputField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            type = InputType.Password,
            placeholder = "Enter your password (min 6 chars)",
            required = true
        )
        
        // Confirm Password
        InputField(
            label = "Confirm Password",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            type = InputType.Password,
            placeholder = "Confirm your password",
            required = true
        )
        
        // Error Message
        errorMessage?.let { message ->
            ErrorMessage(message)
        }
        
        // Submit Button
        Button({
            type(ButtonType.Submit)
            disabled(isLoading || 
                email.isBlank() || 
                password.isBlank() || 
                fullName.isBlank() || 
                hostelId.isBlank() || 
                roomNumber.isBlank() || 
                phoneNumber.isBlank())
            style {
                width(100.percent)
                padding(AppSpacing.medium.px)
                backgroundColor(AppColors.primary)
                color(Color.white)
                border(0.px)
                borderRadius(8.px)
                fontSize(16.px)
                fontWeight("500")
                cursor("pointer")
                marginBottom(AppSpacing.medium.px)
                
                if (isLoading) {
                    opacity(0.7)
                    cursor("not-allowed")
                }
            }
        }) {
            Text(if (isLoading) "Creating account..." else "Create Account")
        }
        
        // Switch to Login
        Div({
            style {
                textAlign("center")
            }
        }) {
            Span({
                style {
                    color(AppColors.textSecondary)
                    fontSize(14.px)
                }
            }) {
                Text("Already have an account? ")
            }
            Span({
                style {
                    color(AppColors.primary)
                    cursor("pointer")
                    textDecoration("underline")
                }
                onClick { onSwitchToLogin() }
            }) {
                Text("Sign in")
            }
        }
    }
}

/**
 * 📝 Reusable Input Field Component
 */
@Composable
private fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    type: InputType<String> = InputType.Text,
    placeholder: String = "",
    required: Boolean = false
) {
    Div({
        style {
            marginBottom(AppSpacing.medium.px)
        }
    }) {
        Label({
            style {
                display(DisplayStyle.Block)
                marginBottom(AppSpacing.small.px)
                fontSize(14.px)
                fontWeight("500")
                color(AppColors.textPrimary)
            }
        }) {
            Text(label)
            if (required) {
                Span({
                    style {
                        color(AppColors.error)
                    }
                }) {
                    Text(" *")
                }
            }
        }
        
        Input(type) {
            value(value)
            placeholder(placeholder)
            required(required)
            onInput { event ->
                onValueChange(event.value)
            }
            style {
                width(100.percent)
                padding(AppSpacing.medium.px)
                border(1.px, LineStyle.Solid, AppColors.border)
                borderRadius(8.px)
                fontSize(16.px)
                backgroundColor(Color.white)
                
                focus {
                    outline("none")
                    borderColor(AppColors.primary)
                    boxShadow("0 0 0 3px rgba(33, 150, 243, 0.1)")
                }
            }
        }
    }
}

/**
 * ❌ Error Message Component
 */
@Composable
private fun ErrorMessage(message: String) {
    Div({
        style {
            backgroundColor(Color("#ffebee"))
            color(AppColors.error)
            padding(AppSpacing.medium.px)
            borderRadius(8.px)
            marginBottom(AppSpacing.medium.px)
            fontSize(14.px)
            border(1.px, LineStyle.Solid, Color("#ffcdd2"))
        }
    }) {
        Text("⚠️ $message")
    }
}
