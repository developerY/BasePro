package com.ylabz.basepro.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

// Light Theme Pastel Palette
val md_theme_light_primary = Color(0xFF4A6572)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFCFE6F2)
val md_theme_light_onPrimaryContainer = Color(0xFF04202B)
val md_theme_light_secondary = Color(0xFFD32F2F)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFDAD8)
val md_theme_light_onSecondaryContainer = Color(0xFF410004)
val md_theme_light_tertiary = Color(0xFF5E5379)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFE4D9FF)
val md_theme_light_onTertiaryContainer = Color(0xFF1B0F33)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFF8FDFF)
val md_theme_light_onBackground = Color(0xFF001F25)
val md_theme_light_surface = Color(0xFFF8FDFF)
val md_theme_light_onSurface = Color(0xFF001F25)
val md_theme_light_surfaceVariant = Color(0xFFDBE4E8)
val md_theme_light_onSurfaceVariant = Color(0xFF3F484B)
val md_theme_light_outline = Color(0xFF6F797B)
val md_theme_light_inverseOnSurface = Color(0xFFD6F6FF)
val md_theme_light_inverseSurface = Color(0xFF00363F)
val md_theme_light_inversePrimary = Color(0xFFB3CAD5)
val md_theme_light_surfaceTint = Color(0xFF4A6572)
val md_theme_light_outlineVariant = Color(0xFFBFC8CC)
val md_theme_light_scrim = Color(0xFF000000)

// Dark Theme Pastel Palette
val md_theme_dark_primary = Color(0xFFB3CAD5)
val md_theme_dark_onPrimary = Color(0xFF1B3541)
val md_theme_dark_primaryContainer = Color(0xFF324C58)
val md_theme_dark_onPrimaryContainer = Color(0xFFCFE6F2)
val md_theme_dark_secondary = Color(0xFFFFB3B0)
val md_theme_dark_onSecondary = Color(0xFF68000B)
val md_theme_dark_secondaryContainer = Color(0xFF930015)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDAD8)
val md_theme_dark_tertiary = Color(0xFFC7BEE0)
val md_theme_dark_onTertiary = Color(0xFF2F2448)
val md_theme_dark_tertiaryContainer = Color(0xFF463A60)
val md_theme_dark_onTertiaryContainer = Color(0xFFE4D9FF)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF001F25)
val md_theme_dark_onBackground = Color(0xFFA6EEFF)
val md_theme_dark_surface = Color(0xFF001F25)
val md_theme_dark_onSurface = Color(0xFFA6EEFF)
val md_theme_dark_surfaceVariant = Color(0xFF3F484B)
val md_theme_dark_onSurfaceVariant = Color(0xFFBFC8CC)
val md_theme_dark_outline = Color(0xFF899295)
val md_theme_dark_inverseOnSurface = Color(0xFF001F25)
val md_theme_dark_inverseSurface = Color(0xFFA6EEFF)
val md_theme_dark_inversePrimary = Color(0xFF4A6572)
val md_theme_dark_surfaceTint = Color(0xFFB3CAD5)
val md_theme_dark_outlineVariant = Color(0xFF3F484B)
val md_theme_dark_scrim = Color(0xFF000000)

val SpeedometerGreen = Color(0xFF4CAF50)
val SpeedometerYellow = Color(0xFFFFEB3B)
val SpeedometerRed = Color(0xFFF44336)

val IconBrown = Color(0xFF795548)
val IconYellow = Color(0xFFFFEB3B)
val IconPurple = Color(0xFF9C27B0)
val IconOrange = Color(0xFFFF9800)
val IconBlue = Color(0xFF2196F3)
val IconGreen = Color(0xFF4CAF50)
val IconRed = Color(0xFFF44336)
val BikeIconGreen = Color(0xFF00E676)


// ~~
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val PastelRed = Color(0xFFFFADAD)
val PastelOrange = Color(0xFFFFD6A5)
val PastelYellow = Color(0xFFFDFFB6)
val PastelGreen = Color(0xFFCAFFBF)
val PastelBlue = Color(0xFF9BF6FF)
val PastelMauve = Color(0xFFA0C4FF)
val PastelPurple = Color(0xFFBDB2FF)
val PastelPink = Color(0xFFFFC6FF)

val colors = listOf(
    PastelRed,
    PastelOrange,
    PastelYellow,
    PastelGreen,
    PastelBlue,
    PastelMauve,
    PastelPurple,
    PastelPink
)

fun List<Color>.random() = this[Math.random().toInt() % this.size]


val Blue10 = Color(0xFF000F5E)
val Blue20 = Color(0xFF001E92)
val Blue30 = Color(0xFF002ECC)
val Blue40 = Color(0xFF1546F6)
val Blue80 = Color(0xFFB8C3FF)
val Blue90 = Color(0xFFDDE1FF)

val DarkBlue10 = Color(0xFF00036B)
val DarkBlue20 = Color(0xFF000BA6)
val DarkBlue30 = Color(0xFF1026D3)
val DarkBlue40 = Color(0xFF3648EA)
val DarkBlue80 = Color(0xFFBBC2FF)
val DarkBlue90 = Color(0xFFDEE0FF)

val Yellow10 = Color(0xFF261900)
val Yellow20 = Color(0xFF402D00)
val Yellow30 = Color(0xFF5C4200)
val Yellow40 = Color(0xFF7A5900)
val Yellow80 = Color(0xFFFABD1B)
val Yellow90 = Color(0xFFFFDE9C)

val Red10 = Color(0xFF410001)
val Red20 = Color(0xFF680003)
val Red30 = Color(0xFF930006)
val Red40 = Color(0xFFBA1B1B)
val Red80 = Color(0xFFFFB4A9)
val Red90 = Color(0xFFFFDAD4)

val Grey10 = Color(0xFF191C1D)
val Grey20 = Color(0xFF2D3132)
val Grey80 = Color(0xFFC4C7C7)
val Grey90 = Color(0xFFE0E3E3)
val Grey95 = Color(0xFFEFF1F1)
val Grey99 = Color(0xFFFBFDFD)

val BlueGrey30 = Color(0xFF45464F)
val BlueGrey50 = Color(0xFF767680)
val BlueGrey60 = Color(0xFF90909A)
val BlueGrey80 = Color(0xFFC6C5D0)
val BlueGrey90 = Color(0xFFE2E1EC)

// ~~


// On but not used
val IconGlayBlue = Color(0xFF769AB2)


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


/**
 * Returns a color along a green-yellow-red gradient based on the speed.
 */
fun getColorForSpeed(speed: Float, maxSpeed: Float): Color {
    val speedPercentage = (speed / maxSpeed).coerceIn(0f, 1f)
    return when {
        speedPercentage < 0.5f -> lerp(SpeedometerGreen, SpeedometerYellow, speedPercentage * 2)
        else -> lerp(SpeedometerYellow, SpeedometerRed, (speedPercentage - 0.5f) * 2)
    }
}

/**
 * Returns `true` if the color is considered "light" and `false` if it's "dark".
 *
 * This is based on the color's calculated luminance.
 */
fun Color.isLight(): Boolean {
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255
    val luminance = 1 - (0.299 * red + 0.587 * green + 0.114 * blue) / 255
    return luminance < 0.5
}
