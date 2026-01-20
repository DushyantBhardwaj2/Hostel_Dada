package com.hosteldada.android.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hosteldada.android.presentation.viewmodel.*
import kotlinx.coroutines.delay

/**
 * UI Screens for Hostel Dada App
 * 
 * Implements Material Design 3 with:
 * - Modern Compose UI
 * - Responsive layouts
 * - State-driven UI updates
 * - Accessibility support
 */

// Brand Colors
val PrimaryOrange = Color(0xFFFF6B35)
val PrimaryDark = Color(0xFF1A1A2E)
val AccentPurple = Color(0xFF6C63FF)
val SurfaceLight = Color(0xFFF7F7F7)

// ============================================================================
// SPLASH SCREEN
// ============================================================================

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2000)
        // Check if user is logged in
        // For now, navigate to login
        onNavigateToLogin()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryOrange, PrimaryDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Text(
                text = "🏠",
                fontSize = 80.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Hostel Dada",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Text(
                text = "Your Hostel Companion",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// ============================================================================
// LOGIN SCREEN
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }
    
    var passwordVisible by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "Welcome Back!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryDark
        )
        
        Text(
            text = "Login to your account",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Email Field
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password Field
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Error message
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Forgot Password
        Text(
            text = "Forgot Password?",
            color = PrimaryOrange,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { /* Handle forgot password */ }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Login Button
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Or divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "  OR  ",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Google Sign-In Button
        OutlinedButton(
            onClick = onGoogleSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continue with Google", fontWeight = FontWeight.Medium)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Register Link
        Row {
            Text("Don't have an account? ", color = Color.Gray)
            Text(
                text = "Register",
                color = PrimaryOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onRegisterClick)
            )
        }
    }
}

// ============================================================================
// REGISTER SCREEN
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onBackToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Header
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryDark
        )
        
        Text(
            text = "Join the Hostel Dada community",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Name Field
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Email Field
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password Field
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confirm Password Field
        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = state.passwordMismatch,
            supportingText = if (state.passwordMismatch) {
                { Text("Passwords do not match") }
            } else null,
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Register Button
        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            enabled = !state.isLoading && !state.passwordMismatch
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Back to Login
        Row {
            Text("Already have an account? ", color = Color.Gray)
            Text(
                text = "Login",
                color = PrimaryOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onBackToLogin)
            )
        }
    }
}

// ============================================================================
// DASHBOARD SCREEN
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onSnackCartClick: () -> Unit,
    onRoomieClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Welcome back!", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            state.userName.ifEmpty { "User" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Quick Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryOrange.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(icon = Icons.Default.ShoppingCart, value = "5", label = "Orders")
                    StatItem(icon = Icons.Default.People, value = "12", label = "Matches")
                    StatItem(icon = Icons.Default.Star, value = "4.8", label = "Rating")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Features",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Feature Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    title = "SnackCart",
                    description = "Order snacks from hostel shops",
                    icon = Icons.Default.ShoppingCart,
                    color = PrimaryOrange,
                    onClick = onSnackCartClick
                )
                
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    title = "Roomie",
                    description = "Find your perfect roommate",
                    icon = Icons.Default.People,
                    color = AccentPurple,
                    onClick = onRoomieClick
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Recent Activity
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ActivityItem(
                        title = "Order #1234 delivered",
                        subtitle = "2 hours ago",
                        icon = Icons.Default.Check
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ActivityItem(
                        title = "New roomie match: Rahul",
                        subtitle = "5 hours ago",
                        icon = Icons.Default.PersonAdd
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    ActivityItem(
                        title = "Profile updated",
                        subtitle = "1 day ago",
                        icon = Icons.Default.Edit
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = PrimaryOrange)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun FeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun ActivityItem(title: String, subtitle: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryOrange)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

// ============================================================================
// SNACKCART SCREEN
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnackCartScreen(
    state: SnackCartUiState,
    onSearchQuery: (String) -> Unit,
    onAddToCart: (String, Int) -> Unit,
    onPlaceOrder: () -> Unit,
    onBackClick: () -> Unit
) {
    var showCart by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SnackCart") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (state.cartItems.isNotEmpty()) {
                                Badge { Text("${state.cartItems.size}") }
                            }
                        }
                    ) {
                        IconButton(onClick = { showCart = !showCart }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQuery,
                placeholder = { Text("Search snacks...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
            
            // Categories
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("All", "Noodles", "Chips", "Biscuits", "Beverages", "Sweets")
                items(categories) { category ->
                    FilterChip(
                        selected = false,
                        onClick = { onSearchQuery(category) },
                        label = { Text(category) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Snacks List
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val displaySnacks = state.filteredSnacks.ifEmpty { state.snacks }
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displaySnacks) { snack ->
                        SnackItemCard(
                            snack = snack,
                            onAddToCart = { onAddToCart(snack.id, 1) }
                        )
                    }
                }
            }
        }
        
        // Cart Bottom Sheet
        if (showCart) {
            ModalBottomSheet(
                onDismissRequest = { showCart = false }
            ) {
                CartContent(
                    items = state.cartItems,
                    totalAmount = state.totalAmount,
                    onPlaceOrder = {
                        onPlaceOrder()
                        showCart = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SnackItemCard(
    snack: SnackItem,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for image
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("🍿", fontSize = 24.sp)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(snack.name, fontWeight = FontWeight.Bold)
                Text(snack.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(
                    "₹${snack.price}",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange
                )
            }
            
            IconButton(onClick = onAddToCart) {
                Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = PrimaryOrange)
            }
        }
    }
}

@Composable
private fun CartContent(
    items: List<CartItemUi>,
    totalAmount: Double,
    onPlaceOrder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            "Your Cart",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (items.isEmpty()) {
            Text("Your cart is empty", color = Color.Gray, textAlign = TextAlign.Center)
        } else {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.snack.name} x${item.quantity}")
                    Text("₹${item.snack.price * item.quantity}")
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text("₹$totalAmount", fontWeight = FontWeight.Bold, color = PrimaryOrange)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onPlaceOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Place Order", fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ============================================================================
// ROOMIE SCREEN
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomieScreen(
    state: RoomieUiState,
    onSaveProfile: (RoomieProfileUi) -> Unit,
    onFindMatches: () -> Unit,
    onSendRequest: (String, String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Roomie Matcher") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Bar
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Profile") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { 
                        selectedTab = 1
                        onFindMatches()
                    },
                    text = { Text("Matches") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Requests") }
                )
            }
            
            when (selectedTab) {
                0 -> RoomieProfileTab(onSaveProfile = onSaveProfile)
                1 -> RoomieMatchesTab(
                    matches = state.matches,
                    isLoading = state.isLoading,
                    onSendRequest = onSendRequest
                )
                2 -> RoomieRequestsTab()
            }
        }
    }
}

@Composable
private fun RoomieProfileTab(
    onSaveProfile: (RoomieProfileUi) -> Unit
) {
    var bio by remember { mutableStateOf("") }
    var sleepSchedule by remember { mutableStateOf("Flexible") }
    var cleanliness by remember { mutableStateOf(3f) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Your Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            placeholder = { Text("Tell about yourself...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Sleep Schedule")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Early Bird", "Night Owl", "Flexible").forEach { option ->
                FilterChip(
                    selected = sleepSchedule == option,
                    onClick = { sleepSchedule = option },
                    label = { Text(option) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Cleanliness Level: ${cleanliness.toInt()}/5")
        Slider(
            value = cleanliness,
            onValueChange = { cleanliness = it },
            valueRange = 1f..5f,
            steps = 3
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                onSaveProfile(
                    RoomieProfileUi(
                        bio = bio,
                        sleepSchedule = sleepSchedule,
                        cleanlinessLevel = cleanliness.toInt()
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
        ) {
            Text("Save Profile")
        }
    }
}

@Composable
private fun RoomieMatchesTab(
    matches: List<RoomieMatchUi>,
    isLoading: Boolean,
    onSendRequest: (String, String) -> Unit
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (matches.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👥", fontSize = 48.sp)
                Text("No matches yet", color = Color.Gray)
                Text("Complete your profile to find matches", style = MaterialTheme.typography.bodySmall)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(matches) { match ->
                MatchCard(
                    match = match,
                    onSendRequest = { onSendRequest(match.userId, "Hi! I'd like to be your roommate.") }
                )
            }
        }
    }
}

@Composable
private fun MatchCard(
    match: RoomieMatchUi,
    onSendRequest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(match.name.first().toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(match.name, fontWeight = FontWeight.Bold)
                Text(match.bio, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Compatibility bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { match.compatibilityScore / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (match.compatibilityScore > 80) Color.Green else AccentPurple
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${match.compatibilityScore}%",
                        fontWeight = FontWeight.Bold,
                        color = AccentPurple
                    )
                }
            }
            
            IconButton(onClick = onSendRequest) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Send Request", tint = AccentPurple)
            }
        }
    }
}

@Composable
private fun RoomieRequestsTab() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📬", fontSize = 48.sp)
            Text("No pending requests", color = Color.Gray)
        }
    }
}

// ============================================================================
// PROFILE SCREEN
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(PrimaryOrange.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("D", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Dushy", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("dushy@hosteldada.com", color = Color.Gray)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Profile Options
            ProfileOption(icon = Icons.Default.Edit, title = "Edit Profile")
            ProfileOption(icon = Icons.Default.Settings, title = "Settings")
            ProfileOption(icon = Icons.Default.Notifications, title = "Notifications")
            ProfileOption(icon = Icons.Default.Info, title = "About")
        }
    }
}

@Composable
private fun ProfileOption(icon: ImageVector, title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryOrange)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
