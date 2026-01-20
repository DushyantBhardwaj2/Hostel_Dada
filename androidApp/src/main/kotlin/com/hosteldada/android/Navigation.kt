package com.hosteldada.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hosteldada.android.presentation.screens.*
import com.hosteldada.android.presentation.viewmodel.*
import org.koin.androidx.compose.koinViewModel

/**
 * Main Navigation Host
 * 
 * Handles all app navigation using Jetpack Navigation Compose
 * Routes:
 * - /splash -> Splash Screen
 * - /login -> Login Screen
 * - /register -> Registration Screen
 * - /dashboard -> Main Dashboard
 * - /snackcart -> SnackCart Feature
 * - /roomie -> Roomie Matcher Feature
 * - /profile -> User Profile
 */
@Composable
fun HostelDadaNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        // Login Screen
        composable(Routes.LOGIN) {
            val viewModel: LoginViewModel = koinViewModel()
            val state by viewModel.state.collectAsState()
            
            LoginScreen(
                state = state,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onLoginClick = viewModel::login,
                onGoogleSignInClick = viewModel::googleSignIn,
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        // Register Screen
        composable(Routes.REGISTER) {
            val viewModel: RegisterViewModel = koinViewModel()
            val state by viewModel.state.collectAsState()
            
            RegisterScreen(
                state = state,
                onNameChange = viewModel::onNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onRegisterClick = viewModel::register,
                onBackToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        
        // Dashboard
        composable(Routes.DASHBOARD) {
            val viewModel: DashboardViewModel = koinViewModel()
            val state by viewModel.state.collectAsState()
            
            DashboardScreen(
                state = state,
                onSnackCartClick = { navController.navigate(Routes.SNACKCART) },
                onRoomieClick = { navController.navigate(Routes.ROOMIE) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onLogoutClick = {
                    viewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }
        
        // SnackCart
        composable(Routes.SNACKCART) {
            val viewModel: SnackCartViewModel = koinViewModel()
            val state by viewModel.state.collectAsState()
            
            SnackCartScreen(
                state = state,
                onSearchQuery = viewModel::search,
                onAddToCart = viewModel::addToCart,
                onPlaceOrder = viewModel::placeOrder,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Roomie Matcher
        composable(Routes.ROOMIE) {
            val viewModel: RoomieViewModel = koinViewModel()
            val state by viewModel.state.collectAsState()
            
            RoomieScreen(
                state = state,
                onSaveProfile = viewModel::saveProfile,
                onFindMatches = viewModel::findMatches,
                onSendRequest = viewModel::sendRequest,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Profile
        composable(Routes.PROFILE) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Navigation Routes
 */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val SNACKCART = "snackcart"
    const val ROOMIE = "roomie"
    const val PROFILE = "profile"
}
