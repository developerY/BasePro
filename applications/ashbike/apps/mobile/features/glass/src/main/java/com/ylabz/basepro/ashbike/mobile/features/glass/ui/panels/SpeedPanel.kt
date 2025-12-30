package com.ylabz.basepro.ashbike.mobile.features.glass.ui.panels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.telemetry.MetricDisplay

@Composable
fun SpeedPanel(speed: String, heading: String) {
    // Allow card to wrap content height so text isn't cut off
    Card(modifier = Modifier.fillMaxSize().wrapContentHeight()) {
        Row(
            modifier = Modifier.fillMaxSize()
                .fillMaxWidth()
                .padding(vertical = 16.dp), // Add breathing room
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speed Value
            MetricDisplay(
                label = "SPEED",
                value = speed,
                highlightColor = GlimmerTheme.colors.primary,
                bottomContent = {}
            )

            // Heading with Icon
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Rounded.Explore,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    // Use Secondary (Darker Blue) for Icons to distinguish from Text
                    tint = GlimmerTheme.colors.secondary
                )
                Text(
                    text = heading,
                    style = GlimmerTheme.typography.titleMedium // Strict: Second-Largest Title
                    // No Color set -> Defaults to Calculated White
                )
            }
        }
    }
}