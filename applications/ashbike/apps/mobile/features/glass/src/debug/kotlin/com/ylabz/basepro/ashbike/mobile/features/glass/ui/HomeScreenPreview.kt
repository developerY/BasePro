package com.ylabz.basepro.ashbike.mobile.features.glass.ui


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.HomeScreen
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
        currentSpeed = "18.5",
        suspension = SuspensionState.TRAIL,
        connectionStatus = "Connected"
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