package com.hosteldada.shared.navigation

/**
 * Navigation routes for the Hostel Dada app
 * Follows type-safe navigation pattern
 */
object Routes {
    // Auth routes
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val FORGOT_PASSWORD = "forgot_password"
    
    // Profile routes
    const val PROFILE_SETUP = "profile_setup"
    const val PROFILE_VIEW = "profile/{userId}"
    const val PROFILE_EDIT = "profile/edit"
    
    // Dashboard
    const val DASHBOARD = "dashboard"
    
    // SnackCart routes
    const val SNACK_CART_HOME = "snackcart"
    const val SNACK_DETAIL = "snackcart/snack/{snackId}"
    const val CART = "snackcart/cart"
    const val CHECKOUT = "snackcart/checkout"
    const val ORDER_HISTORY = "snackcart/orders"
    const val ORDER_DETAIL = "snackcart/order/{orderId}"
    const val SNACK_CART_ADMIN = "snackcart/admin"
    const val SNACK_CART_ADMIN_ORDERS = "snackcart/admin/orders"
    const val SNACK_CART_ADMIN_INVENTORY = "snackcart/admin/inventory"
    const val SNACK_CART_ADMIN_STATS = "snackcart/admin/stats"
    
    // Roomie routes
    const val ROOMIE_HOME = "roomie"
    const val ROOMIE_SURVEY = "roomie/survey"
    const val ROOMIE_SURVEY_STEP = "roomie/survey/{step}"
    const val ROOMIE_MATCHES = "roomie/matches"
    const val ROOMIE_MATCH_DETAIL = "roomie/match/{studentId}"
    const val ROOMIE_ADMIN = "roomie/admin"
    const val ROOMIE_ADMIN_SURVEYS = "roomie/admin/surveys"
    const val ROOMIE_ADMIN_ROOMS = "roomie/admin/rooms"
    const val ROOMIE_ADMIN_ASSIGNMENTS = "roomie/admin/assignments"
    
    // Future modules (placeholders)
    const val LAUNDRY_HOME = "laundry"
    const val MAINTENANCE_HOME = "maintenance"
    const val MESS_HOME = "mess"
    const val EVENTS_HOME = "events"
    const val LOST_FOUND_HOME = "lost_found"
    
    // Settings
    const val SETTINGS = "settings"
    const val SETTINGS_NOTIFICATIONS = "settings/notifications"
    const val SETTINGS_THEME = "settings/theme"
    const val SETTINGS_ABOUT = "settings/about"
    
    // Helper functions to build routes with arguments
    object Args {
        fun snackDetail(snackId: String) = "snackcart/snack/$snackId"
        fun orderDetail(orderId: String) = "snackcart/order/$orderId"
        fun matchDetail(studentId: String) = "roomie/match/$studentId"
        fun surveyStep(step: Int) = "roomie/survey/$step"
        fun profileView(userId: String) = "profile/$userId"
    }
}

/**
 * Navigation events sealed class
 */
sealed class NavigationEvent {
    data class NavigateTo(val route: String) : NavigationEvent()
    object NavigateBack : NavigationEvent()
    data class NavigateBackTo(val route: String) : NavigationEvent()
    object ClearBackStack : NavigationEvent()
    data class NavigateWithPopUp(
        val route: String,
        val popUpTo: String,
        val inclusive: Boolean = false
    ) : NavigationEvent()
}

/**
 * Deep link configuration
 */
object DeepLinks {
    const val BASE_URI = "hosteldada://"
    const val WEB_URI = "https://hosteldada.app"
    
    object Patterns {
        const val ORDER = "order/{orderId}"
        const val SNACK = "snack/{snackId}"
        const val MATCH = "match/{studentId}"
        const val SURVEY = "survey"
    }
    
    fun parseDeepLink(uri: String): DeepLinkResult? {
        return when {
            uri.startsWith("$BASE_URI/order/") -> {
                val orderId = uri.removePrefix("$BASE_URI/order/")
                DeepLinkResult.Order(orderId)
            }
            uri.startsWith("$BASE_URI/snack/") -> {
                val snackId = uri.removePrefix("$BASE_URI/snack/")
                DeepLinkResult.Snack(snackId)
            }
            uri.startsWith("$BASE_URI/match/") -> {
                val studentId = uri.removePrefix("$BASE_URI/match/")
                DeepLinkResult.Match(studentId)
            }
            uri.startsWith("$BASE_URI/survey") -> {
                DeepLinkResult.Survey
            }
            else -> null
        }
    }
}

sealed class DeepLinkResult {
    data class Order(val orderId: String) : DeepLinkResult()
    data class Snack(val snackId: String) : DeepLinkResult()
    data class Match(val studentId: String) : DeepLinkResult()
    object Survey : DeepLinkResult()
}

/**
 * Bottom navigation items
 */
enum class BottomNavItem(
    val route: String,
    val title: String,
    val iconName: String
) {
    HOME(Routes.DASHBOARD, "Home", "home"),
    SNACK_CART(Routes.SNACK_CART_HOME, "SnackCart", "shopping_cart"),
    ROOMIE(Routes.ROOMIE_HOME, "Roomie", "people"),
    PROFILE(Routes.PROFILE_VIEW, "Profile", "person")
}

/**
 * Navigation state holder
 */
data class NavigationState(
    val currentRoute: String = Routes.LOGIN,
    val isAuthenticated: Boolean = false,
    val isProfileComplete: Boolean = false,
    val backStack: List<String> = emptyList()
) {
    val showBottomNav: Boolean
        get() = isAuthenticated && isProfileComplete && !isAuthRoute
    
    private val isAuthRoute: Boolean
        get() = currentRoute in listOf(
            Routes.LOGIN,
            Routes.SIGNUP,
            Routes.FORGOT_PASSWORD,
            Routes.PROFILE_SETUP
        )
    
    val startDestination: String
        get() = when {
            !isAuthenticated -> Routes.LOGIN
            !isProfileComplete -> Routes.PROFILE_SETUP
            else -> Routes.DASHBOARD
        }
}
