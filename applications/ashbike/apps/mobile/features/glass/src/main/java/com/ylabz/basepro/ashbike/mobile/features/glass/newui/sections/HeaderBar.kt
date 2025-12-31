package com.ylabz.basepro.ashbike.mobile.features.glass.newui.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.newui.elements.BatteryBadge
import com.ylabz.basepro.ashbike.mobile.features.glass.newui.elements.ConnectionBadge
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.BatteryZone


@Composable
fun HeaderBar(
    isConnected: Boolean,
    gear: Int,
    batteryZone: BatteryZone,
    batteryText: String,
    onGearUp: () -> Unit,
    onGearDown: () -> Unit,
    // focusRequester: FocusRequester
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isConnected) {
            // MODE: CONTROLS
            Text(
                text = "GEAR $gear",
                color = GlimmerTheme.colors.positive,
                style = GlimmerTheme.typography.titleLarge
            )
            Spacer(Modifier.width(16.dp))
            Button(onClick = onGearDown, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Rounded.Remove, contentDescription = "Down")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onGearUp,
                modifier = Modifier.size(40.dp)// .focusRequester(focusRequester)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Up")
            }
        }
        /* else {
            // MODE: BRANDING
            Text(
                text = "ASHBIKE",
                color = GlimmerTheme.colors.primary,
                style = GlimmerTheme.typography.titleLarge
            )
        } */

        Spacer(Modifier.weight(1f))

        // STATUS COLUMN
        // 2. RIGHT SIDE: Status Badges (Only when connected)
        if (isConnected) {
            Column(horizontalAlignment = Alignment.End) {
                ConnectionBadge(active = isConnected)
                Spacer(modifier = Modifier.height(4.dp))
                BatteryBadge(
                    zone = batteryZone,
                    level = batteryText,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}