import androidx.compose.runtime.*
import firebase.FirebaseConfig
import firebase.FirebaseDatabaseSetup
import ui.auth.AuthScreen
import ui.dashboard.DashboardScreen
import auth.Auth
import auth.AuthState
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import ui.components.LoadingSpinner
import ui.theme.applyGlobalStyles
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 🏠 Hostel Dada - Main Application Entry Point
 * 
 * Kotlin/JS + Jetpack Compose Web application for comprehensive
 * hostel management with 5 core modules and Firebase backend
 */

fun main() {
    // Initialize Firebase and Database
    try {
        console.log("🚀 Initializing Hostel Dada Application...")
        
        // Initialize Firebase
        FirebaseConfig.initialize()
        
        // Set up database with sample data (for initial setup only)
        GlobalScope.launch {
            console.log("� Setting up Firebase database with sample data...")
            val success = FirebaseDatabaseSetup.initializeDatabase()
            if (success) {
                console.log("✅ Database setup completed successfully!")
                console.log("🎯 You can now view your data at: https://console.firebase.google.com/project/hostel-dada/database")
            } else {
                console.warn("⚠️ Database setup failed - you may need to set it up manually")
            }
        }
        
        console.log("🎯 Hostel Dada application starting with real Firebase backend...")
        
    } catch (e: Exception) {
        console.error("❌ Firebase initialization failed: ${e.message}")
        console.error("🔧 Please check your Firebase configuration and internet connection")
    }
    
    // Render the application
    renderComposable(rootElementId = "root") {
        HostelDadaApp()
    }
}

/**
 * 🎯 Main Application Component
 * 
 * Handles authentication state and app-level routing
 */
@Composable
fun HostelDadaApp() {
    // Global Styles
    Style {
        applyGlobalStyles()
        
        // Add spinner animation
        """
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        * {
            box-sizing: border-box;
        }
        
        body {
            margin: 0;
            padding: 0;
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        }
        
        #root {
            min-height: 100vh;
        }
        """.trimIndent()
    }
    
    // Authentication State Management
    val authState by Auth.manager.authState.collectAsState()
    
    // App Content Based on Auth State
    when (authState) {
        is AuthState.Loading -> {
            LoadingContainer {
                LoadingSpinner(
                    size = 50.px,
                    message = "Loading Hostel Dada..."
                )
            }
        }
        
        is AuthState.Unauthenticated -> {
            AuthScreen()
        }
        
        is AuthState.Authenticated -> {
            DashboardScreen(user = authState.user)
        }
        
        is AuthState.Error -> {
            ErrorContainer(authState.message)
        }
    }
}

/**
 * ⏳ Loading Container Component
 */
@Composable
private fun LoadingContainer(content: @Composable () -> Unit) {
    Div({
        style {
            minHeight(100.vh)
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            backgroundColor(Color("#FAFAFA"))
        }
    }) {
        content()
    }
}

/**
 * ❌ Error Container Component
 */
@Composable
private fun ErrorContainer(message: String) {
    Div({
        style {
            minHeight(100.vh)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            backgroundColor(Color("#FAFAFA"))
            padding(24.px)
        }
    }) {
        Div({
            style {
                backgroundColor(Color.white)
                padding(32.px)
                borderRadius(12.px)
                boxShadow("0 4px 12px rgba(0,0,0,0.1)")
                textAlign("center")
                maxWidth(400.px)
            }
        }) {
            H2({
                style {
                    color(Color("#F44336"))
                    fontSize(24.px)
                    marginBottom(16.px)
                }
            }) {
                Text("⚠️ Application Error")
            }
            
            P({
                style {
                    color(Color("#757575"))
                    fontSize(16.px)
                    lineHeight(1.5)
                    marginBottom(24.px)
                }
            }) {
                Text(message)
            }
            
            Button({
                style {
                    backgroundColor(Color("#2196F3"))
                    color(Color.white)
                    border(0.px)
                    padding(12.px, 24.px)
                    borderRadius(8.px)
                    fontSize(14.px)
                    cursor("pointer")
                }
                onClick {
                    // Reload the page
                    js("window.location.reload()")
                }
            }) {
                Text("🔄 Reload Application")
            }
        }
    }
}
