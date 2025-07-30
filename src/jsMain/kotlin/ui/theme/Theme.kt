package ui.theme

import org.jetbrains.compose.web.css.*

/**
 * 🎨 Design System for Hostel Dada Web Application
 * 
 * Centralized theme configuration following Material Design principles
 * with custom hostel-focused color palette and responsive design tokens
 */

/**
 * 🌈 Color Palette
 */
object AppColors {
    // Primary Colors
    val primary = Color("#2196F3")          // Hostel Blue
    val primaryDark = Color("#1976D2")      // Darker Blue
    val primaryLight = Color("#64B5F6")     // Lighter Blue
    
    // Secondary Colors
    val secondary = Color("#FF9800")        // Warm Orange
    val secondaryDark = Color("#F57C00")    // Darker Orange
    val secondaryLight = Color("#FFB74D")   // Lighter Orange
    
    // Status Colors
    val success = Color("#4CAF50")          // Green
    val warning = Color("#FF9800")          // Orange
    val error = Color("#F44336")            // Red
    val info = Color("#2196F3")             // Blue
    
    // Neutral Colors
    val background = Color("#FAFAFA")       // Light Gray Background
    val surface = Color("#FFFFFF")          // White Surface
    val surfaceVariant = Color("#F5F5F5")   // Light Gray Surface
    
    // Text Colors
    val textPrimary = Color("#212121")      // Dark Gray
    val textSecondary = Color("#757575")    // Medium Gray
    val textDisabled = Color("#BDBDBD")     // Light Gray
    val textOnPrimary = Color("#FFFFFF")    // White on Primary
    
    // Border & Outline Colors
    val border = Color("#E0E0E0")           // Light Border
    val borderDark = Color("#BDBDBD")       // Dark Border
    val outline = Color("#9E9E9E")          // Outline Gray
    
    // Module-Specific Colors
    val snackCart = Color("#FF6B6B")        // Red for SnackCart
    val roomieMatcher = Color("#4ECDC4")    // Teal for RoomieMatcher
    val laundryBalancer = Color("#45B7D1")  // Blue for LaundryBalancer
    val messyMess = Color("#96CEB4")        // Green for MessyMess
    val hostelFixer = Color("#FFEAA7")      // Yellow for HostelFixer
}

/**
 * 📏 Spacing System
 */
object AppSpacing {
    val none = 0
    val tiny = 4
    val small = 8
    val medium = 16
    val large = 24
    val extraLarge = 32
    val huge = 48
    val massive = 64
    
    // Component-specific spacing
    val cardPadding = medium
    val sectionSpacing = large
    val componentGap = small
}

/**
 * 📝 Typography Scale
 */
object AppTypography {
    // Font Families
    val fontFamily = "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif"
    val monoFamily = "'JetBrains Mono', 'Monaco', 'Menlo', monospace"
    
    // Font Sizes
    val h1 = 32.px      // Hero headings
    val h2 = 28.px      // Section headings
    val h3 = 24.px      // Subsection headings
    val h4 = 20.px      // Card titles
    val h5 = 18.px      // Component titles
    val h6 = 16.px      // Small headings
    
    val body1 = 16.px   // Regular text
    val body2 = 14.px   // Secondary text
    val caption = 12.px // Captions, labels
    val button = 14.px  // Button text
    
    // Font Weights
    val weightLight = 300
    val weightRegular = 400
    val weightMedium = 500
    val weightSemiBold = 600
    val weightBold = 700
    
    // Line Heights
    val lineHeightTight = 1.2
    val lineHeightNormal = 1.5
    val lineHeightRelaxed = 1.6
}

/**
 * 🎭 Elevation & Shadows
 */
object AppElevation {
    val none = "none"
    val small = "0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24)"
    val medium = "0 3px 6px rgba(0,0,0,0.16), 0 3px 6px rgba(0,0,0,0.23)"
    val large = "0 10px 20px rgba(0,0,0,0.19), 0 6px 6px rgba(0,0,0,0.23)"
    val extraLarge = "0 14px 28px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22)"
    
    // Interactive shadows
    val hover = "0 6px 12px rgba(0,0,0,0.15), 0 4px 4px rgba(0,0,0,0.15)"
    val active = "0 2px 4px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24)"
}

/**
 * 📐 Border Radius
 */
object AppRadius {
    val none = 0.px
    val small = 4.px
    val medium = 8.px
    val large = 12.px
    val extraLarge = 16.px
    val circle = 50.percent
    
    // Component-specific radius
    val button = medium
    val card = large
    val input = medium
    val badge = circle
}

/**
 * 📱 Responsive Breakpoints
 */
object AppBreakpoints {
    val mobile = 320.px
    val tablet = 768.px
    val desktop = 1024.px
    val widescreen = 1200.px
    
    // Media query helpers
    fun mobile() = "(max-width: 767px)"
    fun tablet() = "(min-width: 768px) and (max-width: 1023px)"
    fun desktop() = "(min-width: 1024px)"
    fun widescreen() = "(min-width: 1200px)"
}

/**
 * ⚡ Animation & Transitions
 */
object AppTransitions {
    val fast = "150ms"
    val normal = "250ms"
    val slow = "400ms"
    
    // Easing functions
    val easeOut = "cubic-bezier(0.25, 0.46, 0.45, 0.94)"
    val easeIn = "cubic-bezier(0.55, 0.055, 0.675, 0.19)"
    val easeInOut = "cubic-bezier(0.645, 0.045, 0.355, 1)"
    
    // Common transitions
    val button = "all ${normal} ${easeOut}"
    val hover = "all ${fast} ${easeOut}"
    val modal = "all ${normal} ${easeInOut}"
}

/**
 * 🎯 Z-Index Scale
 */
object AppZIndex {
    val base = 1
    val dropdown = 1000
    val modal = 1050
    val popover = 1060
    val tooltip = 1070
    val notification = 1080
    val overlay = 1090
}

/**
 * 🎨 CSS Custom Properties (CSS Variables)
 */
fun StyleScope.applyThemeVariables() {
    // Colors
    property("--color-primary", AppColors.primary.toString())
    property("--color-secondary", AppColors.secondary.toString())
    property("--color-success", AppColors.success.toString())
    property("--color-warning", AppColors.warning.toString())
    property("--color-error", AppColors.error.toString())
    property("--color-background", AppColors.background.toString())
    property("--color-surface", AppColors.surface.toString())
    property("--color-text-primary", AppColors.textPrimary.toString())
    property("--color-text-secondary", AppColors.textSecondary.toString())
    
    // Spacing
    property("--spacing-small", "${AppSpacing.small}px")
    property("--spacing-medium", "${AppSpacing.medium}px")
    property("--spacing-large", "${AppSpacing.large}px")
    
    // Typography
    property("--font-family", AppTypography.fontFamily)
    property("--font-size-body", AppTypography.body1.toString())
    property("--line-height-normal", AppTypography.lineHeightNormal.toString())
    
    // Borders
    property("--border-radius", AppRadius.medium.toString())
    property("--border-color", AppColors.border.toString())
    
    // Shadows
    property("--shadow-small", AppElevation.small)
    property("--shadow-medium", AppElevation.medium)
    
    // Transitions
    property("--transition-normal", AppTransitions.normal)
    property("--transition-fast", AppTransitions.fast)
}

/**
 * 🎨 Global Styles
 */
fun StyleScope.applyGlobalStyles() {
    // Apply theme variables
    applyThemeVariables()
    
    // Reset and base styles
    property("box-sizing", "border-box")
    fontFamily(AppTypography.fontFamily)
    fontSize(AppTypography.body1)
    lineHeight(AppTypography.lineHeightNormal)
    color(AppColors.textPrimary)
    backgroundColor(AppColors.background)
}

/**
 * 🎭 Module Theme Colors Helper
 */
object ModuleColors {
    fun getModuleColor(moduleName: String): Color {
        return when (moduleName.lowercase()) {
            "snackcart" -> AppColors.snackCart
            "roomiematcher" -> AppColors.roomieMatcher
            "laundrybalancer" -> AppColors.laundryBalancer
            "messymess" -> AppColors.messyMess
            "hostelfixer" -> AppColors.hostelFixer
            else -> AppColors.primary
        }
    }
    
    fun getModuleIcon(moduleName: String): String {
        return when (moduleName.lowercase()) {
            "snackcart" -> "🍿"
            "roomiematcher" -> "🤝"
            "laundrybalancer" -> "👕"
            "messymess" -> "🍽️"
            "hostelfixer" -> "🔧"
            else -> "🏠"
        }
    }
}
