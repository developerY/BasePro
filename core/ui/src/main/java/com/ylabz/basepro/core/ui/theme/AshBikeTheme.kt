package com.ylabz.basepro.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    onError = md_theme_dark_onError,
    errorContainer = md_theme_dark_errorContainer,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    errorContainer = md_theme_light_errorContainer,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

@Immutable
data class ExtendedColorScheme(
    val iconColorDistance: Color = Color.Unspecified,
    val iconColorDuration: Color = Color.Unspecified,
    val iconColorAvgSpeed: Color = Color.Unspecified,
    val iconColorSpeed: Color = Color.Unspecified, // For a potential separate speed stat icon
    val iconColorElevation: Color = Color.Unspecified,
    val iconColorCalories: Color = Color.Unspecified,
    val iconColorBikeActive: Color = Color.Unspecified
)

val LocalExtendedColorScheme = staticCompositionLocalOf { ExtendedColorScheme() }

val ColorScheme.iconColorDistance: Color @Composable get() = IconBrown
val ColorScheme.iconColorDuration: Color @Composable get() = IconPurple
val ColorScheme.iconColorAvgSpeed: Color @Composable get() = IconOrange
val ColorScheme.iconColorSpeed: Color @Composable get() = IconBlue // For a potential separate speed stat icon
val ColorScheme.iconColorElevation: Color @Composable get() = IconGreen
val ColorScheme.iconColorCalories: Color @Composable get() = IconRed
val ColorScheme.iconColorBikeActive: Color @Composable get() = BikeIconGreen

val ColorScheme.iconColorHeartRate: Color @Composable get() = IconGlayBlue // on but not connected ???


object ThemeIdentifiers {
    const val LIGHT = "Light"
    const val DARK = "Dark"
    const val SYSTEM = "System"
}

@Composable
fun AshBikeTheme(
    theme: String = ThemeIdentifiers.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (theme) {
        ThemeIdentifiers.LIGHT -> false
        ThemeIdentifiers.DARK -> true
        else -> isSystemInDarkTheme() // Defaults to System if an unknown string is passed
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // You might want to provide the extended color scheme here if you prefer a CompositionLocal approach
    // For now, direct extensions on MaterialTheme.colorScheme are used.

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
