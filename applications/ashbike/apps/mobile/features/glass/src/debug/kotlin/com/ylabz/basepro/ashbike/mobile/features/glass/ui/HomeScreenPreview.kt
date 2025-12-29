package com.ylabz.basepro.ashbike.mobile.features.glass.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.screens.HomeScreen
import com.ylabz.basepro.core.model.bike.SuspensionState

/**
 * Preview for the Glass HomeScreen.
 * Uses 640x360 dimensions to simulate a typical Smart Glass display.
 */
@Preview(device = "id:ai_glasses_device")
@Composable
fun HomeScreenPreview() {
    // 1. Mock Data for the Preview
    val sampleState = GlassUiState(
        currentGear = 4,
        suspension = SuspensionState.TRAIL,
        isBikeConnected = true,

        // --- UPDATED: Use Raw Types (Double/Float/Int) ---
        rawSpeed = 18.5,          // Was "18.5"
        rawHeading = 315f,        // Was "NW" (315° is North West)
        rawMotorPower = 250.0,    // Was "250"
        rawBattery = 85,          // Was 85 (mapped to rawBattery)

        // These remain strings if they are formatted text
        tripDistance = "12.4"     // Was distance = "12.4"
    )

    // 2. Wrap in Theme (MaterialTheme or your AppTheme)
    MaterialTheme {
        HomeScreen(
            uiState = sampleState,
            onEvent = {
                // No-op for preview
            }
        )
    }
}
