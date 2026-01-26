package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.stage

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.R
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.telemetry.MetricDisplay
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.theme.GlassColors

@Composable
fun RideStatsDisplayPanel(
    distance: String,
    calories: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        // We now use MetricDisplay to reuse the exact same layout style as Speed/Heading
        MetricDisplay(
            label = stringResource(R.string.distance_km),
            value = distance,
            // We can keep it Cyan to match Speed, or use a different color like White
            highlightColor = GlassColors.NeonCyan,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            bottomContent = {
                // The Calories section now mirrors the "Heading" style
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment, // Fire icon
                        contentDescription = stringResource(R.string.distance_km),
                        tint = Color(0xFFFF9800), // Keep Orange tint for the icon
                        modifier = Modifier.width(16.dp) // Similar size to compass icon
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.kcal, calories), // "$calories kcal",
                        color = Color.White, // White text, just like Heading
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
    }
}