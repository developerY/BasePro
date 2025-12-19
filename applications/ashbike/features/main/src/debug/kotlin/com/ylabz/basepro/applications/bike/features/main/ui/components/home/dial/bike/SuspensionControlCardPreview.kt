package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dial.bike

import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.SuspensionControlCard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Preview for SuspensionControlCard.
 * Placed in the 'debug' source set to avoid including preview code in release builds.
 */
@Preview(showBackground = true, name = "Suspension States")
@Composable
private fun SuspensionControlCardPreview() {
    // We wrap this in MaterialTheme to ensure the dynamic colors (primary/surface) resolve correctly.
    MaterialTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {

                // 1. OPEN State (Should be Green)
                SuspensionControlCard(
                    suspensionState = "OPEN",
                    onToggleSuspension = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. TRAIL State (Should be Orange)
                SuspensionControlCard(
                    suspensionState = "TRAIL",
                    onToggleSuspension = {}
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. LOCK State (Should be Red)
                SuspensionControlCard(
                    suspensionState = "LOCK",
                    onToggleSuspension = {}
                )
            }
        }
    }
}