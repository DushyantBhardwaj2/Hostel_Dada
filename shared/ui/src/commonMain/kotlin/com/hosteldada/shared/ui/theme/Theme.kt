package com.hosteldada.shared.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Hostel Dada Color Palette
 * Based on Modern Material Design with Indigo primary
 */
object HostelDadaColors {
    // Primary Colors - Indigo
    val Primary = Color(0xFF6366F1)
    val PrimaryLight = Color(0xFF818CF8)
    val PrimaryDark = Color(0xFF4F46E5)
    val PrimaryContainer = Color(0xFFE0E7FF)
    val OnPrimary = Color.White
    val OnPrimaryContainer = Color(0xFF312E81)
    
    // Secondary Colors - Violet
    val Secondary = Color(0xFF8B5CF6)
    val SecondaryLight = Color(0xFFA78BFA)
    val SecondaryDark = Color(0xFF7C3AED)
    val SecondaryContainer = Color(0xFFEDE9FE)
    val OnSecondary = Color.White
    val OnSecondaryContainer = Color(0xFF4C1D95)
    
    // Tertiary Colors - Cyan
    val Tertiary = Color(0xFF06B6D4)
    val TertiaryLight = Color(0xFF22D3EE)
    val TertiaryDark = Color(0xFF0891B2)
    val TertiaryContainer = Color(0xFFCFFAFE)
    val OnTertiary = Color.White
    val OnTertiaryContainer = Color(0xFF164E63)
    
    // Status Colors
    val Success = Color(0xFF10B981)
    val SuccessLight = Color(0xFF34D399)
    val SuccessDark = Color(0xFF059669)
    val SuccessContainer = Color(0xFFD1FAE5)
    val OnSuccess = Color.White
    
    val Warning = Color(0xFFF59E0B)
    val WarningLight = Color(0xFFFBBF24)
    val WarningDark = Color(0xFFD97706)
    val WarningContainer = Color(0xFFFEF3C7)
    val OnWarning = Color.Black
    
    val Error = Color(0xFFEF4444)
    val ErrorLight = Color(0xFFF87171)
    val ErrorDark = Color(0xFFDC2626)
    val ErrorContainer = Color(0xFFFEE2E2)
    val OnError = Color.White
    
    val Info = Color(0xFF3B82F6)
    val InfoLight = Color(0xFF60A5FA)
    val InfoDark = Color(0xFF2563EB)
    val InfoContainer = Color(0xFFDBEAFE)
    val OnInfo = Color.White
    
    // Background Colors
    val Background = Color(0xFFF8FAFC)
    val BackgroundSecondary = Color(0xFFF1F5F9)
    val Surface = Color.White
    val SurfaceVariant = Color(0xFFF1F5F9)
    val OnBackground = Color(0xFF0F172A)
    val OnSurface = Color(0xFF1E293B)
    val OnSurfaceVariant = Color(0xFF475569)
    
    // Dark Theme
    val BackgroundDark = Color(0xFF0F172A)
    val BackgroundSecondaryDark = Color(0xFF1E293B)
    val SurfaceDark = Color(0xFF1E293B)
    val SurfaceVariantDark = Color(0xFF334155)
    val OnBackgroundDark = Color(0xFFF8FAFC)
    val OnSurfaceDark = Color(0xFFE2E8F0)
    val OnSurfaceVariantDark = Color(0xFF94A3B8)
    
    // Neutral Colors
    val Outline = Color(0xFFCBD5E1)
    val OutlineDark = Color(0xFF475569)
    val Divider = Color(0xFFE2E8F0)
    val DividerDark = Color(0xFF334155)
    
    // Compatibility Score Colors
    fun getScoreColor(score: Int): Color = when {
        score >= 80 -> Success
        score >= 60 -> Warning
        score >= 40 -> Color(0xFFF97316) // Orange
        else -> Error
    }
    
    // Order Status Colors
    fun getOrderStatusColor(status: String): Color = when (status.uppercase()) {
        "PENDING" -> Warning
        "CONFIRMED" -> Info
        "PREPARING" -> Tertiary
        "READY" -> Success
        "DELIVERED" -> SuccessDark
        "CANCELLED" -> Error
        else -> OnSurfaceVariant
    }
}

/**
 * Typography definitions using Inter font
 */
object HostelDadaTypography {
    // Font weights
    const val WeightLight = 300
    const val WeightRegular = 400
    const val WeightMedium = 500
    const val WeightSemiBold = 600
    const val WeightBold = 700
    
    // Font sizes (in sp)
    const val DisplayLarge = 57
    const val DisplayMedium = 45
    const val DisplaySmall = 36
    const val HeadlineLarge = 32
    const val HeadlineMedium = 28
    const val HeadlineSmall = 24
    const val TitleLarge = 22
    const val TitleMedium = 16
    const val TitleSmall = 14
    const val BodyLarge = 16
    const val BodyMedium = 14
    const val BodySmall = 12
    const val LabelLarge = 14
    const val LabelMedium = 12
    const val LabelSmall = 11
}

/**
 * Spacing definitions using 8px base unit
 */
object HostelDadaSpacing {
    const val XXS = 2
    const val XS = 4
    const val S = 8
    const val M = 12
    const val L = 16
    const val XL = 20
    const val XXL = 24
    const val XXXL = 32
    const val XXXXL = 48
    const val XXXXXL = 64
}

/**
 * Corner radius definitions
 */
object HostelDadaRadius {
    const val None = 0
    const val XS = 4
    const val S = 8
    const val M = 12
    const val L = 16
    const val XL = 20
    const val XXL = 24
    const val Full = 9999
}

/**
 * Shadow/Elevation definitions
 */
object HostelDadaElevation {
    const val None = 0
    const val XS = 1
    const val S = 2
    const val M = 4
    const val L = 8
    const val XL = 12
    const val XXL = 16
}
