package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark Scheme: Premium Obsidian, Midnight, and Amber Gold accents
private val DarkColorScheme = darkColorScheme(
    primary = AmberGold,
    onPrimary = DarkInk,
    primaryContainer = DeepSlateBlue,
    onPrimaryContainer = SoftWhiteText,
    secondary = SoftAmber,
    onSecondary = DarkInk,
    secondaryContainer = DeepCaviar,
    onSecondaryContainer = SoftWhiteText,
    tertiary = MutedSlate,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = SoftWhiteText,
    surface = DeepCaviar,
    onSurface = SoftWhiteText,
    surfaceVariant = DeepSlateBlue,
    onSurfaceVariant = SoftWhiteText,
    error = CoralRed,
    onError = Color.White,
    outline = MutedSlate
)

// Light Scheme: Soft Parchment paper, Deep Slate Blue ink, and Amber Gold highlights
private val LightColorScheme = lightColorScheme(
    primary = DeepSlateBlue,
    onPrimary = Color.White,
    primaryContainer = ParchmentWhite,
    onPrimaryContainer = DarkInk,
    secondary = AmberGold,
    onSecondary = DarkInk,
    secondaryContainer = IvorySurface,
    onSecondaryContainer = DarkInk,
    tertiary = DarkInk,
    onTertiary = ParchmentWhite,
    background = ParchmentWhite,
    onBackground = DarkInk,
    surface = IvorySurface,
    onSurface = DarkInk,
    surfaceVariant = SoftClay,
    onSurfaceVariant = DarkInk,
    error = CoralRed,
    onError = Color.White,
    outline = SoftClay
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // In this premium editor, we want to force our signature author palette by default
    overrideCustomColors: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Loaded from Type.kt
        content = content
    )
}
