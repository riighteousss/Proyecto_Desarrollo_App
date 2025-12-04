package com.example.uinavegacion.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Colores personalizados naranja y azul
private val Orange = androidx.compose.ui.graphics.Color(0xFFFF6B35)
private val Blue = androidx.compose.ui.graphics.Color(0xFF2E86AB)
private val DarkOrange = androidx.compose.ui.graphics.Color(0xFFE55A2B)
private val DarkBlue = androidx.compose.ui.graphics.Color(0xFF1B4F72)

private val LightColorScheme = lightColorScheme(
    primary = Orange,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Orange.copy(alpha = 0.1f),
    onPrimaryContainer = Orange,
    secondary = Blue,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Blue.copy(alpha = 0.1f),
    onSecondaryContainer = Blue,
    tertiary = DarkBlue,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = DarkBlue.copy(alpha = 0.1f),
    onTertiaryContainer = DarkBlue,
    error = androidx.compose.ui.graphics.Color(0xFFE53E3E),
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = androidx.compose.ui.graphics.Color(0xFFE53E3E).copy(alpha = 0.1f),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFE53E3E),
    background = androidx.compose.ui.graphics.Color(0xFFF8F9FA),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1A1A1A),
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF1A1A1A),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF1F3F4),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF424242),
    outline = androidx.compose.ui.graphics.Color(0xFFBDBDBD),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFFE0E0E0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Orange,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = Orange.copy(alpha = 0.2f),
    onPrimaryContainer = Orange,
    secondary = Blue,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = Blue.copy(alpha = 0.2f),
    onSecondaryContainer = Blue,
    tertiary = DarkBlue,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = DarkBlue.copy(alpha = 0.2f),
    onTertiaryContainer = DarkBlue,
    error = androidx.compose.ui.graphics.Color(0xFFE53E3E),
    onError = androidx.compose.ui.graphics.Color.Black,
    errorContainer = androidx.compose.ui.graphics.Color(0xFFE53E3E).copy(alpha = 0.2f),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFE53E3E),
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE1E1E1),
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE1E1E1),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFBDBDBD),
    outline = androidx.compose.ui.graphics.Color(0xFF666666),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFF404040)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

