package com.ylabz.basepro.core.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


// Light Theme Colors
val LightPrimary = Color(0xFF8B93FF)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFE0E0FF)
val LightOnPrimaryContainer = Color(0xFF00006E)

val LightSecondary = Color(0xFFB9C3FF)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFE0E0FF)
val LightOnSecondaryContainer = Color(0xFF161B33)

val LightTertiary = Color(0xFFA6C8FF)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFD4E3FF)
val LightOnTertiaryContainer = Color(0xFF001D36)

val LightBackground = Color(0xFFFDFBFF)
val LightOnBackground = Color(0xFF1B1B1F)
val LightSurface = Color(0xFFFDFBFF)
val LightOnSurface = Color(0xFF1B1B1F)
val LightSurfaceVariant = Color(0xFFE3E1EC)
val LightOnSurfaceVariant = Color(0xFF46464F)
val LightOutline = Color(0xFF777680)

// Dark Theme Colors
val DarkPrimary = Color(0xFFBFC2FF)
val DarkOnPrimary = Color(0xFF222977)
val DarkPrimaryContainer = Color(0xFF3B428F)
val DarkOnPrimaryContainer = Color(0xFFE0E0FF)

val DarkSecondary = Color(0xFFBEC6EB)
val DarkOnSecondary = Color(0xFF2B3048)
val DarkSecondaryContainer = Color(0xFF42475F)
val DarkOnSecondaryContainer = Color(0xFFDAE2FF)

val DarkTertiary = Color(0xFFA6C8FF)
val DarkOnTertiary = Color(0xFF003258)
val DarkTertiaryContainer = Color(0xFF284970)
val DarkOnTertiaryContainer = Color(0xFFD4E3FF)

val DarkBackground = Color(0xFF1B1B1F)
val DarkOnBackground = Color(0xFFE4E1E6)
val DarkSurface = Color(0xFF1B1B1F)
val DarkOnSurface = Color(0xFFE4E1E6)
val DarkSurfaceVariant = Color(0xFF46464F)
val DarkOnSurfaceVariant = Color(0xFFC7C5D0)
val DarkOutline = Color(0xFF91909A)


// Add these to your existing Color.kt

// —————————————————————————————————————————————————————————
//  PASTEL SETTINGS COLORS
// —————————————————————————————————————————————————————————
val LightSettingsProfile = Color(0xFFF3E5F5) // PastelLavender
val LightSettingsNfc = Color(0xFFDCEEFB)     // PastelBlue
val LightSettingsTheme = Color(0xFFEFECF6)    // PastelLilac
val LightSettingsBike = Color(0xFFDBF1DB)     // PastelGreen

// Dark theme versions of the pastel colors
val DarkSettingsProfile = Color(0xFF4A444C)
val DarkSettingsNfc = Color(0xFF3B4851)
val DarkSettingsTheme = Color(0xFF46454A)
val DarkSettingsBike = Color(0xFF3F4B3F)

// Add these to your existing Color.kt

// Theme-aware colors for the Speedometer gradient
val LightSpeedSlow = Color(0xFF4CAF50)      // Material Green 500
val LightSpeedMedium = Color(0xFFFFEB3B)    // Material Yellow 500
val LightSpeedFast = Color(0xFFF44336)      // Material Red 500

val DarkSpeedSlow = Color(0xFF81C784)       // Material Green 300
val DarkSpeedMedium = Color(0xFFFFF176)     // Material Yellow 300
val DarkSpeedFast = Color(0xFFE57373)       // Material Red 300


// Your exact color stops for the light theme speedometer.
val LightSpeedometerColorStopsRest = arrayOf(
    0.0f to Color(0xFF1E561F),  // Start with Green
    0.1f to Color(0xFF349439),
    0.2f to Color(0xFF68B739),
    0.4f to Color(0xFFA6C476),
    0.6f to Color(0xFFCFFF22),
    0.8f to Color(0xFFFFE607),
    0.9f to Color(0xFFFFB13B),
    1.0f to Color(0xFFF44336)   // End with Red
)

// A brighter, more vibrant version for dark theme.
val DarkSpeedometerColorStopsRest = arrayOf(
    0.0f to Color(0xFF68B739),  // Start with a brighter Green
    0.1f to Color(0xFF81C784),
    0.2f to Color(0xFFAED581),
    0.4f to Color(0xFFDCE775),
    0.6f to Color(0xFFFFF176),
    0.8f to Color(0xFFFFD54F),
    0.9f to Color(0xFFFFB74D),
    1.0f to Color(0xFFE57373)   // End with a softer Red
)


// Your exact color stops for the light theme speedometer.
val LightSpeedometerColorStops = arrayOf(
    0.0f to Color(0xFFFF5722),
    0.2f to Color(0xFFF44336),
    0.3f to Color(0xFF1E561F),
    0.4f to Color(0xFF349439),
    0.5f to Color(0xFF68B739),
    0.6f to Color(0xFFA6C476),
    0.7f to Color(0xFFCFFF22),
    0.8f to Color(0xFFFFE607),
    0.9f to Color(0xFFFFB13B),
    1.0f to Color(0xFFFF5722)
)

// A brighter, more vibrant version of YOUR color palette for dark theme.
val DarkSpeedometerColorStops = arrayOf(
    0.0f to Color(0xFFFF8A65), // Brighter version of 0xFFFF5722
    0.2f to Color(0xFFE57373), // Brighter version of 0xFFF44336
    0.3f to Color(0xFF66BB6A), // Brighter version of 0xFF1E561F
    0.4f to Color(0xFF81C784), // Brighter version of 0xFF349439
    0.5f to Color(0xFFAED581), // Brighter version of 0xFF68B739
    0.6f to Color(0xFFDCE775), // Brighter version of 0xFFA6C476
    0.7f to Color(0xFFEEFF41), // Brighter version of 0xFFCFFF22
    0.8f to Color(0xFFFFF176), // Brighter version of 0xFFFFE607
    0.9f to Color(0xFFFFD54F), // Brighter version of 0xFFFFB13B
    1.0f to Color(0xFFFF8A65)  // Brighter version of 0xFFFF5722
)


val progressBrush = Brush.sweepGradient(
    colorStops = arrayOf(
        0.0f to Color(0xFFFF5722),  // dark green
        0.2f to Color(0xFFF44336),
        0.3f to Color(0xFF1E561F),
        0.4f to Color(0xFF349439),
        0.5f to Color(0xFF68B739),
        0.6f to Color(0xFFA6C476),
        0.7f to Color(0xFFCFFF22),
        0.8f to Color(0xFFFFE607),
        0.9f to Color(0xFFFFB13B),
        1.0f to Color(0xFFFF5722)// Color(0xFFFF9800)
    )
)