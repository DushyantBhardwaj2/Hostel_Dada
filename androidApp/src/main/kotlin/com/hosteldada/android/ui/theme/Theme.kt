package com.hosteldada.android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Hostel Dada Theme
 * 
 * Material Design 3 theme with custom brand colors
 */

// Brand Colors
val Orange500 = Color(0xFFFF6B35)
val Orange700 = Color(0xFFE55A2B)
val Purple500 = Color(0xFF6C63FF)
val Purple700 = Color(0xFF5A52E0)
val DarkBackground = Color(0xFF1A1A2E)
val LightBackground = Color(0xFFF7F7F7)

// Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = Orange500,
    onPrimary = Color.White,
    primaryContainer = Orange500.copy(alpha = 0.1f),
    onPrimaryContainer = Orange700,
    
    secondary = Purple500,
    onSecondary = Color.White,
    secondaryContainer = Purple500.copy(alpha = 0.1f),
    onSecondaryContainer = Purple700,
    
    background = LightBackground,
    onBackground = DarkBackground,
    
    surface = Color.White,
    onSurface = DarkBackground,
    surfaceVariant = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFF666666),
    
    error = Color(0xFFE53935),
    onError = Color.White
)

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = Orange500,
    onPrimary = Color.White,
    primaryContainer = Orange700,
    onPrimaryContainer = Color.White,
    
    secondary = Purple500,
    onSecondary = Color.White,
    secondaryContainer = Purple700,
    onSecondaryContainer = Color.White,
    
    background = DarkBackground,
    onBackground = Color.White,
    
    surface = Color(0xFF252542),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF333355),
    onSurfaceVariant = Color(0xFFCCCCCC),
    
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun HostelDadaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Typography
val Typography = Typography()
