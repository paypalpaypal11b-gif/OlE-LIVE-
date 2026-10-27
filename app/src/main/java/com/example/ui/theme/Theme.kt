package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = OleGreenPrimary,
    onPrimary = Color.Black,
    primaryContainer = OleGreenDark,
    onPrimaryContainer = OleWhite,
    secondary = OleGreenAccent,
    onSecondary = Color.Black,
    secondaryContainer = OleBlackSurfaceVariant,
    onSecondaryContainer = OleGreenLight,
    tertiary = OleGold,
    onTertiary = Color.Black,
    background = OleBlack,
    onBackground = OleWhite,
    surface = OleBlackSurface,
    onSurface = OleWhite,
    surfaceVariant = OleBlackSurfaceVariant,
    onSurfaceVariant = OleWhiteSubtle,
    outline = OleBlackBorder,
    error = OleRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = OleGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F5E9),
    onPrimaryContainer = OleGreenDark,
    secondary = OleGreenDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = OleGold,
    onTertiary = Color.Black,
    background = Color(0xFFF7FBF8),
    onBackground = OleBlack,
    surface = Color.White,
    onSurface = OleBlack,
    surfaceVariant = Color(0xFFEBF2EC),
    onSurfaceVariant = Color(0xFF2C3E30),
    outline = Color(0xFFB0C4B4),
    error = OleRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek live streaming dark aesthetic
    dynamicColor: Boolean = false, // Keep branded green/black/white palette
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
