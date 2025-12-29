package com.ylabz.basepro.ashbike.mobile.features.glass.ui.theme

// ... imports ...
import androidx.compose.ui.graphics.Color

// --- GLASS HUD DESIGN SYSTEM ---
object GlassColors {
    val NeonCyan = Color(0xFF00E5FF)   // Speed / Primary Data
    val NeonGreen = Color(0xFF00FF00)  // Active State / Gear
    val WarningRed = Color(0xFFFF1744) // Limits / Errors
    val HudBackground = Color(0xFF121212).copy(alpha = 0.85f) // Dark semi-transparent
    val TextPrimary = Color.White
    val TextSecondary = Color(0xFFB0BEC5)

    // ... existing ...
    val ZoneEasy = Color(0xFF00E5FF)   // Blue (Warmup)
    val ZoneAerobic = Color(0xFF00FF00) // Green (Fat Burn)
    val ZoneThreshold = Color(0xFFFF9800) // Orange (Cardio)
    val ZoneMax = Color(0xFFFF1744)    // Red (Peak)
}