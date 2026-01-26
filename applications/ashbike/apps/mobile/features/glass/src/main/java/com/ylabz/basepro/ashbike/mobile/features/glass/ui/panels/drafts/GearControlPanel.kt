package com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels.drafts

// Glimmer Imports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.theme.GlassColors

@Composable
fun GearControlPanel(
    currentGear: Int,
    onGearUp: () -> Unit,
    onGearDown: () -> Unit,
    focusRequester: FocusRequester
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "ACTIVE GEAR",
            style = MaterialTheme.typography.labelSmall,
            color = GlassColors.TextSecondary,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Big Gear Number
        Text(
            text = "$currentGear",
            fontSize = 64.sp,
            color = GlassColors.NeonGreen,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Control Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Down Button
            // Note: We use a custom Modifier to make circular buttons look sharp
            Button(
                onClick = onGearDown,
                modifier = Modifier
                    .height(48.dp)
                    .width(48.dp)
            ) {
                Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            // Up Button (Focused)
            Button(
                onClick = onGearUp,
                modifier = Modifier
                    .height(48.dp)
                    .width(48.dp)
                    .focusRequester(focusRequester) // Focus lands here
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}