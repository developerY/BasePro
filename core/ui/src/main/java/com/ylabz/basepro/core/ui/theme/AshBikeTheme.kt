package com.ylabz.basepro.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

// At the top of Theme.kt, define the colors for each theme
// Update the custom color definitions at the top of the file
private val LightCustomColors = CustomColors(
    speedSlow = LightSpeedSlow,
    speedMedium = LightSpeedMedium,
    speedFast = LightSpeedFast,
    settingsProfile = LightSettingsProfile,
    settingsNfc = LightSettingsNfc,
    settingsTheme = LightSettingsTheme,
    settingsBike = LightSettingsBike
)

private val DarkCustomColors = CustomColors(
    speedSlow = DarkSpeedSlow,
    speedMedium = DarkSpeedMedium,
    speedFast = DarkSpeedFast,
    settingsProfile = DarkSettingsProfile,
    settingsNfc = DarkSettingsNfc,
    settingsTheme = DarkSettingsTheme,
    settingsBike = DarkSettingsBike
)
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
surfaceVariant = LightSurfaceVariant,
onSurfaceVariant = LightOnSurfaceVariant,
outline = LightOutline
)

@Composable
fun AshBikeTheme(
    theme: String = "System",
    content: @Composable () -> Unit
) {
    val darkTheme = when (theme) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    // Provide both color sets to the theme
    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}