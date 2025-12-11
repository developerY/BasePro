package com.ylabz.basepro.ashbike.wear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

// Define your brand colors (AshBike likely needs something energetic)
val AshBikePrimary = Color(0xFF00E676) // Bright Green
val AshBikePrimaryContainer = Color(0xFF005026)
val AshBikeOnPrimary = Color(0xFF003C1B)

// 1. Create the M3 Color Scheme
private val AppColorScheme = ColorScheme(
    primary = AshBikePrimary,
    primaryContainer = AshBikePrimaryContainer,
    onPrimary = AshBikeOnPrimary,
    // Define other colors (secondary, surface, etc.) or let them default
    onPrimaryContainer = AshBikePrimaryContainer,
    onSecondary = Color.White,
    onSecondaryContainer = Color.Black,
    onTertiary = Color.White,
    onTertiaryContainer = Color.Black,
    onSurfaceVariant = Color.White,
    onSurface = Color.White, // (0xFF1C1C1C), // Slightly darker
    onBackground = Color.White,
    background = Color.Black,
)

// 2. Create the Theme Wrapper
@Composable
fun BaseProTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        // typography = AppTypography, // Define custom type if you have it
        content = content
    )
}