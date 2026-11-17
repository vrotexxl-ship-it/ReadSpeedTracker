package com.example.readspeedtracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

enum class AppThemeColor {
    GRAPHITE, OCEAN, FOREST, BURGUNDY
}

// --- СВЕТЛЫЕ ТЕМЫ ---
private val LightGraphite = lightColorScheme(
    primary = Color(0xFF374151),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3F4F6),
    onPrimaryContainer = Color(0xFF111827),
    secondary = Color(0xFF6B7280),
    tertiary = Color(0xFF9CA3AF),
    background = Color(0xFFFAF9F6),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB)
)

private val LightOcean = lightColorScheme(
    primary = Color(0xFF1E3A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = Color(0xFF3B82F6),
    tertiary = Color(0xFF60A5FA),
    background = Color(0xFFFAF9F6),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB)
)

private val LightForest = lightColorScheme(
    primary = Color(0xFF064E3B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = Color(0xFF059669),
    tertiary = Color(0xFF10B981),
    background = Color(0xFFFAF9F6),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB)
)

private val LightBurgundy = lightColorScheme(
    primary = Color(0xFF7F1D1D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEE2E2),
    onPrimaryContainer = Color(0xFF7F1D1D),
    secondary = Color(0xFFB91C1C),
    tertiary = Color(0xFFDC2626),
    background = Color(0xFFFAF9F6),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB)
)

// --- ТЁМНЫЕ ТЕМЫ ---
private val DarkGraphite = darkColorScheme(
    primary = Color(0xFFE5E7EB),
    onPrimary = Color(0xFF111827),
    primaryContainer = Color(0xFF374151),
    onPrimaryContainer = Color(0xFFF9FAFB),
    secondary = Color(0xFF9CA3AF),
    tertiary = Color(0xFF6B7280),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = Color(0xFF404040)
)

private val DarkOcean = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF1E3A8A),
    primaryContainer = Color(0xFF1E40AF),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF3B82F6),
    tertiary = Color(0xFF93C5FD),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = Color(0xFF404040)
)

private val DarkForest = darkColorScheme(
    primary = Color(0xFF34D399),
    onPrimary = Color(0xFF064E3B),
    primaryContainer = Color(0xFF047857),
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = Color(0xFF10B981),
    tertiary = Color(0xFF6EE7B7),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = Color(0xFF404040)
)

private val DarkBurgundy = darkColorScheme(
    primary = Color(0xFFFCA5A5),
    onPrimary = Color(0xFF7F1D1D),
    primaryContainer = Color(0xFF991B1B),
    onPrimaryContainer = Color(0xFFFEE2E2),
    secondary = Color(0xFFEF4444),
    tertiary = Color(0xFFF87171),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = Color(0xFF404040)
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun ReadSpeedTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: AppThemeColor = AppThemeColor.GRAPHITE,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> when (themeColor) {
            AppThemeColor.GRAPHITE -> DarkGraphite
            AppThemeColor.OCEAN -> DarkOcean
            AppThemeColor.FOREST -> DarkForest
            AppThemeColor.BURGUNDY -> DarkBurgundy
        }
        else -> when (themeColor) {
            AppThemeColor.GRAPHITE -> LightGraphite
            AppThemeColor.OCEAN -> LightOcean
            AppThemeColor.FOREST -> LightForest
            AppThemeColor.BURGUNDY -> LightBurgundy
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}