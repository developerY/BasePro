package com.ylabz.basepro.feature.material3.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Define AppTypography with baseline styles that MaterialTheme expects
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal, // Baseline TitleLarge is Normal weight
        fontSize = 22.sp,              // As per user example for titleLarge
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal, // Baseline HeadlineMedium is Normal weight
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )
    // TODO: Fill in all other standard Typography styles (displayLarge, labelSmall, etc.)
    // You can copy them from MaterialTheme.typography and adjust as needed, or
    // rely on the defaults provided by the Typography constructor for unlisted styles.
)

// Define corresponding emphasized styles as separate constants
val EmphasizedTitleLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,   // Emphasized is Bold
    fontSize = 22.sp,               // Matching user'''s example size for titleLarge
    lineHeight = 28.sp,
    letterSpacing = 0.1.sp          // As per user example
)

val EmphasizedBodyLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium, // Example: Emphasized with Medium weight
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
)

val EmphasizedHeadlineMedium = TextStyle(
    fontFamily = FontFamily.Serif,
    fontWeight = FontWeight.SemiBold, // Emphasized with SemiBold, as in former ExpressiveTypography
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = 0.sp
)
