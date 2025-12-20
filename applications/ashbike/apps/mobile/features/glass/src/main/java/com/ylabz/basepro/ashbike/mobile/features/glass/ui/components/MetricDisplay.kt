package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components

// Glimmer Imports
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Text

@Composable
fun MetricDisplay(
    label: String,
    value: String,
    subValue: String? = null,
    highlightColor: Color = GlassColors.NeonCyan,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        // Label (Small, Uppercase)
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = GlassColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        // Main Value (Huge, Colored)
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium, // Bigger font
            color = highlightColor,
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold
        )
        // Sub Value (e.g., Heading)
        if (subValue != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subValue,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}