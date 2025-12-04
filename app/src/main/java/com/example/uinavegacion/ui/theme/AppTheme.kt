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

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = SurfaceLight,
    primaryContainer = PrimaryOrange.copy(alpha = 0.1f),
    onPrimaryContainer = PrimaryOrange,
    secondary = SecondaryBlue,
    onSecondary = SurfaceLight,
    secondaryContainer = SecondaryBlue.copy(alpha = 0.1f),
    onSecondaryContainer = SecondaryBlue,
    tertiary = DarkBlue,
    onTertiary = SurfaceLight,
    tertiaryContainer = DarkBlue.copy(alpha = 0.1f),
    onTertiaryContainer = DarkBlue,
    error = ErrorRed,
    onError = SurfaceLight,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = TextSecondaryLight,
    outlineVariant = TextSecondaryLight.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    onPrimary = BackgroundDark,
    primaryContainer = PrimaryOrange.copy(alpha = 0.2f),
    onPrimaryContainer = PrimaryOrange,
    secondary = SecondaryBlue,
    onSecondary = BackgroundDark,
    secondaryContainer = SecondaryBlue.copy(alpha = 0.2f),
    onSecondaryContainer = SecondaryBlue,
    tertiary = DarkBlue,
    onTertiary = SurfaceLight,
    tertiaryContainer = DarkBlue.copy(alpha = 0.2f),
    onTertiaryContainer = DarkBlue,
    error = ErrorRed,
    onError = BackgroundDark,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = TextSecondaryDark,
    outlineVariant = TextSecondaryDark.copy(alpha = 0.5f)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

