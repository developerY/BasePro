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
    highlightColor: Color = GlassColors.NeonCyan,
    modifier: Modifier = Modifier,
    // The "Slot" - allows us to pass any complex UI into the bottom area
    bottomContent: @Composable () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        // LABEL
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = GlassColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        // VALUE
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium,
            color = highlightColor,
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // BOTTOM CONTENT (The new Data Row)
        bottomContent()
    }
}