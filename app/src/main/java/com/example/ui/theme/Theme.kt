package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = WarmAmber,
    onSecondary = Color.Black,
    surface = DarkCard,
    onSurface = DarkText,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = DarkMuted,
    background = DarkBackground,
    onBackground = DarkText,
    error = DangerRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = SurfaceTintSubtle,
    onPrimaryContainer = PrimaryIndigo,
    secondary = WarmAmber,
    onSecondary = Color.White,
    surface = CardPureWhite,
    onSurface = TextDarkSlate,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextMutedGray,
    background = BackgroundOffWhite,
    onBackground = TextDarkSlate,
    outline = BorderSubtle,
    error = DangerRed
)

@Composable
fun TimeTableTheme(
    darkTheme: Boolean = false, // Light theme as default per specification
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = TimeTableTheme(darkTheme, dynamicColor, content)
