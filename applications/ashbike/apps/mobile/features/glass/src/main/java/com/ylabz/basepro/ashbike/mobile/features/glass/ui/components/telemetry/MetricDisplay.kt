package com.ylabz.basepro.ashbike.mobile.features.glass.ui.components.telemetry

// Glimmer Imports
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import com.ylabz.basepro.ashbike.mobile.features.glass.ui.theme.GlassColors

@Composable
fun MetricDisplay(
    label: String,
    value: String,
    // Use Glimmer Secondary (usually a readable blue/cyan) as default
    highlightColor: Color = GlimmerTheme.colors.secondary,
    modifier: Modifier = Modifier,
    // The "Slot" - allows us to pass any complex UI into the bottom area
    bottomContent: @Composable () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        // LABEL
        Text(
            text = label.uppercase(),
            // Glimmer typography is optimized for legibility
            style = GlimmerTheme.typography.bodySmall, // Strict: Smallest Body
            // We can't set color to onSurfaceVariant as it's not exposed,
            // so we rely on default or explicit Theme colors if needed.
            // But Glimmer handles default text color automatically.
            color = GlassColors.TextSecondary, // might remove
        )
        // VALUE
        Text(
            text = value,
            // FIX: Use headlineLarge if displayMedium is too big,
            // but displayMedium is standard for Hero numbers.
            // If it clips, switch to headlineLarge.
            style = GlimmerTheme.typography.titleLarge, // Strict: Largest Title
            color = highlightColor,
        )

        Spacer(modifier = Modifier.height(4.dp))

        // BOTTOM CONTENT (The new Data Row)
        bottomContent()
    }
}